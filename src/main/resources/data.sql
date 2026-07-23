/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Branislav Vujanov
 * Created: Mar 11, 2026
 */


INSERT INTO users (id, username, password, role, enabled) VALUES
(10, 'moderator', '{noop}mod123', 'ROLE_MODERATOR', true),
(11, 'john', '{noop}abc123', 'ROLE_USER', true),
(12, 'anna', '{noop}xyz123', 'ROLE_USER', true),
(20, 'mika', '{noop}asd131', 'ROLE_USER', false);


INSERT INTO bookmark (id, title, url, description, created_at, user_id) VALUES
(
100,
'Spring Boot Documentation',
'https://docs.spring.io/spring-boot/docs/current/reference/html/',
'Official reference documentation for Spring Boot',
'2026-03-11T16:45:00',
12
),
(
101,
'Stack Overflow',
'https://stackoverflow.com',
'Programming Q&A site',
'2026-03-10T10:00:00',
12
),
(
102,
'Baeldung',
'https://www.baeldung.com',
'Spring tutorials and guides',
'2026-03-09T09:30:00',
12
),
(
103,
'Spring Initializr',
'https://start.spring.io',
'Tool for generating Spring Boot projects',
'2026-03-12T08:15:00',
11
),
(
200,
'Mika Private Bookmark', 
'https://example.com/mika', 
'A bookmark for Mika (disabled user)', 
'2026-03-16T15:30:00', 
20);

ALTER TABLE bookmark ALTER COLUMN id RESTART WITH 201;