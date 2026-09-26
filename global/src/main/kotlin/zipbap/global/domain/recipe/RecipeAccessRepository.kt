package zipbap.global.domain.recipe

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import zipbap.global.domain.bookmark.QBookmark
import zipbap.global.domain.user.QUser
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Repository
class RecipeAccessRepository(private val query: JPAQueryFactory) {
    fun requireVisible(recipeId: String, viewerId: Long): Recipe {
        val r = QRecipe.recipe
        val author = QUser.user
        return query.selectFrom(r).join(r.user, author).fetchJoin()
            .where(r.id.eq(recipeId), RecipeVisibility.visibleTo(viewerId, r, author)).fetchOne()
            ?: throw GeneralException(ErrorStatus.RECIPE_NOT_FOUND)
    }

    fun findVisibleBookmarks(viewerId: Long): List<Recipe> {
        val r = QRecipe.recipe
        val b = QBookmark.bookmark
        val author = QUser.user
        return query.select(r).from(b).join(b.recipe, r).join(r.user, author)
            .where(b.user.id.eq(viewerId), b.deletedAt.isNull, RecipeVisibility.visibleTo(viewerId, r, author))
            .fetch()
    }
}
