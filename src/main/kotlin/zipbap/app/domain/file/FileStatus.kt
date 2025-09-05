package zipbap.app.domain.file

/**
 * 파일의 저장 상태
 * - TEMPORARY_UPLOAD : Presigned URL 업로드 완료, 레시피에 연결되지 않은 상태
 * - FINALIZED        : 레시피에 연결된 최종 저장 상태
 * - UNTRACKED        : 추적 불가 (DB에 연결 정보가 없거나, 레시피와 무관하게 업로드된 상태)
 */
enum class FileStatus {
    TEMPORARY_UPLOAD,
    FINALIZED,
    UNTRACKED
}
