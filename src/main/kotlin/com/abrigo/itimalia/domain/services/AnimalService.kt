package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.animal.AnimalDTO
import com.abrigo.itimalia.domain.entities.animal.AnimalDTORequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie

interface AnimalService {
    fun add(newAnimal: NewAnimalRequest): AnimalDTO
    fun get(id:Int): AnimalDTO
    fun update(id: Int, updatedAnimalDTO: AnimalDTORequest): AnimalDTO
    fun delete(id: Int)
    fun getAll():List<AnimalDTO>
    fun adopt(id:Int): AnimalDTO
    fun getByStatus(animalStatus: AnimalStatus):List<AnimalDTO>
    fun getBySpecie(specie: Specie):List<AnimalDTO>
    fun getByName(name:String):List<AnimalDTO>

}
