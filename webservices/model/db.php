<?php

require_once(__DIR__ . "/../types/event.php");
require_once(__DIR__ . "/../types/deviceConfig.php");

/*
 * Each database call should either return data or a return a boolean that indicates if the statement
 * executed successfully
 */

class db
{
    private $algorithm = "sha256";
    public $lastError;

    private function getDbConnection() {
        // Setup DB Connection Information
        $servername = "localhost";
        $username = "skunkworks";
        $password = "skunkw0rks";
        $database = "skunkworks";

        // Establish DB Connection
        $dbConnection = new mysqli($servername, $username, $password, $database);

        if ($dbConnection->connect_error) {
            echo "Cannot connect to DB " . $dbConnection->connect_error;
            return false;
        }

        return $dbConnection;
    }

    /*
     * User
     */
    public function changeAccountInfo($userId, $username, $password) {
        $db = $this->getDbConnection();

        $password = hash($this->algorithm, $password);

        $sqlStatement = $db->prepare("update tblUsers set username = ?, password = ? where userId = ?");
        $sqlStatement->bind_param("ssi", $username, $password, $userId);

        // Check if we have an issue executing
        if ($sqlStatement->execute() === FALSE) {
            return false;
        }
        return true;
    }

    public function authenticateUser($username, $password) {
        $db = $this->getDbConnection();

        // Hash password before checking
        $password = hash($this->algorithm, $password);

        $sqlStatement = $db->prepare("select userId from tblUsers WHERE username = ? AND password = ?");

        $sqlStatement->bind_param("ss", $username, $password);

        // Check if we have an issue executing
        if ($sqlStatement->execute() === FALSE) {
            return -1;
            //return "Error: " . $sqlStatement->error;
        }

        $sqlStatement->bind_result($userId);
        if ($sqlStatement->fetch()) {
            return $userId;
        } else {
            return -1;
        }
    }

    function getHash($password) {
        return hash($this->algorithm, $password);
    }

    /*
     * Events
     */
    public function createEvent($deviceIdentifier, $timeStarted, $timeEnded) {
        $db = $this->getDbConnection();

        $sqlStatementInsert = $db->prepare("INSERT INTO tblEvents SET deviceIdentifier = ?, timeStarted = ?, timeEnded = ?");
        $sqlStatementInsert->bind_param("sii", $deviceIdentifier, $timeStarted, $timeEnded);

        // Check if we have an issue executing
        if ($sqlStatementInsert->execute() === FALSE) {
            $this->lastError = $sqlStatementInsert->error;
            return false;
        }

        $sqlStatementSelect = $db->prepare("SELECT LAST_INSERT_ID();");
        if ($sqlStatementSelect->execute() === FALSE) {
            return -1;
        }
        $sqlStatementSelect->bind_result($eventId);
        $sqlStatementSelect->fetch();

        if ($eventId >= 0) {
            return $eventId;
        } else {
            return false;
        }
    }

    public function getEvent($eventId) {
        $db = $this->getDbConnection();

        $sqlStatement = $db->prepare("SELECT eventId, deviceIdentifier, timeStarted, timeEnded FROM tblEvents where eventId = ?");
        $sqlStatement->bind_param("i", $eventId);

        // Check if we have an issue executing
        if ($sqlStatement->execute() === FALSE) {
            $this->lastError = $sqlStatement->error;
            return false;
        }

        $sqlStatement->bind_result($eventId, $deviceIdentifier, $timeStarted, $timeEnded);
        if ($sqlStatement->fetch()) {
            $event = new event($eventId, $this->getDeviceName($deviceIdentifier), $timeStarted, $timeEnded);

            return $event;
        }

        return null;
    }

    public function getEvents() {
        $db = $this->getDbConnection();

        $events = array();

        $sqlStatement = $db->prepare("SELECT eventId, deviceIdentifier, timeStarted, timeEnded FROM tblEvents ORDER BY timeStarted DESC");
        $sqlStatement->bind_param("i", $eventSummaryId);

        // Check if we have an issue executing
        if ($sqlStatement->execute() === FALSE) {
            $this->lastError = $sqlStatement->error;
            return false;
        }

        $sqlStatement->bind_result($eventId, $deviceIdentifier, $timeStarted, $timeEnded);
        while ($sqlStatement->fetch()) {
            $event = new event($eventId, $this->getDeviceName($deviceIdentifier), $timeStarted, $timeEnded);

            array_push($events, $event);
        }

        return $events;
    }

    public function deleteAllEvents() {
        $db = $this->getDbConnection();

        $deleteStatement = $db->prepare("DELETE FROM tblEvents");
        $deleteStatement->execute();
        return true;
    }

    public function deleteEvent($eventId) {
        $db = $this->getDbConnection();

        $deleteStatement = $db->prepare("DELETE FROM tblEvents WHERE eventId = ?");

        $deleteStatement->bind_param("i", $eventId);

        if ($deleteStatement->execute() === FALSE) {
            $this->lastError = $deleteStatement->error;
            return false;
        }
        return true;
    }

    public function getDeviceName($deviceId) {
        $db = $this->getDbConnection();

        $sqlStatement = $db->prepare("SELECT deviceName from tblDevices where deviceIdentifier = ?");
        $sqlStatement->bind_param("s", $deviceId);
        $sqlStatement->bind_result($deviceName);
        $sqlStatement->execute();
        $sqlStatement->fetch();
        return $deviceName;
    }

    /*
     * Notifications
     */

    public function getLastEventId() {
        $db = $this->getDbConnection();

        $sqlStatement = $db->prepare("SELECT eventId FROM tblEvents ORDER BY eventId DESC LIMIT 1");

        // Check if we have an issue executing
        if ($sqlStatement->execute() === FALSE) {
            $this->lastError = $sqlStatement->error;
            return false;
        }

        $sqlStatement->bind_result($lastEventId);
        $sqlStatement->fetch();

        return $lastEventId;
    }

    /*
     * Device
     */

    function getDeviceConfig($deviceIdentifier) {

        $db = $this->getDbConnection();

        $sqlStatement = $db->prepare("SELECT count(*) FROM tblDevices WHERE deviceIdentifier = ?");
        $sqlStatement->bind_param("s", $deviceIdentifier);
        $sqlStatement->bind_result($deviceExist);

        // Check if we have an issue executing
        if ($sqlStatement->execute() === FALSE) {
            $this->lastError = $sqlStatement->error;
            return false;
        }

        $sqlStatement->fetch();
        $sqlStatement = null;

        // If the device does not exist, then create one
        if ($deviceExist != 1) {
            $createStatement = $db->prepare("INSERT INTO tblDevices (deviceIdentifier, deviceName) VALUES (?, ?)");
            $defaultName = "New Eye (Please Set Name)";
            $createStatement->bind_param("ss", $deviceIdentifier, $defaultName);
            $createStatement->execute();
        }

        // Retrieve the configuration
        $sqlStatement = $db->prepare("SELECT deviceIdentifier, ipAddress, deviceEnabled, lastContact, deviceName, preMotionCapture, postMotionCapture, captureResolution, motionDetectionMagnitudeThreshold, motionDetectionVectorThreshold, orientation FROM tblDevices WHERE deviceIdentifier = ?");
        $sqlStatement->bind_param("s", $deviceIdentifier);
        $sqlStatement->bind_result($deviceIdentifier, $ipAddress, $deviceEnabled, $lastContact, $deviceName,
            $preMotionCapture, $postMotionCapture, $captureResolution, $motionDetectionMagnitudeThreshold,
            $motionDetectionVectorThreshold, $orientation);

        // Check if we have an issue executing
        if ($sqlStatement->execute() === FALSE) {
            $this->lastError = $sqlStatement->error;
            return false;
        }

        $sqlStatement->fetch();

        $deviceConfig = new deviceConfig($deviceIdentifier, $ipAddress, $deviceEnabled, $lastContact, $deviceName,
            $preMotionCapture, $postMotionCapture, $captureResolution, $motionDetectionMagnitudeThreshold,
            $motionDetectionVectorThreshold, $orientation);

        return $deviceConfig;

    }

    function enableAllDevices() {
      $this->enable("Yes");
    }
    function disableAllDevices() {
      $this->enable("No");
    }

    function enable($enable) {
        $db = $this->getDbConnection();
        $updateStatement = $db->prepare("UPDATE tblDevices SET deviceEnabled = ?");
	$updateStatement->bind_param("s", $enable);
	$updateStatement->execute();
    }

    function updateDeviceConfig($deviceIdentifier, $configKey, $configValue) {
        $db = $this->getDbConnection();

        // Set the value type of our value based on the type we can infer
        $valueType = "s";
        if (is_int($configValue) && $configKey != "captureResolution") { // FIX BUG: number will select enum in captureResolution and not value
            $valueType = "i";
        }

        /*
         * Column names and table names cannot be parameterized. We can use our deviceConfig object to see if it
         * has a property that matches the configkey
         */
        if (!property_exists("deviceConfig", $configKey)) {
            die("Attempting to update a config value that does not exist");
        }

        $updateStatement = $db->prepare("UPDATE tblDevices SET $configKey = ? WHERE deviceIdentifier = ?");

        $bindTypes = $valueType . "s";

        $updateStatement->bind_param($bindTypes, $configValue, $deviceIdentifier);

        if ($updateStatement->execute() === FALSE) {
            $this->lastError = $updateStatement->error;
            return false;
        }
        return true;
    }
    function getDevices() {
        $devices = array();
        $db = $this->getDbConnection();

        // Retrieve the configuration
        $sqlStatement = $db->prepare("SELECT deviceIdentifier, ipAddress, deviceEnabled, lastContact, deviceName, preMotionCapture, postMotionCapture, captureResolution, motionDetectionMagnitudeThreshold, motionDetectionVectorThreshold, orientation FROM tblDevices ");
        $sqlStatement->bind_result($deviceIdentifier, $ipAddress, $deviceEnabled, $lastContact, $deviceName,
            $preMotionCapture, $postMotionCapture, $captureResolution, $motionDetectionMagnitudeThreshold,
            $motionDetectionVectorThreshold, $orientation);

        // Check if we have an issue executing
        if ($sqlStatement->execute() === FALSE) {
            $this->lastError = $sqlStatement->error;
            return false;
        }

        while($sqlStatement->fetch()) {
            $deviceConfig = new deviceConfig($deviceIdentifier, $ipAddress, $deviceEnabled, $lastContact, $deviceName,
                $preMotionCapture, $postMotionCapture, $captureResolution, $motionDetectionMagnitudeThreshold,
                $motionDetectionVectorThreshold, $orientation);

            array_push($devices, $deviceConfig);
        }

        return $devices;
    }
}
