package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.AnimalDTO
import com.abrigo.itimalia.domain.entities.AnimalStatus
import com.abrigo.itimalia.domain.entities.NewAnimal
import com.abrigo.itimalia.domain.entities.Specie

interface AnimalService {
    fun add(newAnimal: NewAnimal): AnimalDTO
    fun get(id:Int): AnimalDTO
    fun update(id: Int, updatedAnimalDTO: AnimalDTO): AnimalDTO
    fun delete(id: Int)
    fun getAll():List<AnimalDTO>
    fun adopt(id:Int): AnimalDTO
    fun getByStatus(animalStatus: AnimalStatus):List<AnimalDTO>
    fun getBySpecie(specie: Specie):List<AnimalDTO>
    fun getByName(name:String):List<AnimalDTO>

}
