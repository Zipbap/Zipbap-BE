package zipbap.global.domain.recipe

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class RecipeQueryRepositoryImpl(
        private val queryFactory: JPAQueryFactory
) : RecipeQueryRepository {

    private val recipe = QRecipe.recipe

    // Condition에 따라 인덱스 안 탈 수도 있음
    override fun findRecipes(condition: RecipeSearchCondition): List<Recipe> {
        return queryFactory
                .selectFrom(recipe)
                .leftJoin(recipe.myCategory).fetchJoin()
                .leftJoin(recipe.cookingTime).fetchJoin()
                .where(
                        userIdEq(condition.userId),
                        statusEq(condition.status),
                        myCategoryIdsIn(condition.myCategoryIds),
                        isPrivateEq(condition.isPrivate)
                )
                .fetch()
    }

    override fun findLatestByUserIdOrderByCreatedAtDesc(userId: Long): Recipe? {
        return queryFactory
                .selectFrom(recipe)
                .where(userIdEq(userId))
                .orderBy(recipe.createdAt.desc())
                .limit(1)
                .fetchOne()
    }

    override fun findAllByMyCategoryIds(myCategoryIds: Collection<String>): List<Recipe> {
        return queryFactory
                .selectFrom(recipe)
                .where(myCategoryIdsIn(myCategoryIds))
                .fetch()
    }

    override fun findRecipeDetail(recipeId: String): Recipe? {
        return queryFactory
                .selectFrom(recipe)
                .join(recipe.user).fetchJoin()
                .leftJoin(recipe.myCategory).fetchJoin()
                .leftJoin(recipe.cookingType).fetchJoin()
                .leftJoin(recipe.situation).fetchJoin()
                .leftJoin(recipe.mainIngredient).fetchJoin()
                .leftJoin(recipe.method).fetchJoin()
                .leftJoin(recipe.headcount).fetchJoin()
                .leftJoin(recipe.cookingTime).fetchJoin()
                .leftJoin(recipe.level).fetchJoin()
                .where(recipe.id.eq(recipeId))
                .fetchOne()
    }

    // Dynamic Query Conditions
    private fun userIdEq(userId: Long?): BooleanExpression? {
        return userId?.let { recipe.user.id.eq(it) }
    }

    private fun statusEq(status: RecipeStatus?): BooleanExpression? {
        return status?.let { recipe.recipeStatus.eq(it) }
    }

    private fun myCategoryIdsIn(myCategoryIds: Collection<String>?): BooleanExpression? {
        return myCategoryIds?.takeIf { it.isNotEmpty() }?.let {
            recipe.myCategory.id.`in`(it)
        }
    }

    private fun isPrivateEq(isPrivate: Boolean?): BooleanExpression? {
        return isPrivate?.let { recipe.isPrivate.eq(it) }
    }
}