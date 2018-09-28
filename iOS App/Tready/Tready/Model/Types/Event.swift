//
//  Event.swift
//  Tready
//
//  Created by Brian Krupp on 4/9/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class Event: Codable {
    public let eventId:Int
    public let nameOfDevice:String
    public let timeStarted:Double
    public let timeEnded:Double
    
    init(eventId:Int, nameOfDevice:String, timeStarted:Double, timeEnded:Double) {
        self.eventId = eventId
        self.nameOfDevice = nameOfDevice
        self.timeStarted = timeStarted
        self.timeEnded = timeEnded
    }
}
