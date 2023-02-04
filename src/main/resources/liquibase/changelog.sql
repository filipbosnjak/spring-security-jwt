-- liquibase formatted sql

-- changeset filip:1675524441570-1
CREATE TABLE roles
(
    role_id   UUID NOT NULL,
    role_name VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (role_id)
);

-- changeset filip:1675524441570-2
CREATE TABLE user_roles
(
    role_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

-- changeset filip:1675524441570-3
CREATE TABLE users
(
    user_id   UUID NOT NULL,
    user_name VARCHAR(255),
    password  VARCHAR(255),
    email     VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

-- changeset filip:1675524441570-4
ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (role_id);

-- changeset filip:1675524441570-5
ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (user_id);

