package gun0912.tedimagepicker.builder

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import gun0912.tedimagepicker.builder.listener.OnErrorListener
import gun0912.tedimagepicker.builder.listener.ImageSelectCancelListener
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener
import gun0912.tedimagepicker.builder.listener.OnSelectedListener
import gun0912.tedimagepicker.builder.type.SelectType
import gun0912.tedimagepicker.util.Logger
import java.lang.ref.WeakReference


class TedImagePicker {
    companion object {
        @JvmStatic
        fun with(context: Context) = Builder(WeakReference(context))
    }

    @SuppressLint("ParcelCreator")
    class Builder(private val contextWeakReference: WeakReference<Context>) :
        TedImagePickerBaseBuilder<Builder>() {

        fun errorListener(onErrorListener: OnErrorListener): Builder {
            Logger.verbose("+")

            this.onErrorListener = onErrorListener
            return this
        }

        fun errorListener(action: (Throwable) -> Unit): Builder {
            Logger.verbose("+")

            this.onErrorListener = object : OnErrorListener {
                override fun onError(throwable: Throwable) {
                    action(throwable)
                }
            }
            return this
        }

        fun cancelListener(imageSelectCancelListener: ImageSelectCancelListener): Builder {
            Logger.verbose("+")

            this.imageSelectCancelListener = imageSelectCancelListener
            return this
        }

        fun cancelListener(action: () -> Unit): Builder =
            cancelListener(object : ImageSelectCancelListener {
                override fun onImageSelectCancel() {
                    Logger.verbose("+")

                    action.invoke()
                }
            })


        fun start(onSelectedListener: OnSelectedListener) {
            Logger.verbose("+")

            this.onSelectedListener = onSelectedListener
            selectType = SelectType.SINGLE
            contextWeakReference.get()?.let {
                startInternal(it)
            }

        }

        fun start(action: (Uri) -> Unit) {
            Logger.verbose("+")

            start(object : OnSelectedListener {
                override fun onSelected(uri: Uri) {
                    action(uri)
                }
            })
        }

        fun startMultiImage(action: (List<Uri>) -> Unit) {
            Logger.verbose("+")

            startMultiImage(object : OnMultiSelectedListener {
                override fun onSelected(uriList: List<Uri>) {
                    action(uriList)
                }
            })
        }

        fun startMultiImage(onMultiSelectedListener: OnMultiSelectedListener) {
            Logger.verbose("+")

            this.onMultiSelectedListener = onMultiSelectedListener
            selectType = SelectType.MULTI
            contextWeakReference.get()?.let {
                startInternal(it)
            }
        }

    }


}


