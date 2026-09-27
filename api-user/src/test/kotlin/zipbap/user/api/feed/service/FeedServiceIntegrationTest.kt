package zipbap.user.api.feed.service

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.SessionFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestPropertySource
import support.annotation.IntegrationTest
import support.fixture.RecipeFixture
import support.fixture.UserFixture
import zipbap.global.domain.bookmark.Bookmark
import zipbap.global.domain.bookmark.BookmarkRepository
import zipbap.global.domain.category.cookingtime.CookingTime
import zipbap.global.domain.category.level.Level
import zipbap.global.domain.comment.Comment
import zipbap.global.domain.feed.FeedFilterType
import zipbap.global.domain.feed.FeedQueryRepositoryImpl
import zipbap.global.domain.follow.Follow
import zipbap.global.domain.like.RecipeLike
import zipbap.global.domain.like.RecipeLikeRepository
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.user.UserRepository
import zipbap.user.api.feed.converter.FeedConverter
import java.time.LocalDate
import java.time.ZoneId

@IntegrationTest
@Import(FeedService::class, FeedQueryRepositoryImpl::class)
@TestPropertySource(properties = [
    "spring.jpa.properties.hibernate.generate_statistics=true",
    "logging.level.org.hibernate.stat=OFF",
    "logging.level.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=OFF"
])
class FeedServiceIntegrationTest @Autowired constructor(
    private val feedService: FeedService,
    private val feedQueryRepository: FeedQueryRepositoryImpl,
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
    private val recipeLikeRepository: RecipeLikeRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val em: EntityManager,
    private val jdbc: JdbcTemplate
) {
    private var viewerId = 0L
    private var authorId = 0L
    private val statistics get() = em.entityManagerFactory.unwrap(SessionFactory::class.java).statistics

    @BeforeEach
    fun setUp() {
        val author = UserFixture.create().also(em::persist)
        val viewer = UserFixture.create().also(em::persist)
        val other = UserFixture.create().also(em::persist)
        authorId = author.id!!
        viewerId = viewer.id!!
        em.persist(Follow(following = author, follower = viewer))
        val cookingTime = CookingTime("10").also(em::persist)
        val level = Level("easy").also(em::persist)
        for (index in 1..52) {
            val recipe = RecipeFixture.create(
                user = author,
                id = recipeId(index),
                title = "B-feed-recipe-$index",
                cookingTime = cookingTime,
                level = level,
                viewCount = index.toLong()
            ).also(em::persist)
            // Counts include another user's actions even when the viewer's flags are false.
            em.persist(RecipeLike(user = other, recipe = recipe))
            em.persist(Bookmark(user = other, recipe = recipe, id = "BM-other-$index"))
            if (index % 2 == 0) em.persist(RecipeLike(user = viewer, recipe = recipe))
            if (index % 3 == 0) em.persist(Bookmark(user = viewer, recipe = recipe, id = "BM-viewer-$index"))
            repeat(2) { em.persist(Comment(user = other, recipe = recipe, content = "comment-$it")) }
        }
        em.flush()
        // Distinct timestamps prevent ranking ties and keep TODAY fixtures on the KST date.
        val today = LocalDate.now(ZoneId.of("Asia/Seoul")).atTime(12, 0)
        for (index in 1..52) {
            jdbc.update("UPDATE recipe SET created_at = ? WHERE id = ?", today.minusMinutes(index.toLong()), recipeId(index))
        }
        resetMeasurement()
    }

    @ParameterizedTest(name = "size={0}: SQL stays at 5 and personalization remains correct")
    @ValueSource(ints = [10, 20, 30])
    fun `page size does not increase query count`(size: Int) {
        val page = feedService.getFeedList(viewerId, FeedFilterType.ALL, PageRequest.of(0, size), null)

        assertThat(statistics.prepareStatementCount).isEqualTo(5L)
        // Scalar projections must not materialize Recipe, RecipeLike, or Bookmark entities.
        assertThat(statistics.entityLoadCount).isEqualTo(1L)
        assertThat(page.totalElements).isEqualTo(52L)
        assertThat(page.content.map { it.recipeId }).containsExactlyElementsOf((1..size).map(::recipeId))
        page.content.forEachIndexed { offset, item ->
            val index = offset + 1
            assertThat(item.isLiked).isEqualTo(index % 2 == 0)
            assertThat(item.isBookmarked).isEqualTo(index % 3 == 0)
            assertThat(item.likeCount).isEqualTo(if (index % 2 == 0) 2L else 1L)
            assertThat(item.bookmarkCount).isEqualTo(if (index % 3 == 0) 2L else 1L)
            assertThat(item.commentCount).isEqualTo(2L)
            assertThat(item.viewCount).isEqualTo(index.toLong())
        }
    }

    @ParameterizedTest
    @EnumSource(FeedFilterType::class)
    fun `all filters preserve the previous response including ranking and pagination`(filter: FeedFilterType) {
        val pageable = PageRequest.of(1, 10)
        val expected = previousFeedList(filter, pageable, null)
        assertThat(expected.content).hasSize(10)
        assertThat(statistics.prepareStatementCount).isEqualTo(33L)
        resetMeasurement()

        val actual = feedService.getFeedList(viewerId, filter, pageable, null)

        assertThat(statistics.prepareStatementCount).isEqualTo(5L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `search results preserve all response fields`() {
        val pageable = PageRequest.of(0, 20)
        val expected = previousFeedList(FeedFilterType.ALL, pageable, "recipe-1")
        assertThat(expected.totalElements).isEqualTo(11L)
        resetMeasurement()

        val actual = feedService.getFeedList(viewerId, FeedFilterType.ALL, pageable, "recipe-1")

        assertThat(statistics.prepareStatementCount).isEqualTo(5L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `another user's actions do not leak into viewer flags`() {
        val page = feedService.getFeedList(authorId, FeedFilterType.ALL, PageRequest.of(0, 20), null)

        assertThat(statistics.prepareStatementCount).isEqualTo(5L)
        assertThat(page.content).hasSize(20)
        assertThat(page.content).allSatisfy { item ->
            assertThat(item.isLiked).isFalse()
            assertThat(item.isBookmarked).isFalse()
            assertThat(item.likeCount).isPositive()
            assertThat(item.bookmarkCount).isPositive()
        }
    }

    @Test
    fun `last partial page uses five queries and preserves total`() {
        val page = feedService.getFeedList(viewerId, FeedFilterType.ALL, PageRequest.of(5, 10), null)

        assertThat(statistics.prepareStatementCount).isEqualTo(5L)
        assertThat(page.content.map { it.recipeId }).containsExactly(recipeId(51), recipeId(52))
        assertThat(page.content.map { it.isLiked }).containsExactly(false, true)
        assertThat(page.content.map { it.isBookmarked }).containsExactly(true, false)
        assertThat(page.totalElements).isEqualTo(52L)
        assertThat(page.totalPages).isEqualTo(6)
        assertThat(page.isLast).isTrue()
    }

    @Test
    fun `out of range page skips bulk queries without losing total`() {
        val page = feedService.getFeedList(viewerId, FeedFilterType.ALL, PageRequest.of(6, 10), null)

        assertThat(statistics.prepareStatementCount).isEqualTo(3L)
        assertThat(page.content).isEmpty()
        assertThat(page.totalElements).isEqualTo(52L)
        assertThat(page.totalPages).isEqualTo(6)
        assertThat(page.number).isEqualTo(6)
    }

    @Test
    fun `no matches skip bulk queries`() {
        val page = feedService.getFeedList(viewerId, FeedFilterType.ALL, PageRequest.of(0, 20), "no-match")

        assertThat(statistics.prepareStatementCount).isEqualTo(3L)
        assertThat(page.content).isEmpty()
        assertThat(page.totalElements).isZero()
    }

    @Test
    fun `bulk repositories return only requested IDs without loading entities`() {
        val ids = listOf(recipeId(1), recipeId(2), recipeId(3), "RC-missing")

        assertThat(recipeLikeRepository.findRecipeIdsByUserIdAndRecipeIdIn(viewerId, ids))
            .containsExactly(recipeId(2))
        assertThat(bookmarkRepository.findRecipeIdsByUserIdAndRecipeIdIn(viewerId, ids))
            .containsExactly(recipeId(3))
        assertThat(statistics.prepareStatementCount).isEqualTo(2L)
        assertThat(statistics.entityLoadCount).isZero()
    }

    // A's per-item lookup path is retained only as a regression oracle on the same database.
    private fun previousFeedList(filter: FeedFilterType, pageable: PageRequest, keyword: String?) =
        userRepository.findById(viewerId).orElseThrow().let { user ->
            val page = feedQueryRepository.findFeed(user, filter, pageable, keyword)
            val content = page.content.map { row ->
                val recipe = recipeRepository.findById(row.recipeId!!).orElseThrow()
                row.isLiked = recipeLikeRepository.existsByUserAndRecipe(user, recipe)
                row.isBookmarked = bookmarkRepository.existsByUserAndRecipe(user, recipe)
                FeedConverter.toFeedItemDto(row)
            }
            PageImpl(content, pageable, page.totalElements)
        }

    private fun resetMeasurement() {
        em.clear()
        statistics.clear()
    }

    private fun recipeId(index: Int) = "RC-feed-B-$index"
}
