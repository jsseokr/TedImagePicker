package gun0912.tedimagepicker.media

import android.content.Context
import android.net.Uri

internal fun Context.isVideo(uri: Uri) =
    (contentResolver.getType(uri) ?: "").lowercase().startsWith("video")

internal fun Context.isGif(uri: Uri) =
    (contentResolver.getType(uri) ?: "").lowercase().contains("image/gif")


