//
//  EventTableViewController.swift
//  Tready
//
//  Created by Brian Krupp on 5/11/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import UIKit
import AVKit
import UserNotifications
import Photos

enum ArmStatus {
    case Disarmed
    case Armed
    case Mixed
}

class EventTableViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, AVAssetResourceLoaderDelegate, UIGestureRecognizerDelegate, URLSessionDownloadDelegate {
    @IBOutlet weak var lockButton: UIBarButtonItem!
    @IBOutlet weak var eventTableView: UITableView!
    
    var armStatus:ArmStatus = ArmStatus.Disarmed
    var events:Array<Event> = []
    var devices:Array<Device> = []
    
    var eventModel:EventModel!
    var deviceModel:DeviceModel!
    
    var lastSelectedIndex:IndexPath?
    
    var thumbnailCache = NSCache<Event, UIImage>()
    
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {

    }

    
    @IBAction func trashPressed(_ sender: Any) {
        let alertController = UIAlertController(title: "Delete All", message: "Do you want to remove all events?", preferredStyle: .actionSheet)
        alertController.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: { (action) in
            //self.presentingViewController?.dismiss(animated: true, completion: nil)
        }))
        alertController.addAction(UIAlertAction(title: "Delete All", style: .destructive, handler: { (action) in
            self.eventModel.deleteAllEvents(completionHandler: {
                self.loadData()
            }, errorHandler: { (error) in
                let errorAlert = ErrorAlert(message: "Unable to delete all events: " + error.localizedDescription, error: nil)
                errorAlert.show(fromController: self)
            })
            
        }))
        self.present(alertController, animated: true, completion: nil)
    }
    @IBAction func lockButtonPressed(_ sender: Any) {
        var shouldEnable = false;
        var message = "Disarming Devices"
        if (armStatus == ArmStatus.Disarmed || armStatus == ArmStatus.Mixed) {
            shouldEnable = true;
            message = "Arming Devices"
        }
        
        let alertController = UIAlertController(title: "Updating", message: message, preferredStyle: .alert)
        
        DispatchQueue.main.async {
            self.present(alertController, animated: true, completion: nil)
        }
        
        self.deviceModel.setAllDevices(enable: shouldEnable, completionHandler: {
            DispatchQueue.main.async {
                self.dismiss(animated: true, completion: {
                    self.loadData()
                })
            }
        }) { (error) in
            DispatchQueue.main.async {
                self.dismiss(animated: true, completion: {
                    let errorAlert = ErrorAlert(message: "Unable to set devices: " + error.localizedDescription, error: nil)
                    errorAlert.show(fromController: self)
                    self.eventTableView.refreshControl?.endRefreshing();
                })
            }
        }
    }
    
    
    @objc func refresh(_ refreshControl: UIRefreshControl) {
        self.loadData()
        
    }
    
    
    override func viewDidAppear(_ animated: Bool) {
        let notificationCenter = UNUserNotificationCenter.current()
        notificationCenter.requestAuthorization(options: [.alert, .badge, .sound, .carPlay]) { (status, error) in
        }
        

    }
    
    override func viewWillAppear(_ animated: Bool) {
        DispatchQueue.main.async {
            self.updateDeviceIcon()
            self.eventTableView.reloadData()
            if let lastSelectedIndex = self.lastSelectedIndex {
                self.eventTableView.scrollToRow(at: lastSelectedIndex, at: .top, animated: false)
            }
        }
    }   
    
    @IBAction func handleLongPress(recognizer:UILongPressGestureRecognizer) {
        
        if recognizer.state == UIGestureRecognizerState.began {
            // Retreive the event from the long press
            let point = recognizer.location(in: self.eventTableView!)
            let index = self.eventTableView.indexPathForRow(at: point)
            let event:Event = self.events[index!.row]
            
            var alertController = UIAlertController(title: "Save Video?", message: "Do you want to save this video?", preferredStyle: .alert)
            alertController.addAction(UIAlertAction(title: "Yes", style: .default, handler: { (action) in
                alertController.dismiss(animated: true, completion: nil)
                alertController = UIAlertController(title: "Saving", message: "Downloading video ...", preferredStyle: .alert)
                DispatchQueue.main.async {
                    self.present(alertController, animated: true, completion: nil)
                }
                
                self.eventModel.downloadVideo(event: event, delegate: self) { (url) in
                    alertController.dismiss(animated: true, completion: nil)
                    
                    // Move the temp file to save into our photos
                    let documentDirectory = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
                    
                    
                    var destinationUrl = URL(fileURLWithPath: documentDirectory)
                    destinationUrl.appendPathComponent("video.mp4")
                    
                    do {
                        do {
                            try FileManager.default.removeItem(at: destinationUrl)
                        }
                        catch  {
                            // No worries if file is gone
                        }
                        
                        try FileManager.default.moveItem(at: url, to: destinationUrl)
                        
                        PHPhotoLibrary.requestAuthorization({ (status) in
                            PHPhotoLibrary.shared().performChanges({
                                PHAssetChangeRequest.creationRequestForAssetFromVideo(atFileURL: destinationUrl)
                            }) { saved, error in
                                if saved {
                                    let alertController = UIAlertController(title: "Success", message: "Your video was successfully saved.", preferredStyle: .alert)
                                    let defaultAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                                    alertController.addAction(defaultAction)
                                    DispatchQueue.main.async {
                                        self.present(alertController, animated: true, completion: nil)
                                    }
                                    // Cleanup
                                    do {
                                        try FileManager.default.removeItem(at: destinationUrl)
                                    }
                                    catch  {
                                        // No worries if file is gone
                                    }
                                }
                            }
                        })
                    }
                    catch let error {
                        let alertController = UIAlertController(title: "Error", message: "Unable to save the video: " + error.localizedDescription, preferredStyle: .alert)
                        let defaultAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                        alertController.addAction(defaultAction)
                        DispatchQueue.main.async {
                            self.present(alertController, animated: true, completion: nil)
                        }
                    }
                    
                    
                    
                }
            }))
            alertController.addAction(UIAlertAction(title:"No", style: .cancel , handler: { (action) in
                alertController.dismiss(animated: true, completion: nil)
            }))
            DispatchQueue.main.async {
                self.present(alertController, animated: true, completion: nil)
            }
            
        }
    }
    
    func updateDeviceIcon() {
        self.deviceModel.getDevices(completionHandler: { (devices) in
            self.devices = devices
            
            var enabledCount:Int = 0
            var disabledCount:Int = 0
            for device in devices {
                if (device.isDeviceEnabled()) {
                    enabledCount+=1
                }
                else {
                    disabledCount+=1
                }
            }
            
            DispatchQueue.main.async {
                if (enabledCount == self.devices.count) {
                    // All are enabled, show lock
                    self.lockButton.image = UIImage(named: "lock.png")
                    self.armStatus = ArmStatus.Armed
                }
                else if (disabledCount == self.devices.count) {
                    // All are disabled, show unlock
                    self.lockButton.image = UIImage(named: "unlock.png")
                    self.armStatus = ArmStatus.Disarmed
                }
                else {
                    self.lockButton.image = UIImage(named: "middlelock.png")
                    self.armStatus = ArmStatus.Mixed
                }
            }
        }) { (error) in
            
        }
    }
    
    func loadData() {
        
        self.eventModel.getEvents(completionHandler: { (events) in
            self.events = events
            
            // Set the last event ID that wast retrieved
            if (self.events.count > 0) {
                let event = events[0]
                EventModel.lastEventId = event.eventId
            }
            
            DispatchQueue.main.async {
                self.eventTableView.reloadData()
                self.eventTableView.refreshControl?.endRefreshing()
            }
        }) { (error) in
            let errorAlert = ErrorAlert(message: "Unable to get events: " + error.localizedDescription, error: nil)
            errorAlert.show(fromController: self)
            DispatchQueue.main.async {
                self.eventTableView.refreshControl?.endRefreshing();
            }
        }
        
        updateDeviceIcon()
        
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.eventModel = EventModel(serviceClient: .getSharedInstance())
        self.deviceModel = DeviceModel(serviceClient: .getSharedInstance())
        
        self.loadData()
        
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refresh(_:)), for: .valueChanged)
        eventTableView.refreshControl = refreshControl
        
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return events.count
    }

    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:EventCell = tableView.dequeueReusableCell(withIdentifier: "EventCell", for: indexPath) as! EventCell
    
        let event:Event = events[indexPath.row]
        
        // Configure the cell...
        cell.roomLabel.text = event.nameOfDevice
        let date = Date(timeIntervalSince1970: event.timeStarted)
        let formatter = DateFormatter()
        formatter.timeStyle = .medium
        formatter.dateStyle = .medium
        let localDate = formatter.string(from: date)
        cell.timeLabel.text = localDate
        
        if let cacheImage = self.thumbnailCache.object(forKey: event) {
            cell.thumbnail.image = cacheImage
        }
        else {
            eventModel.getImageThumbnail(eventId: event.eventId, completionHandler: { (data) in
                if let image = UIImage(data: data) {
                    self.thumbnailCache.setObject(image, forKey: event)
                    DispatchQueue.main.async {
                        cell.thumbnail.image = image
                    }
                }
                else if let image = UIImage(named: "Unavailable.png") {
                    DispatchQueue.main.async {
                        cell.thumbnail.image = image
                    }
                }
            }) { (error) in
                let errorAlert = ErrorAlert(message: "Unable to set load thumbnail: " + error.localizedDescription, error: nil)
                errorAlert.show(fromController: self)
            }
        }
        
        return cell
    }
    
    // Override to support conditional editing of the table view.
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    
    
    // Override to support editing the table view.
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            
            self.eventModel.deleteEvent(eventId: self.events[indexPath.row].eventId, completionHandler: {
            }) { (error) in
                let errorAlert = ErrorAlert(message: "Unable to delete event: " + error.localizedDescription, error: nil)
                errorAlert.show(fromController: self)
                self.loadData()
            }
            self.events.remove(at: indexPath.row)
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }
    }
    
    
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.lastSelectedIndex = indexPath
        let videoURL = self.eventModel.getVideoUrl(eventId: self.events[indexPath.row].eventId)
        
        // Setup using the cookies we have so that we can pass up the session ID
        let cookies = HTTPCookieStorage.shared.cookies
        
        
        let asset = AVURLAsset(url: videoURL, options: [AVURLAssetHTTPCookiesKey : cookies!])
        asset.resourceLoader.setDelegate(self, queue: DispatchQueue.init(label: "Video"))
        
        let playerItem = AVPlayerItem(asset: asset)
        let player = AVPlayer(playerItem: playerItem)
        
        let playerViewController = AVPlayerViewController()
        playerViewController.player = player
        
        self.present(playerViewController, animated: true) {
            playerViewController.player!.play()
        }
    }
    
    // Allows htacess to be used
    func resourceLoader(_ resourceLoader: AVAssetResourceLoader, shouldWaitForResponseTo authenticationChallenge: URLAuthenticationChallenge) -> Bool {
        authenticationChallenge.sender?.use(self.eventModel.serviceClient.credential, for: authenticationChallenge)
        return false
    }
}
