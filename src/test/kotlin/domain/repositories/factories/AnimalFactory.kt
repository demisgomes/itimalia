package domain.repositories.factories

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.Specie
import domain.entities.TimeUnit
import org.joda.time.DateTime

object AnimalFactory{
    fun sample(name:String = "animal", age: Int? = 3, timeUnit: TimeUnit? = TimeUnit.MONTH, creationDate: DateTime? = DateTime.now(), modificationDate: DateTime? = DateTime.now(), specie:Specie=Specie.CAT, status:AnimalStatus=AnimalStatus.AVAILABLE): AnimalDTO {
        return AnimalDTO(
            name,
            age,
            timeUnit,
            specie,
            "An animal that needs attention",
            creationDate,
            modificationDate,
            status
        )
    }
}