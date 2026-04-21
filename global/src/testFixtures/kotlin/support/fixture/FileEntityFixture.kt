package support.fixture

import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.file.FileStatus
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User
import java.util.UUID

object FileEntityFixture {

    /**
     * 가장 기본적인 형태의 FileEntity 생성
     * (특정 도메인에 종속되지 않은 고아(Orphan) 파일이나 테스트용 더미 파일이 필요할 때 사용)
     */
    fun create(
        fileUrl: String = "test-${UUID.randomUUID().toString().substring(0, 6)}.com",
        status: FileStatus = FileStatus.TEMPORARY_UPLOAD,
        recipe: Recipe? = null,
        user: User? = null
    ): FileEntity {
        return FileEntity(
            fileUrl = fileUrl,
            status = status,
            recipe = recipe,
            user = user
        )
    }

    /**
     * 💡 [편의 메서드] 유저 프로필 이미지 전용 픽스처
     * 생성 시 자동으로 user가 매핑되고, 기본 상태를 FINALIZED로 세팅합니다.
     */
    fun createForUser(
        user: User,
        fileUrl: String = "test-${UUID.randomUUID().toString().substring(0, 6)}.com",
        status: FileStatus = FileStatus.FINALIZED
    ): FileEntity {
        return FileEntity(
            fileUrl = fileUrl,
            status = status,
            user = user,
            recipe = null // 유저 프로필이므로 레시피는 확실하게 null 처리
        )
    }

    /**
     * 💡 [편의 메서드] 레시피 이미지 전용 픽스처
     * 생성 시 자동으로 recipe가 매핑되고, 기본 상태를 FINALIZED로 세팅합니다.
     */
    fun createForRecipe(
        recipe: Recipe,
        fileUrl: String = "test-${UUID.randomUUID().toString().substring(0, 6)}.com",
        status: FileStatus = FileStatus.FINALIZED
    ): FileEntity {
        return FileEntity(
            fileUrl = fileUrl,
            status = status,
            recipe = recipe,
            user = null // 레시피 이미지이므로 유저는 확실하게 null 처리
        )
    }
}