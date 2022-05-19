package com.bottlevin.bv_youtube_video

import android.content.Context
import android.view.View
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.plugin.common.BinaryMessenger
import java.util.concurrent.atomic.AtomicReference
import androidx.lifecycle.Lifecycle

class BvYouTubeVideoFactory(
    private val messenger: BinaryMessenger,
    private val state: AtomicReference<Lifecycle.Event>
    ) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String, Any?>
        return BvYouTubeVideoView(context, viewId, creationParams, state, messenger)
    }
}