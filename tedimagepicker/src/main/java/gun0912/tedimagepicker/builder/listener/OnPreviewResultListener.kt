package gun0912.tedimagepicker.builder.listener

import android.net.Uri

/**
 * 프리뷰 액티비티에서 선택 상태 변경 결과를 받기 위한 리스너
 */
interface OnPreviewResultListener {
    /**
     * 프리뷰에서 선택 상태가 변경되었을 때 호출
     * @param selectedUriList 변경된 선택된 URI 리스트
     */
    fun onPreviewResult(selectedUriList: List<Uri>)
}
