#!/usr/bin/python
from io import BytesIO
from time import sleep
import ConfigParser
import os
import datetime
import json
import base64
import requests
import logging.handlers
from uuid import getnode as get_mac
import re
import sys


class CreateEventRequest(object):
    global cfgHubUsername, cfgHubPassword

    def __init__(self, videoData, startTime, videoLength, thumbnailData):
        self.deviceIdentifier = str(get_mac())
        self.timeStarted = startTime
        self.timeEnded = int(self.timeStarted) + int(videoLength)
        self.thumbnailData = thumbnailData
        self.videoData = videoData
        self.username = cfgHubUsername
        self.password = cfgHubPassword


def postVideo(streamData, startTime, videoLength, thumbnailData):
    retryAttempt = 1
    retryMax = 5
    while True:
        try:
            logDebug("In Post Video")
            global cfgHubLocation, cfgHubUsername, cfgHubPassword
            base64EncodedVideo = base64.encodestring(streamData.read())
            base64EncodedThumbnail = base64.encodestring(thumbnailData.read())
            createEventRequest = CreateEventRequest(base64EncodedVideo, startTime, videoLength, base64EncodedThumbnail)

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


def logDebug(message):
    global cfgDebugLog, logger
    if cfgDebugLog:
        logger.debug(str(message))


def logError(message):
    global logger
    logger.error(message)


# Setup logger
logger = logging.getLogger()
fh = logging.handlers.RotatingFileHandler(filename="/home/pi/treadyEye-Upload.log", maxBytes=1000000, backupCount=2)
fh.setLevel(logging.DEBUG)
fhError = logging.handlers.RotatingFileHandler(filename="/home/pi/treadyEye-UploadError.log", maxBytes=1000000, backupCount=2)
fhError.setLevel(logging.ERROR)
formatter = logging.Formatter("%(asctime)s [%(threadName)-12.12s] [%(levelname)-5.5s]  %(message)s")
fh.setFormatter(formatter)
fhError.setFormatter(formatter)
logger.addHandler(fh)
logger.addHandler(fhError)
logger.setLevel(logging.DEBUG)
logger.propagate = False

loadConfigurationFile()

reValidFile = re.compile('(\d+)\-(\d+)\.h264$')

timeBetweenChecks = 15

# Go into loop monitoring for data to post to server
while True:

    try:
        logDebug("Checking again in " + str(timeBetweenChecks) + " seconds ...")
        sleep(timeBetweenChecks)

        os.chdir("/home/pi")
        files = os.listdir("/home/pi")
        files.sort(key=os.path.getmtime)

        for candidateFile in files:
            if reValidFile.match(candidateFile):
                logDebug("Found candidate file, processing " + str(candidateFile))
                parts = reValidFile.match(candidateFile)

                startTime = parts.group(1)
                videoLength = parts.group(2)

                thumbnailFile = str(startTime) + ".jpg"

                with open(candidateFile, 'rb') as f:
                    videoData = BytesIO(f.read())
                    videoData.seek(0)

                with open(thumbnailFile, 'rb') as f:
                    thumbnailData = BytesIO(f.read())
                    thumbnailData.seek(0)

                # Now that we have all the data, send it
                postVideo(videoData, startTime, videoLength, thumbnailData)

                # Once we come back, remove it!
                os.remove(candidateFile)
                os.remove(thumbnailFile)
    except Exception as e:
        logError("Exception occurred " + str(e))
    except:
        logError("Unexpected error:" + str(sys.exc_info()[0]))
