package domain.repositories.factories

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.Specie
import domain.entities.TimeUnit
import org.joda.time.DateTime

object AnimalFactory{
    fun sample(name:String = "animal", age: Int? = 3, timeUnit: TimeUnit? = TimeUnit.MONTH): AnimalDTO {
        return AnimalDTO(
            name,
            age,
            timeUnit,
            Specie.CAT,
            "An animal that needs attention",
            DateTime.now(),
            DateTime.now(),
            AnimalStatus.AVAILABLE
        )
    }
}