package com.abrigo.itimalia.domain.repositories

import com.abrigo.itimalia.domain.entities.animal.Animal

interface AnimalRepository {
    fun get(id: Int): Animal
    fun add(newAnimal: Animal): Animal
    fun update(id: Int, animal: Animal): Animal
    fun delete(id: Int)
    fun getAll(): List<Animal>
    fun adopt(animal: Animal, adopterId: Int): Animal
}
