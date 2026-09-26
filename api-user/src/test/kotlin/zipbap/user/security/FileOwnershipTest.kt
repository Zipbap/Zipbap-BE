package zipbap.user.security

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.annotation.IntegrationTest
import support.fixture.UserFixture
import support.fixture.RecipeFixture
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.category.mycategory.MyCategory
import zipbap.global.domain.category.mycategory.MyCategoryRepository
import zipbap.user.api.recipe.dto.CategoryValidatable
import zipbap.user.api.recipe.validator.CategoryValidator
import zipbap.global.domain.file.*
import zipbap.global.domain.user.*
import zipbap.global.global.exception.GeneralException
import zipbap.user.api.file.service.FileService
import zipbap.user.api.user.service.UserService

@IntegrationTest
class FileOwnershipTest @Autowired constructor(
    private val files: FileRepository,
    private val users: UserRepository,
    private val fileService: FileService,
    private val userService: UserService,
    private val recipes: RecipeRepository,
    private val categories: MyCategoryRepository,
    private val categoryValidator: CategoryValidator
) {
    @Test fun `another user's file cannot be claimed`() {
        val owner = users.save(UserFixture.create())
        val attacker = users.save(UserFixture.create())
        val file = files.save(FileEntity("https://example.invalid/owned", uploader = owner))
        assertThatThrownBy { fileService.updateUserFileStatuses(setOf(file.fileUrl), FileStatus.FINALIZED, attacker) }
            .isInstanceOf(GeneralException::class.java)
        assertThat(file.user).isNull()
        assertThat(file.status).isEqualTo(FileStatus.TEMPORARY_UPLOAD)
    }

    @Test fun `unlinked legacy file has no implicit owner`() {
        val user = users.save(UserFixture.create())
        val file = files.save(FileEntity("https://example.invalid/unowned"))
        assertThatThrownBy { fileService.updateUserFileStatuses(setOf(file.fileUrl), FileStatus.FINALIZED, user) }
            .isInstanceOf(GeneralException::class.java)
    }

    @Test fun `foreign upload cannot be attached to recipe`() {
        val owner = users.save(UserFixture.create())
        val attacker = users.save(UserFixture.create())
        val recipe = recipes.save(RecipeFixture.create(attacker))
        val file = files.save(FileEntity("https://example.invalid/foreign", uploader = owner))
        assertThatThrownBy { fileService.updateRecipeFileStatuses(recipe.id, setOf(file.fileUrl), FileStatus.FINALIZED, recipe) }
            .isInstanceOf(GeneralException::class.java)
        assertThat(file.recipe).isNull()
    }

    @Test fun `foreign category cannot be referenced by partial or final update`() {
        val owner = users.save(UserFixture.create())
        val attacker = users.save(UserFixture.create())
        val category = categories.save(MyCategory(owner, "personal", "MC-ownership"))
        val request = object : CategoryValidatable {
            override val myCategoryId = category.id
            override val cookingTypeId: Long? = null
            override val situationId: Long? = null
            override val mainIngredientId: Long? = null
            override val methodId: Long? = null
            override val headcountId: Long? = null
            override val cookingTimeId: Long? = null
            override val levelId: Long? = null
        }
        assertThatThrownBy { categoryValidator.validateOptional(request, attacker.id!!) }
            .isInstanceOf(GeneralException::class.java)
        assertThatThrownBy { categoryValidator.validateAll(request, true, attacker.id!!) }
            .isInstanceOf(GeneralException::class.java)
        assertThat(categoryValidator.validateOptional(request, owner.id!!).myCategory?.id).isEqualTo(category.id)
    }

    @Test fun `removing and reusing profile image retains uploader ownership`() {
        val owner = users.save(UserFixture.create())
        val file = files.save(FileEntity("https://example.invalid/reuse", uploader = owner))
        fileService.updateUserFileStatuses(setOf(file.fileUrl), FileStatus.FINALIZED, owner)
        fileService.updateUserFileStatuses(emptySet(), FileStatus.FINALIZED, owner)
        files.flush()
        assertThat(file.user).isNull()
        assertThat(file.uploader?.id).isEqualTo(owner.id)
        fileService.updateUserFileStatuses(setOf(file.fileUrl), FileStatus.FINALIZED, owner)
        assertThat(file.user?.id).isEqualTo(owner.id)
    }

    @Test fun `email alone does not link different social providers`() {
        val existing = users.save(UserFixture.create(email = "same@example.invalid", socialType = SocialType.APPLE))
        assertThatThrownBy { userService.register("kakao", "other", existing.email) }
            .isInstanceOf(GeneralException::class.java)
        assertThat(users.findByEmail(existing.email)?.socialType).isEqualTo(SocialType.APPLE)
    }
}
