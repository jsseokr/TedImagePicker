package gun0912.tedimagepicker.ui



import android.net.Uri

import android.view.LayoutInflater

import android.view.View

import android.view.ViewGroup

import android.widget.ImageView

import android.widget.TextView

import androidx.core.view.isVisible

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.bumptech.glide.load.resource.gif.GifDrawable

import gun0912.tedimagepicker.R

import gun0912.tedimagepicker.media.isGif

import gun0912.tedimagepicker.media.isVideo

import gun0912.tedimagepicker.selection.SelectionStore



internal class MediaGridAdapter(

    private val items: List<Uri>,

    private val onOpenPreview: (startPosition: Int) -> Unit,

    private val onMaxReached: () -> Unit

) : RecyclerView.Adapter<MediaGridAdapter.VH>() {



    inner class VH(v: View) : RecyclerView.ViewHolder(v) {

        private val thumb: ImageView = v.findViewById(R.id.ivThumb)

        private val check: View = v.findViewById(R.id.viewCheck)

        private val badgeGif: TextView = v.findViewById(R.id.badgeGif)

        private val badgeVideo: TextView = v.findViewById(R.id.badgeVideo)



        fun bind(uri: Uri) {

            // GIF 애니메이션을 위해 asDrawable()

            Glide.with(thumb)

                .asDrawable()

                .load(uri)

                .centerCrop()

                .thumbnail(0.25f)

                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

                .into(object: com.bumptech.glide.request.target.CustomTarget<android.graphics.drawable.Drawable>() {

                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {

                        thumb.setImageDrawable(placeholder)

                    }

                    override fun onResourceReady(

                        res: android.graphics.drawable.Drawable,

                        t: com.bumptech.glide.request.transition.Transition<in android.graphics.drawable.Drawable>?

                    ) {

                        thumb.setImageDrawable(res)

                        if (res is GifDrawable) { res.setLoopCount(GifDrawable.LOOP_FOREVER); res.start() }

                    }

                })



            val ctx = itemView.context

            badgeGif.isVisible = ctx.isGif(uri)

            badgeVideo.isVisible = ctx.isVideo(uri)

            check.isVisible = SelectionStore.isSelected(uri)



            // 탭 = 선택/해제

            itemView.setOnClickListener {

                when (SelectionStore.toggle(uri)) {

                    true  -> check.isVisible = true

                    false -> check.isVisible = false

                    null  -> onMaxReached()

                }

            }

            // 길게 누름 = 프리뷰 진입

            itemView.setOnLongClickListener {

                onOpenPreview(bindingAdapterPosition)

                true

            }

        }

    }



    override fun onCreateViewHolder(p: ViewGroup, vt: Int) =

        VH(LayoutInflater.from(p.context).inflate(R.layout.item_media_grid, p, false))

    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(items[pos])

    override fun getItemCount() = items.size

}

