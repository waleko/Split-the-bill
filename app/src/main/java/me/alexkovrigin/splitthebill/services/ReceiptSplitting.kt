package me.alexkovrigin.splitthebill.services

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.SplittingMap
import me.alexkovrigin.splitthebill.data.entity.User

/**
 * Class that contains receipt splitting logic
 */
class ReceiptSplitting(
    val users: List<User>,
    val items: List<Item>
) {
    private val userCount = users.size
    private val itemCount = items.size
    val splitting: MutableMap<Int, SnapshotStateList<Int?>> =
        (0 until itemCount).associateWith { List<Int?>(size = userCount) { null }.toMutableStateList() }
            .toMutableMap()

    /**
     * Recalculate the [splitting] for all users by equally splitting pay among paying (non-null in [splitting]) users.
     */
    @Synchronized
    private fun runSimpleSplittingUpdate(page: Int) {
        // check page index validity
        require(page in 0 until itemCount) { "Page $page out of bounds. There are total of $itemCount pages." }
        // calculate how many users are paying
        val payingUsers = splitting[page]?.count { it != null } ?: error("Splitting changed")
        // get the sum of the item that is payed
        val sum = items[page]._raw_sum
        // if no one is paying, in order to avoid dividing by zero, return
        if (payingUsers == 0)
            return
        // get base amount, i.e. that everyone is paying
        val baseAmount = sum / payingUsers
        // get additional once cent payments that will be distributed among first `additionalToPay` users
        var additionalToPay = sum % payingUsers

        // update each user's pay
        for (i in 0 until userCount) {
            // if saved splitting for user is `null`, they are not paying, skip
            if (splitting[page]?.get(i) == null)
                continue
            // calculate amount to pay
            // start with base amount that every paying user has to give
            var toPay = baseAmount
            // if `additionalToPay` has not been yet resolved, add single cent
            if (additionalToPay != 0) {
                additionalToPay--
                toPay++
            }
            // update user's pay
            splitting[page]?.set(
                index = i,
                element = toPay
            )
        }
    }

    /**
     * Switches user's paying status to [enabled]. Updates splitting for each user.
     */
    fun simpleSwitchUser(page: Int, userIndex: Int, enabled: Boolean) {
        // set paying status. `null` means not paying, 0 means paying
        // (the exact value will be calculated during splitting update)
        splitting[page]?.set(userIndex, element = if (enabled) 0 else null)
        // update splitting
        runSimpleSplittingUpdate(page)
    }

    /**
     * Validates that splitting has been successfully completed.
     *
     * Returns failed pages.
     */
    fun validateSplittingAndGetIncompletePages(): List<Int> {
        return splitting.mapValues { (_, list) ->
            list.any { it != null }
        }.toList()
            .filterNot { it.second }
            .map { it.first }
    }

    /**
     * Checks whether splitting has been successfully completed
     */
    fun isSplittingCompleted(): Boolean = validateSplittingAndGetIncompletePages().isEmpty()

    fun produceSplitting(): SplittingMap {
        val splittingMap = mutableMapOf<User, MutableList<Int?>>()

        // TODO: rewrite all so that O(itemCount * userCount) is not required
        //  inserting `splitting` still requires O(I * U), so this is not severe
        users.forEach { splittingMap[it] = mutableListOf() }

        splitting.forEach { (_, usersPays) ->
            usersPays.forEachIndexed { userIndex, sum ->
                val user = users[userIndex]
                splittingMap[user]?.add(sum)
            }
        }

        return splittingMap
    }
}