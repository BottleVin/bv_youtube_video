package com.bottlevin.bv_youtube_video

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.platform.PlatformView
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import java.util.concurrent.atomic.AtomicReference

class BvYouTubeVideoView(
        private val context: Context?,
        id: Int,
        private val params: Map<String, Any?>,
        private val state: AtomicReference<Lifecycle.Event>,
        private val messenger: BinaryMessenger
) :
        PlatformView,
        MethodChannel.MethodCallHandler,
        ActivityAware {

    private val TAG = "BvYouTubeVideoView"

    private lateinit var youtubePlayerView: YouTubePlayerView
    private lateinit var container: FLTPlayerView
    private val view: FrameLayout
    private var youtubePlayer: YouTubePlayer? = null
    private var activity: Activity? = null
    private val methodChannel: MethodChannel

    init {
        val mode = params["scale_mode"] as? Int ?: 0
        view = createView(mode = mode)
        changeScaleMode(mode = mode)
        methodChannel = MethodChannel(messenger, "bv_youtube_video")
        methodChannel.setMethodCallHandler(this)
        initYouTubePlayerView()
    }

    private fun createView(mode: Int = 0): FrameLayout {
        val videoMode = BvYouTubeVideoScaleMode.values().find { it.mode == mode }
        container = FLTPlayerView(context!!).apply {
            setVideoScaleMode(mode = videoMode!!)
        }
        container.apply {
            youtubePlayerView = YouTubePlayerView(context)
            val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            )
            addView(youtubePlayerView, layoutParams)
        }
        return FrameLayout(context!!).apply {
            val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.gravity = Gravity.CENTER
            addView(container, layoutParams)
        }
    }

    private fun initYouTubePlayerView() {
        Log.d(TAG, "params = $params")
        val videoId = params["videoId"] as? String
        val startSeconds = (params["startSeconds"] as Double).toFloat()
        val showUI = params["showUI"] as Boolean
        /*val controller = youtubePlayerView.getPlayerUiController()
        controller.showYouTubeButton(false)
        controller.showFullscreenButton(false)
        if (!showUI) {
            controller.showUi(false)
            controller.showVideoTitle(false)
        }*/
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youtubePlayer = youTubePlayer
                methodChannel.invokeMethod("onReady", null)
                if (videoId != null) {
                    loadOrCueVideo(videoId, startSeconds)
                }
            }

            override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
            ) {
                onStateChange(state)
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                onError(error)
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                methodChannel.invokeMethod("onVideoDuration", duration)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                methodChannel.invokeMethod("onCurrentSecond", second)
            }
        })
    }

    override fun getView(): View {
        return view
    }

    override fun dispose() {
        youtubePlayerView.release()
//        registrar.activity().application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
        when (methodCall.method) {
            "initialization" -> result.success(null)
            "loadOrCueVideo" -> loadOrCueVideo(methodCall, result)
            "play" -> play(result)
            "pause" -> pause(result)
            "seekTo" -> seekTo(methodCall, result)
            "setVolume" -> setVolume(methodCall, result)
            "mute" -> {
                youtubePlayer?.setVolume(0)
                result.success(null)
            }
            "unMute" -> {
                youtubePlayer?.setVolume(100)
                result.success(null)
            }
            "scaleMode" -> {
                changeScaleMode(methodCall.arguments as Int)
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    private fun loadOrCueVideo(methodCall: MethodCall, result: MethodChannel.Result) {
        val params = methodCall.arguments as HashMap<String, *>
        val videoId = params["videoId"] as String
        val startSeconds = (params["startSeconds"] as? Double ?: 0.0).toFloat()
        loadOrCueVideo(videoId, startSeconds)
        result.success(null)
    }

    private fun loadOrCueVideo(videoId: String, startSeconds: Float) {
        val canLoad = state.get() == Lifecycle.Event.ON_RESUME
        if (canLoad)
            youtubePlayer?.loadVideo(videoId, startSeconds)
        else
            youtubePlayer?.cueVideo(videoId, startSeconds)
    }

    private fun pause(result: MethodChannel.Result) {
        youtubePlayer?.pause()
        result.success(null)
    }

    private fun play(result: MethodChannel.Result) {
        youtubePlayer?.play()
        result.success(null)
    }

    private fun seekTo(methodCall: MethodCall, result: MethodChannel.Result) {
        val time = (methodCall.arguments as Double).toFloat()
        youtubePlayer?.seekTo(time)
        result.success(null)
    }

    /**
     * @param volumePercent Integer between 0 and 100
     */
    private fun setVolume(methodCall: MethodCall, result: MethodChannel.Result) {
        val volumePercent = methodCall.arguments as Int
        youtubePlayer?.setVolume(volumePercent)
        result.success(null)
    }

    private fun changeScaleMode(mode: Int) {
        val videoMode = BvYouTubeVideoScaleMode.values().find { it.mode == mode }
        container.setVideoScaleMode(videoMode!!)
        val playerHeight = when (videoMode) {
            BvYouTubeVideoScaleMode.NONE -> FrameLayout.LayoutParams.MATCH_PARENT
            BvYouTubeVideoScaleMode.FIT_WIDTH -> FrameLayout.LayoutParams.WRAP_CONTENT
            BvYouTubeVideoScaleMode.FIT_HEIGHT -> FrameLayout.LayoutParams.WRAP_CONTENT
        }
        val videoParams = youtubePlayerView.layoutParams.apply {
            width = FrameLayout.LayoutParams.MATCH_PARENT
            height = playerHeight
        }
        youtubePlayerView.layoutParams = videoParams
        val containerWidth = when (videoMode) {
            BvYouTubeVideoScaleMode.NONE -> FrameLayout.LayoutParams.MATCH_PARENT
            BvYouTubeVideoScaleMode.FIT_WIDTH -> FrameLayout.LayoutParams.MATCH_PARENT
            BvYouTubeVideoScaleMode.FIT_HEIGHT -> FrameLayout.LayoutParams.WRAP_CONTENT
        }
        val containerHeight = when (videoMode) {
            BvYouTubeVideoScaleMode.NONE -> FrameLayout.LayoutParams.MATCH_PARENT
            BvYouTubeVideoScaleMode.FIT_WIDTH -> FrameLayout.LayoutParams.WRAP_CONTENT
            BvYouTubeVideoScaleMode.FIT_HEIGHT -> FrameLayout.LayoutParams.MATCH_PARENT
        }
        val containerGravity = when (videoMode) {
            BvYouTubeVideoScaleMode.NONE -> Gravity.CENTER
            BvYouTubeVideoScaleMode.FIT_WIDTH -> Gravity.CENTER_HORIZONTAL
            BvYouTubeVideoScaleMode.FIT_HEIGHT -> Gravity.CENTER_HORIZONTAL
        }
        val containerParams = (container.layoutParams as FrameLayout.LayoutParams).apply {
            width = containerWidth
            height = containerHeight
            gravity = containerGravity
        }
        container.layoutParams = containerParams
        view.requestLayout()
    }

    private fun onStateChange(state: PlayerConstants.PlayerState) {
        Log.d(TAG, "state = $state")
        val customState = when (state) {
            PlayerConstants.PlayerState.VIDEO_CUED -> PlayerState.VIDEO_CUED.value
            PlayerConstants.PlayerState.UNSTARTED -> PlayerState.UNSTARTED.value
            PlayerConstants.PlayerState.ENDED -> PlayerState.ENDED.value
            PlayerConstants.PlayerState.PLAYING -> PlayerState.PLAYING.value
            PlayerConstants.PlayerState.PAUSED -> PlayerState.PAUSED.value
            PlayerConstants.PlayerState.BUFFERING -> PlayerState.BUFFERING.value
            else -> PlayerState.UNKNOWN.value
        }
        methodChannel.invokeMethod("onStateChange", customState)
    }

    private fun onError(error: PlayerConstants.PlayerError) {
        Log.d(TAG, "error = ${error.name}")
        val customError = when (error) {
            PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST -> PlayerError.INVALID_PARAMETER_IN_REQUEST.value
            PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER -> PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER.value
            PlayerConstants.PlayerError.HTML_5_PLAYER -> PlayerError.HTML_5_PLAYER.value
            PlayerConstants.PlayerError.VIDEO_NOT_FOUND -> PlayerError.VIDEO_NOT_FOUND.value
            else -> PlayerError.UNKNOWN.value
        }
        methodChannel.invokeMethod("onError", customError)
    }


    //--
    // ActivityAware
    //--

    override fun onDetachedFromActivity() {
        this.activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }
}