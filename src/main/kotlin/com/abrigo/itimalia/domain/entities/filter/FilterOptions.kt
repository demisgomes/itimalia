package com.abrigo.itimalia.domain.entities.filter

import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.Specie

data class FilterOptions(
    val name: String? = null,
    val specie: Specie? = null,
    val status: AnimalStatus? = null,
    val sex: AnimalSex? = null,
    val size: AnimalSize? = null,
    val castrated: Boolean? = null
)
