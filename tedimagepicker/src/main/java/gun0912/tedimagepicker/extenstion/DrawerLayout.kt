package gun0912.tedimagepicker.extenstion

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import gun0912.tedimagepicker.util.Logger

fun DrawerLayout.close() {
    Logger.verbose("+")

    if (isOpen()) {
        closeDrawer(GravityCompat.START)
    }
}


fun DrawerLayout.open() {
    Logger.verbose("+")

    if (!isOpen()) {
        openDrawer(GravityCompat.START)
    }
}

fun DrawerLayout.isOpen() = isDrawerOpen(GravityCompat.START)

fun DrawerLayout.toggle() {
    Logger.verbose("+")

    if (isOpen()) {
        close()
    } else {
        open()
    }
}

fun DrawerLayout.setLock(lock: Boolean) {
    Logger.verbose("lock = $lock")

    if (lock) {
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
    } else {
        setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}