package resources.images.cloudinary.gateways

import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.entities.image.ImageToBeUploaded
import com.abrigo.itimalia.domain.exceptions.ImageUploadException
import com.abrigo.itimalia.domain.services.ImageService
import com.abrigo.itimalia.resources.images.cloudinary.gateways.CloudinaryService
import com.cloudinary.Cloudinary
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.lang.RuntimeException
import kotlin.test.assertTrue

class CloudinaryServiceTest {
    private val cloudinaryConfig = mockk<Cloudinary>()
    private val cloudinaryService : ImageService = CloudinaryService(cloudinaryConfig)

    @Test
    fun `given a valid list of byteArrays, should return a list of images`(){
        val imageToBeUploaded = ImageToBeUploaded("test", "test".encodeToByteArray())
        val imageBytes = 1232434L
        val imageFormat = "png"
        every { cloudinaryConfig.uploader().upload(imageToBeUploaded.byteArray, any()) }returns mapOf("public_id" to imageToBeUploaded.fileName, "format" to imageFormat, "bytes" to "1232434")
        every { cloudinaryConfig.url().transformation(any()).generate(any()) } returns "http://res.cloudinary.com/q_60/idimage"

        val imagesList = cloudinaryService.add(listOf(imageToBeUploaded))

        assertTrue(imagesList.contains(Image(imageToBeUploaded.fileName,"http://res.cloudinary.com/q_60/idimage", imageFormat, imageBytes)))
    }

    @Test(expected = ImageUploadException::class)
    fun `given an error on upload, should return an ImageUploadError`(){
        val imageToBeUploaded = ImageToBeUploaded("test", "test".encodeToByteArray())
        every { cloudinaryConfig.uploader().upload(imageToBeUploaded.byteArray, any()) } throws (RuntimeException())

        cloudinaryService.add(listOf(imageToBeUploaded))
    }
}