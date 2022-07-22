package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.filter.FilterOptions
import com.abrigo.itimalia.domain.entities.paging.PagingOptions

interface AnimalService {
    fun add(newAnimal: NewAnimalRequest, creatorId: Int): Animal
    fun get(id: Int): Animal
    fun update(id: Int, updatedAnimal: AnimalRequest): Animal
    fun delete(id: Int)
    fun getAll(filterOptions: FilterOptions = FilterOptions(), pagingOptions: PagingOptions = PagingOptions()): List<Animal>
    fun adopt(id: Int, adopterId: Int): Animal
    fun getByStatus(animalStatus: AnimalStatus): List<Animal>
    fun getBySpecie(specie: Specie): List<Animal>
    fun getByName(name: String): List<Animal>
}
