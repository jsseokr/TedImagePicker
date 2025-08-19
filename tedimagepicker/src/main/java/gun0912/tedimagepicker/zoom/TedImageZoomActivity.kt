package gun0912.tedimagepicker.zoom

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import gun0912.tedimagepicker.R
import gun0912.tedimagepicker.databinding.ActivityZoomOutBinding
import gun0912.tedimagepicker.util.Logger
import gun0912.tedimagepicker.util.MediaUtil

internal class TedImageZoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZoomOutBinding
    private lateinit var uri: Uri
    private var exoPlayer: ExoPlayer? = null
    private var isVideo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.verbose("+")

        setSavedInstanceState(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zoom_out)

        // 미디어 타입 확인
        isVideo = MediaUtil.isVideo(this, uri)
        
        if (isVideo) {
            setupVideoPlayer()
        } else {
            setupImageView()
        }
    }

    private fun setupImageView() {
        Logger.verbose("+")
        
        ViewCompat.setTransitionName(binding.ivMedia, uri.toString())
        binding.ivMedia.visibility = View.VISIBLE
        binding.playerView.visibility = View.GONE
        
        supportPostponeEnterTransition()
        loadImage {
            supportStartPostponedEnterTransition()
        }
    }
    
    private fun setupVideoPlayer() {
        Logger.verbose("+")
        
        binding.ivMedia.visibility = View.GONE
        binding.playerView.visibility = View.VISIBLE
        
        // ExoPlayer 초기화
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        
        binding.playerView.player = exoPlayer
    }
    
    private fun loadImage(onLoadingFinished: () -> Unit) {
        Logger.verbose("+")

        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                onLoadingFinished()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onLoadingFinished()
                return false
            }
        }
        Glide.with(this)
            .load(uri)
            .apply(RequestOptions().dontTransform())
            .listener(listener)
            .into(binding.ivMedia)
    }

    private fun setSavedInstanceState(savedInstanceState: Bundle?) {
        Logger.verbose("+")

        val bundle: Bundle? = when {
            savedInstanceState != null -> savedInstanceState
            else -> intent.extras
        }

        uri = bundle?.getParcelable(EXTRA_URI) ?: return finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Logger.verbose("+")

        outState.putParcelable(EXTRA_URI, uri)
        super.onSaveInstanceState(outState)
    }
    
    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }
    
    override fun onResume() {
        super.onResume()
        if (isVideo) {
            exoPlayer?.play()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }

    companion object {
        private const val EXTRA_URI = "EXTRA_URI"
        fun getIntent(context: Context, uri: Uri) =
            Intent(context, TedImageZoomActivity::class.java)
                .apply {
                    putExtra(EXTRA_URI, uri)
                }
    }
}