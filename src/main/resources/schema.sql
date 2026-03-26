/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Branislav Vujanov
 * Created: Mar 11, 2026
 */

CREATE TABLE USERS (
    ID          BIGINT PRIMARY KEY AUTO_INCREMENT,
    USERNAME    VARCHAR(50) NOT NULL,
    PASSWORD    VARCHAR(255) NOT NULL,
    ROLE        VARCHAR(20) NOT NULL,
    ENABLED     BOOLEAN NOT NULL
);

CREATE TABLE BOOKMARK (
    ID             BIGINT PRIMARY KEY AUTO_INCREMENT,
    TITLE          VARCHAR(200) NOT NULL,
    URL            VARCHAR(1000) NOT NULL,
    DESCRIPTION    TEXT,
    CREATED_AT     TIMESTAMP NOT NULL,
    USER_ID        BIGINT   NOT NULL,

    CONSTRAINT fk_bookmark_user
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT unique_user_url
        UNIQUE (user_id, url)
);