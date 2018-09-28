//
//  DeviceCell.swift
//  Tready
//
//  Created by Brian Krupp on 5/17/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import UIKit

protocol DeviceCellDelegate:AnyObject {
    func enableButtonTapped(cellSelected:UITableViewCell, relatedDevice:Device) -> Void
}

class DeviceCell: UITableViewCell {
    @IBOutlet weak var deviceName: UILabel!
    @IBOutlet weak var resolution: UILabel!
    @IBOutlet weak var lastContact: UILabel!
    @IBOutlet weak var enableButton: UIButton!
    @IBOutlet weak var orientation: UILabel!
    @IBOutlet weak var preMotionCapture: UILabel!
    @IBOutlet weak var postMotionCapture: UILabel!
    @IBOutlet weak var magnitudeThreshold: UILabel!
    @IBOutlet weak var vectorThreshold: UILabel!
    @IBOutlet weak var ipAddress: UILabel!
    
    public var relatedDevice:Device!
    
    private weak var delegate:DeviceCellDelegate?
    
    func setDelegate(delegate:DeviceCellDelegate) -> Void {
        self.delegate = delegate
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.enableButton.layer.cornerRadius = 5
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func buttonClicked(_ sender: Any) {
        if let delegate = delegate {
            delegate.enableButtonTapped(cellSelected: self, relatedDevice: relatedDevice)
        }
    }
    
}
