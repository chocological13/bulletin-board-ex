package com.finshot.bulletin.posts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.finshot.bulletin.posts.entity.Post;
import com.finshot.bulletin.posts.mapper.PostMapper;
import com.finshot.bulletin.posts.service.impl.PostServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

  @Mock
  private PostMapper postMapper;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  private PostServiceImpl postService;
  List<Post> mockPostList;
  Post mockPost;

  @BeforeEach
  void setUp() {
    postService = new PostServiceImpl(postMapper, passwordEncoder);
    mockPostList = Arrays.asList(
        createPost(1L, "Title 1", "Author 1"),
        createPost(2L, "Title 2", "Author 2")
    );
    mockPost = createPost(1L, "Test title", "Test author");
  }

  @Test
  void getAllPosts_ShouldReturnAllPosts() {
    // Arrange
    when(postMapper.findAll()).thenReturn(mockPostList);

    // Act
    List<Post> postLists = postService.getAllPosts();

    // Assert
    assertEquals(mockPostList, postLists);
    verify(postMapper, times(1)).findAll();
  }

  @Test
  void getPostById_ShouldReturnPostAndIncrementViewCount() {
    // Arrange
    when(postMapper.findAndIncrementViewCount(1L)).thenReturn(mockPost);

    // Act
    Post post = postService.getPostById(1L);

    // Assert
    assertEquals(mockPost, post);
    verify(postMapper, times(1)).findAndIncrementViewCount(1L);
  }

  @Test
  void getPostForEdit_ShouldReturnPostWithoutIncrementingViewCount() {
    // Arrange
    when(postMapper.findById(1L)).thenReturn(mockPost);

    // Act
    Post post = postService.getPostForEdit(1L);

    // Assert
    assertEquals(mockPost, post);
    verify(postMapper, times(1)).findById(1L);
  }

  @Test
  void createPost_ShouldEncodePasswordAndInsertPost() {
    // Arrange
    Post newPost = createPost(null, "New Title", "New Post");
    String rawPassword = "password";
    String hashedPassword = "hashed";
    newPost.setRawPassword(rawPassword);

    when(passwordEncoder.encode(anyString())).thenReturn(hashedPassword);
    when(postMapper.insert(any(Post.class))).thenReturn(1);

    // Act
    boolean created = postService.createPost(newPost);

    // Assert
    assertTrue(created);
    assertEquals(hashedPassword, newPost.getPassword());
    verify(passwordEncoder, times(1)).encode("password");
    verify(postMapper, times(1)).insert(newPost);
  }

  @Test
  void updatePost_WithCorrectPassword_ShouldUpdatePost() {
    // Arrange
    String correctPassword = "password";
    String storedHash = "hashed";
    mockPost.setRawPassword(correctPassword);

    when(postMapper.getPasswordById(1L)).thenReturn(storedHash);
    when(passwordEncoder.matches(correctPassword, storedHash)).thenReturn(true);
    when(postMapper.update(mockPost)).thenReturn(1);

    // Act
    boolean updated = postService.updatePost(mockPost);

    // Assert
    assertTrue(updated);
    verify(postMapper, times(1)).getPasswordById(1L);
    verify(passwordEncoder, times(1)).matches(correctPassword, storedHash);
    verify(postMapper, times(1)).update(mockPost);
  }

  @Test
  void updatePost_WithIncorrectPassword_ShouldNotUpdatePost() {
    // Arrange
    String incorrectPassword = "incorrect";
    String storedHash = "hashed";
    mockPost.setRawPassword(incorrectPassword);

    when(postMapper.getPasswordById(1L)).thenReturn(storedHash);
    when(passwordEncoder.matches(incorrectPassword, storedHash)).thenReturn(false);

    // Act
    boolean updated = postService.updatePost(mockPost);

    // Assert
    assertFalse(updated);
    verify(postMapper, times(1)).getPasswordById(1L);
    verify(passwordEncoder, times(1)).matches(incorrectPassword, storedHash);
    verify(postMapper, times(0)).update(any(Post.class));
  }

  @Test
  void deletePost_WithCorrectPassword_ShouldDeletePost() {
    // Arrange
    String correctPassword = "password";
    String storedHash = "hashed";
    mockPost.setRawPassword(correctPassword);

    when(postMapper.getPasswordById(1L)).thenReturn(storedHash);
    when(passwordEncoder.matches(correctPassword, storedHash)).thenReturn(true);
    when(postMapper.softDelete(1L)).thenReturn(1);

    // Act
    boolean deleted = postService.deletePost(1L, correctPassword);

    // Assert
    assertTrue(deleted);
    verify(postMapper, times(1)).getPasswordById(1L);
    verify(passwordEncoder, times(1)).matches(correctPassword, storedHash);
    verify(postMapper, times(1)).softDelete(1L);
  }

  @Test
  void deletePost_WithIncorrectPassword_ShouldNotDeletePost() {
    // Arrange
    String incorrectPassword = "incorrect";
    String storedHash = "hashed";
    mockPost.setRawPassword(incorrectPassword);

    when(postMapper.getPasswordById(1L)).thenReturn(storedHash);
    when(passwordEncoder.matches(incorrectPassword, storedHash)).thenReturn(false);

    // Act
    boolean deleted = postService.updatePost(mockPost);

    // Assert
    assertFalse(deleted);
    verify(postMapper, times(1)).getPasswordById(1L);
    verify(passwordEncoder, times(1)).matches(incorrectPassword, storedHash);
    verify(postMapper, times(0)).softDelete(anyLong());
  }


  // Helper
  private Post createPost(Long id, String title, String author) {
    Post post = new Post();
    post.setId(id);
    post.setTitle(title);
    post.setAuthor(author);
    post.setContent("Test content");
    return post;
  }
}
