//
//  UpdateDeviceRequest.swift
//  Tready
//
//  Created by Brian Krupp on 5/18/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation
class UpdateDeviceRequest: Encodable {
    let deviceIdentifier:String
    let configKeyValuePairToUpdate:ConfigKeyValuePair

    init(deviceIdentifier:String, configKeyValuePairToUpdate:ConfigKeyValuePair) {
        self.deviceIdentifier = deviceIdentifier
        self.configKeyValuePairToUpdate = configKeyValuePairToUpdate
    }
}

class ConfigKeyValuePair: Encodable {
    let deviceEnabled:String
    let motionDetectionMagnitudeThreshold:Int
    let motionDetectionVectorThreshold:Int
    let deviceName:String
    let orientation:String
    let preMotionCapture:Int
    let postMotionCapture:Int
    let captureResolution:Int
    
    init(updatedDevice:Device) {
        if updatedDevice.isDeviceEnabled(){
            self.deviceEnabled = "Yes"
        }
        else {
            self.deviceEnabled = "No"
        }
        
        self.motionDetectionVectorThreshold = updatedDevice.motionDetectionVectorThreshold
        self.motionDetectionMagnitudeThreshold  = updatedDevice.motionDetectionMagnitudeThreshold
        self.deviceName = updatedDevice.deviceName
        self.orientation = updatedDevice.orientation
        self.captureResolution = Int(updatedDevice.captureResolution)!
        self.preMotionCapture = updatedDevice.preMotionCapture
        self.postMotionCapture = updatedDevice.postMotionCapture
    }
}
