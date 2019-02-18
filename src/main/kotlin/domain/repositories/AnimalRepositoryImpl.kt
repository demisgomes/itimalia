package domain.repositories

import domain.entities.AnimalDTO
import domain.exceptions.AnimalNotFoundException
import java.lang.IndexOutOfBoundsException
import java.util.ArrayList

class AnimalRepositoryImpl:AnimalRepository{
    companion object {
        val animalsList=ArrayList<AnimalDTO>()
    }
    override fun get(id: Int): AnimalDTO {
        try{
            return animalsList[id]
        }
        catch (exception:IndexOutOfBoundsException){
            throw AnimalNotFoundException()
        }

    }

    override fun add(newAnimal: AnimalDTO): AnimalDTO {
        animalsList.add(newAnimal)
        return newAnimal
    }

    override fun update(id: Int, animalDTO: AnimalDTO): AnimalDTO {
        animalsList[id]=animalDTO
        return animalDTO
    }

    override fun delete(id: Int): AnimalDTO {
        val animalToBeRemoved=get(id)
        animalsList.removeAt(id)
        return animalToBeRemoved
    }

}