//
//  ServiceClient+DeviceServices.swift
//  Tready
//
//  Created by Brian Krupp on 5/12/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class DeviceModel : Model {
    public typealias GetDevicesResponseCompletionHandler = (Array<Device>) -> Void
    public typealias UpdateDeviceResponseCompletionHandler = () -> Void
    public typealias SetAllDevicesResponseCompletionHandler = () -> Void

    func getDevices(completionHandler:@escaping GetDevicesResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        self.serviceClient.callPostService(serviceBroker: "device", serviceName: "getDevices", serviceResponse: GetDevicesResponse.self, completionHandler: { (decodableResponse) in
            
            if let response = decodableResponse as? GetDevicesResponse {
                completionHandler(response.responseData)
            }
            else {
                errorHandler(super.createModelError(model: String(describing: self)))
            }
            
        }) { (error) in
            errorHandler(error)
        }
    }
    
    func updateDevice(updateDevice:Device, completionHandler:@escaping UpdateDeviceResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        let configKeys = ConfigKeyValuePair(updatedDevice: updateDevice)
        let serviceRequest = UpdateDeviceRequest(deviceIdentifier: updateDevice.deviceIdentifier, configKeyValuePairToUpdate: configKeys)
        
        self.serviceClient.callPostService(serviceBroker: "device", serviceName: "setConfig", serviceRequest: serviceRequest, serviceResponse: Response.self, completionHandler: { (decodableResponse) in
            
            if decodableResponse is Response {
                completionHandler()
            }
            else {
                errorHandler(super.createModelError(model: String(describing: self)))
            }
            
        }) { (error) in
            errorHandler(error)
        }
    }
    
    
    
    func setAllDevices(enable:Bool, completionHandler:@escaping SetAllDevicesResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        var serviceName = ""
        
        if (enable) {
            serviceName = "enableAllDevices"
        }
        else {
            serviceName = "disableAllDevices"
        }

        self.serviceClient.callPostService(serviceBroker: "device", serviceName: serviceName, serviceResponse: Response.self, completionHandler: { (decodableResponse) in
            completionHandler()
        }) { (error) in
            errorHandler(error)
        }
    }
    
}

