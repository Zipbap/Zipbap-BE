package zipbap.app.global.util

object CustomIdGenerator {

    /**
     * 도메인별 prefix + userId + sequence 로 ID 생성
     *
     * @param prefix 도메인 구분용 접두어 (예: "MC", "RC")
     * @param userId 사용자 ID
     * @param sequence 순번 (0-padding 5자리)
     */
    fun generate(prefix: String, userId: Long, sequence: Long): String {
        return "$prefix-$userId-${sequence.toString().padStart(5, '0')}"
    }
}
