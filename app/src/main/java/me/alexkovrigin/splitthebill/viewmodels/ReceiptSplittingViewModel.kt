package me.alexkovrigin.splitthebill.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.User

class ReceiptSplittingViewModel : ViewModel() {
    val users = mutableStateListOf<User>()

    inner class ReceiptSplitting(
        val users: List<User>,
        val items: List<Item>,
    ) {
        val userCount = users.size
        val itemCount = items.size
        val splitting: MutableMap<Int, SnapshotStateList<Int?>> = (0 until itemCount).associateWith { List<Int?>(size = userCount) { null }.toMutableStateList() }.toMutableMap()

        private fun updateSplitting(page: Int) {
            val payingUsers = splitting[page]?.count { it != null } ?: error(TODO())
            val sum = items[page]._raw_sum

            for (i in 0 until userCount) {
                // TODO: last cent distribution
                val prev = splitting[page]?.get(i)
                splitting[page]?.set(
                    index = i,
                    element = if (prev != null)
                        sum / payingUsers
                    else
                        null
                )
            }
        }

        fun switchUser(page: Int, userIndex: Int, enabled: Boolean) {
            splitting[page]?.set(userIndex, element = if (enabled) 0 else null)
            updateSplitting(page)
        }
    }

    lateinit var rc: ReceiptSplitting

    private var initialized: Boolean = false

    @Synchronized
    fun initialize(users: List<User>, items: List<Item>) {
        if (initialized)
            return
        initialized = true

        rc = ReceiptSplitting(users, items)
    }
}