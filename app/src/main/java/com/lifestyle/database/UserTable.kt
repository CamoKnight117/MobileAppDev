package com.lifestyle.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user_table")
data class UserTable(
    @field:ColumnInfo(name = "coordinate")
    @field:PrimaryKey
    var uuid : UUID,
    @field:ColumnInfo(name = "userdata")
    var userJson: String
)
