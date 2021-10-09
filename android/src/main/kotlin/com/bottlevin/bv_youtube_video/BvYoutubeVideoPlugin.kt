package com.bottlevin.bv_youtube_video

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import java.util.concurrent.atomic.AtomicReference
import androidx.lifecycle.Lifecycle

/** BvYoutubeVideoPlugin */
class BvYoutubeVideoPlugin: FlutterPlugin {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  //private lateinit var channel : MethodChannel
  private val state: AtomicReference<Lifecycle.Event> = AtomicReference(Lifecycle.Event.ON_CREATE)

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    flutterPluginBinding
            .platformViewRegistry
            .registerViewFactory("bv_youtube_video", BvYouTubeVideoFactory(flutterPluginBinding.getBinaryMessenger(), state))
    //channel = MethodChannel(flutterPluginBinding.binaryMessenger, "bv_youtube_video")
    //channel.setMethodCallHandler(this)
  }

/*  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }
  } */

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
//    channel.setMethodCallHandler(null)
  }
}
