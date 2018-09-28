//
//  ServiceClient+Events.swift
//  Tready
//
//  Created by Brian Krupp on 4/9/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class EventModel : Model  {
    public typealias GetEventsResponseCompletionHandler = (Array<Event>) -> Void
    public typealias DeleteEventResponseCompletionHandler = () -> Void
    public typealias GetImageThumbnailCompletionHandler = (Data) -> Void
    public typealias DownloadVideoHandler = (URL) -> Void
    
    public static var lastEventId:Int = -1
    
    private func updateLastEventId(events:Array<Event>) {
        if events.count > 0 {
            EventModel.lastEventId = events[0].eventId
        }
    }
    
    func downloadVideo(event:Event, delegate:URLSessionDownloadDelegate, completionHandler:@escaping DownloadVideoHandler) {
        self.serviceClient.download(url: self.getVideoUrl(eventId: event.eventId), delegate: delegate) { (data) in
            completionHandler(data)
        }
    }
    
    func getNewEvents(completionHandler:@escaping GetEventsResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        
        // Store copy of last event ID before retrieving (don't depend on timing)
        let lastEventId = EventModel.lastEventId
        
        // Grab events and then grab the new ones only
        self.getEvents(completionHandler: { (events) in
            var newEvents = Array<Event>()
            for event in events {
                if (event.eventId > lastEventId) {
                    newEvents.append(event)
                }
            }
        
            completionHandler(newEvents)
        }) { (error) in
            errorHandler(error)
        }
    }
    
    func getEvents(completionHandler:@escaping GetEventsResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        
        self.serviceClient.callPostService(serviceBroker: "event", serviceName: "getEvents", serviceResponse: GetEventsResponse.self, completionHandler: { (decodableResponse) in
            
            if let response = decodableResponse as? GetEventsResponse {
                self.updateLastEventId(events: response.responseData)
                completionHandler(response.responseData)
            }
            else {
                errorHandler(super.createModelError(model: String(describing: self)))
            }
            
        }) { (error) in
            errorHandler(error)
        }
        
    }
    
    func deleteAllEvents(completionHandler:@escaping DeleteEventResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        self.serviceClient.callPostService(serviceBroker: "event", serviceName: "deleteAllEvents", serviceResponse: Response.self, completionHandler: { (decodableResponse) in
            completionHandler()
        }) { (error) in
            errorHandler(error)
        }
    }
    
    func deleteEvent(eventId:Int, completionHandler:@escaping DeleteEventResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        let serviceRequest = DeleteEventRequest(eventId: eventId)
        self.serviceClient.callPostService(serviceBroker: "event", serviceName: "deleteEvent", serviceRequest: serviceRequest, serviceResponse: Response.self, completionHandler: { (decodableResponse) in
            completionHandler()
        }) { (error) in
            errorHandler(error)
        }
  
    }
    func getImageThumbnail(eventId:Int, completionHandler:@escaping GetImageThumbnailCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        self.serviceClient.callGetService(getParamString: "eventId=" + String(eventId) + "&thumbnail=true", completionHandler: { (data) in
            completionHandler(data)
        }) { (error) in
            errorHandler(error)
        }
    }
    
    func getVideoUrl(eventId:Int) -> URL {
        var urlString = self.serviceClient.url.absoluteString.replacingOccurrences(of: "service.php", with: "eventImages/")
        urlString.append(String(eventId) + ".mp4")
        return URL(string: urlString)!
    }
}
