//
//  ViewController.swift
//  Tready
//
//  Created by Brian Krupp on 1/26/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    
    @IBOutlet weak var usernameTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var hubLocationTextField: UITextField!
    @IBOutlet weak var loginButton: UIButton!
    @IBOutlet weak var loginIndicator: UIActivityIndicatorView!
    
    var initialTextFieldBackgroundColor:UIColor?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initialTextFieldBackgroundColor = self.usernameTextField.backgroundColor
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func validateForm() -> Bool {
        var formValid = true
        
        let fieldsToCheck = [usernameTextField, passwordTextField, hubLocationTextField]
        
        for textField in fieldsToCheck {
            if (textField?.text!.count == 0) {
                textField?.backgroundColor = UIColor.init(red: 0.8, green: 0.5, blue: 0.5, alpha: 1.0)
                formValid = false
            }
            else {
                textField?.backgroundColor = self.initialTextFieldBackgroundColor
            }
        }
        
        return formValid
    }
    
    func disableButton() {
        DispatchQueue.main.async {
            self.loginButton.isEnabled = false
            self.loginIndicator.startAnimating()
            
        }
    }
    func enableButton() {
        DispatchQueue.main.async {
            self.loginButton.isEnabled = true
            self.loginIndicator.stopAnimating()
        }
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        guard self.validateForm() else {
            return false
        }
        
        let username:String = usernameTextField.text!;
        let password:String = passwordTextField.text!;
        let hubLocation:String = hubLocationTextField.text!;
        
        guard let url = URL(string:hubLocation) else {
            let errorAlert = ErrorAlert(message: "Invalid URL", error: nil)
            errorAlert.show(fromController: self)
            self.loginButton.isEnabled = true
            return false
        }
       
        
        ServiceClient.initSharedInstance(url: url, username:username, password:password)
        let client:ServiceClient = ServiceClient.getSharedInstance()
        
        let model:SecurityModel = SecurityModel(serviceClient: client)
        self.disableButton()
        model.authenticate(username: username, password: password, completionHandler: { (userId) in
            DispatchQueue.main.async {
                if (userId.userId == -1) {
                    let errorAlert = ErrorAlert(message: "Invalid username and/or password", error: nil)
                    errorAlert.show(fromController: self)
                    self.enableButton()
                }
                else {
                    // Logged in
                    self.enableButton()
                    self.performSegue(withIdentifier: identifier, sender: sender)
                }
            }
        }) { (error) in
            let errorAlert = ErrorAlert(message: "Could not login: " + error.localizedDescription, error: nil)
            errorAlert.show(fromController: self)
            self.enableButton()
           
        }
        // Always return false, let completion block perforfm
        return false
    }
    

}


