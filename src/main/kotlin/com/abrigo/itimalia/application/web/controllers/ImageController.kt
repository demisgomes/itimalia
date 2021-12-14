package com.abrigo.itimalia.application.web.controllers

import com.cloudinary.Cloudinary
import com.cloudinary.EagerTransformation
import io.javalin.http.Context
import io.javalin.http.UploadedFile
import java.io.File
import java.io.FileOutputStream
import com.cloudinary.Transformation

class ImageController {
    fun addImage(context: Context) {
        val cloudinary = Cloudinary(
            mapOf(
                "cloud_name" to "",
                "api_key" to  "",
                "api_secret" to ""
            )
        )

       val upload = cloudinary.uploader().upload(convertToFile(context.uploadedFiles("files").first()),
            emptyMap<String,String>())

        val transf = cloudinary.url().transformation(Transformation<EagerTransformation>().quality(60)).generate(upload["public_id"].toString()+"."+upload["format"])

        println(transf)
    }

    private fun convertToFile(uploadedFile: UploadedFile): File {
        val convFile = File(uploadedFile.filename)
        val fos = FileOutputStream(convFile)
        fos.write(uploadedFile.content.readBytes())
        fos.close()
        return convFile
    }
}