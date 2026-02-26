//
//  IosCrashManager.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-24.
//

import Foundation
import Shared
import UIKit

class IOSCrashManager: CrashManager {
    
    private let logPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] + "/latest_crash.txt"

    func initialize() {
        NSSetUncaughtExceptionHandler { exception in
            let report = """
            Name: \(exception.name.rawValue)
            Reason: \(exception.reason ?? "Unknown")
            Call Stack:
            \(exception.callStackSymbols.joined(separator: "\n"))
            """
            
            let path = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] + "/latest_crash.txt"
            try? report.write(toFile: path, atomically: true, encoding: .utf8)
        }
    }

    func getLastCrashLog() -> String? {
        return try? String(contentsOfFile: logPath, encoding: .utf8)
    }

    func clearCrashLog() {
        try? FileManager.default.removeItem(atPath: logPath)
    }
    
    func shareCrashLog(log: String) {
        let activityVC = UIActivityViewController(activityItems: [log], applicationActivities: nil)
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootVC = windowScene.windows.first?.rootViewController {

            if let popover = activityVC.popoverPresentationController {
                popover.sourceView = rootVC.view
                popover.sourceRect = CGRect(x: rootVC.view.bounds.midX, y: rootVC.view.bounds.midY, width: 0, height: 0)
                popover.permittedArrowDirections = []
            }
            
            rootVC.present(activityVC, animated: true, completion: nil)
        }
    }
}
