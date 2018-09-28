#!/usr/bin/python
from io import BytesIO
from time import sleep
from picamera import PiCamera
import picamera
import picamera.array
import numpy as np
from PIL import Image, ImageChops
import ConfigParser
import time
import os
import threading
import picamera
import socket
import sys
import time
import os
import datetime
import json
import base64
import requests
import urllib2
import logging
import logging.handlers
from uuid import getnode as get_mac

class MotionDetector(picamera.array.PiMotionAnalysis):
    def analyze(self, a):
        return


# After testing this on the pi, 30 seconds takes up approx 40 to 50% of the memory on a Pi Zero
streamMaxSeconds = 30

# Set resolutions up for capture and detection
captureWidth = 1024
captureHeight = 768

# Setup Camera
camera = PiCamera()
camera.resolution = (captureWidth, captureHeight)
camera.rotation = 270
camera.framerate = 30

# Setup Circular stream to monitor for motion
motionMonitorStream = picamera.PiCameraCircularIO(camera, seconds=streamMaxSeconds)
print("The size is " + str(motionMonitorStream.size))

camera.start_recording(
    motionMonitorStream, format='h264',
    motion_output=MotionDetector(camera)
)
print("Started monitoring for motion")
while True:
    time.sleep(60 * 10)
    print("The size of stream before clear " + str(motionMonitorStream.size))
    motionMonitorStream.clear()
    print("The size of stream after clear  " + str(motionMonitorStream.size))
    print("Cleared out stream")

