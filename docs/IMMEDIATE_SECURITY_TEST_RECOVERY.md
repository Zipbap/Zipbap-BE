# 즉시 단계: 테스트 복구와 외부 인증 우회 차단

작성일: 2026-09-26. `ZIPBAP_TOP5_FINAL.md`의 실제 작업 순서 중 즉시 단계 구현 기록이다. 기존 분석 보고서는 수정 전 상태의 기록이며, 이 문서는 수정 후 동작을 설명한다. 로컬 코드와 CI 구성을 변경했으며 운영 배포는 수행하지 않았다.

## 적용한 변경

| 영역 | 수정 후 동작 |
|---|---|
| 테스트 환경 | H2 대신 Testcontainers MariaDB로 V1/V2 Flyway migration을 실행하고 Hibernate `validate`로 매핑을 검사한다. AWS·OAuth 운영 자격증명 없이 서비스 테스트를 실행한다. |
| 빌드·CI | 관리자 JWT 필터의 잘못된 의존성을 수정했다. 사용자·관리자 테스트와 bootJar 빌드가 통과해야 기존 배포 workflow의 build가 실행된다. |
| 공개 토큰 발급 | 이메일 기반 관리자 토큰 발급 컨트롤러와 `/api/auth/test`를 삭제했다. 인증 경로 전체를 공개하던 설정을 로그인·재발급 경로로 좁혔다. |
| 관리자 경계 | `/admin/**`와 Swagger는 ADMIN 세션 인증이 필요하다. 관리자 쓰기는 CSRF 검사를 유지한다. 명시하지 않은 경로는 기본 거부한다. |
| JWT | 일반 API는 서명·만료 검증을 통과한 `type=access` 토큰과 양수 사용자 ID만 허용한다. Refresh 토큰을 Bearer로 사용하면 401이다. |
| 로그인 | Apple JWKS의 RS256 서명, issuer, audience, 만료, subject와 검증된 이메일을 확인한다. Kakao도 검증된 이메일을 요구한다. 다른 소셜 제공자의 기존 계정을 이메일만으로 연결하지 않는다. |
| 요청 주체 | `@UserInjection`은 인증 principal의 Long ID를 반환한다. 지원하지 않는 타입도 resolver가 맡아 실패시키므로 요청 파라미터의 User 모델 바인딩으로 우회하지 않는다. |
| 콘텐츠 권한 | 피드 목록·상세·마이페이지·북마크·댓글 조회 및 상호작용에서 공유된 공개 범위 조건을 사용한다. 삭제 콘텐츠·삭제 작성자·임시 글을 제외하며 개인 레시피는 작성자만 조회한다. 비공개 계정의 공개 레시피는 작성자와 팔로워에게 허용한다. |
| 파일·카테고리 | 업로드 소유자를 principal로 기록한다. 파일 연결 변경 시 업로더를 검사하고 다른 레시피의 파일 연결을 빼앗지 못하게 한다. 개인 카테고리도 작성자 소유인지 검사한다. |
| 로그·오류 | HTTP 로그는 메서드·경로 템플릿·상태·소요 시간만 남긴다. 요청/응답 본문·헤더·쿼리 값과 JWT 예외 상세는 기록하지 않는다. 내부 예외 메시지는 클라이언트에 반환하지 않는다. |

## 재현 방법

Java 17과 실행 중인 Docker 엔진이 필요하다. 기존 MariaDB 인스턴스나 운영 비밀번호는 필요하지 않다. 테스트마다 별도 DB를 사용하는 컨테이너가 생성되고 종료된다. 기본 이미지 `mariadb:13.0.2`는 확인한 로컬 MariaDB 버전에 맞췄으며 운영 DB 버전을 확인했다는 뜻은 아니다. 필요한 경우 `TEST_MARIADB_TAG` 환경변수로 검증할 버전을 지정한다.

```sh
./gradlew :global:test :api-user:test :api-admin:test :api-user:bootJar :api-admin:bootJar --no-daemon
```

Windows PowerShell에서 wrapper launcher 문제가 있으면 같은 Gradle wrapper를 직접 실행할 수 있다.

```powershell
& "$env:JAVA_HOME/bin/java.exe" -jar gradle/wrapper/gradle-wrapper.jar :global:test :api-user:test :api-admin:test :api-user:bootJar :api-admin:bootJar --no-daemon
```

Testcontainers 1.21.4를 모든 모듈에 통일했다. Spring Boot가 추가로 가져오는 BOM에도 버전을 지정해 Docker 29에서 구버전 Docker API를 사용하는 문제를 해결했다. 테스트의 시각·ID 정렬은 짧은 sleep이나 무작위 ID 대신 명시적 데이터로 검증한다.

결과 HTML은 각 모듈의 `build/reports/tests/test/index.html`에 생성된다. GitHub Actions의 `verify.yml`은 PR과 배포 선행 검증에 같은 명령을 사용하고 결과를 artifact로 보관한다. GitHub 원격 실행 자체는 이번 작업에서 수행하지 않았다.

최종 로컬 검증은 **BUILD SUCCESSFUL**이다. `global` 59개, `api-user` 39개, `api-admin` 6개로 **총 104개 테스트가 실패·오류·건너뛰기 없이 통과**했다. 사용자·관리자 `bootJar` 생성도 성공했으며 `git diff --check` 오류가 없다. 검증 후 테스트 컨테이너는 정리되었고 기존 `mariadb-server`는 33069 포트에서 계속 실행 중임을 확인했다.

## 검증한 위협 시나리오

- 실제 SecurityFilterChain과 MVC resolver를 포함해 principal 123에 요청 ID 456을 넣어도 서비스에는 123이 전달되는지 확인한다.
- Refresh·만료·위조 서명·잘못된 형식의 JWT가 일반 API에서 401인지 확인한다.
- 익명 관리자 접근, USER 역할의 읽기·쓰기, 삭제한 토큰 발급 경로, CSRF 없는 ADMIN 쓰기를 거부한다. 명시한 테스트 관리자 계정의 폼 로그인과 정상 ADMIN 요청도 검증한다.
- 실제 RSA 키와 Nimbus 검증기로 Apple 정상 토큰과 잘못된 서명·issuer·audience·만료·누락 claim·검증되지 않은 이메일을 검사한다. 실제 Apple 서비스와의 통신은 수행하지 않는다.
- 작성자 공개 여부 × 레시피 공개 여부 × 상태 × 소유자/팔로워/제3자의 24개 조합을 MariaDB에서 검사해 조회 경로별 정책 일치를 확인한다.
- 타인 업로드·소유자가 없는 레거시 파일·타인 개인 카테고리 연결을 거부하고, 프로필 이미지를 분리했다 다시 연결해도 업로더가 유지되는지 확인한다.
- 로그에 넣은 토큰 식별 문자열이 출력되지 않는지, 필터가 요청·응답 본문을 훼손하지 않는지 확인한다.

## 배포에 영향을 주는 사항

1. `spring.security.user.name`과 `spring.security.user.password`를 명시해야 관리자/Swagger 로그인이 가능하다. 비밀번호에는 Spring Security 형식의 인코딩된 값(예: `{bcrypt}` 접두사와 해시)을 사용한다. 설정이 없으면 관리 계정이 생성되지 않으며 기존 `change-me` 기본값은 거부한다. 테스트 전용 계정은 테스트 코드 안에만 있다.
2. `V2__file_uploader.sql`이 `file.uploader_id`와 FK·인덱스를 추가한다. 기존 레시피 작성자 또는 프로필 소유자로 소유권을 채우며, 어느 쪽에도 연결되지 않은 파일은 NULL로 남아 새 연결이 차단된다. 해당 파일은 재업로드하거나 별도 근거로 소유권을 확인해야 한다. 과거 잘못된 연결까지 이 migration이 판별하지는 않는다.
3. 기존 V1은 수정하지 않았다. 신규 테스트 DB에서 V1→V2와 매핑 검증을 수행했다. 운영 데이터의 backfill 결과·규모별 DDL 시간·전체 서버의 운영 설정을 포함한 시작은 검증하지 않았으므로 배포 전에 운영 복제본에서 확인해야 한다. 사용자의 기존 33069 포트 MariaDB에는 migration을 실행하지 않았다.
4. 재발급은 기존 GET을 유지하고 POST도 지원하며 응답에 `Cache-Control: no-store`를 설정했다. 제거한 디버그·관리자 토큰 발급 경로에 의존하는 클라이언트는 정상 로그인으로 전환해야 한다.

## 다음 단계로 남겨 둔 범위

provider+subject 기반 계정 키와 데이터 이관, Apple 서버 nonce 흐름, Refresh rotation·재사용 탐지·로그아웃 폐기는 별도 인증 수명 관리 작업이다. 현재 같은 제공자의 계정 조회는 여전히 이메일 기반이다. 피드 N+1·다중 집계, ID 동시성, 조회수 분리, 배포 전환·롤백 자동화도 후속 과제다. 이번 변경을 해당 과제 전체 완료나 모든 취약점 제거로 해석하지 않는다.
