# Apple 토큰과 사용자 ID 바인딩: 수정 전후 실험

이 실험은 `api-user/src/test/kotlin/zipbap/user/security/lab/SecurityLabTest.kt`에서 실행한다. 이전 코드의 취약한 부분을 테스트 안에 재현하고, 같은 입력을 현재 구현에 전달해 결과를 비교한다. 수정 전 전체 서버를 실행하거나 현재 서버의 보안을 해제하지 않는다. 테스트 코드는 운영 bootJar에 포함되지 않는다.

## 1. 준비와 실행

필수: JDK 17, 프로젝트 Gradle wrapper. 최초 의존성 다운로드에는 네트워크가 필요하다. 이 두 실험에는 MariaDB·Docker·Redis·AWS 설정·Apple 개발자 계정이 필요 없다. 데이터는 테스트 프로세스 안의 가상 사용자와 로컬 생성 RSA 키다.

현재 PC의 PowerShell에서 다음을 실행한다.

```powershell
Set-Location D:\SpringProject\Zipbap-BE
& 'C:\Program Files\Java\jdk-17\bin\java.exe' -jar gradle/wrapper/gradle-wrapper.jar :api-user:securityLab --console=plain
```

일반적인 환경에서는 `./gradlew :api-user:securityLab --console=plain` 또는 `./gradlew.bat :api-user:securityLab --console=plain`로 실행해도 된다. `securityLab`은 매번 다시 실행되며 콘솔에 `[BEFORE]`, `[AFTER]`, `[CONTROL]`을 출력한다.

콘솔 외에 `api-user/build/reports/tests/securityLab/index.html`을 브라우저로 열고 **SecurityLabTest → Standard output**에서 같은 결과를 볼 수 있다. IntelliJ에서는 해당 클래스의 두 테스트를 직접 Run/Debug해도 된다. Gradle JVM과 Project SDK를 17로 설정한다.

## 2. Apple: JWT 내용을 읽었다고 발급자가 검증된 것은 아니다

실험은 신뢰할 발급자 역할의 RSA 키 A와 공격자 역할의 RSA 키 B를 로컬에서 생성한다. Apple 키를 다운로드하지 않으며 실제 Apple 토큰을 발급하는 실험이 아니다.

1. B로 서명하면서 payload에 Apple issuer, `lab-client` audience, `victim@example.invalid` 이메일을 넣는다. 내용만 보면 정상처럼 보이지만 A가 서명한 토큰은 아니다.
2. 기존 코드와 동일한 `JWSObject.parse(token).payload.toJSONObject()`로 읽으면 이메일과 subject가 그대로 나온다. **parse는 서명 검증을 호출하지 않는다.**
3. 같은 토큰을 실제 수정 코드 `AppleIdTokenVerifier`에 전달한다. decoder가 A의 공개키를 신뢰하므로 B의 서명은 거부된다.
4. 정상 대조군으로 A의 개인키로 서명한 토큰을 전달하면 통과한다.

예상 출력:

```text
[BEFORE] attacker-signed token parsed; claimed email accepted by parser=victim@example.invalid
[AFTER] SAME attacker-signed token rejected by AppleIdTokenVerifier
[CONTROL] token signed with the trusted LOCAL issuer key accepted
```

이 실험은 파싱 경계의 취약성을 증명한다. 운영 계정 탈취나 실제 로그인 전체를 실행한 결과는 아니다. 실제 서비스는 `AppleTokenConfig`의 Apple JWKS 공개키로 검증한다. 기존 로그인은 파싱한 이메일·subject를 신뢰하고 계정 처리에 사용했으므로 이 경계가 중요하다.

**직접 바꿔 볼 것:** `claims`의 email을 다른 가상 이메일로 바꾸면 이전 parser는 그대로 읽는다. 정상 대조군의 audience를 다른 값으로 바꾸면 서명이 맞아도 새 verifier가 거부해 테스트가 실패한다. `AppleIdTokenVerifierTest`에는 잘못된 issuer·audience·만료·서명·필수 claim 등을 각각 거부하는 별도의 테스트도 있다.

**중단점:** `JWSObject.parse(forged)`의 다음 줄에서 `before`를 확인하고, `AppleIdTokenVerifier.verify`의 `decoder.decode(token)`에서 서명 거부를 확인한다. 예외가 기대된 결과이므로 실험 전체는 녹색으로 통과하는 것이 정상이다.

## 3. UserInjection: 로그인한 사람과 서비스에 전달된 사람이 달랐다

수정은 annotation을 제거하거나 `@RequestParam id`를 받도록 바꾼 것이 아니다. 현재 선언은 `@UserInjection user: Long`이며 변수 이름보다 **resolver가 인증 principal에서 값을 꺼낸다는 점**이 핵심이다.

```kotlin
// 이전: resolver는 Long만 지원해서 이 파라미터를 처리하지 않았다.
fun getMyRecipes(@UserInjection user: User)

// 현재: Long 값을 SecurityContext의 principal에서 가져온다.
fun getMyRecipes(@UserInjection user: Long)
```

실험은 SecurityContext에 이미 인증된 사용자 123을 넣고 다음과 같은 요청을 MockMvc로 처리한다.

```http
GET /api/temp/recipes?id=456&email=other%40example.invalid&nickname=other&socialType=KAKAO
```

| 관찰 지점 | 수정 전 재현 코드 | 현재 실제 컨트롤러·resolver |
|---|---|---|
| 인증된 사용자 | 123 | 123 |
| 요청 파라미터의 id | 456 | 456 |
| 파라미터 처리 | resolver가 User를 지원하지 않아 MVC의 모델 바인딩으로 이동 | resolver가 principal에서 Long ID 반환 |
| 서비스가 받은 사용자 ID | **456** | **123** |
| HTTP 상태 | 200 | 200 |

**HTTP 상태만 보면 차이가 보이지 않는다.** 서비스 호출 인자가 누구인지 확인해야 한다. 테스트의 서비스는 mock이며 실제 다른 사용자의 레시피를 조회하지 않는다. 인증 필터는 이 실험에서 생략하고 바인딩 단계만 분리한다. 전체 JWT 필터를 포함한 검증은 `UserSecurityRegressionTest`가 담당한다.

다른 ID로 반복하려면:

```powershell
& 'C:\Program Files\Java\jdk-17\bin\java.exe' -jar gradle/wrapper/gradle-wrapper.jar :api-user:securityLab -PlabTargetId=789 --console=plain
```

이 경우 이전 서비스 인자는 789로 바뀌지만 현재 서비스 인자는 123으로 유지되어야 한다. 대상은 123과 다른 양수로 지정한다. IntelliJ에서 실행할 때는 VM options에 `-Dlab.targetId=789`를 넣는다.

**중단점:** `LegacyResolver.supportsParameter`에서 `parameter.parameterType`이 User라 false가 되는지 확인한다. 다음으로 `LegacyController.list`의 `user.id`를 본다. 현재 resolver의 `resolveArgument`에서는 `authentication.principal`과 반환 ID를 확인한다. resolver만 새 것으로 바꾸고 User 파라미터를 남겨도, 현재 구현은 오류를 발생시켜 잘못된 모델 바인딩을 막는다.

## 4. 다른 보안 변경도 실행하기

DB 없이 전체 보안 필터와 Apple 검증 테스트를 실행할 수 있다.

```powershell
& 'C:\Program Files\Java\jdk-17\bin\java.exe' -jar gradle/wrapper/gradle-wrapper.jar :api-user:test --tests '*UserSecurityRegressionTest' --tests '*AppleIdTokenVerifierTest' --console=plain
& 'C:\Program Files\Java\jdk-17\bin\java.exe' -jar gradle/wrapper/gradle-wrapper.jar :api-admin:test --console=plain
```

첫 명령은 Refresh로 일반 API 접근 시 401, 잘못된 JWT 401, 디버그 경로 제거, 업로드 소유자 주입 등을 검증한다. 관리자 테스트는 익명·USER 접근 거부, ADMIN 정상 요청과 CSRF 없는 쓰기 거부를 비교한다. 결과는 각 모듈의 `build/reports/tests/test/index.html`에서 확인한다.

파일 소유권과 비공개 레시피의 실제 SQL까지 확인하려면 Docker Desktop의 Linux 엔진을 실행한 뒤 전체 명령을 사용한다.

```powershell
& 'C:\Program Files\Java\jdk-17\bin\java.exe' -jar gradle/wrapper/gradle-wrapper.jar :global:test :api-user:test :api-admin:test --console=plain
```

Testcontainers가 별도 MariaDB를 생성한다. 기존 `localhost:33069` DB와 그 비밀번호는 사용하지 않는다. 자세한 migration·운영 설정 영향은 [즉시 단계 적용 기록](IMMEDIATE_SECURITY_TEST_RECOVERY.md)을 참고한다.

## 5. 비교 범위와 Git

수정 전 기준은 `ef33c2da6d24fd7a00d980e6750643ba34c5f754`다. 다음 명령은 체크아웃을 바꾸지 않고 이전 코드를 보여 준다.

```powershell
git show ef33c2d:global/src/main/kotlin/zipbap/global/global/auth/resolver/UserInjectionArgumentResolver.kt
git show ef33c2d:api-user/src/main/kotlin/zipbap/user/api/auth/service/CustomOAuth2UserService.kt
git diff ef33c2d 'fix/#41' -- api-user/src/main global/src/main
```

실험의 이전 버전은 위 코드에서 원인이 되는 동작만 가져온 비교용 fixture다. 현재 실제 서버 전체와 이전 서버 전체의 E2E 비교는 아니다. 로컬 테스트 성공만으로 운영 배포나 실제 Apple 로그인 성공까지 검증했다고 판단하지 않는다.
