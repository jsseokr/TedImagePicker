package gun0912.tedimagepicker.util

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class TedImagePickerContentProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        Logger.verbose("+")

        ToastUtil.context = context as Application
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Logger.verbose("+")

        throw UnsupportedOperationException()
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Logger.verbose("+")

        throw UnsupportedOperationException()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Logger.verbose("+")

        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Logger.verbose("+")

        throw UnsupportedOperationException()
    }

    override fun getType(uri: Uri): String {
        Logger.verbose("+")

        throw UnsupportedOperationException()
    }
}
