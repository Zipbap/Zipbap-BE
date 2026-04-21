package zipbap.user.api.user.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import support.annotation.IntegrationTest
import support.fixture.UserFixture
import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.file.FileRepository
import zipbap.global.domain.file.FileStatus
import zipbap.global.domain.user.SocialType
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.exception.GeneralException
import zipbap.user.api.user.dto.UserRequestDto
import kotlin.jvm.optionals.getOrNull

@IntegrationTest
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository,
){
    @Test
    @DisplayName("이미 가입된 이메일로 조회하면 true를 반환한다.")
    fun isUserExists() {
        //given
        val user = UserFixture.create(email = "email@naver.com")
        userRepository.save(user)

        //when
        val result = userService.isUserExists(user.email)

        //then
        assertThat(result).isTrue
    }

    @Test
    @DisplayName("가입되지 않은 이메일로 조회하면 false를 반환한다.")
    fun isNotUserExists() {
        //given

        //when
        val result = userService.isUserExists("email@naver.com")


        //then
        assertThat(result).isFalse
    }

    @Test
    @DisplayName("존재하지 않는 이메일과 올바른 소셜 타입이 주어지면, 유저 객체가 생성되고 저장된다.")
    fun register() {
        //given
        val email = "zipbap@zipbap.com"
        val social = "kakao"
        val username = "kimsunwoo"

        //when
        val id = userService.register(
            registrationId = social,
            username = username,
            email = email
        )

        val user = userRepository.findByEmail(email)

        //then
        assertAll(
            { assertThat(user).isNotNull },
            { assertThat(user!!.id).isEqualTo(id)},
            { assertThat(user!!.email).isEqualTo(email) },
            { assertThat(user!!.socialType).isEqualTo(SocialType.valueOf(social.uppercase())) },
            { assertThat(user!!.nickname).isEqualTo(username)}
        )
    }

    @Test
    @DisplayName("""
        이미 존재하는 이메일로 가입을 시도하면, 메소드가 바로 종료된다.
        즉 가입된 이메일로 유저를 조회시 기존에 가입된 유저의 정보가 조회되어야 한다.
    """)
    fun registerWithDuplicatedEmail() {
        //given
        val email = "zipbap@zipbap.com"
        val social = "apple"
        val username = "leechangjun"
        val advancedUser = UserFixture.create(
            email = email,
            socialType = SocialType.valueOf(social.uppercase()),
            nickname = username
        )
        userRepository.save(advancedUser)

        //when
        val id = userService.register(
            email = email,
            registrationId = "kakao",
            username = "kimsunwoo"
        )

        val foundUser = userRepository.findByEmail(advancedUser.email)

        //then
        assertAll(
            { assertThat(foundUser).isNotNull },
            { assertThat(foundUser!!.id).isEqualTo(id) },
            { assertThat(foundUser!!.socialType).isEqualTo(SocialType.valueOf(social.uppercase())) },
            { assertThat(foundUser!!.nickname).isEqualTo(username) }
        )
    }

    @Test
    @DisplayName("지원하지 않는 registrationId가 입력시 IllegalArgumentException 예외가 발생한다.")
    fun registerWithNotSupportedRegistrationId() {
        //given
        val email = "zipbap@zipbap.com"
        val social = "naver" // 추후 네이버 지원시 바꿔야함
        val username = "kimsunwoo"

        //when & then
        assertThatThrownBy { userService.register(social, username, email) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("존재하는 유저 id로 조회시 정상적으로 UserProfileDto를 반환한다.")
    fun getUserProfile() {
        //given
        val email = "zipbap@zipbap.com"
        val social = "apple"
        val username = "leechangjun"
        val advancedUser = UserFixture.create(
            email = email,
            socialType = SocialType.valueOf(social.uppercase()),
            nickname = username
        )
        val savedUser = userRepository.save(advancedUser)

        //when
        val userProfile = userService.getUserProfile(savedUser.id!!)

        //then
        assertAll(
            { assertThat(userProfile.id).isEqualTo(savedUser.id!!) },
            { assertThat(userProfile.nickname).isEqualTo(savedUser.nickname) },
            { assertThat(userProfile.profileImage).isEqualTo(savedUser.profileImage) },
            { assertThat(userProfile.statusMessage).isEqualTo(savedUser.statusMessage) },
            { assertThat(userProfile.isPrivate).isEqualTo(savedUser.isPrivate) },
        )
    }

    @Test
    @DisplayName("존재하지 않는 user id로 조회시 GeneralException이 발생한다.")
    fun getUserProfileWithInvalidUserId() {
        //given
        val userId: Long = 1L

        //when & then
        assertThatThrownBy { userService.getUserProfile(userId) }
            .isInstanceOf(GeneralException::class.java)
    }

    @Test
    @DisplayName("""
        기존 프로필 이미지가 없는 유저가 새로운 이미지로 업데이트를 요청하면:

        요청된 이미지의 FileStatus는 FINALZIED가 되고 유저와 연결된다.
        
        유저의 내부 정보(nickname, isPrivate 등)가 정상적으로 변경되고 DTO가 반환된다.
    """)
    fun updateUserProfile() {
        // given
        val savedUser = userRepository.save(UserFixture.create(nickname = "이전 닉네임"))

        val fakePresignedUrl = "https://s3.aws.com/fake-image.png"
        fileRepository.save(FileEntity(fileUrl = fakePresignedUrl, status = FileStatus.UNTRACKED, user = null))

        val requestDto = UserRequestDto.UserUpdateDto(
            nickname = "새 닉네임",
            isPrivate = true,
            profileImage = fakePresignedUrl,
            statusMessage = "안녕"
        )

        // when
        val result = userService.updateUserProfile(savedUser.id!!, requestDto)
        val foundFile = fileRepository.findByFileUrl(fakePresignedUrl)

        // then
        assertAll(
            { assertThat(result.nickname).isEqualTo("새 닉네임") },
            { assertThat(result.profileImage).isEqualTo(fakePresignedUrl) },
            { assertThat(foundFile).isNotNull },
            { assertThat(result.isPrivate).isEqualTo(true) },
            { assertThat(result.statusMessage).isEqualTo(requestDto.statusMessage) },
            { assertThat(foundFile!!.status).isEqualTo(FileStatus.FINALIZED) },
            { assertThat(foundFile!!.user!!.id).isEqualTo(savedUser.id!!) },
        )

        // FileRepository로 fakePresignedUrl 조회해서 상태가 FINALIZED로 바뀌었는지 확인!
    }

    @Test
    @DisplayName("""
        기존 프로필 이미지가 있는 유저가 새로운 이미지로 업데이트를 요청하면:

        기존 이미지의 FileStatus는 UNTRACKED로 변하고, user 매핑이 null로 끊어진다.
        
        새로운 이미지의 FileStatus는 FINALIZED로 변하고, user가 매핑된다.
        
        유저의 내부 정보(nickname, isPrivate 등)가 정상적으로 변경되고 DTO가 반환된다.
    """)
    fun updateUserProfileWithAlreadyProfileImage() {
        //given
        val savedUser = userRepository.save(UserFixture.create(nickname = "이전 닉네임"))

        val fakeOldPresignedUrl = "https://s3.aws.com/fake-image.png"
        val fakeNewPresignedUrl = "https://s3.aws.com/new-image.png"

        fileRepository.save(FileEntity(fileUrl = fakeOldPresignedUrl, status = FileStatus.UNTRACKED, user = null))
        fileRepository.save(FileEntity(fileUrl = fakeNewPresignedUrl, status = FileStatus.UNTRACKED, user = null))

        val oldRequestDto = UserRequestDto.UserUpdateDto(
            nickname = "새 닉네임",
            isPrivate = true,
            profileImage = fakeOldPresignedUrl,
            statusMessage = "안녕"
        )

        val newRequestDto = UserRequestDto.UserUpdateDto(
            nickname = "새 닉네임2",
            isPrivate = false,
            profileImage = fakeNewPresignedUrl,
            statusMessage = "안녕하지마"
        )

        userService.updateUserProfile(savedUser.id!!, oldRequestDto)

        //when
        val result = userService.updateUserProfile(savedUser.id!!, newRequestDto)
        val oldFile = fileRepository.findByFileUrl(fakeOldPresignedUrl)
        val newFile = fileRepository.findByFileUrl(fakeNewPresignedUrl)

        //then
        assertAll(
            { assertThat(result.nickname).isEqualTo("새 닉네임2") },
            { assertThat(result.profileImage).isEqualTo(fakeNewPresignedUrl) },
            { assertThat(result.isPrivate).isFalse },
            { assertThat(result.statusMessage).isEqualTo(newRequestDto.statusMessage) },
            { assertThat(newFile!!.status).isEqualTo(FileStatus.FINALIZED) },
            { assertThat(newFile!!.user!!.id).isEqualTo(savedUser.id!!) },
            { assertThat(oldFile!!.user).isNull() },
            { assertThat(oldFile!!.status).isEqualTo(FileStatus.UNTRACKED) },
        )
    }

    @Test
    @DisplayName("프로필 이미지 값을 null로 업데이트 요청하면, 기존에 연결되어 있던 파일들의 상태가 모두 UNTRACKED로 변경된다.")
    fun updateUserProfileWithNullImage() {
        //given
        val savedUser = userRepository.save(UserFixture.create(nickname = "이전 닉네임"))

        val fakeOldPresignedUrl = "https://s3.aws.com/fake-image.png"

        fileRepository.save(FileEntity(fileUrl = fakeOldPresignedUrl, status = FileStatus.UNTRACKED, user = null))

        val oldRequestDto = UserRequestDto.UserUpdateDto(
            nickname = "새 닉네임",
            isPrivate = true,
            profileImage = fakeOldPresignedUrl,
            statusMessage = "안녕"
        )

        val newRequestDto = UserRequestDto.UserUpdateDto(
            nickname = "새 닉네임2",
            isPrivate = false,
            profileImage = null,
            statusMessage = "안녕하지마"
        )

        userService.updateUserProfile(savedUser.id!!, oldRequestDto)

        //when
        val result = userService.updateUserProfile(savedUser.id!!, newRequestDto)
        val oldFile = fileRepository.findByFileUrl(fakeOldPresignedUrl)

        //then
        assertAll(
            { assertThat(result.nickname).isEqualTo("새 닉네임2") },
            { assertThat(result.profileImage).isNull() },
            { assertThat(result.isPrivate).isFalse },
            { assertThat(result.statusMessage).isEqualTo(newRequestDto.statusMessage) },
            { assertThat(oldFile!!.user).isNull() },
            { assertThat(oldFile!!.status).isEqualTo(FileStatus.UNTRACKED) },
        )
    }

    @Test
    @DisplayName("존재하지 않는 userId로 update를 요청하면 GeneralException이 발생한다.")
    fun updateUserProfileWithNoUserId() {
        //given
        val noSavedId: Long = 10000L

        val newRequestDto = UserRequestDto.UserUpdateDto(
            nickname = "새 닉네임2",
            isPrivate = false,
            profileImage = null,
            statusMessage = "안녕하지마"
        )

        //when & then
        assertThatThrownBy { userService.updateUserProfile(noSavedId, newRequestDto) }
            .isInstanceOf(GeneralException::class.java)
    }

    @Test
    @DisplayName("가입된 유저를 삭제한다.")
    fun deleteUser() {
        //given
        val savedUserId = userRepository.save(UserFixture.create(nickname = "이전 닉네임")).id

        //when
        userService.deleteUser(savedUserId!!)
        val foundUser = userRepository.findById(savedUserId).getOrNull()

        //then
        assertThat(foundUser).isNull()
    }

    @Test
    @DisplayName("존재하지 않는 userId로 탈퇴를 시도할시, GeneralException이 발생한다.")
    fun deleteUserWithNoUserId() {
        //given
        val noSavedId: Long = 10000L

        //when & then
        assertThatThrownBy { userService.deleteUser(noSavedId) }
            .isInstanceOf(GeneralException::class.java)
    }

}