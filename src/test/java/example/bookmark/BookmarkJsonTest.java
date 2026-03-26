/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.bookmark;

import example.bookmark.model.Bookmark;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

/**
 *
 * @author Branislav Vujanov
 */
@JsonTest
public class BookmarkJsonTest {

    @Autowired
    private JacksonTester<Bookmark> json;
    
    @Autowired
    private JacksonTester<List<Bookmark>> jsonList;
    
    private List <Bookmark> bookmarks;
    
    
    private Bookmark createBookmark(Long id, String title, String url, String description, Long userId, 
                                                                                     LocalDateTime createdAt) {
        Bookmark bookmark = new Bookmark(title, url, description);
        bookmark.setId(id);
        bookmark.setUserId(userId);
        bookmark.setCreatedAt(createdAt);
        return bookmark;
    }
    
    @BeforeEach
    void setUp() {
        long userId = 12L;
        bookmarks = new ArrayList<>();
        bookmarks.add(createBookmark(100L,
                "Spring Boot Documentation",
                "https://docs.spring.io/spring-boot/docs/current/reference/html/",
                "Official reference documentation for Spring Boot",
                userId,
                LocalDateTime.of(2026, 3, 11, 16, 45, 0)
        ));
        bookmarks.add(createBookmark(101L,
                "Stack Overflow",
                "https://stackoverflow.com",
                "Programming Q&A site",
                userId,
                LocalDateTime.of(2026, 3, 10, 10, 0, 0)
        ));
        bookmarks.add(createBookmark(102L,
                "Baeldung",
                "https://www.baeldung.com",
                "Spring tutorials and guides",
                userId,
                LocalDateTime.of(2026, 3, 9, 9, 30, 0)
        ));
    }

    
    @Test
    void shouldSerializeBookmarkCorrectly() throws Exception {

        Bookmark bookmark = bookmarks.get(0);
        var result = json.write(bookmark);
 
//        assertThat(result).isStrictlyEqualToJson("single.json");

        // JSON path assertions
        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("@.title")
                          .isEqualTo("Spring Boot Documentation");
        assertThat(result).extractingJsonPathStringValue("@.url")
                          .isEqualTo("https://docs.spring.io/spring-boot/docs/current/reference/html/");
        assertThat(result).extractingJsonPathStringValue("@.description")
                          .isEqualTo("Official reference documentation for Spring Boot");
        assertThat(result).extractingJsonPathStringValue("@.createdAt")
                          .isEqualTo("2026-03-11T16:45:00");
        assertThat(result).extractingJsonPathNumberValue("@.userId").isEqualTo(12);
}

    @Test
    void bookmarkDeserializationTest() throws IOException {

        String expected = """
    {
      "id": 100,
      "title": "Spring Boot Documentation",
      "url": "https://docs.spring.io/spring-boot/docs/current/reference/html/",
      "description": "Official reference documentation for Spring Boot",
      "createdAt": "2026-03-11T16:45:00",
      "userId": 12
    }
    """;

        Bookmark bookmark = json.parseObject(expected);

        assertThat(bookmark.getId()).isEqualTo(100L);
        assertThat(bookmark.getTitle()).isEqualTo("Spring Boot Documentation");
        assertThat(bookmark.getUrl())
                .isEqualTo("https://docs.spring.io/spring-boot/docs/current/reference/html/");
        assertThat(bookmark.getDescription())
                .isEqualTo("Official reference documentation for Spring Boot");
        assertThat(bookmark.getCreatedAt())
                .isEqualTo(LocalDateTime.of(2026, 3, 11, 16, 45, 0));
        assertThat(bookmark.getUserId()).isEqualTo(12L);
    }

    @Test
    void bookmarkListSerializationTest() throws IOException {
        assertThat(jsonList.write(bookmarks)).isStrictlyEqualToJson("list.json");
    } 
   
    @Test
    void bookmarkListDeserializationTest() throws IOException {
        String expected = """
         [
           {
             "id": 100,
             "title": "Spring Boot Documentation",
             "url": "https://docs.spring.io/spring-boot/docs/current/reference/html/",
             "description": "Official reference documentation for Spring Boot",
             "createdAt": "2026-03-11T16:45:00",
             "userId": 12
           },
           {
             "id": 101,
             "title": "Stack Overflow",
             "url": "https://stackoverflow.com",
             "description": "Programming Q&A site",
             "createdAt": "2026-03-10T10:00:00",
             "userId": 12
           },
           {
             "id": 102,
             "title": "Baeldung",
             "url": "https://www.baeldung.com",
             "description": "Spring tutorials and guides",
             "createdAt": "2026-03-09T09:30:00",
             "userId": 12
           }
         ]
         
         """;
        assertThat(jsonList.parse(expected)).isEqualTo(bookmarks);
    }
}
