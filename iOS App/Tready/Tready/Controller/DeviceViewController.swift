//
//  DeviceViewController.swift
//  Tready
//
//  Created by Brian Krupp on 5/24/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import UIKit

protocol DeviceUpdateDelegate: AnyObject {
    func deviceUpdated(device:Device) -> Void
}

class DeviceViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {

    var device:Device!
    var delegate:DeviceUpdateDelegate!
    @IBOutlet weak var preMotionCapture: UILabel!
    @IBOutlet weak var preMotionCaptureControl: UIStepper!
    @IBOutlet weak var postMotionCapture: UILabel!
    @IBOutlet weak var postMotionCaptureControl: UIStepper!
    @IBOutlet weak var resolution: UILabel!
    @IBOutlet weak var resolutionControl: UIStepper!
    @IBOutlet weak var vector: UILabel!
    @IBOutlet weak var vectorControl: UISlider!
    @IBOutlet weak var magnitude: UILabel!
    @IBOutlet weak var magnitudeControl: UISlider!
    @IBOutlet weak var deviceName: UITextField!
    @IBOutlet weak var orientationPicker: UIPickerView!
    let resolutionValues = ["(640, 800)", "(800, 600)", "(1024, 768)", "(1640, 1232)","(3280, 2464)"]
    
    public func setDelegate(_ delegate:DeviceUpdateDelegate) -> Void {
        self.delegate = delegate
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.preMotionCapture.text = String(self.device.preMotionCapture)
        self.preMotionCaptureControl.value = Double(self.device.preMotionCapture)
        
        self.postMotionCapture.text = String(self.device.postMotionCapture)
        self.postMotionCaptureControl.value = Double(self.device.postMotionCapture)
        
        self.resolution.text = String(self.device.captureResolution) + " " + self.resolutionValues[Int(self.device.captureResolution)!]
        self.resolutionControl.value = Double(Int(self.device.captureResolution)!)
        
        self.deviceName.text = device.deviceName
        
        self.vector.text = String(self.device.motionDetectionVectorThreshold)
        self.vectorControl.value = Float(self.device.motionDetectionVectorThreshold)
        
        self.magnitude.text = String(self.device.motionDetectionMagnitudeThreshold)
        self.magnitudeControl.value = Float(self.device.motionDetectionMagnitudeThreshold)
       
        
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    @IBAction func nameChanged(_ sender: Any) {
        self.device.deviceName = self.deviceName.text
    }
    @IBAction func premotionChanged(_ sender: Any) {
        let preMotionCaptureValue = Int(self.preMotionCaptureControl.value)
        self.preMotionCapture.text = String(preMotionCaptureValue)
        self.device.preMotionCapture = preMotionCaptureValue
    }
    @IBAction func postmotionChanged(_ sender: Any) {
        let postMotionCaptureValue = Int(self.postMotionCaptureControl.value)
        self.postMotionCapture.text = String(postMotionCaptureValue)
        self.device.postMotionCapture = postMotionCaptureValue
    }


    @IBAction func resolutionChanged(_ sender: Any) {
        let resolutionValue = String(Int(self.resolutionControl.value))
        self.resolution.text = resolutionValue + " " + self.resolutionValues[Int(self.resolutionControl.value)]
        self.device.captureResolution = resolutionValue
    }
    @IBAction func vectorChanged(_ sender: Any) {
        let vectorValue = Int(self.vectorControl.value.rounded())
        self.vector.text = String(vectorValue)
        self.device.motionDetectionVectorThreshold = vectorValue
    }
    @IBAction func magnitudeChanged(_ sender: Any) {
        let magnitudeValue = Int(self.magnitudeControl.value.rounded())
        self.magnitude.text = String(magnitudeValue)
        self.device.motionDetectionMagnitudeThreshold = magnitudeValue
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return Device.orientationDisplay.count
    }
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        let chosenIndex =  Device.orientationValues.index(of: self.device.orientation)!
        if chosenIndex == row {
            pickerView.selectRow(chosenIndex, inComponent: 0, animated: false)
        }
        return Device.orientationDisplay[row]
    }
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        self.device.orientation = Device.orientationValues[row]
    }
    
    
    override func viewWillDisappear(_ animated: Bool) {
        self.delegate.deviceUpdated(device: self.device)
    }

}
