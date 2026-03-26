package example.bookmark;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import example.bookmark.model.Bookmark;
import java.net.URI;
import net.minidev.json.JSONArray;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;


/**
 *
 * @author Branislav Vujanov
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookmarkApplicationTests {
    
    
    @Autowired
    TestRestTemplate restTemplate;

    
    
    @Test
    void shouldReturnBookmarkById() {
        
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("anna", "xyz123") 
                .getForEntity("/bookmarks/100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(100);

        String title = documentContext.read("$.title");
        assertThat(title).isEqualTo("Spring Boot Documentation");

        String url = documentContext.read("$.url");
        assertThat(url).isEqualTo("https://docs.spring.io/spring-boot/docs/current/reference/html/");

        String description = documentContext.read("$.description");
        assertThat(description).isEqualTo("Official reference documentation for Spring Boot");

        String createdAt = documentContext.read("$.createdAt");
        assertThat(createdAt).isEqualTo("2026-03-11T16:45:00");

        Number userId = documentContext.read("$.userId");
        assertThat(userId).isEqualTo(12);
    }

    @Test
    void shouldNotReturnABookmarkWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .getForEntity("/bookmarks/555", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateANewBookmark() {
        Bookmark newBookmark = new Bookmark(
                "Spring Framework Guides",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"    
        );

        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("anna", "xyz123")
                .postForEntity("/bookmarks", newBookmark, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        URI locationOfNewBookmark = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("anna", "xyz123")
                .getForEntity(locationOfNewBookmark, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");
        String url = documentContext.read("$.url");
        String description = documentContext.read("$.description");
        String createdAt = documentContext.read("$.createdAt");
        Number userId = documentContext.read("$.userId");

        assertThat(id).isNotNull();
        assertThat(title).isEqualTo("Spring Framework Guides");
        assertThat(url).isEqualTo("https://spring.io/guides");
        assertThat(description).isEqualTo("Collection of Spring framework tutorials and guides");
        assertThat(createdAt).isNotNull(); 
        assertThat(userId).isEqualTo(12);
    }
    
    @Test
    void shouldNotAllowDuplicateBookmarksForSameUser() {

        Bookmark bookmark = new Bookmark(
                "Spring Framework Guides",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"           
        );

        // first insert
        ResponseEntity<Void> firstResponse = restTemplate
                .withBasicAuth("anna", "xyz123")
                .postForEntity("/bookmarks", bookmark, Void.class);
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // duplicate insert
        ResponseEntity<String> secondResponse = restTemplate
                .withBasicAuth("anna", "xyz123")
                .postForEntity("/bookmarks", bookmark, String.class);
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
    
    @Test
    void shouldNotSaveBookmarkWithoutTitle(){
        Bookmark bookmark = new Bookmark(
                "",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"           
        );

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .postForEntity("/bookmarks", bookmark, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotSaveBookmarkWithoutUrl() {
        Bookmark bookmark = new Bookmark(
                "Spring Framework Guides",
                "",
                "Collection of Spring framework tutorials and guides"           
        );

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .postForEntity("/bookmarks", bookmark, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    void shouldReturnAllBookmarksWhenListIsRequested() {

        ResponseEntity<String> response = restTemplate
                        .withBasicAuth("anna", "xyz123")
                        .getForEntity("/bookmarks", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int bookmarkCount = documentContext.read("$.length()");
        assertThat(bookmarkCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(100, 101, 102);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactlyInAnyOrder(
                "Spring Boot Documentation",
                "Stack Overflow",
                "Baeldung"
        );

        JSONArray urls = documentContext.read("$..url");
        assertThat(urls).containsExactlyInAnyOrder(
                "https://docs.spring.io/spring-boot/docs/current/reference/html/",
                "https://stackoverflow.com",
                "https://www.baeldung.com"
        );

        JSONArray userIds = documentContext.read("$..userId");
        assertThat(userIds).containsOnly(12);
    }
    
    @Test
    void shouldReturnAPageOfBookmarks() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .getForEntity("/bookmarks?page=0&size=2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(2);
    }
    
    @Test
    void shouldReturnASortedPageOfBookmarks() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .getForEntity("/bookmarks?page=0&size=1&sort=id,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read.size()).isEqualTo(1);

        Number id = documentContext.read("$[0].id");
        assertThat(id).isEqualTo(100);
    }

    @Test
    void shouldNotReturnABookmarkWhenUsingBadCredentials() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("BAD-USER", "xyz123")
                .getForEntity("/bookmarks/100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate
                .withBasicAuth("anna", "BAD-PASSWORD")
                .getForEntity("/bookmarks/100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    void shouldNotAuthenticateDisabledUsers() { 
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("mika", "asd131")
                .getForEntity("/bookmarks/200", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    void shouldNotAllowAccessToBookmarksTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .getForEntity("/bookmarks/103", String.class); 
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void moderatorCanGetAnyBookmarkById() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("moderator", "mod123")
                .getForEntity("/bookmarks/103", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(103);

        Number userId = documentContext.read("$.userId");
        assertThat(userId).isEqualTo(11);
    }
    
    @Test
    void moderatorCanGetAllBookmarks() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("moderator", "mod123")
                .getForEntity("/bookmarks?page=0&size=5", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int bookmarkCount = documentContext.read("$.length()");
        assertThat(bookmarkCount).isEqualTo(5);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(100, 101, 102, 103, 200);

        JSONArray userIds = documentContext.read("$..userId");
        assertThat(userIds).containsOnly(11, 12, 20);
    }
    
    @Test
    void moderatorShouldNotCreateBookmarkForThemselves() {
        Bookmark newBookmark = new Bookmark(
                "Spring Framework Guides",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"
        );

        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("moderator", "mod123")
                .postForEntity("/bookmarks", newBookmark, Void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    
    @Test   
    void shouldDeleteAnExistingBookmark() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/100", HttpMethod.DELETE, null, Void.class);    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("anna", "xyz123")
                .getForEntity("/bookmarks/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteABookmarkThatDoesNotExist() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/555", HttpMethod.DELETE, null, Void.class);
       
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test   
    void shouldNotDeleteBookmarkTheyDoNotOwn() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/103", HttpMethod.DELETE, null, Void.class);
       
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void moderatorCanDeleteAnyBookmark() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("moderator", "mod123")
                .exchange("/bookmarks/103", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("moderator", "mod123")
                .getForEntity("/bookmarks/103", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void shouldUpdateAnExistingBookmark() {
        Bookmark update = new Bookmark(
                "Spring Framework Guides",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"           
        );
        HttpEntity<Bookmark> request = new HttpEntity<>(update);
        
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/100", HttpMethod.PUT, request, Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("anna", "xyz123")
                .getForEntity("/bookmarks/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");
        String url = documentContext.read("$.url");
        String description = documentContext.read("$.description");
        Number userId = documentContext.read("$.userId");

        assertThat(id).isEqualTo(100);
        assertThat(title).isEqualTo("Spring Framework Guides");
        assertThat(url).isEqualTo("https://spring.io/guides");
        assertThat(description).isEqualTo("Collection of Spring framework tutorials and guides");
        assertThat(userId).isEqualTo(12);
    }

    @Test
    void shouldNotUpdateABookmarkThatDoesNotExist() {
        Bookmark update = new Bookmark(
                "Spring Framework Guides",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"           
        );
        HttpEntity<Bookmark> request = new HttpEntity<>(update);
        
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/555", HttpMethod.PUT, request, Void.class);
        
       
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotUpdateABookmarkThatIsOwnedBySomeoneElse() {
        Bookmark update = new Bookmark(
                "Spring Framework Guides",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"           
        );
        HttpEntity<Bookmark> request = new HttpEntity<>(update);//First we create the HttpEntity that the exchange() method needs
        
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/103", HttpMethod.PUT, request, Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void moderatorShouldNotUpdateBookmarks() {
         Bookmark update = new Bookmark(
                "Spring Framework Guides",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"           
        );
        HttpEntity<Bookmark> request = new HttpEntity<>(update);
        
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("moderator", "mod123")
                .exchange("/bookmarks/100", HttpMethod.PUT, request, Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    
    @Test
    void shouldNotUpdateBookmarkWithDuplicateUrl() {
        Bookmark update = new Bookmark(
                "Test",
                "https://stackoverflow.com", 
                "desc"
        );
        HttpEntity<Bookmark> request = new HttpEntity<>(update);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/100", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
    
    @Test
    void shouldNotUpdateBookmarkWithoutTitle(){
        Bookmark bookmark = new Bookmark(
                "",
                "https://spring.io/guides",
                "Collection of Spring framework tutorials and guides"           
        );
        HttpEntity<Bookmark> request = new HttpEntity<>(bookmark);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/100", HttpMethod.PUT, request, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotUpdateBookmarkWithoutUrl() {
        Bookmark bookmark = new Bookmark(
                "Spring Framework Guides",
                "",
                "Collection of Spring framework tutorials and guides"           
        );
        HttpEntity<Bookmark> request = new HttpEntity<>(bookmark);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("anna", "xyz123")
                .exchange("/bookmarks/100", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
}
