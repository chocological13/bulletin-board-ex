package com.finshot.bulletin.posts.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.finshot.bulletin.config.TestConfig;
import com.finshot.bulletin.posts.entity.Post;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
@Sql("/sql/test-schema.sql")
public class PostMapperTest {

  @Autowired
  private PostMapper postMapper;

  @Test
  void findAll_ShouldReturnAllNonDeletedPost() {
    // Act
    List<Post> posts = postMapper.findAll();
    System.out.println(posts);

    // Assert
    assertNotNull(posts);
    assertEquals(2, posts.size());
  }

  @Test
  void findById_WithExistingId_ShouldReturnPost() {
    // Act
    Post post = postMapper.findById(1L);

    // Assert
    assertNotNull(post);
    assertEquals(1L, post.getId());
    assertEquals("Test Title 1", post.getTitle());
  }

  @Test
  void findById_WithNonexistentId_ShouldNotReturnPost() {
    // Act
    Post nonexistentPost = postMapper.findById(999L);

    // Assert
    assertNull(nonexistentPost);
  }


  @Test
  void findAndIncrementViewCount_ShouldIncrementViewCountAndReturnPost() {
    // Arrange
    Post initialPost = postMapper.findById(1L);
    int initialViewCount = initialPost.getViewCount();

    // Act
    Post actualPost = postMapper.findAndIncrementViewCount(1L);

    // Assert
    assertNotNull(actualPost);
    assertEquals(initialViewCount + 1, actualPost.getViewCount());
  }

  @Test
  void insert_ShouldCreateNewPostAndReturnGeneratedId() {
    // Arrange
    Post newPost = new Post();
    newPost.setTitle("New Post");
    newPost.setAuthor("Author");
    newPost.setPassword("password");
    newPost.setContent("Test content");

    // Act
    int result = postMapper.insert(newPost);

    // Assert
    assertEquals(1, result);
    assertNotNull(newPost.getId());

    // Test if it can be retrieved
    Post fetchedNewPost = postMapper.findById(newPost.getId());
    assertNotNull(fetchedNewPost);
    assertEquals("New Post", fetchedNewPost.getTitle());
  }

  @Test
  void update_ShouldUpdatePostFields() {
    // Arrange
    Post existingPost = postMapper.findById(1L);
    String initialTitle = existingPost.getTitle();
    existingPost.setTitle("Updated Title");
    existingPost.setContent("Updated content");

    // Act
    int result = postMapper.update(existingPost);

    // Assert
    assertEquals(1, result);

    // Verify persistence
    Post updatedPost = postMapper.findById(1L);
    assertNotEquals(initialTitle, updatedPost.getTitle());
    assertEquals("Updated Title", updatedPost.getTitle());
    assertEquals("Updated content", updatedPost.getContent());

  }

  @Test
  void softDelete_ShouldMarkPostAsDeleted() {
    // Act
    int result = postMapper.softDelete(1L);

    // Assert
    assertEquals(1, result);

    // Verify post is no longer retrievable
    Post deletedPost = postMapper.findById(1L);
    assertNull(deletedPost);
  }

  @Test
  void getPasswordById_ShouldReturnStoredPassword() {
    // Act
    String password = postMapper.getPasswordById(1L);

    // Assert
    assertNotNull(password);
  }
}
