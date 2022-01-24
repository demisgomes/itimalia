package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.entities.image.ImageToBeUploaded
import com.abrigo.itimalia.domain.exceptions.ImageUploadException
import com.abrigo.itimalia.domain.services.ImageService
import io.javalin.http.Context
import io.javalin.http.UploadedFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Ignore
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File

@Ignore
class ImageControllerTest {

    private val imageService = mockk<ImageService>(relaxed = true)
    private val contextMock = mockk<Context>(relaxed = true)
    private val imageController = ImageController(imageService)

    @Test
    fun `should call imageService upload when have images in request`() {
        val sampleFile = File("./src/test/kotlin/samples/sample-image.png")
        val filename = "filename.png"
        val imageServiceResult = listOf(Image(filename, "path", "png", 12345))
        val animalId = 1

        every { contextMock.uploadedFiles("files") } returns listOf(UploadedFile(sampleFile.inputStream(), "png", 12345, filename, "png",  12345))
        every { imageService.add(listOf(ImageToBeUploaded(filename, sampleFile.readBytes())), animalId) } returns imageServiceResult
        imageController.addImage(contextMock)

        verify { imageService.add(listOf(ImageToBeUploaded(filename, sampleFile.readBytes())), animalId) }
        verify { contextMock.json(any<List<Image>>()) }
    }

    @Test(expected = ImageUploadException::class)
    fun `should throw an exception when do not images in request`() {
       every { contextMock.uploadedFiles("files") } returns emptyList()

       imageController.addImage(contextMock)
    }

    @Test(expected = ImageUploadException::class)
    fun `should throw an exception when one or more images surpass the max size`() {
        val content = ByteArrayInputStream("byteArray".encodeToByteArray())
        val filename = "filename.png"
        every { contextMock.uploadedFiles("files") } returns listOf(UploadedFile(content, "png", 12345678, filename, "png",  12345678))

        imageController.addImage(contextMock)
    }

    @Test(expected = ImageUploadException::class)
    fun `should throw an exception when one or more files are not images`() {
        val content = ByteArrayInputStream("bytes".encodeToByteArray())
        val filename = "filename.png"
        val animalId = 1

        val imageServiceResult = listOf(Image(filename, "path", "png", 12345))
        every { contextMock.uploadedFiles("files") } returns listOf(UploadedFile(content, "png", 12345, filename, "png",  12345))
        every { imageService.add(listOf(ImageToBeUploaded(filename, content.readBytes())), animalId) } returns imageServiceResult
        imageController.addImage(contextMock)

        verify { imageService.add(listOf(ImageToBeUploaded(filename, content.readBytes())), animalId) }
        verify { contextMock.json(any<List<Image>>()) }
    }
}