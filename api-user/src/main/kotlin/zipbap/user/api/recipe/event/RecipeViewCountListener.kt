package zipbap.user.api.recipe.event

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import zipbap.global.domain.recipe.RecipeRepository

@Component
class RecipeViewCountListener(
        private val recipeRepository: RecipeRepository
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun onRecipeViewed(event: RecipeViewedEvent) {
        recipeRepository.updateViewCount(event.recipeId)
    }
}