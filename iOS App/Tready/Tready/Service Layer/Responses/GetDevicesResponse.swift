//
//  GetDevicesResponse.swift
//  Tready
//
//  Created by Brian Krupp on 5/12/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class GetDevicesResponse : Response {
    public var responseData:Array<Device>

    enum CodingKeys: String, CodingKey {
        case responseData
    }
    
    required init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        self.responseData = try values.decode(Array<Device>.self, forKey: .responseData)
        try super.init(from: decoder)
    }
    
}
