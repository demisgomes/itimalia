package com.abrigo.itimalia.domain.repositories.factories

import com.abrigo.itimalia.domain.entities.AnimalDTO
import com.abrigo.itimalia.domain.entities.AnimalStatus
import com.abrigo.itimalia.domain.entities.NewAnimal
import com.abrigo.itimalia.domain.entities.Specie
import com.abrigo.itimalia.domain.entities.TimeUnit
import org.joda.time.DateTime

object AnimalFactory{
    fun sampleNew(name:String = "animal", age: Int? = 3, timeUnit: TimeUnit? = TimeUnit.MONTH, specie:Specie?=Specie.CAT, description:String="An animal that needs attention"):NewAnimal{
        return NewAnimal(
            name,
            age,
            timeUnit,
            specie,
            description
        )
    }
    fun sampleDTO(id:Int = 1, name:String = "animal", age: Int? = 3, timeUnit: TimeUnit? = TimeUnit.MONTH, creationDate: DateTime? = DateTime.now(), modificationDate: DateTime? = DateTime.now(), specie:Specie=Specie.CAT, status:AnimalStatus=AnimalStatus.AVAILABLE, description: String = "An animal that needs attention"): AnimalDTO {
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
}