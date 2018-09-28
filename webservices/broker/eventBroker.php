<?php

require_once("model/db.php");
require_once("model/fileSystemManager.php");
require_once("lib/session.php");
require_once("types/response.php");
require_once("broker/securityBroker.php");

/*
 * General pattern
 *  Get data from DB
 *  Check if result was returned
 *  Build response object and return it
 */

class eventBroker
{
    public function getLastEventId($request) {
        $db = new db();
        $lastEventId = $db->getLastEventId();

        return new response(0, $lastEventId);
    }

    function createEvent($request) {
        /*
         * Pi sends up credentials each time, need to authenticate first
         */
        $securityBroker = new securityBroker();
        $session = new session();

        $securityBroker->authenticate($request);
        if (!$session->isSessionValid()) {
            return new response(-1, "Cannot create event, credentials first");
        }

        // Update device time
        $deviceBroker = new deviceBroker();
        $deviceBroker->updateDeviceContact($request->deviceIdentifier);

        $db = new db();

        $eventId = $db->createEvent($request->deviceIdentifier, $request->timeStarted, $request->timeEnded);
        $fs = new fileSystemManager();
        $fs->createVideo($eventId, base64_decode($request->videoData));
	if (strlen($request->thumbnailData) != 0) {
          $fs->storeThumbnail($eventId, base64_decode($request->thumbnailData));
	}
	else {
          $fs->createThumbnail($eventId);
	}

        return new response(0, $eventId);
    }

    public function getEvents($request) {
        $db = new db();
        $events = $db->getEvents();
        ($events !== false) ? $rc = 0 : $rc = 1;

        return new response($rc, $events);
    }

    public function getEvent($request) {
        $db = new db();
        $event = $db->getEvent($request->eventId);

        ($event !== false) ? $rc = 0 : $rc = 1;

        return new response($rc, $event);
    }

    public function deleteEvent($request) {
        $db = new db();
        $fs = new fileSystemManager();

        $success = true;

        $success = $success && $db->deleteEvent($request->eventId);
        $success = $success && $fs->deleteFile($request->eventId);

        ($success) ? $rc = 0 : $rc = 1;
        
        return new response($rc, $success);
    }

    public function deleteEvents($request) {
        $failedEvents = array();
        foreach ($request->eventIds as $eventId) {
            $deleteEventRequest = new stdClass();
            $deleteEventRequest->eventId = $eventId;
            $response = $this->deleteEvent($deleteEventRequest);

            if ($response->rc != 0) {
                array_push($failedEvents, $eventId);
            }
        }
        return new response(sizeof($failedEvents), $failedEvents);
    }

    public function deleteAllEvents($request) {
        $db = new db();
        $db->deleteAllEvents();
        $fs = new fileSystemManager();
        $fs->deleteAllFiles();

        return new response(0, null);
    }
}
