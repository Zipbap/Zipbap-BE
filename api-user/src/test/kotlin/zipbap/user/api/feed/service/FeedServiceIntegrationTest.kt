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
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestPropertySource
import support.FeedTestClockConfiguration
import support.query.LegacyFeedListQuery
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
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@IntegrationTest
@Import(FeedService::class, FeedQueryRepositoryImpl::class, FeedTestClockConfiguration::class)
@TestPropertySource(properties = [
    "spring.jpa.properties.hibernate.generate_statistics=true",
    "logging.level.org.hibernate.stat=OFF",
    "logging.level.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=OFF"
])
class FeedServiceIntegrationTest @Autowired constructor(
    private val feedService: FeedService,
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
    private val recipeLikeRepository: RecipeLikeRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val em: EntityManager,
    private val jdbc: JdbcTemplate,
    private val clock: Clock
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
        val today = LocalDate.now(clock.withZone(ZoneId.of("Asia/Seoul"))).atTime(12, 0)
        for (index in 1..52) {
            jdbc.update("UPDATE recipe SET created_at = ? WHERE id = ?", today.minusMinutes(index.toLong()), recipeId(index))
        }
        resetMeasurement()
    }

    @ParameterizedTest(name = "size={0}: SQL stays at 8 and personalization remains correct")
    @ValueSource(ints = [10, 20, 30])
    fun `page size does not increase query count`(size: Int) {
        val page = feedService.getFeedList(viewerId, FeedFilterType.ALL, PageRequest.of(0, size), null)

        assertThat(statistics.prepareStatementCount).isEqualTo(8L)
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

        assertThat(statistics.prepareStatementCount).isEqualTo(queryCount(filter))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `search results preserve all response fields`() {
        val pageable = PageRequest.of(0, 20)
        val expected = previousFeedList(FeedFilterType.ALL, pageable, "recipe-1")
        assertThat(expected.totalElements).isEqualTo(11L)
        resetMeasurement()

        val actual = feedService.getFeedList(viewerId, FeedFilterType.ALL, pageable, "recipe-1")

        assertThat(statistics.prepareStatementCount).isEqualTo(8L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `another user's actions do not leak into viewer flags`() {
        val page = feedService.getFeedList(authorId, FeedFilterType.ALL, PageRequest.of(0, 20), null)

        assertThat(statistics.prepareStatementCount).isEqualTo(8L)
        assertThat(page.content).hasSize(20)
        assertThat(page.content).allSatisfy { item ->
            assertThat(item.isLiked).isFalse()
            assertThat(item.isBookmarked).isFalse()
            assertThat(item.likeCount).isPositive()
            assertThat(item.bookmarkCount).isPositive()
        }
    }

    @Test
    fun `last partial page uses eight queries and preserves total`() {
        val page = feedService.getFeedList(viewerId, FeedFilterType.ALL, PageRequest.of(5, 10), null)

        assertThat(statistics.prepareStatementCount).isEqualTo(8L)
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
    fun `TODAY includes the KST start and excludes the next midnight`() {
        val start = LocalDateTime.of(2026, 9, 26, 0, 0)
        val times = listOf(start.minusSeconds(1), start, start.plusHours(12), start.plusDays(1))
        times.forEachIndexed { index, time ->
            jdbc.update("UPDATE recipe SET created_at = ?, title = ? WHERE id = ?", time, "clock-boundary", recipeId(index + 1))
        }
        resetMeasurement()

        val page = feedService.getFeedList(viewerId, FeedFilterType.TODAY, PageRequest.of(0, 20), "clock-boundary")

        assertThat(page.content.map { it.recipeId }).containsExactly(recipeId(3), recipeId(2))
        assertThat(page.totalElements).isEqualTo(2L)
    }

    @ParameterizedTest
    @EnumSource(value = FeedFilterType::class, names = ["HOT", "RECOMMEND"])
    fun `ranking includes an old popular recipe outside the latest page`(filter: FeedFilterType) {
        val oldRecipe = recipeRepository.findById(recipeId(52)).orElseThrow()
        repeat(3) { index ->
            val fan = UserFixture.create().also(em::persist)
            em.persist(RecipeLike(user = fan, recipe = oldRecipe))
            em.persist(Bookmark(user = fan, recipe = oldRecipe, id = "BM-rank-$index"))
        }
        em.flush()
        resetMeasurement()
        val pageable = PageRequest.of(0, 20)
        val expected = previousFeedList(filter, pageable, null)
        resetMeasurement()

        val page = feedService.getFeedList(viewerId, filter, pageable, null)

        assertThat(statistics.prepareStatementCount).isEqualTo(7L)
        assertThat(page).isEqualTo(expected)
        assertThat(page.content.first().recipeId).isEqualTo(recipeId(52))
        assertThat(page.content.first().likeCount).isEqualTo(5L)
        assertThat(page.content.first().bookmarkCount).isEqualTo(4L)
    }

    @ParameterizedTest
    @EnumSource(FeedFilterType::class)
    fun `missing aggregates default to zero including ranking left joins`(filter: FeedFilterType) {
        listOf("recipe_like", "bookmark", "comment").forEach { table ->
            jdbc.update("DELETE FROM $table WHERE recipe_id = ?", recipeId(1))
        }
        jdbc.update("UPDATE recipe SET title = ? WHERE id = ?", "zero-activity", recipeId(1))
        resetMeasurement()

        val page = feedService.getFeedList(viewerId, filter, PageRequest.of(0, 20), "zero-activity")

        assertThat(statistics.prepareStatementCount).isEqualTo(queryCount(filter))
        val item = page.content.single()
        assertThat(item.likeCount).isZero()
        assertThat(item.bookmarkCount).isZero()
        assertThat(item.commentCount).isZero()
        assertThat(item.isLiked).isFalse()
        assertThat(item.isBookmarked).isFalse()
    }

    @ParameterizedTest
    @EnumSource(value = FeedFilterType::class, names = ["ALL", "HOT", "RECOMMEND"])
    fun `search field priority precedes popularity and recency`(filter: FeedFilterType) {
        jdbc.update("UPDATE recipe SET ingredient_info = ? WHERE id = ?", "priority-needle", recipeId(1))
        jdbc.update("UPDATE recipe SET subtitle = ? WHERE id = ?", "priority-needle", recipeId(2))
        jdbc.update("UPDATE recipe SET title = ? WHERE id = ?", "priority-needle", recipeId(51))
        resetMeasurement()
        val pageable = PageRequest.of(0, 20)
        val expected = previousFeedList(filter, pageable, "  PRIORITY-NEEDLE  ")
        resetMeasurement()

        val page = feedService.getFeedList(viewerId, filter, pageable, "  PRIORITY-NEEDLE  ")

        assertThat(page).isEqualTo(expected)
        assertThat(page.content.map { it.recipeId }).containsExactly(recipeId(51), recipeId(2), recipeId(1))
    }

    @Test
    fun `reply and soft deleted activity counts retain the existing contract`() {
        val recipe = recipeRepository.findById(recipeId(1)).orElseThrow()
        val viewer = userRepository.findById(viewerId).orElseThrow()
        val parent = em.createQuery("select c from Comment c where c.recipe.id = :id", Comment::class.java)
            .setParameter("id", recipe.id).resultList.first()
        em.persist(Comment(user = viewer, recipe = recipe, content = "reply", parent = parent))
        em.flush()
        listOf("recipe_like", "bookmark", "comment").forEach { table ->
            jdbc.update("UPDATE $table SET deleted_at = ? WHERE recipe_id = ?", LocalDateTime.of(2026, 9, 26, 12, 0), recipe.id)
        }
        resetMeasurement()
        val pageable = PageRequest.of(0, 10)
        val expected = previousFeedList(FeedFilterType.ALL, pageable, null)
        resetMeasurement()

        val page = feedService.getFeedList(viewerId, FeedFilterType.ALL, pageable, null)

        assertThat(page).isEqualTo(expected)
        assertThat(page.content.first().commentCount).isEqualTo(3L)
        assertThat(page.content.first().likeCount).isEqualTo(1L)
        assertThat(page.content.first().bookmarkCount).isEqualTo(1L)
    }

    @ParameterizedTest
    @EnumSource(FeedFilterType::class)
    fun `category eligibility is applied before pagination without changing legacy totals`(filter: FeedFilterType) {
        jdbc.update("UPDATE recipe SET category_cooking_time_id = NULL WHERE id = ?", recipeId(1))
        jdbc.update("UPDATE recipe SET category_level_id = NULL WHERE id = ?", recipeId(2))
        resetMeasurement()
        val pageable = PageRequest.of(0, 20)
        val expected = previousFeedList(filter, pageable, null)
        resetMeasurement()

        val page = feedService.getFeedList(viewerId, filter, pageable, null)

        assertThat(page).isEqualTo(expected)
        assertThat(page.content).hasSize(20)
        assertThat(page.content.map { it.recipeId }).doesNotContain(recipeId(1), recipeId(2))
        assertThat(page.totalElements).isEqualTo(52L)
    }

    @ParameterizedTest
    @EnumSource(FeedFilterType::class)
    fun `visibility and status restrictions also apply to ranking candidates`(filter: FeedFilterType) {
        jdbc.update("UPDATE recipe SET is_private = TRUE WHERE id = ?", recipeId(1))
        jdbc.update("UPDATE recipe SET deleted_at = ? WHERE id = ?", LocalDateTime.of(2026, 9, 26, 12, 0), recipeId(2))
        jdbc.update("UPDATE recipe SET recipe_status = 'TEMPORARY' WHERE id = ?", recipeId(3))
        jdbc.update("UPDATE users SET is_private = TRUE WHERE id = ?", authorId)
        resetMeasurement()
        val pageable = PageRequest.of(0, 20)
        val expected = previousFeedList(filter, pageable, null)
        resetMeasurement()

        val page = feedService.getFeedList(viewerId, filter, pageable, null)

        assertThat(page).isEqualTo(expected)
        assertThat(page.totalElements).isEqualTo(49L)
        assertThat(page.content.map { it.recipeId }).doesNotContain(recipeId(1), recipeId(2), recipeId(3))
        // Without the follow, none of this private author's posts are visible to the viewer.
        jdbc.update("DELETE FROM follow WHERE follower = ? AND following = ?", viewerId, authorId)
        resetMeasurement()
        val hidden = feedService.getFeedList(viewerId, filter, pageable, null)
        assertThat(hidden.content).isEmpty()
        assertThat(hidden.totalElements).isZero()
        assertThat(statistics.prepareStatementCount).isEqualTo(3L)
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

    @ParameterizedTest
    @EnumSource(FeedFilterType::class)
    fun `tied page boundaries have stable ordering without duplicates or omissions`(filter: FeedFilterType) {
        jdbc.update("UPDATE recipe SET created_at = ?", LocalDateTime.of(2026, 9, 26, 12, 0))
        val expected = (1..52).sortedWith(
            compareByDescending<Int> {
                when (filter) {
                    FeedFilterType.HOT -> if (it % 2 == 0) 2 else 1
                    FeedFilterType.RECOMMEND -> if (it % 3 == 0) 2 else 1
                    else -> 0
                }
            }.thenByDescending { recipeId(it) }
        ).map(::recipeId)

        val actual = (0..2).flatMap { number ->
            resetMeasurement()
            val page = feedService.getFeedList(viewerId, filter, PageRequest.of(number, 20), null)
            assertThat(statistics.prepareStatementCount).isEqualTo(queryCount(filter))
            assertThat(page.totalElements).isEqualTo(52L)
            page.content.map { it.recipeId }
        }

        assertThat(actual).containsExactlyElementsOf(expected)
        assertThat(actual).doesNotHaveDuplicates()
    }

    @ParameterizedTest
    @ValueSource(ints = [50, 51, 5000])
    fun `feed size is capped before querying and reflected in page metadata`(requestedSize: Int) {
        val first = feedService.getFeedList(viewerId, FeedFilterType.ALL, PageRequest.of(0, requestedSize), null)

        assertThat(statistics.prepareStatementCount).isEqualTo(8L)
        assertThat(first.content.map { it.recipeId }).containsExactlyElementsOf((1..50).map(::recipeId))
        assertThat(first.size).isEqualTo(50)
        assertThat(first.totalElements).isEqualTo(52L)
        assertThat(first.totalPages).isEqualTo(2)

        resetMeasurement()
        val next = feedService.getFeedList(viewerId, FeedFilterType.ALL, PageRequest.of(1, requestedSize), null)
        assertThat(next.content.map { it.recipeId }).containsExactly(recipeId(51), recipeId(52))
        assertThat(next.size).isEqualTo(50)
        assertThat(next.number).isEqualTo(1)
        assertThat(next.pageable.offset).isEqualTo(50L)
        assertThat(next.isLast).isTrue()
    }

    @Test
    fun `unpaged internal calls are bounded as well`() {
        val page = feedService.getFeedList(viewerId, FeedFilterType.ALL, Pageable.unpaged(), null)

        assertThat(page.size).isEqualTo(50)
        assertThat(page.number).isZero()
        assertThat(page.content).hasSize(50)
        assertThat(page.totalElements).isEqualTo(52L)
        assertThat(statistics.prepareStatementCount).isEqualTo(8L)
    }

    // The frozen pre-C query plus A's per-item lookups form an independent regression oracle.
    private fun previousFeedList(filter: FeedFilterType, pageable: PageRequest, keyword: String?) =
        userRepository.findById(viewerId).orElseThrow().let { user ->
            val page = LegacyFeedListQuery(em, clock).findFeed(user, filter, pageable, keyword)
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

    private fun queryCount(filter: FeedFilterType) =
        if (filter == FeedFilterType.HOT || filter == FeedFilterType.RECOMMEND) 7L else 8L
}
