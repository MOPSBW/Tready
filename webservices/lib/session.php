<?php

/**
 * Created by PhpStorm.
 * User: briankrupp
 * Date: 2/28/17
 * Time: 10:54 AM
 */
class session {
  public function startSession() {
    session_start();
  }

  public function setSessionUserId($userId) {
    $_SESSION["userId"] = $userId;
  }
  public function getSessionUserId() {
    return $_SESSION["userId"];
  }
  public function isSessionValid() {
    if (isset($_SESSION["userId"]) && $_SESSION["userId"] >= 0) {
      return true;
    }
    return false;
  }
  public function destroySession() {
    $_SESSION["userId"] = null;
    session_unset();
    session_destroy();
  }
}
