package zipbap.user.api.recipe.event

data class RecipeViewedEvent(
        val recipeId: String,
        val viewerUserId: Long
)
