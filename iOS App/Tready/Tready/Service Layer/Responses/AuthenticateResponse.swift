//
//  AuthenticateResponse.swift
//  Tready
//
//  Created by Brian Krupp on 5/1/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class AuthenticateResponse : Response {
    public let responseData:UserId

    enum CodingKeys: String, CodingKey {
        case responseData
    }
    
    required init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        self.responseData = try values.decode(UserId.self, forKey: .responseData)
        try super.init(from: decoder)
    }
}
