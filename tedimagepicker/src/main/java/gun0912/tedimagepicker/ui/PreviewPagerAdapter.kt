package gun0912.tedimagepicker.ui



import android.net.Uri
import android.util.Log.v

import android.view.LayoutInflater

import android.view.View

import android.view.ViewGroup

import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView
import com.alexvasilkov.gestures.views.GestureImageView

import com.bumptech.glide.Glide

import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.bumptech.glide.load.resource.gif.GifDrawable

import com.google.android.exoplayer2.ExoPlayer

import com.google.android.exoplayer2.MediaItem

import com.google.android.exoplayer2.ui.PlayerView

import gun0912.tedimagepicker.R

import gun0912.tedimagepicker.media.isGif

import gun0912.tedimagepicker.media.isVideo



internal class PreviewPagerAdapter(

    private val items: List<Uri>

) : RecyclerView.Adapter<PreviewPagerAdapter.VH>() {



    inner class VH(v: View) : RecyclerView.ViewHolder(v) {

        private val ssiv: GestureImageView = v.findViewById(R.id.ssiv)

        private val iv: ImageView = v.findViewById(R.id.iv)

        private val playerView: PlayerView = v.findViewById(R.id.playerView)

        private var player: ExoPlayer? = null



        fun bind(uri: Uri) {

            ssiv.visibility = View.GONE; iv.visibility = View.GONE; playerView.visibility = View.GONE

            releasePlayer()



            val ctx = itemView.context

            when {

                ctx.isVideo(uri) -> {

                    playerView.visibility = View.VISIBLE

                    player = ExoPlayer.Builder(ctx).build().also { p ->

                        playerView.player = p

                        p.setMediaItem(MediaItem.fromUri(uri))

                        p.prepare()

                        p.playWhenReady = false

                    }

                }

                ctx.isGif(uri) -> {

                    iv.visibility = View.VISIBLE

                    Glide.with(iv)

                        .asGif()

                        .load(uri)

                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

                        .dontTransform()

                        .into(object: com.bumptech.glide.request.target.CustomTarget<GifDrawable>() {

                            override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {

                                iv.setImageDrawable(placeholder)

                            }

                            override fun onResourceReady(

                                res: GifDrawable,

                                t: com.bumptech.glide.request.transition.Transition<in GifDrawable>?

                            ) {

                                iv.setImageDrawable(res)

                                res.setLoopCount(GifDrawable.LOOP_FOREVER)

                                res.start()

                            }

                        })

                }

                else -> {

                    ssiv.visibility = View.VISIBLE
                    Glide.with(ctx)
                        .load(uri)
                        .into(ssiv)
                }

            }

        }



        fun unbind() { releasePlayer() }

        private fun releasePlayer() {

            player?.release(); player = null; playerView.player = null

        }

    }



    override fun onCreateViewHolder(p: ViewGroup, vt: Int) =

        VH(LayoutInflater.from(p.context).inflate(R.layout.item_preview_page, p, false))

    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(items[pos])

    override fun onViewRecycled(h: VH) { h.unbind(); super.onViewRecycled(h) }

    override fun getItemCount() = items.size

}