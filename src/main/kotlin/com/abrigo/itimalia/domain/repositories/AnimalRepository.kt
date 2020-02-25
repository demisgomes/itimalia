package com.abrigo.itimalia.domain.repositories

import com.abrigo.itimalia.domain.entities.animal.AnimalDTO

interface AnimalRepository {
    fun get(id:Int): AnimalDTO
    fun add(newAnimal: AnimalDTO): AnimalDTO
    fun update(id:Int,animalDTO: AnimalDTO): AnimalDTO
    fun delete(id: Int)
    fun getAll():List<AnimalDTO>
}
