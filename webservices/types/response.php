<?php

require_once("lib/session.php");

class response {
  public $reauthRequest;
  public $responseCode;
  public $responseData;

  function __construct($responseCode, $responseData) {
    $session  = new session();
    // Set the reauthrequest to whether or not the session is valid
    $this->reauthRequest = ! $session->isSessionValid();
    $this->responseCode = $responseCode;
    $this->responseData = $responseData;
  }
}