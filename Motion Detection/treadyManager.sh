#!/bin/bash
treadyEyePID=$(pgrep treadyEye.py)
treadyUploadPID=$(pgrep treadyEye-Uploa)

if [ -z $treadyEyePID ]; then
  /home/pi/treadyEye.py & >/home/pi/treadyEyeError.log 2>&1
fi

if [ -z $treadyUploadPID ]; then
  /home/pi/treadyEye-Uploader.py & >/home/pi/treadyEye-UploadError.log 2>&1
fi
