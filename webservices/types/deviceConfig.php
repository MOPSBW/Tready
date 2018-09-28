<?php
/**
 * Created by PhpStorm.
 * User: bkrupp
 * Date: 1/10/18
 * Time: 8:35 AM
 */

class deviceConfig
{
    public $deviceIdentifier;
    public $ipAddress;
    public $deviceEnabled;
    public $lastContact;
    public $deviceName;
    public $preMotionCapture;
    public $postMotionCapture;
    public $captureResolution;
    public $motionDetectionMagnitudeThreshold;
    public $motionDetectionVectorThreshold;
    public $orientation;


    function __construct($deviceIdentifier, $ipAddress, $deviceEnabled, $lastContact, $deviceName, $preMotionCapture,
                         $postMotionCapture, $captureResolution, $motionDetectionMagnitudeThreshold,
                         $motionDetectionVectorThreshold, $orientation) {
        $this->deviceIdentifier = $deviceIdentifier;
        $this->ipAddress = $ipAddress;
        $this->deviceEnabled = $deviceEnabled;
        $this->lastContact = $lastContact;
        $this->deviceName = $deviceName;
        $this->preMotionCapture = $preMotionCapture;
        $this->postMotionCapture = $postMotionCapture;
        $this->captureResolution = $captureResolution;
        $this->motionDetectionMagnitudeThreshold = $motionDetectionMagnitudeThreshold;
        $this->motionDetectionVectorThreshold = $motionDetectionVectorThreshold;
        $this->orientation = $orientation;
    }

}