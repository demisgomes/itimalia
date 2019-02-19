package domain.repositories

import domain.entities.AnimalDTO
import domain.exceptions.AnimalNotFoundException
import java.lang.NullPointerException

class AnimalRepositoryImpl:AnimalRepository{
    override fun getAll(): List<AnimalDTO> {
        return animalsList.values.toList()
    }

    companion object {
        val animalsList=HashMap<Int, AnimalDTO>()
    }
    override fun get(id: Int): AnimalDTO {
        try{
            return animalsList[id]!!
        }
        catch (exception:NullPointerException){
            throw AnimalNotFoundException()
        }

    }

    override fun add(newAnimal: AnimalDTO): AnimalDTO {
        animalsList[animalsList.size+1] = newAnimal
        return newAnimal
    }

    override fun update(id: Int, animalDTO: AnimalDTO): AnimalDTO {
        animalsList[id]=animalDTO
        return animalDTO
    }

    override fun delete(id: Int): AnimalDTO {
        val animalToBeRemoved=get(id)
        animalsList.remove(id)
        return animalToBeRemoved
    }

}