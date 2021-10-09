import Flutter
import UIKit

public class SwiftBvYoutubeVideoPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let viewFactory = BvYouTubeVideoFactory(messenger: registrar.messenger())
    //let instance = SwiftBvYoutubeVideoPlugin()
//    registrar.addMethodCallDelegate(instance, channel: channel)
    registrar.register(viewFactory, withId: "bv_youtube_video")
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
