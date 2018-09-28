//
//  Device.swift
//  Tready
//
//  Created by Brian Krupp on 5/12/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class Device: Codable {
    public var deviceIdentifier:String!
    public var ipAddress:String!
    private var deviceEnabled:String!
    public var lastContact:Double!
    public var deviceName:String!
    public var preMotionCapture:Int!
    public var postMotionCapture:Int!
    public var captureResolution:String!
    public var motionDetectionMagnitudeThreshold:Int!
    public var motionDetectionVectorThreshold:Int!
    public var orientation:String!
    
    static let orientationDisplay = ["Portrait 0°", "Portrait Upside Down 180°", "Landscape 90°", "Landscape Upside Down 270°"]
    static let orientationValues = ["portrait", "portraitUpsideDown", "landscapePowerUp", "landscapePowerDown"]
    
    init(deviceIdentifier:String, ipAddress:String, deviceEnabled:String, lastContact:Double, deviceName:String, preMotionCapture:Int, postMotionCapture:Int, captureResolution:String, motionDetectionMagnitudeThreshold:Int, motionDetectionVectorThreshold:Int, orientation:String) {
        self.deviceIdentifier = deviceIdentifier
        self.ipAddress = ipAddress
        self.deviceEnabled = deviceEnabled
        self.lastContact = lastContact
        self.deviceName = deviceName
        self.preMotionCapture = preMotionCapture
        self.postMotionCapture = postMotionCapture
        self.captureResolution = captureResolution
        self.motionDetectionMagnitudeThreshold = motionDetectionMagnitudeThreshold
        self.motionDetectionVectorThreshold = motionDetectionVectorThreshold
        self.orientation = orientation
    }
    
    public func getLastContactDate() -> String {
        let date = Date(timeIntervalSince1970: self.lastContact)
        let formatter = DateFormatter()
        formatter.timeStyle = .medium
        formatter.dateStyle = .medium
        return formatter.string(from: date)
    }
    
    public func getOrientationDegress() -> String {
        let index = Device.orientationValues.index(of: self.orientation)
        
        return Device.orientationDisplay[index!]
    }
    
    public func isDeviceEnabled() -> Bool {
        if (self.deviceEnabled == "Yes") {
            return true
        }
        return false
    }
    
    public func setDeviceEnabled(enabled:Bool) {
        if (enabled) {
            self.deviceEnabled = "Yes"
        }
        else {
            self.deviceEnabled = "No"
        }
    }
}
