//
//  Response.swift
//  Tready
//
//  Created by Brian Krupp on 5/12/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class Response: Decodable {
    public var reauthRequest:Bool
    public var responseCode:Int

    public init(reauthRequest:Bool, responseCode: Int, responseData:Codable) {
        self.reauthRequest = reauthRequest
        self.responseCode = responseCode
    }
}
