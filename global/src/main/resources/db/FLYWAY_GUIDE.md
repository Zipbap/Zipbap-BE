# 📝 Flyway DB Migration Guide
last update : 2026.04.21

본 프로젝트는 데이터베이스 스키마의 버전 관리와 안정적인 배포를 위해 Flyway를 사용합니다. 모든 DB 구조 변경은 SQL 스크립트를 통해 관리됩니다.

해당 기능을 통해 로컬, 운영에서 동일한 DDL로 DB가 생성되도록 할 수 있습니다.


1. 기본 원칙
   불변성 (Immutability): 한 번 배포된(실행된) 스크립트는 절대 수정하지 않습니다. 수정이 필요하면 새로운 버전의 파일을 생성합니다.

    순차성 (Ordering): 파일명의 버전 숫자를 기준으로 순차적으로 실행됩니다.

    단일 책임: 하나의 스크립트 파일에는 하나의 논리적 변경 사항(테이블 생성, 인덱스 추가 등)만 담는 것을 권장합니다.


2. 파일 명명 규칙
   스크립트 파일은 global 모듈의 src/main/resources/db/migration/ 경로에 위치해야 합니다.

    형식: V<Version>__<Description>.sql (언더바 2개 주의)

    V: Version (정수 또는 타임스탬프)

    __: 구분자 (Double Underscore)

    Description: 변경 내용 요약 (snake_case)

    예시:

    V1__init_schema.sql (초기 스키마 생성)

    V2__add_index_to_recipe.sql (레시피 인덱스 추가)

    V202604211530__alter_user_nickname_length.sql (타임스탬프 활용 방식)


3. 핵심 설정 (application.yml)
   프로젝트 실행 시 적용되는 주요 옵션입니다.

    YAML


      spring:
        flyway:
          enabled: true              # Flyway 활성화
          baseline-on-migrate: true  # 기존 DB가 있을 경우 기준점(V0) 생성
          baseline-version: 0        # 기준점 버전 설정
          locations: classpath:db/migration # 스크립트 위치

4. 실무 운영 프로세스
   새로운 변경 사항 반영 시

   db/migration 경로에 다음 버전 번호로 SQL 파일을 생성합니다.

    로컬에서 애플리케이션을 구동하여 스크립트가 정상 실행되는지 확인합니다.
    
    flyway_schema_history 테이블에 해당 행이 success로 들어갔는지 확인합니다.
    
    JPA Entity 수정: SQL 스크립트의 변경 내용과 JPA 엔티티 클래스의 필드가 일치하도록 수정합니다.

    배포 시 주의사항

    Checksum mismatch: 이미 실행된 파일의 내용을 수정하고 서버를 띄우면 체크섬 오류가 발생하며 서버가 뜨지 않습니다.
    
    해결법: 수정 전으로 파일을 복구하거나, 정말 필요한 경우에만 flyway repair 명령어를 사용해야 합니다. (운영 환경에서는 절대 지양)


5. FAQ
    
    Q: 실수로 스크립트를 잘못 썼는데 이미 push 했어요.

    A: 해당 파일을 수정하지 마세요. 잘못된 부분을 바로잡는 새로운 버전의 스크립트(V+1)를 작성하여 배포하십시오.

    Q: MariaDB 전용 문법을 써도 되나요?

    A: 네, 우리 프로젝트는 flyway-database-mariadb 의존성을 사용하므로 MariaDB 특화 문법(예: ON UPDATE CURRENT_TIMESTAMP)을 적극적으로 활용합니다.