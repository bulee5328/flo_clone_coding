package com.example.flo.data.remote

import com.example.flo.data.entities.Album

interface SendDataInterface {
    fun sendData(album: Album)
}