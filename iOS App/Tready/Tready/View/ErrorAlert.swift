//
//  ErrorAlert.swift
//  Tready
//
//  Created by Brian Krupp on 5/17/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation
import UIKit

class ErrorAlert {
    var alertController:UIAlertController
    
    func show(fromController from:UIViewController) {
        DispatchQueue.main.async {
            from.present(self.alertController, animated: true, completion: nil)
        }
    }
    init(message:String, error:Error?) {
        self.alertController = UIAlertController(title: "Error", message: message, preferredStyle: .alert )
        alertController.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
    }
}
