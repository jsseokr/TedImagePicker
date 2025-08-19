package gun0912.tedimagepicker.ui



import android.os.Bundle

import android.widget.ImageButton

import android.widget.TextView

import androidx.activity.ComponentActivity

import androidx.lifecycle.lifecycleScope

import androidx.viewpager2.widget.ViewPager2

import gun0912.tedimagepicker.R

import gun0912.tedimagepicker.selection.SelectionStore

import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch

import android.net.Uri

import android.widget.Toast



internal class PreviewActivity : ComponentActivity() {



    companion object {

        const val KEY_ITEMS = "KEY_ITEMS"           // ArrayList<Uri>

        const val KEY_START_POSITION = "KEY_START"

    }



    private lateinit var pager: ViewPager2

    private lateinit var tvCount: TextView

    private lateinit var btnBack: ImageButton

    private lateinit var btnToggle: ImageButton

    private lateinit var items: ArrayList<Uri>



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.ted_preview_activity)



        items = intent.getParcelableArrayListExtra(KEY_ITEMS) ?: arrayListOf()

        val adapter = PreviewPagerAdapter(items)



        pager = findViewById(R.id.viewPager)

        tvCount = findViewById(R.id.tvCount)

        btnBack = findViewById(R.id.btnBack)

        btnToggle = findViewById(R.id.btnToggle)



        pager.adapter = adapter

        pager.setCurrentItem(intent.getIntExtra(KEY_START_POSITION, 0), false)



        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        btnToggle.setOnClickListener {

            val cur = items[pager.currentItem]

            when (SelectionStore.toggle(cur)) {

                null  -> Toast.makeText(this, "최대 ${SelectionStore.maxCount}개까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()

                else  -> Unit

            }

        }



        lifecycleScope.launch {

            SelectionStore.selected.collectLatest { set ->

                val cur = items[pager.currentItem]

                btnToggle.setImageResource(

                    if (set.contains(cur)) R.drawable.ic_check_on else R.drawable.ic_check_off

                )

                tvCount.text = "${set.size} / ${SelectionStore.maxCount}"

            }

        }



        pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {

                val set = SelectionStore.selected.value

                val cur = items[position]

                btnToggle.setImageResource(

                    if (set.contains(cur)) R.drawable.ic_check_on else R.drawable.ic_check_off

                )

            }

        })

    }

}
