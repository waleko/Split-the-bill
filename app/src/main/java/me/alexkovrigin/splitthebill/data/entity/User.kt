package me.alexkovrigin.splitthebill.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Users")
@Parcelize
data class User constructor (
    @PrimaryKey
    val userId: String,
    val displayName: String
) : Parcelable