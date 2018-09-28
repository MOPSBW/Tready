//
//  DeleteEventRequest.swift
//  Tready
//
//  Created by Brian Krupp on 5/17/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class DeleteEventRequest: Encodable {
    let eventId:Int
    
    init(eventId:Int) {
        self.eventId = eventId
    }
}
