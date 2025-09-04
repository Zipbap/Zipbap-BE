package zipbap.app.domain.category.situation

import org.springframework.data.jpa.repository.JpaRepository

interface SituationRepository : JpaRepository<Situation, Long> {
    fun existsBySituation(situation: String):Boolean
}