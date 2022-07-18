package com.abrigo.itimalia.domain.repositories

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.filter.FilterOptions

interface AnimalRepository {
    fun get(id: Int): Animal
    fun add(newAnimal: Animal): Animal
    fun update(id: Int, animal: Animal): Animal
    fun delete(id: Int)
    fun getAll(filterOptions: FilterOptions = FilterOptions()): List<Animal>
    fun adopt(animal: Animal, adopterId: Int): Animal
}
