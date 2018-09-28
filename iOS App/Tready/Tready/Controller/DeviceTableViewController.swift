//
//  DeviceTableViewController.swift
//  Tready
//
//  Created by Brian Krupp on 5/17/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import UIKit

class DeviceTableViewController: UITableViewController, DeviceCellDelegate, DeviceUpdateDelegate {
    
    
    let TEN_MINUTES:Double = 60 * 10
    
    var deviceModel:DeviceModel!
    var devices = Array<Device>()
    
    
    override func viewWillAppear(_ animated: Bool) {
        loadData()
    }
    func loadData() {
        self.deviceModel.getDevices(completionHandler: { (devices) in
            self.devices = devices
            DispatchQueue.main.async {
                self.tableView.reloadData()
                self.refreshControl?.endRefreshing();
            }
            
        }) { (error) in
            DispatchQueue.main.async {
                self.refreshControl?.endRefreshing();
            }
            let errorAlert = ErrorAlert(message: "Unable to load devices: " + error.localizedDescription, error: nil)
            errorAlert.show(fromController: self)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.deviceModel = DeviceModel(serviceClient: .getSharedInstance())
        self.loadData()
        
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refresh(_:)), for: .valueChanged)
        self.refreshControl = refreshControl
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    @objc func refresh(_ refreshControl: UIRefreshControl) {
        self.loadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func deviceUpdated(device: Device) {
        self.deviceModel.updateDevice(updateDevice: device, completionHandler: {
            DispatchQueue.main.async {
                self.loadData()
            }
        }) { (error) in
            let errorAlert = ErrorAlert(message: "Unable to update device settings: " + error.localizedDescription, error: nil)
            DispatchQueue.main.async {
                errorAlert.show(fromController: self)
            }
        }
    }
    
    func enableButtonTapped(cellSelected: UITableViewCell, relatedDevice: Device) {
        var message = "Disarming Device"
        if (relatedDevice.isDeviceEnabled()) {
            // Disable Device
            relatedDevice.setDeviceEnabled(enabled: false)
        }
        else {
            // Enable Device
            relatedDevice.setDeviceEnabled(enabled: true)
            message = "Arming Device"
        }
        
        let alertController = UIAlertController(title: "Updating", message: message, preferredStyle: .alert)
        
        DispatchQueue.main.async {
            self.present(alertController, animated: true, completion: nil)
        }
        self.deviceModel.updateDevice(updateDevice: relatedDevice, completionHandler: {
            DispatchQueue.main.async {
                self.dismiss(animated: true, completion: {
                    self.loadData()
                })
            }
        }) { (error) in
            let errorAlert = ErrorAlert(message: "Unable to arm/disarm device: " + error.localizedDescription, error: nil)
            DispatchQueue.main.async {
                self.dismiss(animated: true, completion: {
                    errorAlert.show(fromController: self)
                })
            }
            
        }
        
        
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return self.devices.count
    }
    
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let device = self.devices[indexPath.row]
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "DeviceCell", for: indexPath) as! DeviceCell
        
        cell.relatedDevice = device
        
        cell.deviceName.text = device.deviceName
        cell.ipAddress.text = device.ipAddress
        
        // Check if the device has not contacted the hub recently
        if (Date().timeIntervalSince1970 - device.lastContact > TEN_MINUTES) {
            cell.lastContact.textColor = UIColor.red
        }
        else {
            cell.lastContact.textColor = UIColor(red: 0.2, green: 0.5, blue: 0.2, alpha: 1.0)
        }
        
        let date = Date(timeIntervalSince1970: device.lastContact)
        let formatter = DateFormatter()
        formatter.timeStyle = .medium
        formatter.dateStyle = .medium
        let localDate = formatter.string(from: date)
        cell.lastContact.text = localDate
        
        cell.orientation.text = device.getOrientationDegress()
        cell.preMotionCapture.text = String(device.preMotionCapture)
        cell.postMotionCapture.text = String(device.postMotionCapture)
        cell.resolution.text = device.captureResolution
        cell.vectorThreshold.text = String(device.motionDetectionVectorThreshold)
        cell.magnitudeThreshold.text = String(device.motionDetectionMagnitudeThreshold)
        
        DispatchQueue.main.async {
            if (device.isDeviceEnabled()) {
                cell.enableButton.imageView?.image = UIImage(named: "lock.png")
            }
            else {
                cell.enableButton.imageView?.image = UIImage(named: "unlock.png")
            }
        }
        
        cell.setDelegate(delegate: self)
        
        return cell
    }
    
    
    /*
     // Override to support conditional editing of the table view.
     override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
     // Return false if you do not want the specified item to be editable.
     return true
     }
     */
    
    /*
     // Override to support editing the table view.
     override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
     if editingStyle == .delete {
     // Delete the row from the data source
     tableView.deleteRows(at: [indexPath], with: .fade)
     } else if editingStyle == .insert {
     // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
     }
     }
     */
    
    /*
     // Override to support rearranging the table view.
     override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {
     
     }
     */
    
    /*
     // Override to support conditional rearranging of the table view.
     override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
     // Return false if you do not want the item to be re-orderable.
     return true
     }
     */
    
    // MARK: - Navigation
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        let destinationViewController = segue.destination as! DeviceViewController
        destinationViewController.device = self.devices[self.tableView.indexPathForSelectedRow!.row]
        destinationViewController.setDelegate(self)
    }
    
}
