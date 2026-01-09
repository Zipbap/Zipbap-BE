package zipbap.global.domain.recipe

data class RecipeSearchCondition(
        val userId: Long? = null,
        val status: RecipeStatus? = null,
        val myCategoryIds: Collection<String>? = null,
        val isPrivate: Boolean? = null
)
