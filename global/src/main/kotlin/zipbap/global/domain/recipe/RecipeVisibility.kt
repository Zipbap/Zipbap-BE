package zipbap.global.domain.recipe

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import zipbap.global.domain.follow.QFollow
import zipbap.global.domain.user.QUser

/** Shared by feed, detail, interactions and saved cards. Drafts stay in owner-only APIs. */
object RecipeVisibility {
    fun visibleTo(viewerId: Long?, recipe: QRecipe = QRecipe.recipe, author: QUser = recipe.user): BooleanExpression {
        val owner = viewerId?.let { author.id.eq(it) } ?: Expressions.FALSE
        val follow = QFollow("visibilityFollow")
        val following = viewerId?.let {
            JPAExpressions.selectOne().from(follow)
                .where(follow.follower.id.eq(it), follow.following.id.eq(author.id)).exists()
        } ?: Expressions.FALSE
        return recipe.recipeStatus.eq(RecipeStatus.ACTIVE).and(recipe.deletedAt.isNull)
            .and(author.deletedAt.isNull)
            .and(owner.or(recipe.isPrivate.isFalse.and(author.isPrivate.isFalse.or(following))))
    }
}
