import SwiftUI
import UserNotifications
import ComposeApp

@main
struct iOSApp: App {
    // 1. Setup the Delegate
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    // Instantiate the KMP Notification Handler
    private let notificationHandler = IOSNotificationHandler()

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        // Set delegate to listen to clicks
        UNUserNotificationCenter.current().delegate = self
        
        // 1. RESCHEDULE ON APP LAUNCH (iOS equivalent of BootReceiver)
        notificationHandler.onAppLaunched()
        
        return true
    }

    // Handle Notification Click (Foreground & Background)
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        processNotification(response.notification)
        completionHandler()
    }

    // Handle Notification Delivery (When app is in Foreground)
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        processNotification(notification)
        completionHandler([.banner, .sound])
    }

    private func processNotification(_ notification: UNNotification) {
        let userInfo = notification.request.content.userInfo

        if let rawId = userInfo["navigate_to_reminder_time"] as? NSNumber {
            let triggerId = rawId.int64Value

            // 1. Handle UI Navigation
            NotificationNavigator.shared.onNotificationClicked(triggerTimeId: triggerId)

            // 2. Handle Logic/Rescheduling
            notificationHandler.onNotificationDelivered(triggerTimeId: triggerId)
        }
    }
}