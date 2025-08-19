package gun0912.tedimagepicker.selection



import android.net.Uri

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow



/**

 * 썸네일(Grid)과 프리뷰(ViewPager)에서 공통으로 사용하는 선택 상태 저장소

 * - LinkedHashSet 으로 중복 방지 + 선택 순서 유지

 * - maxCount(기본 20) 제한

 */

internal object SelectionStore {

    private val _selected = MutableStateFlow<LinkedHashSet<Uri>>(linkedSetOf())

    val selected: StateFlow<Set<Uri>> = _selected.asStateFlow()



    @Volatile var maxCount: Int = 20



    fun isSelected(uri: Uri) = _selected.value.contains(uri)



    /** true=선택, false=해제, null=실패(개수 초과) */

    fun toggle(uri: Uri): Boolean? {

        val set = LinkedHashSet(_selected.value)

        if (set.remove(uri)) { _selected.value = set; return false }

        if (set.size >= maxCount) return null

        set.add(uri); _selected.value = set; return true

    }



    fun clear() { _selected.value = linkedSetOf() }

}