from io import BytesIO
from picamera import PiCamera
import picamera.array
import numpy as np
import datetime
from uuid import getnode as get_mac
import json
import base64
import requests
import threading
from copy import deepcopy
import os

class CreateEventRequest(object):
    global cfgHubUsername, cfgHubPassword

    def __init__(self, videoData, videoLength, thumbnailData, username, password):
        self.deviceIdentifier = str(get_mac())
        self.timeStarted = datetime.datetime.now().strftime("%s")
        self.timeEnded = int(self.timeStarted) + int(videoLength)
        self.thumbnailData = thumbnailData
        self.videoData = videoData
        self.username = username
        self.password = password

def postVideo(streamData, videoLength, thumbnailData):
    cfgHubLocation = "http://astralqueen.bw.edu/skunkworks/service.php"
    cfgHubUsername = "agent0"
    cfgHubPassword = "password"

    try:
        base64EncodedVideo = base64.encodestring(streamData.read())

        base64EncodedThumbnail = base64.encodestring(thumbnailData.read())
        createEventRequest = CreateEventRequest(base64EncodedVideo, videoLength, base64EncodedThumbnail, cfgHubUsername, cfgHubPassword)


        print("Sending Video to Hub")
        reqjson = {
            "servicePath": "/event/createEvent",
            "serviceRequest": json.dumps(createEventRequest.__dict__)
        }

        response = requests.post(cfgHubLocation, data=reqjson)

        print("Response from Hub : " + response.text)

    except Exception as e:
        print("Critical exception caught in posting video" + str(e.message))


class MotionDetector(picamera.array.PiMotionAnalysis):
    # The motion detection is derived from here: http://picamera.readthedocs.io/en/release-1.13/api_array.html#pimotionanalysis
    # Alternatively, we can look at different approaches but in testing this seemed to work well

    # Motion detection uses sum of absolute differences to determine if motion exists
    def analyze(self, motionArray):
        global motionDetected

        motionArray = np.sqrt(
            np.square(motionArray['x'].astype(np.float)) +
            np.square(motionArray['y'].astype(np.float))
        ).clip(0, 255).astype(np.uint8)

        # If there're more than cfgMotionDetectionVectorThreshold vectors with a magnitude greater
        # than cfgMotionDetectionMagnitudeThreshold, then say we've detected motion
        if (motionArray > 60).sum() > 10:
            motionDetected = True
            # VERY IMPORTANT! You cannot wait inside of analyse, otherwise it will not keep recording the stream
            # This is why we use a shared variable that motion detection will set, and another thread will monitor

camera = PiCamera()
camera.resolution = (1280, 720)
camera.rotation = 180
motionMonitorStream = picamera.PiCameraCircularIO(camera, seconds=10)
camera.start_recording(motionMonitorStream, format='h264', motion_output=MotionDetector(camera))

motionDetected = False
timeRecorded = 0
streamRecordingCount = 0

while True:
    # Wait a second before checking for motion each time
    camera.wait_recording(1)

    if motionDetected:
        print('Motion detected!\n')
        thumbnailStream = BytesIO()
        camera.capture(thumbnailStream, format='jpeg', use_video_port=True)
        thumbnailStream.seek(0)
        open("capture.jpg", 'wb').write(thumbnailStream.read())

        # Copy x amount of seconds to a new BytesIO buffer
        recordingStream = BytesIO()
        motionMonitorStream.copy_to(recordingStream, seconds=10)
        motionMonitorStream.clear()

        # Start recording to recordingStream
        camera.split_recording(recordingStream)

        # Wait until motion is no longer detected, then split
        # recording back to the in-memory circular buffer
        while motionDetected:
            motionDetected = False

            if timeRecorded > 30:
                print("Max time met, copying stream to send to server")
                # Create a send stream and copy our recording stream to it, after we are done, flush our recording stream
                transferToServerStream = deepcopy(recordingStream)

                # Jump back to motionMonitorStream and then create new recording stream (truncate alone does not work)
                # to start recording a continuation of the video
                camera.split_recording(motionMonitorStream)
                recordingStream = BytesIO()
                camera.split_recording(recordingStream)

                # Save file for processing
                transferToServerStream.seek(0)
                recordingFile = str(streamRecordingCount) + ".h264"
                tempRecordingFile = str(recordingFile) + "tmp"

                open(tempRecordingFile, 'wb').write(transferToServerStream.read())
                os.rename(tempRecordingFile, recordingFile)

                streamRecordingCount = streamRecordingCount + 1

                # If you uncomment below, it will send to server and cause gap to appear in the video.
                #transferToServerStream.seek(0)
                #thumbnailStream.seek(0)
                #t = threading.Thread(target=postVideo, args=(transferToServerStream, timeRecorded, thumbnailStream))
                #t.start()

                if streamRecordingCount == 2:
                    break

                timeRecorded = 0

            print('Motion still detected, waiting 5 seconds before checking again\n')
            timeRecorded = timeRecorded + 5
            camera.wait_recording(5)

        print('Motion stopped!\n')

        # Clear our recording stream
        recordingStream.seek(0)
        recordingStream.truncate()

        # Start recording back to motion detection stream
        camera.split_recording(motionMonitorStream)
    else:
        print("Motion not detected\n")