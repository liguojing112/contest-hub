CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    name VARCHAR(50),
    role VARCHAR(20) NOT NULL,
    college VARCHAR(100),
    major VARCHAR(100),
    class_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    work_time VARCHAR(100),
    avatar VARCHAR(500),
    enabled BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS competition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(50),
    category_id BIGINT,
    description TEXT,
    creator VARCHAR(50),
    creator_id BIGINT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    register_deadline TIMESTAMP,
    max_team_size INT DEFAULT 1,
    registered INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'pending',
    reviewers VARCHAR(500),
    heat_value DOUBLE DEFAULT 0,
    view_count BIGINT DEFAULT 0,
    message_count BIGINT DEFAULT 0,
    upload_works BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    competition_id BIGINT NOT NULL,
    team_id BIGINT,
    status VARCHAR(20) DEFAULT 'pending',
    register_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    competition_id BIGINT NOT NULL,
    leader_id BIGINT NOT NULL,
    team_code VARCHAR(20) NOT NULL UNIQUE,
    declaration VARCHAR(500),
    status VARCHAR(20) DEFAULT 'recruiting',
    advisor_id BIGINT,
    audit_status VARCHAR(20) DEFAULT 'pending',
    audit_comment VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS team_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) DEFAULT 'member',
    status VARCHAR(20) DEFAULT 'pending',
    join_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    registration_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    score INT,
    comment TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    review_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS appeal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    content TEXT,
    reply TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reply_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    status VARCHAR(20) DEFAULT 'published',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(50),
    content TEXT NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    sender_id BIGINT,
    sender_name VARCHAR(50),
    type VARCHAR(20),
    content TEXT,
    status VARCHAR(20) DEFAULT 'unread',
    send_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
