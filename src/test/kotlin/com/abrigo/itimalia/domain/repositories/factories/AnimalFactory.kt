package com.abrigo.itimalia.domain.repositories.factories

import com.abrigo.itimalia.domain.entities.animal.AnimalDTO
import com.abrigo.itimalia.domain.entities.animal.AnimalDTORequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimal
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import org.joda.time.DateTime

object AnimalFactory{
    fun sampleNew(name:String = "animal", age: Int? = 3, timeUnit: TimeUnit? = TimeUnit.MONTH, specie: Specie?= Specie.CAT, description:String="An animal that needs attention"): NewAnimal {
        return NewAnimal(
            name,
            age,
            timeUnit,
            specie,
            description
        )
    }

    fun sampleNewRequest(name:String = "animal", age: Int? = 3, timeUnit: TimeUnit? = TimeUnit.MONTH, specie: Specie?= Specie.CAT, description:String="An animal that needs attention"): NewAnimalRequest {
        return NewAnimalRequest(
            name,
            age,
            timeUnit,
            specie,
            description
        )
    }

    fun sampleDTO(id:Int = 1, name:String = "animal", age: Int? = 3, timeUnit: TimeUnit? = TimeUnit.MONTH, creationDate: DateTime? = DateTime.now(), modificationDate: DateTime? = DateTime.now(), specie: Specie = Specie.CAT, status: AnimalStatus = AnimalStatus.AVAILABLE, description: String = "An animal that needs attention"): AnimalDTO {
        return AnimalDTO(
            id,
            name,
            age,
            timeUnit,
            specie,
            description,
            creationDate,
            modificationDate,
            status
        )
    }

    fun sampleDTORequest(id:Int = 1, name:String = "animal", age: Int? = 3, timeUnit: TimeUnit? = TimeUnit.MONTH, creationDate: DateTime? = DateTime.now(), modificationDate: DateTime? = DateTime.now(), specie: Specie = Specie.CAT, status: AnimalStatus = AnimalStatus.AVAILABLE, description: String = "An animal that needs attention"): AnimalDTORequest {
        return AnimalDTORequest(
            id,
            name,
            age,
            timeUnit,
            specie,
            description,
            creationDate,
            modificationDate,
            status
        )
    }
}