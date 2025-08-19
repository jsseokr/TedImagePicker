package gun0912.tedimagepicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import gun0912.tedimagepicker.adapter.PreviewMediaAdapter
import gun0912.tedimagepicker.databinding.ActivityPreviewBinding
import gun0912.tedimagepicker.util.Logger

internal class TedPreViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewBinding
    private lateinit var mediaUriList: List<Uri>
    private var selectedUriList: MutableList<Uri> = mutableListOf()
    private lateinit var adapter: PreviewMediaAdapter
    private var currentPosition = 0
    private var hasSelectionChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.verbose("+")

        setSavedInstanceState(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview)

        setupViews()
        setupViewPager()
        updateUI()
    }

    private fun setupViews() {
        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            finishWithResult()
        }

        // 선택 상태 토글
        binding.ivSelectionStatus.setOnClickListener {
            toggleSelection()
        }
    }

    private fun finishWithResult() {
        // 선택 상태 변경이 있었는지 확인하고 결과 반환
        if (hasSelectionChanged) {
            setResult(RESULT_OK, Intent().apply {
                putParcelableArrayListExtra(EXTRA_SELECTED_URI_LIST, ArrayList(selectedUriList))
            })
        }
        finish()
    }

    private fun setupViewPager() {
        adapter = PreviewMediaAdapter(this, mediaUriList, selectedUriList)
        binding.viewPager.adapter = adapter

        // 페이지 변경 리스너
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                updateUI()
            }
        })

        // 초기 위치 설정 (선택된 첫 번째 아이템이 있다면)
        if (selectedUriList.isNotEmpty()) {
            val firstSelectedIndex = mediaUriList.indexOf(selectedUriList.first())
            if (firstSelectedIndex != -1) {
                binding.viewPager.setCurrentItem(firstSelectedIndex, false)
                currentPosition = firstSelectedIndex
            }
        }
    }

    private fun updateUI() {
        val currentUri = mediaUriList[currentPosition]
        val isSelected = selectedUriList.contains(currentUri)

        // 선택 상태 표시 업데이트
        binding.ivSelectionStatus.setImageResource(
            if (isSelected) R.drawable.ic_check else R.drawable.bg_multi_image_unselected
        )

        // 위치 정보 업데이트
        binding.tvPosition.text = (currentPosition + 1).toString()
        binding.tvTotalCount.text = mediaUriList.size.toString()

        // 제목 업데이트
        binding.tvTitle.text = getString(
            R.string.ted_image_picker_preview_title_format,
            currentPosition + 1,
            mediaUriList.size
        )
    }

    private fun toggleSelection() {
        val currentUri = mediaUriList[currentPosition]
        val isCurrentlySelected = selectedUriList.contains(currentUri)

        if (isCurrentlySelected) {
            selectedUriList.remove(currentUri)
        } else {
            selectedUriList.add(currentUri)
        }

        hasSelectionChanged = true
        updateUI()
    }

    private fun setSavedInstanceState(savedInstanceState: Bundle?) {
        Logger.verbose("+")

        val bundle: Bundle? = when {
            savedInstanceState != null -> savedInstanceState
            else -> intent.extras
        }

        mediaUriList = bundle?.getParcelableArrayList(EXTRA_MEDIA_URI_LIST) ?: run {
            Logger.error("미디어 URI 리스트가 없습니다")
            finish()
            return
        }

        selectedUriList = (bundle?.getParcelableArrayList<Uri>(EXTRA_SELECTED_URI_LIST) ?: emptyList()).toMutableList()

        Logger.verbose("mediaUriList.size = ${mediaUriList.size}, selectedUriList.size = ${selectedUriList.size}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Logger.verbose("+")

        outState.putParcelableArrayList(EXTRA_MEDIA_URI_LIST, ArrayList(mediaUriList))
        outState.putParcelableArrayList(EXTRA_SELECTED_URI_LIST, ArrayList(selectedUriList))
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        // 현재 페이지의 비디오 일시정지
        adapter.getCurrentPlayer(currentPosition)?.pause()
    }

    override fun onResume() {
        super.onResume()
        // 현재 페이지의 비디오 재생 재개
        adapter.getCurrentPlayer(currentPosition)?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 모든 ExoPlayer 리소스 해제
        adapter.releaseAllPlayers()
    }

    @Deprecated("Deprecated in API level 33")
    override fun onBackPressed() {
        finishWithResult()
    }

    companion object {
        private const val EXTRA_MEDIA_URI_LIST = "EXTRA_MEDIA_URI_LIST"
        const val EXTRA_SELECTED_URI_LIST = "EXTRA_SELECTED_URI_LIST"

        fun getIntent(
            context: Context,
            mediaUriList: List<Uri>,
            selectedUriList: List<Uri> = emptyList()
        ): Intent {
            return Intent(context, TedPreViewActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_MEDIA_URI_LIST, ArrayList(mediaUriList))
                putParcelableArrayListExtra(EXTRA_SELECTED_URI_LIST, ArrayList(selectedUriList))
            }
        }
    }
}
