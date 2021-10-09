import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:bv_youtube_video/bv_youtube_video.dart';

void main() {
  const MethodChannel channel = MethodChannel('bv_youtube_video');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    //expect(await BvYoutubeVideo.platformVersion, '42');
  });
}
