<?php

require_once("model/db.php");
require_once("lib/session.php");
require_once("types/response.php");

/**
 * Pattern here is to create a response object on each repsonse
 */
class securityBroker
{
  /*
   * Request:
   *  username  : string
   *  password  : string
   *
   * Response:
   *  sessionId : string or - 1 failure
   *
   */
  public function authenticate($request) {
    $db = new db();
    $userId = $db->authenticateUser($request->username, $request->password);
    $response["userId"] = $userId;

    // Create default message
    $rc = 0;

    // Check for password
    if ($userId >= 0) {
      $session = new session();
      $session->setSessionUserId($userId);
    }
    else {
      $rc = 1;
    }

    return new response($rc, $response);
  }

  public function verify() {
    $session = new session();
    $userId = $session->getSessionUserId();
    ($userId >= 0) ? $rc = 1 : $rc = 0;
    $response["userId"] = $userId;
    return new response($rc, $response);
  }

  public function changeAccountInfo($request) {
    $db = new db();
    $session = new session();

    if ($db->changeAccountInfo($session->getSessionUserId(), $request->username, $request->password)) {
      $rc = 0;
    }
    else {
      $rc = 1;
    }

    return new response($rc, null);
  }

  public function getHash($request) {
    $db = new db();
    $hashResult =  $db->getHash($request->toHash);
    return new response(0, $hashResult);
  }

  public function logoff($request) {
    $session = new session();
    $session->destroySession();
  }
}
