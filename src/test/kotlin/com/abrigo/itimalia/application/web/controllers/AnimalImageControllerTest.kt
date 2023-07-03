package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.application.config.ItimaliaDotenv
import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.entities.image.ImageToBeUploaded
import com.abrigo.itimalia.domain.exceptions.ImageUploadException
import com.abrigo.itimalia.domain.services.AnimalService
import com.abrigo.itimalia.domain.services.ImageService
import com.abrigo.itimalia.factories.AnimalFactory
import io.javalin.http.Context
import io.javalin.http.UploadedFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File

class AnimalImageControllerTest {

    private val imageService = mockk<ImageService>(relaxed = true)
    private val animalService = mockk<AnimalService>(relaxed = true)
    private val contextMock = mockk<Context>(relaxed = true)
    private val environmentConfigMock = EnvironmentConfig(ItimaliaDotenv("variables.test.env").build())
    private val animalImageController = AnimalImageController(imageService, animalService, environmentConfigMock)

    @Test
    fun `should call imageService upload when have images in request`() {
        val sampleFile = File("./src/test/kotlin/samples/sample-image.png")
        val filename = "filename.png"
        val imageServiceResult = listOf(Image(filename, "path", "png", 12345))
        val animalId = 1

        every { contextMock.pathParam("id") } returns "1"
        every { animalService.get(animalId) } returns AnimalFactory.sampleDTO()
        every { contextMock.uploadedFiles("files") } returns listOf(UploadedFile(sampleFile.inputStream(), "png", filename, "png", 12345))
        every { imageService.add(listOf(ImageToBeUploaded(filename, sampleFile.readBytes())), animalId) } returns imageServiceResult
        animalImageController.addImage(contextMock)

        verify { imageService.add(listOf(ImageToBeUploaded(filename, sampleFile.readBytes())), animalId) }
        verify { contextMock.json(any<List<Image>>()) }
    }

    @Test(expected = ImageUploadException::class)
    fun `should throw an exception when do not images in request`() {
        every { contextMock.uploadedFiles("files") } returns emptyList()
        every { contextMock.pathParam("id") } returns "1"
        every { animalService.get(1) } returns AnimalFactory.sampleDTO()

        animalImageController.addImage(contextMock)
    }

    @Test(expected = ImageUploadException::class)
    fun `should throw an exception when one or more images surpass the max size`() {
        val content = ByteArrayInputStream("byteArray".encodeToByteArray())
        val filename = "filename.png"
        every { contextMock.uploadedFiles("files") } returns listOf(UploadedFile(content, "png", filename, "png", 12345678))

        every { contextMock.pathParam("id") } returns "1"
        every { animalService.get(1) } returns AnimalFactory.sampleDTO()
        animalImageController.addImage(contextMock)
    }

    @Test(expected = ImageUploadException::class)
    fun `should throw an exception when one or more files are not images`() {
        val content = ByteArrayInputStream("bytes".encodeToByteArray())
        val filename = "filename.png"
        val animalId = 1

        val imageServiceResult = listOf(Image(filename, "path", "png", 12345))

        every { contextMock.pathParam("id") } returns "1"
        every { animalService.get(animalId) } returns AnimalFactory.sampleDTO()
        every { contextMock.uploadedFiles("files") } returns listOf(UploadedFile(content, "png", filename, "png", 12345))
        every { imageService.add(listOf(ImageToBeUploaded(filename, content.readBytes())), animalId) } returns imageServiceResult
        animalImageController.addImage(contextMock)

        verify { imageService.add(listOf(ImageToBeUploaded(filename, content.readBytes())), animalId) }
        verify { contextMock.json(any<List<Image>>()) }
    }

    @Test(expected = ImageUploadException::class)
    fun `should throw an exception when the sum of images to be uploaded and current images from an animal were bigger than limit`() {
        val content = ByteArrayInputStream("bytes".encodeToByteArray())
        val filename = "filename.png"
        val animalId = 1

        every { contextMock.uploadedFiles("files") } returns
            listOf(
                UploadedFile(content, "png", filename, "png", 12345),
                UploadedFile(content, "png", filename, "png", 12345),
                UploadedFile(content, "png", filename, "png", 12345)
            )
        every { contextMock.pathParam("id") } returns "1"
        every { animalService.get(animalId) } returns AnimalFactory.sampleDTO()

        animalImageController.addImage(contextMock)
    }
}
