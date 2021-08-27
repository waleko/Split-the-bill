package me.alexkovrigin.splitthebill.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import me.alexkovrigin.splitthebill.data.dataviews.ReceiptWithItems
import me.alexkovrigin.splitthebill.data.dataviews.SplitReceipt
import me.alexkovrigin.splitthebill.data.dataviews.SplitReceiptWithItemsAndSplitting
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.Receipt
import me.alexkovrigin.splitthebill.data.entity.SplitReceiptInfo
import me.alexkovrigin.splitthebill.data.entity.Splitting
import me.alexkovrigin.splitthebill.data.entity.SplittingMap
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.services.api.ReceiptInfo

@Dao
interface BillDao {
    @Query("SELECT * FROM Users")
    fun allUsers(): LiveData<List<User>>

    @Query("SELECT * FROM SplitReceipt")
    fun allSplitReceipts(): LiveData<List<SplitReceipt>>

    @Transaction
    @Query("SELECT * FROM SplitReceiptWithItemsAndSplitting WHERE uid = :uid")
    fun splitReceiptWithItemsAndSplittingByUID(uid: String): LiveData<SplitReceiptWithItemsAndSplitting?>

    @Transaction
    @Query("SELECT * FROM ReceiptWithItems WHERE qr = :qr")
    fun receiptWithItemsByQR(qr: String): LiveData<ReceiptWithItems?>

    @Query("SELECT EXISTS(SELECT 1 FROM LoadedReceipts LR WHERE LR.qr = :qr LIMIT 1)")
    suspend fun doesReceiptWithQRExist(qr: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReceipts(vararg receipt: Receipt)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(vararg items: Item)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSplitReceiptInfos(vararg splitReceiptInfos: SplitReceiptInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSplittings(vararg splittings: Splitting)

    @Transaction
    suspend fun insertReceiptInfo(qr: String, receiptInfo: ReceiptInfo) {
        val receipt = Receipt(qr, receiptInfo)
        val items = receiptInfo.items.mapIndexed { index, itemInfo -> Item(qr, itemInfo, index) }
            .toTypedArray()

        insertReceipts(receipt)
        insertItems(*items)
    }

    @Transaction
    suspend fun insertNewSplitting(splitReceiptInfo: SplitReceiptInfo, splitting: SplittingMap) {
        insertSplitReceiptInfos(splitReceiptInfo)
        insertSplittings(
            Splitting(
                uid = splitReceiptInfo.uid,
                value = splitting
            )
        )
    }
}