package zipbap.global.domain.category.situation

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.category.situation.Situation

interface SituationRepository : JpaRepository<Situation, Long> {
    fun existsBySituation(situation: String):Boolean
}