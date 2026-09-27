# 피드 C 구현 및 검증 — 2026-09-27

브랜치: `codex/feed-c-aggregate-split`. B 기준 커밋: `4090c14`.

## 구현 범위

- `325124f`: Clock 주입. 운영은 실제 시간, 테스트는 2026-09-26 기준. TODAY는 KST `[당일 00:00, 다음 날 00:00)`.
- C 핵심: ALL/TODAY/FOLLOWING은 활동 JOIN 없이 페이지를 선택한 뒤 해당 ID의 좋아요·북마크·댓글을 각각 GROUP BY한다.
- HOT/RECOMMEND는 전체 공개 후보에 순위 관계 하나만 LEFT JOIN한다. 순위를 계산한 뒤 페이지를 선택하고, 이미 계산한 count를 재사용한다.
- 페이지 순서대로 count Map을 결합한다. 없는 활동은 0. B의 개인화 ID 일괄 조회 2회는 유지한다.
- 카테고리 INNER JOIN, 공개 범위, 검색 우선순위, 전체 count, 댓글·활동 삭제 처리의 기존 의미를 보존한다. 상세 API는 변경하지 않는다.
- 사용자 조회 포함 SELECT 수: 일반 목록 **8**, HOT/RECOMMEND **7**, 빈 페이지 **3**. 엔티티 반복 로딩은 없다.

## C 핵심 검증 결과

MariaDB 13.0.2 Testcontainers: global 59개, api-user 79개, api-admin 8개, 총 **146개 통과**. 피드 통합 테스트 36개에는 고정된 기존 쿼리와의 응답 비교, 오래된 인기 글, 활동 0개, 검색 우선순위, 비공개 작성자·팔로우·삭제·임시 글, 카테고리 NULL, 자식 댓글·soft delete 활동, TODAY 경계, SQL 수가 포함된다.

기존 쿼리는 `global/src/testFixtures/kotlin/support/query/LegacyFeedListQuery.kt`에 고정했다. 새 repository를 기대값 계산에 재사용하지 않는다. 이 fixture와 Querydsl 의존성은 테스트 전용이다.

### 저장된 A/B 응답과 실제 HTTP 대조

기존 서버와 별도로 `127.0.0.1:19090`에 C JAR를 실행했다. 경량 DB `zipbap_feed_bench_light_1k`, 사용자 1·2, 기존 20개 GET 조합을 순차 요청했다. 스키마 검증만 활성화하고 Flyway 실행은 비활성화했다. 검증 서버는 수집 후 종료했다.

- A→C, B→C 각각 **16/20 배열까지 완전 일치**.
- 나머지 4개: 사용자1 ALL page0 size10/20/30, 사용자2 ALL page0 size20.
- 해당 차이는 `RC-B0000000001`↔`RC-B0000000010`, `RC-B0000000011`↔`RC-B0000000012`의 **동일 createdAt 내 순서**뿐이다. 각 쌍의 시각은 각각 `2026-09-26T03:00:00`, `2026-09-25T03:00:00`이다.
- **20/20 ID별 전체 필드·집계·개인화·페이지 메타데이터 일치**. 집합 비교만으로 정렬이 같다고 판단하지 않았으며 원래 배열 차이도 보존했다.
- 기존 정렬은 createdAt만으로 동점을 해소하지 않아 실행계획 변경으로 순서가 바뀔 수 있다. C 집계 분리와 별도 커밋으로 `id DESC`를 추가한다.
- SQL 로그에서 20개 요청 **132 SELECT**를 분류했고 일반 8/인기 7/빈 페이지 3을 확인했다. size10/20/30에서 일반 목록 SQL 수는 동일하다.

이 수집은 localhost와 SQL DEBUG 로그를 사용한 정확성 확인이다. 저장된 Mac JMeter A/B 지연과 직접 비교할 성능 측정값이 아니다.

### 실제 SQL EXPLAIN / ANALYZE

HTTP 서버가 생성한 SQL과 요청·응답으로 확인한 바인딩을 보관했다. ALL/HOT/RECOMMEND 각각 user1/page0/size20의 모든 구성 SELECT(8+7+7=22개)를 분석했다. 각 SQL은 EXPLAIN 1회, ANALYZE 3회이며 읽기 전용 트랜잭션, `max_statement_time=5`, `max_tmp_session_space_usage=67108864`, 세션 UTC, DB REPEATABLE-READ였다. 부하 테스트는 병행하지 않았다.

| 항목 | C 핵심에서 관측한 결과 |
| --- | --- |
| ALL 페이지 시작 테이블 | recipe, `IDX_recipe_status_created` range |
| ALL 페이지 recipe 실측 | r_loops=1, r_rows=29 → 응답 20개 |
| ALL 페이지 정렬·임시 테이블 | filesort·temporary_table 노드 없음 |
| ALL 페이지 시간 3회 | 0.4435 / 0.2313 / 0.1288ms |
| 페이지 활동 집계 r_rows | 좋아요 71 / 북마크 44 / 댓글 60 (각 r_loops=1) |
| ALL 전체 count | recipe 1,000행, 약 0.99~1.16ms; 페이지 제한을 적용하지 않음 |
| ALL 8개 SQL 시간 합계 3회 | 1.7665 / 1.5232 / 1.2912ms |
| HOT 페이지 시간 3회 | 18.0410 / 8.9375 / 7.6218ms |
| HOT 7개 SQL 시간 합계 3회 | 19.2852 / 10.1291 / 8.8620ms |
| RECOMMEND 페이지 시간 3회 | 6.1999 / 6.2706 / 6.6718ms |
| RECOMMEND 7개 SQL 시간 합계 3회 | 8.3905 / 8.6319 / 7.9614ms |
| 디스크 임시 테이블 | 이 세션의 Created_tmp_disk_tables는 계속 0 |

시간 합계는 **분리 실행한 SQL별 최상위 r_total_time_ms를 합한 값**이다. 실제 HTTP 지연이나 트랜잭션 왕복 시간을 포함하지 않고, 하위 노드 시간을 중복 합산하지 않는다. 3회 측정에는 캐시 상태·변동이 포함되며 p95/p99가 아니다. 기존 A 목록 113~176ms와 쿼리 구조의 개선 방향은 확인했지만, 최종 개선율은 동일 조건의 JMeter 반복 실험으로 판단한다.

HOT/RECOMMEND에는 전체 후보 순위를 위한 filesort·임시 집계가 남는다. 좋아요/북마크 r_loops=722이며, 관계 하나만 읽는다. ALL에서 이미 기존 인덱스 사용과 조기 종료가 확인되어 ID 선조회·추가 DTO 조회나 인덱스 강제를 추가하지 않았다.

## 검증 원본

아래 파일은 로컬 `build` 산출물이다. Git에 포함되지 않으므로 장기 보관 시 별도로 압축한다. 토큰 CSV는 공유하지 않는다.

- `build/feed-benchmark/C-core/build-metadata.json`: 실행 JAR SHA-256 및 조건
- `build/feed-benchmark/C-core/application.log`: 실제 생성 SQL
- `build/feed-benchmark/C-core/sql-counts.json`: 요청별 SELECT 수
- `build/feed-benchmark/C-core/queries.json`: SQL 템플릿·바인딩·치환 SQL
- `build/feed-benchmark/C-core/EXPLAIN_ANALYZE.sql`: 실제 실행한 분석 SQL 전체
- `build/feed-benchmark/C-core/EXPLAIN_ANALYZE.out`, `plans.json`, `plan-summary.json`: 실행계획 원본·구조화 결과
- `build/feed-benchmark/C-core/response-comparison.json`: A/C, B/C 차이와 SHA-256
- `build/feed-benchmark/responses/C/core-20260927`: C 핵심의 HTTP 응답 원문 20개

## 후속 구현·측정 구분

집계 분리의 비교 기준 커밋을 남긴 뒤, 동점 정렬과 피드 전용 size 최대 50을 별도 커밋으로 적용한다. 변경 후 같은 생성 SQL의 실행계획을 다시 확인한다. 실제 JMeter 측정은 Mac → Windows Spring → Windows Docker MariaDB, 동일 경량 DB·로그 OFF·60초 워밍업 조건으로 진행한다. size20/u1, size20/u5를 우선하고 size10/u1·size30/u1을 보조로 수집한다. API 지연·처리량·DB CPU 시계열과 실행 커밋을 같이 기록한다.
