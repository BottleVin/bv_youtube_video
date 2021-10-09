abstract class BVYouTubeVideoListener {
  void onReady();

  void onStateChange(String state);

  void onError(String error);

  void onVideoDuration(double duration);

  void onCurrentSecond(double second);
}
