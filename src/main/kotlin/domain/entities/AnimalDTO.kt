package domain.entities

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class AnimalDTO(
    val name: String,
    val age: Int?,
    val timeUnit : TimeUnit?,
    val specie: Specie?,
    val description: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val creationDate: Date?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val modificationDate: Date?,
    val status: AnimalStatus)
