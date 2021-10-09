import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:bv_youtube_video/bv_youtube_video.dart';
import 'package:bv_youtube_video/bv_youtube_video_listener.dart';
import 'package:bv_youtube_video/youtube_param.dart';
import 'package:bv_youtube_video/bv_youtube_video_controller.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> implements BVYouTubeVideoListener {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.

  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: BVYouTubePlayer(
            onViewCreated: _onYoutubeCreated,
            listener: this,
            params: const YoutubeParam(
            videoId: "PIKGmaXmfCQ", showUI: true, autoStart: false),
          ),
        ),
      ),
    );
  }

  void _onYoutubeCreated(BVYoutubeVideoController controller) {
  }

  @override
  void onCurrentSecond(double second) {
    // TODO: implement onCurrentSecond
  }

  @override
  void onError(String error) {
    // TODO: implement onError
  }

  @override
  void onReady() {
    // TODO: implement onReady
  }

  @override
  void onStateChange(String state) {
    // TODO: implement onStateChange
    debugPrint('State changed: ' + state);
  }

  @override
  void onVideoDuration(double duration) {
    // TODO: implement onVideoDuration
  }

}
