package zipbap.global.security

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import support.annotation.RepositoryTest
import support.fixture.RecipeFixture
import support.fixture.UserFixture
import zipbap.global.domain.bookmark.Bookmark
import zipbap.global.domain.category.cookingtype.CookingType
import zipbap.global.domain.category.cookingtime.CookingTime
import zipbap.global.domain.category.headcount.Headcount
import zipbap.global.domain.category.level.Level
import zipbap.global.domain.category.mainingredient.MainIngredient
import zipbap.global.domain.category.method.Method
import zipbap.global.domain.category.situation.Situation
import zipbap.global.domain.feed.FeedFilterType
import zipbap.global.domain.feed.FeedQueryRepositoryImpl
import zipbap.global.domain.follow.Follow
import zipbap.global.domain.mypage.MyPageQueryRepository
import zipbap.global.domain.recipe.*
import zipbap.global.global.exception.GeneralException

@RepositoryTest
@Import(FeedQueryRepositoryImpl::class, MyPageQueryRepository::class, RecipeAccessRepository::class)
class RecipeVisibilityTest @Autowired constructor(
    private val em: EntityManager,
    private val feed: FeedQueryRepositoryImpl,
    private val cards: MyPageQueryRepository,
    private val access: RecipeAccessRepository
) {
    @ParameterizedTest(name = "authorPrivate={0}, recipePrivate={1}, status={2}, viewer={3}")
    @MethodSource("visibilityCases")
    fun `all read paths enforce the same visibility`(authorPrivate: Boolean, recipePrivate: Boolean, status: RecipeStatus, relation: String) {
        val author = UserFixture.create(isPrivate = authorPrivate).also(em::persist)
        val viewer = if (relation == "owner") author else UserFixture.create().also(em::persist)
        if (relation == "follower") em.persist(Follow(author, viewer))
        val recipe = RecipeFixture.create(author, id = "RC-visibility", isPrivate = recipePrivate, recipeStatus = status,
            cookingType = CookingType("type").also(em::persist), situation = Situation("situation").also(em::persist),
            mainIngredient = MainIngredient("ingredient").also(em::persist), method = Method("method").also(em::persist),
            headcount = Headcount("1").also(em::persist), cookingTime = CookingTime("10").also(em::persist),
            level = Level("easy").also(em::persist)).also(em::persist)
        em.persist(Bookmark(viewer, recipe, "BM-visibility"))
        em.flush()

        val expected = status == RecipeStatus.ACTIVE &&
            (relation == "owner" || (!recipePrivate && (!authorPrivate || relation == "follower")))
        val page = PageRequest.of(0, 20)
        assertThat(feed.findFeedDetail(viewer, recipe.id) != null).isEqualTo(expected)
        assertThat(feed.findFeed(viewer, FeedFilterType.ALL, page, null).totalElements).isEqualTo(if (expected) 1L else 0L)
        assertThat(cards.loadFeedCards(author.id!!, viewer.id!!, page).totalElements).isEqualTo(if (expected) 1L else 0L)
        assertThat(cards.loadBookmarkCards(viewer.id!!, page).totalElements).isEqualTo(if (expected) 1L else 0L)
        assertThat(access.findVisibleBookmarks(viewer.id!!)).hasSize(if (expected) 1 else 0)
        if (expected) assertThat(access.requireVisible(recipe.id, viewer.id!!).id).isEqualTo(recipe.id)
        else assertThatThrownBy { access.requireVisible(recipe.id, viewer.id!!) }.isInstanceOf(GeneralException::class.java)
    }

    companion object {
        @JvmStatic fun visibilityCases(): List<Arguments> = listOf(false, true).flatMap { author ->
            listOf(false, true).flatMap { recipe ->
                RecipeStatus.entries.flatMap { status ->
                    listOf("owner", "follower", "stranger").map { Arguments.of(author, recipe, status, it) }
                }
            }
        }
    }
}
