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
    @field:ColumnInfo(name = "name")
    var name: String,
    @field:ColumnInfo(name = "age")
    var age: Int,
    @field:ColumnInfo(name = "height")
    var height: Float,
    @field:ColumnInfo(name = "weight")
    var weight: Float,
    @field:ColumnInfo(name = "sex")
    var sex: Sex,
)
