package zipbap.global.domain.file

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.annotation.RepositoryTest
import support.extension.saveTestRecipe
import support.fixture.FileEntityFixture
import support.fixture.UserFixture
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.user.UserRepository

@RepositoryTest
class FileRepositoryTest @Autowired constructor(
    private val fileRepository: FileRepository,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {

    @Test
    @DisplayName("URL을 통해 파일을 조회한다.")
    fun findByFileUrl() {
        //given
        val file1 = FileEntityFixture.create()
        val file2 = FileEntityFixture.create()
        val savedFile = fileRepository.save(file1)
        fileRepository.save(file2)

        //when
        val foundFile = fileRepository.findByFileUrl(savedFile.fileUrl)

        //then
        Assertions.assertThat(savedFile.id).isEqualTo(foundFile!!.id)
    }

    @Test
    @DisplayName("레시피 아이디에 연결된 모든 파일을 조회한다.")
    fun findAllByRecipeId() {
        //given
        val user = userRepository.save(UserFixture.create())
        val recipe1 = recipeRepository.saveTestRecipe(user)
        val recipe2 = recipeRepository.saveTestRecipe(user)

        val file1 = fileRepository.save(FileEntityFixture.createForRecipe(recipe1))
        val file2 = fileRepository.save(FileEntityFixture.createForRecipe(recipe1))
        val file3 = fileRepository.save(FileEntityFixture.createForRecipe(recipe2))

        //when
        val result = fileRepository.findAllByRecipeId(recipe1.id)

        //then
        Assertions.assertThat(result).hasSize(2)
            .extracting({it.id})
            .containsExactlyInAnyOrderElementsOf(
                listOf(file1, file2).map {
                    tuple(it.id)
                }
            )
    }

    @Test
    @DisplayName("레시피에 연결된 파일이 없으면 0개가 조회된다.")
    fun findAllByRecipeIdWithNoFile() {
        //given
        val user = userRepository.save(UserFixture.create())
        val recipe1 = recipeRepository.saveTestRecipe(user)


        //when
        val result = fileRepository.findAllByRecipeId(recipe1.id)

        //then
        Assertions.assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("유저와 연결된 모든 파일을 조회한다.")
    fun findAllByUserId() {
        //given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val file1 = fileRepository.save(FileEntityFixture.createForUser(user1))
        val file2 = fileRepository.save(FileEntityFixture.createForUser(user2))
        val file3 = fileRepository.save(FileEntityFixture.createForUser(user1))
        //when
        val result = fileRepository.findAllByUser(user1)

        //then
        Assertions.assertThat(result).hasSize(2)
            .extracting(
                {it.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(file1, file3).map{
                    tuple(it.id)
                }
            )
    }

    @Test
    @DisplayName("유저와 연결된 파일이 없으면 0개가 조회된다.")
    fun findAllByUserIdWithNoFile() {
        //given
        val user1 = userRepository.save(UserFixture.create())

        //when
        val result = fileRepository.findAllByUser(user1)

        //then
        Assertions.assertThat(result).hasSize(0)
    }

    @Test
    @DisplayName("유저와 레시피가 각각 연결되어 있더라도 조회할 수 있다.")
    fun findAllByUserAndRecipeIdMixed() {
        //given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())
        val recipe1 = recipeRepository.saveTestRecipe(user1)
        val recipe2 = recipeRepository.saveTestRecipe(user1)
        val recipe3 = recipeRepository.saveTestRecipe(user2)

        val file1 = fileRepository.save(FileEntityFixture.create(user = user1, recipe = recipe1))
        val file2 = fileRepository.save(FileEntityFixture.create(user = user1, recipe = recipe2))
        val file3 = fileRepository.save(FileEntityFixture.create(user = user1, recipe = recipe2))
        val file4 = fileRepository.save(FileEntityFixture.create(user = user2, recipe = recipe3))

        //when
        val result1 = fileRepository.findAllByUser(user2)
        val result2 = fileRepository.findAllByRecipeId(recipe2.id)

        //then
        Assertions.assertThat(result1).hasSize(1)
            .extracting(
                {it.id}
            ).contains(
                tuple(file4.id)
            )

        Assertions.assertThat(result2).hasSize(2)
            .extracting(
                {it.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(file2, file3).map{
                    tuple(it.id)
                }
            )
    }

}