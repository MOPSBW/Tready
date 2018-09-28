//
//  AppDelegate.swift
//  Tready
//
//  Created by Brian Krupp on 1/26/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import UIKit
import UserNotifications
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        UIApplication.shared.setMinimumBackgroundFetchInterval(UIApplicationBackgroundFetchIntervalMinimum)
        UNUserNotificationCenter.current().delegate = self
        return true
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
        self.initializeIfEventPresent()
    }
    
    func initializeIfEventPresent() {
        if let eventVc = self.window?.rootViewController as? EventTableViewController {
            eventVc.loadData()
        }
        
        
        DispatchQueue.main.async {
            UIApplication.shared.applicationIconBadgeNumber = 0
        }
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        self.initializeIfEventPresent()
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    func application(_ application: UIApplication, performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        let eventModel = EventModel(serviceClient: .getSharedInstance())
        
        eventModel.getNewEvents(completionHandler: { (events) in
            
            guard events.count > 0 else {
                completionHandler(.noData)
                return
            }
            
            // Build a description of events
            var deviceList = ""
            var plural = ""
            for event in events {
                if (!deviceList.contains(event.nameOfDevice)) {
                    if (deviceList.count > 1 ) {
                        plural = "s"
                    }
                    deviceList = deviceList + event.nameOfDevice + " "
                }
            }
            
            // Setup badge count
            DispatchQueue.main.async {
                UIApplication.shared.applicationIconBadgeNumber = events.count
            }
        
            // Setup Notificaiton
            let content = UNMutableNotificationContent()
            content.title = String(events.count) + " Event" + plural + " Occurred"
            content.body = "Event" + plural + " occurred from the following device" + plural + ": " + deviceList
            
            // Create the request
            let uuidString = UUID().uuidString
            let request = UNNotificationRequest(identifier: uuidString,
                                                content: content, trigger: nil) // Nil delivers right away
            
            // Schedule the request with the system.
            let notificationCenter = UNUserNotificationCenter.current()
            notificationCenter.add(request) { (error) in
                if error != nil {
                    completionHandler(.failed)
                }
                else {
                    completionHandler(.newData)
                }
            }
            
        }) { (error) in
            completionHandler(.failed)
        }
        
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        // Unneeded
    }
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        // Unneeded
    }
    
    
}

