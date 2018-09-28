<?php
// Pull in cross cutting libraries
require_once("lib/session.php");
require_once("model/fileSystemManager.php");
require_once("broker/eventBroker.php");
require_once("broker/securityBroker.php");
require_once("broker/deviceBroker.php");
require_once("types/response.php");

$session = new session();
$session->startSession();


// Request object should come to this gateway in the form of the following:
/*
 * request={
 *  servicePath: "/security/authenticate"
 *  serviceRequest: ... json object ...
 * }
 */

if ($_GET["eventId"]) {
    // Check if session is valid first before getting event
    $eventId = intval($_GET["eventId"]);
    if (!$session->isSessionValid() && $serviceName != "authenticate") {
        return new response(-1, "Please authenticate first");
    }
    else if (! is_int($eventId)) {
        return new response(-2, "Invalid eventId");
    }

    $fs = new fileSystemManager();
    if ($_GET["thumbnail"] == "true") {
        $fileContents = $fs->getThumbnail($eventId);
        header('Content-Type: image/jpeg');
        die($fileContents);
    } 

    return;
}

try {

    if (! isset($_POST["servicePath"])) {
        $response["ping"] = time();
        echo json_encode($response);
        exit;

    }
    // Decode request
    $servicePath = $_POST["servicePath"];

    // Process the service that is being requested
    $serviceParts = explode("/", $servicePath);

    // Store the parts into meaningful variable names
    $brokerName = $serviceParts[1] . "Broker"; // Append Broker to end for appropriate Class
    $serviceName = $serviceParts[2];

    $serviceRequest = json_decode($_POST["serviceRequest"]);

    /*
     * Step 3 - Dynamically invoke broker and service based on service path. Pass serviceRequest object
     */

    $broker = new $brokerName; // Creates new broker object

    // Below are a list of services that we will check for authentication within the service
    $bypassAuthCheckServices = [
        "/event/createEvent", // Device passes creds
        "/security/authenticate", // No session yet
        "/device/getConfig", // Device passes creds
	"/security/getHash"
    ];

    $shouldValidateSession = true;

    foreach ($bypassAuthCheckServices as $bypassAuthCheckService) {
        if ($bypassAuthCheckService == $servicePath) $shouldValidateSession = false;
    }

    // Session should be valid when calling service that doesn't require session to exist
    if ($shouldValidateSession && !$session->isSessionValid()) {
        $serviceResponse = new response(-1, "Please authenticate first");
    } else {
        $serviceResponse = $broker->$serviceName($serviceRequest);
    }

    // Send back response encoded in JSON
    echo json_encode($serviceResponse);
} catch (Exception $e) {
    $response = new response("1", $e->getMessage());
    echo json_encode($response);
}
