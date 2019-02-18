package domain.repositories

import domain.entities.AnimalDTO

interface AnimalRepository {
    fun get(id:Int):AnimalDTO
}
