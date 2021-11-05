package com.abrigo.itimalia.domain.entities.animal

import com.abrigo.itimalia.domain.entities.user.UserPublicInfo
import org.joda.time.DateTime

data class Animal(
    val id: Int?,
    val name: String,
    val age: Int?,
    val timeUnit : TimeUnit?,
    val specie: Specie,
    val description: String,
    val creationDate: DateTime,
    val modificationDate: DateTime,
    val status: AnimalStatus,
    val deficiencies: List<AnimalDeficiency>,
    val sex: AnimalSex,
    val size: AnimalSize,
    val castrated: Boolean,
    val createdById: Int,
    val adopterUser : UserPublicInfo? = null
)
