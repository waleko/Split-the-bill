package me.alexkovrigin.splitthebill.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Entity representing existing users, that have payed for items on the receipt.
 */
@Entity(tableName = "Users")
@Parcelize
data class User(
    @PrimaryKey
    val userId: String,
    val displayName: String
) : Parcelable