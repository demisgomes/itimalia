package com.abrigo.itimalia.domain.services

interface ImageService {
    fun add(imageFiles: List<ByteArray>) : List<String>
}