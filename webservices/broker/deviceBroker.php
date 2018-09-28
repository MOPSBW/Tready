<?php
/**
 * Created by PhpStorm.
 * User: bkrupp
 * Date: 1/9/18
 * Time: 10:30 AM
 */

class deviceBroker
{
    public function enableAllDevices($request) {
      $db = new db();
      $db->enableAllDevices();
      return new response(0, null);
    }
    public function disableAllDevices($request) {
      $db = new db();
      $db->disableAllDevices();
      return new response(0, null);
    }

    public function getConfig($request) {
        /*
         * Pi sends up credentials each time, need to authenticate first
         */
        $securityBroker = new securityBroker();
        $session = new session();


        $securityBroker->authenticate($request);
        if (!$session->isSessionValid()) {
            return new response(-1, "Cannot create event, credentials first");
        }

        $db = new db();
        $deviceConfig = $db->getDeviceConfig($request->deviceIdentifier);


        // Update device contact time
        $this->updateDeviceContact($request->deviceIdentifier);

        return new response(0, $deviceConfig);
    }

    // Called from web services
    public function setConfig($request) {
        // Update database with config
        $db = new db();

        $configKeyValuePair = $request->configKeyValuePairToUpdate;
        // Request should take in a key->value pair of device configuration key and values
        foreach(get_object_vars($configKeyValuePair) as $configKey=>$configValue) {
            $db->updateDeviceConfig($request->deviceIdentifier, $configKey, $configValue);
        }

        // Do not update device contact time as this is called from web client or mobile client

        return new response(0, null);
    }

    public function updateDeviceContact($deviceId) {
        $db = new db();

        $db->updateDeviceConfig($deviceId, "ipAddress", $_SERVER['REMOTE_ADDR']);
        $db->updateDeviceConfig($deviceId, "lastContact", time());

        return new response(0, null);
    }

    public function getDevices() {
        $db = new db();
        $devices = $db->getDevices();
        return new response(0, $devices);
    }

}
