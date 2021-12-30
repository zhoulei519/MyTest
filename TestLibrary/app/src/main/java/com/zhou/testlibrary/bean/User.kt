package com.zhou.testlibrary.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user")
class User : Serializable {
    @PrimaryKey
    var id = 0
    @ColumnInfo(name = "name")
    var name: String? = null
}