#import "BvYoutubeVideoPlugin.h"
#if __has_include(<bv_youtube_video/bv_youtube_video-Swift.h>)
#import <bv_youtube_video/bv_youtube_video-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "bv_youtube_video-Swift.h"
#endif

@implementation BvYoutubeVideoPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBvYoutubeVideoPlugin registerWithRegistrar:registrar];
}
@end
