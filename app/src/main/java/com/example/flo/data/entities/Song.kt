package com.example.flo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SongTable")
data class Song(
    val title : String = "",
    val singer : String = "",
    var second : Int = 0, //재생 시간
    var playTime : Int = 0, //정한 재생 시간
    var isPlaying : Boolean = false,
    var music: String = "", //재생할 MP3 파일의 이름
    var albumImg : Int? = null,
    var isLike: Boolean = false,
    val albumIdx:Int=0
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
