package domain.entities

import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime

data class AnimalDTO(
    val id: Int?,
    val name: String,
    val age: Int?,
    val timeUnit : TimeUnit?,
    val specie: Specie?,
    val description: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val creationDate: DateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val modificationDate: DateTime?,
    val status: AnimalStatus)
