package com.lifestyle.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lifestyle.user.Sex
import java.util.*

@Entity(tableName = "user_table")
data class UserTable(
    @field:ColumnInfo(name = "uuid")
    @field:PrimaryKey
    var uuid : UUID,
    @field:ColumnInfo(name = "userJson")
    var userJson: String,
    @field:ColumnInfo(name = "profilePic", typeAffinity = ColumnInfo.BLOB)
    private byte[] profilePic;
)
