package gun0912.tedimagepicker.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import gun0912.tedimagepicker.R
import gun0912.tedimagepicker.databinding.ItemPreviewMediaBinding
import gun0912.tedimagepicker.util.Logger
import gun0912.tedimagepicker.util.MediaUtil

internal class PreviewMediaAdapter(
    private val context: Context,
    private val mediaUriList: List<Uri>,
    private val selectedUriList: List<Uri>? = null
) : RecyclerView.Adapter<PreviewMediaAdapter.PreviewMediaViewHolder>() {

    private val exoPlayerMap = mutableMapOf<Int, ExoPlayer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewMediaViewHolder {
        val binding = ItemPreviewMediaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PreviewMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreviewMediaViewHolder, position: Int) {
        holder.bind(mediaUriList[position], position)
    }

    override fun getItemCount(): Int = mediaUriList.size

    override fun onViewRecycled(holder: PreviewMediaViewHolder) {
        super.onViewRecycled(holder)
        // ExoPlayer 리소스 해제
        val position = holder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            exoPlayerMap[position]?.release()
            exoPlayerMap.remove(position)
        }
    }

    fun releaseAllPlayers() {
        exoPlayerMap.values.forEach { it.release() }
        exoPlayerMap.clear()
    }
    
    fun getCurrentPlayer(position: Int): ExoPlayer? {
        return exoPlayerMap[position]
    }

    inner class PreviewMediaViewHolder(
        val binding: ItemPreviewMediaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri, position: Int) {
            Logger.verbose("position = $position, uri = $uri")

            // 로딩 상태로 초기화
            showLoading()

            // 미디어 타입 확인
            val isVideo = MediaUtil.isVideo(context, uri)

            if (isVideo) {
                setupVideoPlayer(uri, position)
            } else {
                setupImageView(uri)
            }
        }

        private fun setupImageView(uri: Uri) {
            binding.ivMedia.visibility = View.VISIBLE
            binding.playerView.visibility = View.GONE

            val listener = object : RequestListener<android.graphics.drawable.Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<android.graphics.drawable.Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    showError()
                    return false
                }

                override fun onResourceReady(
                    resource: android.graphics.drawable.Drawable?,
                    model: Any?,
                    target: Target<android.graphics.drawable.Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    hideLoading()
                    return false
                }
            }

            Glide.with(context)
                .load(uri)
                .apply(RequestOptions().dontTransform())
                .listener(listener)
                .into(binding.ivMedia)
        }

        private fun setupVideoPlayer(uri: Uri, position: Int) {
            binding.ivMedia.visibility = View.GONE
            binding.playerView.visibility = View.VISIBLE

            // ExoPlayer 초기화
            val exoPlayer = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)
                prepare()
            }

            binding.playerView.player = exoPlayer
            exoPlayerMap[position] = exoPlayer

            // 로딩 완료 시 콜백
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        hideLoading()
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    showError()
                }
            })
        }

        private fun showLoading() {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvError.visibility = View.GONE
        }

        private fun hideLoading() {
            binding.progressBar.visibility = View.GONE
            binding.tvError.visibility = View.GONE
        }

        private fun showError() {
            binding.progressBar.visibility = View.GONE
            binding.tvError.visibility = View.VISIBLE
        }
    }
}
