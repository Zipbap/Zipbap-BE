-- 1. 유저 마스터
CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(100) NOT NULL,
                       nickname VARCHAR(30) NOT NULL,
                       status_message VARCHAR(100),
                       profile_image VARCHAR(255),
                       social_type ENUM('APPLE', 'KAKAO') NOT NULL,
                       is_private TINYINT(1) NOT NULL DEFAULT 0, -- BIT 대신 직관적인 TINYINT 사용
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                       deleted_at TIMESTAMP NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT UK_user_email UNIQUE (email)
) ENGINE=InnoDB;

-- 2. 인증 관리
CREATE TABLE refresh_token (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               user_id BIGINT NOT NULL,
                               refresh_token VARCHAR(255) NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                               deleted_at TIMESTAMP NULL,
                               PRIMARY KEY (id),
                               CONSTRAINT UK_token_user_id UNIQUE (user_id),
                               CONSTRAINT FK_token_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. 카테고리 마스터 테이블 (Unique 제약 및 자동 시간 설정)
CREATE TABLE category_cooking_time (
                                       id BIGINT NOT NULL AUTO_INCREMENT,
                                       cooking_time VARCHAR(50) NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                                       deleted_at TIMESTAMP NULL,
                                       PRIMARY KEY (id),
                                       CONSTRAINT UK_cat_time UNIQUE (cooking_time)
) ENGINE=InnoDB;

CREATE TABLE category_cooking_type (
                                       id BIGINT NOT NULL AUTO_INCREMENT,
                                       type VARCHAR(50) NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                                       deleted_at TIMESTAMP NULL,
                                       PRIMARY KEY (id),
                                       CONSTRAINT UK_cat_type UNIQUE (type)
) ENGINE=InnoDB;

CREATE TABLE category_headcount (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    headcount VARCHAR(50) NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                                    deleted_at TIMESTAMP NULL,
                                    PRIMARY KEY (id),
                                    CONSTRAINT UK_cat_headcount UNIQUE (headcount)
) ENGINE=InnoDB;

CREATE TABLE category_level (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                level VARCHAR(50) NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                                deleted_at TIMESTAMP NULL,
                                PRIMARY KEY (id),
                                CONSTRAINT UK_cat_level UNIQUE (level)
) ENGINE=InnoDB;

CREATE TABLE category_main_ingredient (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          ingredient VARCHAR(50) NOT NULL,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                                          deleted_at TIMESTAMP NULL,
                                          PRIMARY KEY (id),
                                          CONSTRAINT UK_cat_ingredient UNIQUE (ingredient)
) ENGINE=InnoDB;

CREATE TABLE category_method (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 method VARCHAR(50) NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                                 deleted_at TIMESTAMP NULL,
                                 PRIMARY KEY (id),
                                 CONSTRAINT UK_cat_method UNIQUE (method)
) ENGINE=InnoDB;

CREATE TABLE category_situation (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    situation VARCHAR(50) NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                                    deleted_at TIMESTAMP NULL,
                                    PRIMARY KEY (id),
                                    CONSTRAINT UK_cat_situation UNIQUE (situation)
) ENGINE=InnoDB;

-- 4. 커스텀 카테고리 (유저별 그룹화)
CREATE TABLE my_category (
                             id VARCHAR(30) NOT NULL,
                             user_id BIGINT NOT NULL,
                             name VARCHAR(20) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                             deleted_at TIMESTAMP NULL,
                             PRIMARY KEY (id),
                             CONSTRAINT UK_my_cat_user_name UNIQUE (user_id, name),
                             CONSTRAINT FK_my_cat_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. 레시피 본문 (성능 최적화 인덱스 반영)
CREATE TABLE recipe (
                        id VARCHAR(18) NOT NULL,
                        user_id BIGINT NOT NULL,
                        my_category_id VARCHAR(30),
                        category_cooking_situation_id BIGINT,
                        category_cooking_time_id BIGINT,
                        category_cooking_type_id BIGINT,
                        category_headcount_id BIGINT,
                        category_level_id BIGINT,
                        category_main_ingredient_id BIGINT,
                        category_method_id BIGINT,
                        title VARCHAR(100),
                        subtitle VARCHAR(100),
                        introduction VARCHAR(300),
                        ingredient_info VARCHAR(1000),
                        kick VARCHAR(300),
                        thumbnail VARCHAR(200),
                        video VARCHAR(200),
                        view_count BIGINT UNSIGNED NOT NULL DEFAULT 0, -- 음수 방지 및 기본값
                        is_private TINYINT(1) NOT NULL DEFAULT 0,
                        recipe_status ENUM('ACTIVE', 'TEMPORARY') NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                        deleted_at TIMESTAMP NULL,
                        PRIMARY KEY (id),
    -- 복합 인덱스: 상태별 최신순 조회 최적화
                        INDEX IDX_recipe_status_created (recipe_status, created_at DESC),
    -- 외래키 인덱스 자동생성되지만 명시적 관리
                        INDEX IDX_recipe_user_id (user_id),
                        CONSTRAINT FK_recipe_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                        CONSTRAINT FK_recipe_my_cat FOREIGN KEY (my_category_id) REFERENCES my_category (id) ON DELETE SET NULL,
                        CONSTRAINT FK_recipe_cat_sit FOREIGN KEY (category_cooking_situation_id) REFERENCES category_situation (id),
                        CONSTRAINT FK_recipe_cat_time FOREIGN KEY (category_cooking_time_id) REFERENCES category_cooking_time (id),
                        CONSTRAINT FK_recipe_cat_type FOREIGN KEY (category_cooking_type_id) REFERENCES category_cooking_type (id),
                        CONSTRAINT FK_recipe_cat_head FOREIGN KEY (category_headcount_id) REFERENCES category_headcount (id),
                        CONSTRAINT FK_recipe_cat_level FOREIGN KEY (category_level_id) REFERENCES category_level (id),
                        CONSTRAINT FK_recipe_cat_ingre FOREIGN KEY (category_main_ingredient_id) REFERENCES category_main_ingredient (id),
                        CONSTRAINT FK_recipe_cat_method FOREIGN KEY (category_method_id) REFERENCES category_method (id)
) ENGINE=InnoDB;

-- 6. 조리 순서
CREATE TABLE cooking_order (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               recipe_id VARCHAR(18) NOT NULL,
                               turn INTEGER NOT NULL,
                               description VARCHAR(500),
                               image VARCHAR(300),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                               deleted_at TIMESTAMP NULL,
                               PRIMARY KEY (id),
                               CONSTRAINT UK_order_recipe_turn UNIQUE (recipe_id, turn),
                               CONSTRAINT FK_order_recipe_id FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. 유저 활동 (조회 성능을 고려한 복합 인덱스 위주)
CREATE TABLE recipe_like (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             recipe_id VARCHAR(18) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                             deleted_at TIMESTAMP NULL,
                             PRIMARY KEY (id),
                             CONSTRAINT UK_like_user_recipe UNIQUE (user_id, recipe_id),
                             CONSTRAINT FK_like_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                             CONSTRAINT FK_like_recipe_id FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE comment (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         recipe_id VARCHAR(18) NOT NULL,
                         parent_id BIGINT,
                         content VARCHAR(300) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                         deleted_at TIMESTAMP NULL,
                         PRIMARY KEY (id),
    -- 복합 인덱스: 특정 레시피의 댓글을 최신순(또는 과거순)으로 정렬 조회 최적화
                         INDEX IDX_comment_recipe_created (recipe_id, created_at),
                         INDEX IDX_comment_user_id (user_id),
                         CONSTRAINT FK_comment_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                         CONSTRAINT FK_comment_recipe_id FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE,
                         CONSTRAINT FK_comment_parent_id FOREIGN KEY (parent_id) REFERENCES comment (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE bookmark (
                          id VARCHAR(40) NOT NULL,
                          user_id BIGINT NOT NULL,
                          recipe_id VARCHAR(18) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                          deleted_at TIMESTAMP NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT UK_bookmark_user_recipe UNIQUE (user_id, recipe_id),
                          CONSTRAINT FK_bookmark_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                          CONSTRAINT FK_bookmark_recipe_id FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE follow (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        follower BIGINT NOT NULL,
                        following BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                        deleted_at TIMESTAMP NULL,
                        PRIMARY KEY (id),
    -- 복합 인덱스: 나를 팔로우하는 사람, 내가 팔로우하는 사람 검색 최적화
                        INDEX IDX_follow_following (following, follower),
                        CONSTRAINT UK_follow_pair UNIQUE (follower, following),
                        CONSTRAINT FK_follow_follower FOREIGN KEY (follower) REFERENCES users (id) ON DELETE CASCADE,
                        CONSTRAINT FK_follow_following FOREIGN KEY (following) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE file (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      user_id BIGINT,
                      recipe_id VARCHAR(18),
                      file_url VARCHAR(255) NOT NULL,
                      status ENUM('FINALIZED', 'TEMPORARY_UPLOAD', 'UNTRACKED') NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
                      deleted_at TIMESTAMP NULL,
                      PRIMARY KEY (id),
                      CONSTRAINT FK_file_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                      CONSTRAINT FK_file_recipe_id FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE
) ENGINE=InnoDB;