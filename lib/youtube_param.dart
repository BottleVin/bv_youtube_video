class YoutubeParam {
  final String? videoId;
  final bool showUI;
  final double startSeconds;
  final bool autoStart;

  const YoutubeParam({
    this.videoId,
    this.showUI = true,
    this.startSeconds = 0.0,
    this.autoStart = false
  });
}
