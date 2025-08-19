package gun0912.tedimagepicker.builder

import android.Manifest
import android.R.attr.orientation
import android.R.attr.value
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gun0912.tedpermission.TedPermissionUtil
import com.gun0912.tedpermission.rx2.TedPermission
import com.tedpark.tedonactivityresult.rx2.TedRxOnActivityResult
import gun0912.tedimagepicker.R
import gun0912.tedimagepicker.TedImagePickerActivity
import gun0912.tedimagepicker.TedPreViewActivity
import gun0912.tedimagepicker.builder.listener.ImageSelectCancelListener
import gun0912.tedimagepicker.builder.listener.OnErrorListener
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener
import gun0912.tedimagepicker.builder.listener.OnSelectedListener
import gun0912.tedimagepicker.builder.type.AlbumType
import gun0912.tedimagepicker.builder.type.ButtonGravity
import gun0912.tedimagepicker.builder.type.MediaType
import gun0912.tedimagepicker.builder.type.SelectType
import gun0912.tedimagepicker.util.Logger
import gun0912.tedimagepicker.util.ToastUtil
import gun0912.tedimagepicker.util.isPartialAccessGranted
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Suppress("UNCHECKED_CAST")
@Parcelize
open class TedImagePickerBaseBuilder<out B : TedImagePickerBaseBuilder<B>>(
    internal var selectType: SelectType = SelectType.SINGLE,
    internal var mediaType: MediaType = MediaType.IMAGE,
    @ColorRes
    internal var cameraTileBackgroundResId: Int = R.color.ted_image_picker_camera_background,
    @DrawableRes
    internal var cameraTileImageResId: Int = R.drawable.ic_camera_48dp,
    internal var showCameraTile: Boolean = true,
    internal var scrollIndicatorDateFormat: String = "yyyy.MM",
    internal var showTitle: Boolean = true,
    internal var title: String? = null,
    internal var savedDirectoryName: String? = null,
    @StringRes
    internal var titleResId: Int = R.string.ted_image_picker_title,
    internal var buttonGravity: ButtonGravity = ButtonGravity.TOP,
    internal var buttonText: String? = null,
    @DrawableRes
    internal var buttonBackgroundResId: Int = R.drawable.btn_done_button,
    @ColorRes
    internal var buttonTextColorResId: Int = R.color.white,
    internal var buttonDrawableOnly: Boolean = false,
    @StringRes
    internal var buttonTextResId: Int = R.string.ted_image_picker_done,
    internal var selectedUriList: List<Uri>? = null,
    @DrawableRes
    internal var backButtonResId: Int = R.drawable.ic_arrow_back_black_24dp,
    internal var maxCount: Int = Int.MAX_VALUE,
    internal var maxCountMessage: String? = null,
    @StringRes
    internal var maxCountMessageResId: Int = R.string.ted_image_picker_max_count,
    internal var minCount: Int = Int.MIN_VALUE,
    internal var minCountMessage: String? = null,
    @StringRes
    internal var minCountMessageResId: Int = R.string.ted_image_picker_min_count,
    internal var showZoomIndicator: Boolean = true,
    internal var albumType: AlbumType = AlbumType.DRAWER,
    internal var imageCountFormat: String = "%s",
    @AnimRes
    internal var startEnterAnim: Int? = null,
    @AnimRes
    internal var startExitAnim: Int? = null,
    @AnimRes
    internal var finishEnterAnim: Int? = null,
    @AnimRes
    internal var finishExitAnim: Int? = null,
    internal var screenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
    internal var showVideoDuration: Boolean = true,
) : Parcelable {


    @IgnoredOnParcel
    protected var onSelectedListener: OnSelectedListener? = null

    @IgnoredOnParcel
    protected var onMultiSelectedListener: OnMultiSelectedListener? = null

    @IgnoredOnParcel
    protected var onErrorListener: OnErrorListener? = null

    @IgnoredOnParcel
    protected var imageSelectCancelListener: ImageSelectCancelListener? = null

    @SuppressLint("CheckResult")
    protected fun startInternal(context: Context) {
        Logger.verbose("+")

        val requestPermissions = getRequestPermissions()
        if (TedPermissionUtil.isGranted(*requestPermissions) || mediaType.isPartialAccessGranted) {
            startActivity(context)
        } else {
            TedPermission.create()
                .setPermissions(*requestPermissions)
                .request()
                .subscribe({ permissionResult ->
                    if (permissionResult.isGranted || mediaType.isPartialAccessGranted) {
                        startActivity(context)
                    }
                }, { throwable -> onErrorListener?.onError(throwable) })
        }
    }

    private fun getRequestPermissions(): Array<String> {
        Logger.verbose("+")

        val permissions = mediaType.permissions.toMutableList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        }
        return permissions.toTypedArray()
    }

    private fun startActivity(context: Context) {
        Logger.verbose("+")

        TedImagePickerActivity.getIntent(context, this)
            .run {
                TedRxOnActivityResult.with(context).startActivityForResult(this)
            }.run {
                subscribe({ activityResult ->
                    if (activityResult.resultCode == Activity.RESULT_OK) {
                        onComplete(activityResult.data)
                    } else {
                        imageSelectCancelListener?.onImageSelectCancel()
                    }
                }, { throwable -> onErrorListener?.onError(throwable) })
            }
    }

    private fun onComplete(data: Intent) {
        Logger.verbose("+")

        val selectedUri =
            TedImagePickerActivity.getSelectedUri(data)
        val selectedUriList =
            TedImagePickerActivity.getSelectedUriList(data)
        when {
            selectedUri != null -> onSelectedListener?.onSelected(selectedUri)
            selectedUriList != null -> onMultiSelectedListener?.onSelected(selectedUriList)
            else -> onErrorListener?.onError(IllegalStateException("selectedUri/selectedUriList can not null"))
        }
    }

    fun mediaType(mediaType: MediaType): B {
        Logger.verbose("+")

        this.mediaType = mediaType
        return this as B
    }

    fun image(): B = mediaType(MediaType.IMAGE)

    fun video(): B = mediaType(MediaType.VIDEO)

    fun imageAndVideo(): B = mediaType(MediaType.IMAGE_AND_VIDEO)

    fun preview(
        context: Context,
        mediaUriList: List<Uri>,
        selectedUriList: List<Uri> = emptyList()
    ) {
        Logger.verbose("+")
        
        val intent = TedPreViewActivity.getIntent(context, mediaUriList, selectedUriList)
        context.startActivity(intent)
    }

    fun cameraTileBackground(@ColorRes cameraTileBackgroundResId: Int): B {
        Logger.verbose("cameraTileBackgroundResId = $cameraTileBackgroundResId")

        this.cameraTileBackgroundResId = cameraTileBackgroundResId
        return this as B
    }

    fun cameraTileImage(@DrawableRes cameraTileImage: Int): B {
        Logger.verbose("cameraTileImage = $cameraTileImage")

        this.cameraTileImageResId = cameraTileImage
        return this as B
    }

    fun showCameraTile(show: Boolean): B {
        Logger.verbose("show = $show")

        this.showCameraTile = show
        return this as B
    }

    fun scrollIndicatorDateFormat(formatString: String): B {
        Logger.verbose("formatString = $formatString")

        this.scrollIndicatorDateFormat = formatString
        return this as B
    }

    fun showTitle(show: Boolean): B {
        Logger.verbose("show = $show")

        this.showTitle = show
        return this as B
    }

    fun title(text: String): B {
        Logger.verbose("title = $title")

        this.title = text
        return this as B
    }

    fun title(@StringRes textResId: Int): B {
        Logger.verbose("textResId = $textResId")

        this.titleResId = textResId
        return this as B
    }

    fun savedDirectoryName(savedDirectoryName: String): B {
        Logger.verbose("savedDirectoryName = $savedDirectoryName")

        this.savedDirectoryName = savedDirectoryName
        return this as B
    }

    fun buttonGravity(buttonGravity: ButtonGravity): B {
        Logger.verbose("+")

        this.buttonGravity = buttonGravity
        return this as B
    }

    fun buttonText(text: String): B {
        Logger.verbose("text = $text")

        this.buttonText = text
        return this as B
    }

    fun buttonText(@StringRes textResId: Int): B {
        Logger.verbose("textResId = $textResId")

        this.buttonTextResId = textResId
        return this as B
    }

    fun buttonBackground(@DrawableRes buttonBackgroundResId: Int): B {
        Logger.verbose("buttonBackgroundResId = $buttonBackgroundResId")

        this.buttonBackgroundResId = buttonBackgroundResId
        return this as B
    }

    fun buttonTextColor(@ColorRes buttonTextColorResId: Int): B {
        Logger.verbose("buttonTextColorResId = $buttonTextColorResId")

        this.buttonTextColorResId = buttonTextColorResId
        return this as B
    }

    fun buttonDrawableOnly() = buttonDrawableOnly(true)

    fun buttonDrawableOnly(value: Boolean): B {
        Logger.verbose("value = $value")

        buttonDrawableOnly = value
        return this as B
    }

    fun selectedUri(uriList: List<Uri>?): B {
        Logger.verbose("uriList = $uriList")

        this.selectedUriList = uriList
        return this as B
    }

    fun backButton(@DrawableRes backButtonResId: Int): B {
        Logger.verbose("+")

        this.backButtonResId = backButtonResId
        return this as B
    }

    fun max(maxCount: Int, maxCountMessage: String): B {
        Logger.verbose("maxCount = $maxCount")

        this.maxCount = maxCount
        this.maxCountMessage = maxCountMessage
        return this as B
    }

    fun max(maxCount: Int, @StringRes maxCountMessageResId: Int): B {
        Logger.verbose("maxCount = $maxCount")

        this.maxCount = maxCount
        this.maxCountMessageResId = maxCountMessageResId
        return this as B
    }

    fun min(minCount: Int, minCountMessage: String): B {
        Logger.verbose("minCount = $minCount")

        this.minCount = minCount
        this.minCountMessage = minCountMessage
        return this as B
    }

    fun min(minCount: Int, @StringRes minCountMessageResId: Int): B {
        Logger.verbose("minCount = $minCount")

        this.minCount = minCount
        this.minCountMessageResId = minCountMessageResId
        return this as B
    }

    fun zoomIndicator(show: Boolean): B {
        Logger.verbose("show = $show")

        this.showZoomIndicator = show
        return this as B
    }

    fun albumType(albumType: AlbumType): B {
        Logger.verbose("albumType = $albumType")

        this.albumType = albumType
        if (albumType == AlbumType.DROP_DOWN) {
            showTitle(false)
        }
        return this as B
    }

    fun drawerAlbum(): B {
        Logger.verbose("+")

        return albumType(AlbumType.DRAWER)
    }

    fun dropDownAlbum(): B {
        Logger.verbose("+")

        return albumType(AlbumType.DROP_DOWN)
    }

    fun imageCountTextFormat(formatText: String): B {
        Logger.verbose("formatText = $formatText")

        this.imageCountFormat = formatText
        return this as B
    }

    fun startAnimation(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int): B {
        this.startEnterAnim = enterAnim
        this.startExitAnim = exitAnim
        return this as B
    }

    fun finishAnimation(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int): B {
        this.finishEnterAnim = enterAnim
        this.finishExitAnim = exitAnim
        return this as B
    }

    fun toast(toastAction: ((String) -> Unit)): B {
        ToastUtil.toastAction = toastAction
        return this as B
    }

    fun screenOrientation(orientation: Int) {
        Logger.verbose("orientation = $orientation")

        this.screenOrientation = orientation
    }

    fun showVideoDuration(show: Boolean): B {
        Logger.verbose("show = $show")

        this.showVideoDuration = show
        return this as B
    }

}
