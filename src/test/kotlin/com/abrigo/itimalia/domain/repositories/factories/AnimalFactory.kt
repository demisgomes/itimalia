package com.abrigo.itimalia.domain.repositories.factories

import com.abrigo.itimalia.domain.entities.animal.AnimalDTO
import com.abrigo.itimalia.domain.entities.animal.AnimalDTORequest
import com.abrigo.itimalia.domain.entities.animal.AnimalDeficiency
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimal
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import org.joda.time.DateTime

object AnimalFactory {
    fun sampleNew(
        name: String = "animal",
        age: Int? = 3,
        timeUnit: TimeUnit? = TimeUnit.MONTH,
        specie: Specie? = Specie.CAT,
        description: String = "An animal that needs attention",
        deficiencies: List<AnimalDeficiency> = emptyList(),
        sex: AnimalSex = AnimalSex.FEMALE,
        size: AnimalSize = AnimalSize.MEDIUM,
        castrated: Boolean = true
    ): NewAnimal {
        return NewAnimal(
            name,
            age,
            timeUnit,
            specie,
            description,
            deficiencies,
            sex,
            size,
            castrated
        )
    }

    fun sampleNewRequest(
        name: String = "animal",
        age: Int? = 3,
        timeUnit: TimeUnit? = TimeUnit.MONTH,
        specie: Specie? = Specie.CAT,
        description: String = "An animal that needs attention",
        deficiencies: List<AnimalDeficiency> = emptyList(),
        sex: AnimalSex = AnimalSex.FEMALE,
        size: AnimalSize = AnimalSize.MEDIUM,
        castrated: Boolean = true
    ): NewAnimalRequest {
        return NewAnimalRequest(
            name,
            age,
            timeUnit,
            specie,
            description,
            deficiencies,
            sex,
            size,
            castrated
        )
    }

    fun sampleDTO(
        id: Int = 1,
        name: String = "animal",
        age: Int? = 3,
        timeUnit: TimeUnit? = TimeUnit.MONTH,
        creationDate: DateTime? = DateTime.now(),
        modificationDate: DateTime? = DateTime.now(),
        specie: Specie = Specie.CAT,
        status: AnimalStatus = AnimalStatus.AVAILABLE,
        description: String = "An animal that needs attention",
        deficiencies: List<AnimalDeficiency> = emptyList(),
        sex: AnimalSex = AnimalSex.FEMALE,
        size: AnimalSize = AnimalSize.MEDIUM,
        castrated: Boolean = true,
        createdById: Int = 1
    ): AnimalDTO {
        return AnimalDTO(
            id,
            name,
            age,
            timeUnit,
            specie,
            description,
            creationDate,
            modificationDate,
            status,
            deficiencies,
            sex,
            size,
            castrated,
            createdById
        )
    }

    fun sampleDTORequest(
        id: Int = 1,
        name: String = "animal",
        age: Int? = 3,
        timeUnit: TimeUnit? = TimeUnit.MONTH,
        creationDate: DateTime? = DateTime.now(),
        modificationDate: DateTime? = DateTime.now(),
        specie: Specie = Specie.CAT,
        status: AnimalStatus = AnimalStatus.AVAILABLE,
        description: String = "An animal that needs attention",
        deficiencies: List<AnimalDeficiency> = emptyList(),
        sex: AnimalSex = AnimalSex.FEMALE,
        size: AnimalSize = AnimalSize.MEDIUM,
        castrated: Boolean = true,
        createdById: Int = 1
    ): AnimalDTORequest {
        return AnimalDTORequest(
            id,
            name,
            age,
            timeUnit,
            specie,
            description,
            creationDate,
            modificationDate,
            status,
            deficiencies,
            sex,
            size,
            castrated,
            createdById
        )
    }
}