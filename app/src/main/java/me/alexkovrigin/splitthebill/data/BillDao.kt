package me.alexkovrigin.splitthebill.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.Receipt
import me.alexkovrigin.splitthebill.data.entity.ReceiptWithItems
import me.alexkovrigin.splitthebill.services.api.ReceiptInfo

@Dao
interface BillDao {
    @Query("SELECT * FROM LoadedReceipts")
    fun allReceipts(): List<Receipt>

    @Query("SELECT * FROM Items")
    fun allItems(): List<Item>

    @Transaction
    @Query("SELECT LR.*, I.* FROM LoadedReceipts LR LEFT JOIN Items I ON I.qr = :qr WHERE LR.qr = :qr")
    fun receiptWithItemsByQR(qr: String): LiveData<ReceiptWithItems?>

    @Query("SELECT EXISTS(SELECT 1 FROM LoadedReceipts LR WHERE LR.qr = :qr LIMIT 1)")
    suspend fun doesReceiptWithQRExist(qr: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReceipts(vararg receipt: Receipt)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(vararg items: Item)

    @Transaction
    suspend fun insertReceiptInfo(qr: String, receiptInfo: ReceiptInfo) {
        val receipt = Receipt(qr, receiptInfo)
        val items = receiptInfo.items.mapIndexed { index, itemInfo -> Item(qr, itemInfo, index) }.toTypedArray()

        insertReceipts(receipt)
        insertItems(*items)
    }
}