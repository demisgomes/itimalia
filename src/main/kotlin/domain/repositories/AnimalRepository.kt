package domain.repositories

import domain.entities.AnimalDTO

interface AnimalRepository {
    fun get(id:Int):AnimalDTO
    fun add(newAnimal: AnimalDTO): AnimalDTO
    fun update(id:Int,animalDTO: AnimalDTO): AnimalDTO
    fun delete(id: Int): AnimalDTO
    fun getAll():List<AnimalDTO>
}
