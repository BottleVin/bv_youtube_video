import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:bv_youtube_video/bv_youtube_video_listener.dart';
import 'package:bv_youtube_video/youtube_param.dart';
import 'package:bv_youtube_video/bv_youtube_video_controller.dart';

typedef BVYoutubeViewCreatedCallback = void Function(BVYoutubeVideoController controller);

class BVYouTubePlayer extends StatefulWidget {
  const BVYouTubePlayer({
    Key? key,
    this.onViewCreated,
    required this.listener,
    this.scaleMode = YoutubeScaleMode.none,
    this.params = const YoutubeParam()
  }) : super(key: key);

  final BVYoutubeViewCreatedCallback? onViewCreated;
  final BVYouTubeVideoListener listener;
  final YoutubeParam params;
  final YoutubeScaleMode scaleMode;

  @override
  _BVYoutTubePlayerState createState() => _BVYoutTubePlayerState();
}

enum YoutubeScaleMode { none, fitWidth, fitHeight }

class _BVYoutTubePlayerState extends State<BVYouTubePlayer> {

  late BVYoutubeVideoController _controller;

  @override
  Widget build(BuildContext context) {
    return _buildVideo();
  }

  void _onPlatformViewCreated(int id) {
    _controller = BVYoutubeVideoController.of(id, widget.listener);
    if (widget.onViewCreated != null) {
      widget.onViewCreated!(_controller);
    }
    _initialization();
  }

  void _initialization() async {
    _controller.initialization();
  }

  Widget _buildVideo() {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'bv_youtube_video',
        onPlatformViewCreated: _onPlatformViewCreated,
        creationParams: <String, dynamic>{
          "scale_mode": widget.scaleMode.index,
          "videoId": widget.params.videoId,
          "showUI": widget.params.showUI,
          "startSeconds": widget.params.startSeconds,
          "autoStart": widget.params.autoStart,
        },
        creationParamsCodec: const StandardMessageCodec(),
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return UiKitView(
        viewType: 'bv_youtube_video',
        onPlatformViewCreated: _onPlatformViewCreated,
        creationParams: <String, dynamic>{
          "videoId": widget.params.videoId,
          "showUI": widget.params.showUI,
          "startSeconds": widget.params.startSeconds,
          "autoStart": widget.params.autoStart,
        },
        creationParamsCodec: const StandardMessageCodec(),
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the bv_youtube_video plugin');
  }

}
