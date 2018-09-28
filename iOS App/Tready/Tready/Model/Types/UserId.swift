//
//  UserId.swift
//  Tready
//
//  Created by Brian Krupp on 5/1/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class UserId: Codable {
    public let userId:Int

    init(userId:Int) {
        self.userId = userId
    }
}
