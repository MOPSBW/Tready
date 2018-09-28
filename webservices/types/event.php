<?php

class event {
  public $eventId;
  public $nameOfDevice;
  public $timeStarted;
  public $timeEnded;


  function __construct($eventId, $nameOfDevice, $timeStarted, $timeEnded) {
    $this->eventId = $eventId;
    $this->nameOfDevice = $nameOfDevice;
    $this->timeStarted = $timeStarted;
    $this->timeEnded = $timeEnded;
  }
}