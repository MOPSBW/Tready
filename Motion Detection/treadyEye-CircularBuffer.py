#!/usr/bin/python
from io import BytesIO
from time import sleep
from picamera import PiCamera
import picamera.array
import numpy as np
import ConfigParser
import threading
import picamera
import sys
import datetime
import json
import base64
import requests
import os
import urllib2
import logging.handlers
from uuid import getnode as get_mac

class CreateEventRequest(object):
    global cfgHubUsername, cfgHubPassword

    def __init__(self, videoData, videoLength, thumbnailData):
        self.deviceIdentifier = str(get_mac())
        self.timeStarted = datetime.datetime.now().strftime("%s")
        self.timeEnded = int(self.timeStarted) + int(videoLength)
        self.thumbnailData = thumbnailData
        self.videoData = videoData
        self.username = cfgHubUsername
        self.password = cfgHubPassword


class GetConfigRequest(object):
    global cfgHubUsername, cfgHubPassword

    def __init__(self):
        self.deviceIdentifier = str(get_mac())
        self.username = cfgHubUsername
        self.password = cfgHubPassword


def postVideo(streamData, videoLength, thumbnailData):
    retryAttempt = 1
    retryMax = 5
    while True:
        try:
            logDebug("In Post Video")
            global cfgHubLocation, cfgHubUsername, cfgHubPassword
            base64EncodedVideo = base64.encodestring(streamData.read())
            base64EncodedThumbnail = base64.encodestring(thumbnailData.read())
            createEventRequest = CreateEventRequest(base64EncodedVideo, videoLength, base64EncodedThumbnail)

            logDebug("Sending Video to Hub")
            reqjson = {
                "servicePath": "/event/createEvent",
                "serviceRequest": json.dumps(createEventRequest.__dict__)
            }

            response = requests.post(cfgHubLocation, data=reqjson)

            logDebug("Response from Hub : " + response.text)
            break
        except Exception as e:
            if retryAttempt <= retryMax:
                logError("Critical exception caught in posting video, attempting retry " + str(retryAttempt))
                retryAttempt = retryAttempt + 1
            else:
                logError("Critical exception caught in posting video, FAILED, retried max " + str(retryMax))


def loadConfigurationFile():
    # First get configuration settings from configuration file
    try:
        configParser = ConfigParser.RawConfigParser()
        configParser.read("/home/pi/treadyEye.cfg")

        # General Settings
        global cfgHubLocation, cfgHubUsername, cfgHubPassword, cfgEyeEnabled
        cfgHubLocation = configParser.get("General", "hubHostname")
        cfgHubLocation = "http://" + cfgHubLocation + "/skunkworks/service.php"
        cfgHubUsername = configParser.get("General", "hubUsername")
        cfgHubPassword = configParser.get("General", "hubPassword")

        # Debug Settings
        global cfgDebugLog
        cfgDebugLog = configParser.getboolean("Debug", "logDebug")

    except IOError as error:
        logDebug("Unable to find configuration file: " + error.message)
    except ConfigParser.NoOptionError as error:
        logDebug("Unable to find option in configuration file: " + error.message)


def internet_on():
    try:
        urllib2.urlopen(cfgHubLocation, timeout=3)
        return True
    except urllib2.URLError as err:
        logDebug("Unable to connect to server " + cfgHubLocation + ", please check connection")
        return False


def loadConfigurationFromServer():
    # Attempt to get configuration settings via a web service
    while not internet_on():
        logDebug("Internet connection is not available to load configuration, will recheck in a few seconds")
        sleep(2)
    response = ""
    try:
        getConfigRequest = GetConfigRequest()
        logDebug("Getting Configuration From Server")
        reqjson = {
            "servicePath": "/device/getConfig",
            "serviceRequest": json.dumps(getConfigRequest.__dict__)
        }

        response = requests.post(cfgHubLocation, data=reqjson)
        logDebug("Received Configuration")

        # Parse response
        jsonResponse = json.loads(response.text)
        configResponse = jsonResponse["responseData"]

        # Camera Settings
        global cfgOrientationDegrees
        cfgOrientationDegrees = int(getDegreesFromOrientation(configResponse["orientation"]))

        # Motion Settings
        global cfgCaptureResolution, cfgPreMotionCapture, cfgPostMotionCapture
        cfgPreMotionCapture = int(configResponse["preMotionCapture"])
        cfgPostMotionCapture = int(configResponse["postMotionCapture"])
        cfgCaptureResolution = int(configResponse["captureResolution"])

        global cfgMotionDetectionMagnitudeThreshold, cfgMotionDetectionVectorThreshold
        cfgMotionDetectionMagnitudeThreshold = int(configResponse["motionDetectionMagnitudeThreshold"])
        cfgMotionDetectionVectorThreshold = int(configResponse["motionDetectionVectorThreshold"])

        global cfgDeviceEnabled
        deviceEnabled = str(configResponse["deviceEnabled"])
        if deviceEnabled == "Yes":
            cfgDeviceEnabled = True
        else:
            cfgDeviceEnabled = False
    except Exception as e:
        logError("Critical exception caught in loading server configuration " + str(e) + " response received " + str(
            response.text))
    except:
        logError("Unexpected error:" + str(sys.exc_info()[0]))
        raise


# Available Settings
def loadConfiguration(runInThreadMode=False):
    global camera, currentlyRecording
    timeBetweenLoadingConfiguration = 60
    timeBetweenCheckingIfRecording = 5
    while True:
        totalTimeSlept = 0
        try:
            # If we are recording, wait up to another interval before checking again
            if currentlyRecording and totalTimeSlept <= timeBetweenLoadingConfiguration:
                sleep(timeBetweenCheckingIfRecording)
                totalTimeSlept = totalTimeSlept + timeBetweenCheckingIfRecording
                continue
            loadConfigurationFile()
            loadConfigurationFromServer()

            # Enable rotation to be changed on camera, but other settings require restart
            camera.rotation = cfgOrientationDegrees

            if not runInThreadMode:
                break
            else:
                logDebug("Sleeping for 10 seconds to recheck configuration file")
                sleep(timeBetweenLoadingConfiguration)
        except Exception as e:
            logError("Critical exception in running loadConfiguration " + str(e))

def getDegreesFromOrientation(orientation):
    return {
        'landscapePowerUp': 270,
        'landscapePowerDown': 90,
        'portraitUpsideDown': 180,
        'portrait': 0
    }.get(orientation, 0)  # Default to 0 if invalid option


def getCaptureResolution(captureResolution):
    return {
        4: (3280, 2464),
        3: (1640, 1232),
        2: (1024, 768),
        1: (800, 600),
        0: (640, 800)
    }.get(captureResolution, 2)


def logDebug(message):
    global cfgDebugLog, logger
    if cfgDebugLog:
        logger.debug(str(message))


def logError(message):
    global logger
    logger.error(message)


def captureThumbnail(camera):
    thumbnailStream = BytesIO()
    camera.capture(thumbnailStream, format='jpeg', use_video_port=True, resize=(320, 240))
    thumbnailStream.seek(0)
    return thumbnailStream


def captureMotion():
    global motionMonitorStream, motionDetected, camera, cfgPreMotionCapture, cfgPostMotionCapture, streamMaxSeconds
    global cfgDeviceEnabled
    global currentlyRecording

    timeBetweenMotionChecks = 5
    streamRecordingCount = 0

    while True:
        # Check if we should not detect motion
        if not cfgDeviceEnabled:
            logDebug("Device not enabled")
        elif motionDetected:
            logDebug("Starting recording of motion")
            currentlyRecording = True

            thumbnailData = captureThumbnail(camera)

            recordingTime = 0

            # The idea here is to keep recording while we detect motion or until we run out of room to record
            # The only way to exit this loop is after we wend a recording
            while True:
                sendRecording = False

                # If motion is detected still after resetting, then record for another second and reset the flag
                if motionDetected:
                    logDebug("Recorded total of " + str(recordingTime) + " seconds")

                    # Reset motion, then sleep, if it happens again then we will keep recording
                    motionDetected = False  # VERY IMPORTANT! Must exist before sleep, otherwise you will set it and then quickly evaluate it
                    camera.wait_recording(timeBetweenMotionChecks)  # If set too low, you may get more choppy videos motion may not be detected within 1 second
                    recordingTime = recordingTime + timeBetweenMotionChecks

                    # If we go beyond the time we have available, then we need to send the recording out and start new
                    # Uploading 30 second chunks at at time sends a manageable amount to the server
                    if recordingTime > streamMaxSeconds:
                        logDebug("Reached max recording at " + str(recordingTime) + " seconds, starting new")
                        sendRecording = True
                else:
                    logDebug("Motion no longer detected at " + str(recordingTime) + " seconds, sending capture")
                    sendRecording = True
                    currentlyRecording = False

                if sendRecording:
                    logDebug("Sending Recording of " + str(recordingTime) + " seconds")
                    # Capture the post motion
                    camera.wait_recording(cfgPostMotionCapture)

                    # Copy the video to a stream
                    sendStream = BytesIO()
                    actualRecordingTime = recordingTime + cfgPreMotionCapture + cfgPostMotionCapture
                    motionMonitorStream.copy_to(sendStream, seconds=actualRecordingTime)
                    sendStream.seek(0)

                    # Save file for processing
                    recordingFile = str(streamRecordingCount) + ".h264"
                    tempRecordingFile = str(recordingFile) + "tmp"
                    open(tempRecordingFile, 'wb').write(sendStream.read())
                    os.rename(tempRecordingFile, recordingFile)

                    print("saved file " + str(recordingFile))

                    streamRecordingCount = streamRecordingCount + 1

                    #t = threading.Thread(target=postVideo, args=(sendStream, actualRecordingTime, thumbnailData))
                    #t.start()
                    break

        # Wait one second until checking again and reset motion
        motionDetected = False  # This is reset in case motion was detected but the camera was not enabled
        sleep(1)


class MotionDetector(picamera.array.PiMotionAnalysis):
    # The motion detection is derived from here: http://picamera.readthedocs.io/en/release-1.13/api_array.html#pimotionanalysis
    # Alternatively, we can look at different approaches but in testing this seemed to work well

    # Motion detection uses sum of absolute differences to determine if motion exists
    def analyze(self, motionArray):
        global cfgMotionDetectionMagnitudeThreshold, cfgMotionDetectionVectorThreshold
        global motionDetected

        motionArray = np.sqrt(
            np.square(motionArray['x'].astype(np.float)) +
            np.square(motionArray['y'].astype(np.float))
        ).clip(0, 255).astype(np.uint8)

        # If there're more than cfgMotionDetectionVectorThreshold vectors with a magnitude greater
        # than cfgMotionDetectionMagnitudeThreshold, then say we've detected motion
        if (motionArray > cfgMotionDetectionMagnitudeThreshold).sum() > cfgMotionDetectionVectorThreshold:
            motionDetected = True
            # VERY IMPORTANT! You cannot wait inside of analyse, otherwise it will not keep recording the stream
            # This is why we use a shared variable that motion detection will set, and another thread will monitor


# This is a shared variable that is set by motion detection when motion is detected and is checked/reset by our
# capture motion thread
motionDetected = False
currentlyRecording = False

# After testing this on the pi, 30 seconds takes up approx 18-23% of the memory on a Pi Zero
streamMaxSeconds = 30

# Setup logger
logger = logging.getLogger()
fh = logging.handlers.RotatingFileHandler(filename="/home/pi/treadyEye.log", maxBytes=1000000, backupCount=2)
fh.setLevel(logging.DEBUG)
fhError = logging.handlers.RotatingFileHandler(filename="/home/pi/treadyEyeError.log", maxBytes=1000000, backupCount=2)
fhError.setLevel(logging.ERROR)
formatter = logging.Formatter("%(asctime)s [%(threadName)-12.12s] [%(levelname)-5.5s]  %(message)s")
fh.setFormatter(formatter)
fhError.setFormatter(formatter)
logger.addHandler(fh)
logger.addHandler(fhError)
logger.setLevel(logging.DEBUG)
logger.propagate = False

# Initialize camera
global camera
camera = PiCamera()

# Read configuration and start thread to check configuration periodically
loadConfiguration()
confThread = threading.Thread(target=loadConfiguration, args=(True,))
confThread.start()

# Setup Camera from configuration
camera.framerate = 30
(captureWidth, captureHeight) = getCaptureResolution(cfgCaptureResolution)
camera.resolution = (captureWidth, captureHeight)
camera.rotation = cfgOrientationDegrees

# Setup Circular stream to monitor for motion
motionMonitorStream = picamera.PiCameraCircularIO(camera, seconds=streamMaxSeconds)

camera.start_recording(
    motionMonitorStream, format='h264',
    motion_output=MotionDetector(camera)
)
logDebug("Started monitoring for motion")

motionThread = threading.Thread(target=captureMotion)
motionThread.start()

# Periodically we have to clear the stream otherwise a memory leak continues to build.
while True:
    # Clear the stream every 10 minutes
    sleep(10 * 60)

    # If we are currently recording, keep checking if we are recording every 5 seconds before clearing the stream
    while currentlyRecording:
        logDebug("Recording in progress, will check back to clear stream")
        sleep(5)

    logDebug("Clearing Stream")
    motionMonitorStream.clear()
