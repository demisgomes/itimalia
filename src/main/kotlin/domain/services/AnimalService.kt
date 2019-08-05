package domain.services

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.entities.Specie

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
