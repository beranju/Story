package com.nextgen.mystoryapp.data.story.mediator

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
RemoteKeys berfungsi untuk menyimpan informasi tentang halaman terbaru yang diminta server
 */

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
)