
import 'dart:async';

import 'package:flutter/services.dart';

class BvYoutubeVideo {
  static const MethodChannel _channel = MethodChannel('bv_youtube_video');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
