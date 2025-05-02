package com.finshot.bulletin.posts.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.finshot.bulletin.config.SecurityTestConfig;
import com.finshot.bulletin.posts.entity.Post;
import com.finshot.bulletin.posts.service.PostService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PostController.class)
@Import(SecurityTestConfig.class)
public class PostControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PostService postService;

  private List<Post> mockPostList;
  private Post mockPost;

  @BeforeEach
  void setUp() {
    mockPost = createPost(1L, "Test Title", "Test Author", "Test content");

    mockPostList = Arrays.asList(
        mockPost,
        createPost(2L, "Another Title", "Another Author", "Another content")
    );
  }

  @Test
  void listPosts_ShouldReturnAllPosts() throws Exception {
    // Arrange
    when(postService.getAllPosts()).thenReturn(mockPostList);

    // Act & Assert
    mockMvc.perform(get("/posts"))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/list"))
        .andExpect(model().attribute("posts", hasSize(2)))
        .andExpect(model().attribute("posts", is(mockPostList)));
  }

  // Helper
  private Post createPost(Long id, String title, String author, String content) {
    Post post = new Post();
    post.setId(id);
    post.setTitle(title);
    post.setAuthor(author);
    post.setContent(content);
    post.setViewCount(0);
    return post;
  }

  @Test
  void viewPost_WithValidId_ShouldShowPost() throws Exception {
    // Arrange
    when(postService.getPostById(1L)).thenReturn(mockPost);

    // Act & Assert
    mockMvc.perform(get("/posts/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/view"))
        .andExpect(model().attribute("post", hasProperty("id", is(1L))))
        .andExpect(model().attribute("post", hasProperty("title", is("Test Title"))));
  }

  @Test
  void viewPost_FromEdit_ShouldCallGetPostForEdit() throws Exception {
    // Arrange
    when(postService.getPostForEdit(1L)).thenReturn(mockPost);

    // Act & Assert
    mockMvc.perform(get("/posts/1").param("fromEdit", "true"))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/view"))
        .andExpect(model().attribute("post", hasProperty("id", is(1L))))
        .andExpect(model().attribute("post", hasProperty("title", is("Test Title"))));

    verify(postService, times(1)).getPostForEdit(1L);
  }

  @Test
  void viewPost_WithInvalidId_ShouldReturn404() throws Exception {
    // Arrange
    when(postService.getPostById(999L)).thenReturn(null);

    // Act & Assert
    mockMvc.perform(get("/posts/999"))
        .andExpect(status().isOk())
        .andExpect(view().name("error/404"));
  }

  @Test
  void showCreatePostForm_ShouldDisplayEmptyForm() throws Exception {
    mockMvc.perform(get("/posts/create"))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/form"))
        .andExpect(model().attributeExists("post"))
        .andExpect(model().attribute("isEdit", is(false)));
  }

  @Test
  void createPost_WithValidData_ShouldRedirectToPostsList() throws Exception {
    // Arrange
    when(postService.createPost(any(Post.class))).thenReturn(true);

    // Act & Assert
    mockMvc.perform(post("/posts/create")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("title", "New Post")
            .param("author", "New Author")
            .param("rawPassword", "password123")
            .param("content", "New content"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts"));
  }

  @Test
  void createPost_WithInvalidData_ShouldReturnFormWithErrors() throws Exception {
    mockMvc.perform(post("/posts/create")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("title", "")  // Empty title to trigger validation error
            .param("author", "New Author")
            .param("rawPassword", "password123")
            .param("content", "New content"))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/form")); // Back to form with errors
  }

  @Test
  void createPost_WhenServiceFails_ShouldRedirectWithError() throws Exception {
    // Arrange
    when(postService.createPost(any(Post.class))).thenReturn(false);

    // Act & Assert
    mockMvc.perform(post("/posts/create")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("title", "New Post")
            .param("author", "New Author")
            .param("rawPassword", "password123")
            .param("content", "New content"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts/form"))
        .andExpect(flash().attribute("error", containsString("Something went wrong")));
  }

  @Test
  void showEditPostForm_WithValidId_ShouldShowFormWithPost() throws Exception {
    // Arrange
    when(postService.getPostForEdit(1L)).thenReturn(mockPost);

    // Act & Assert
    mockMvc.perform(get("/posts/1/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/form"))
        .andExpect(model().attribute("post", hasProperty("id", is(1L))))
        .andExpect(model().attribute("isEdit", is(true)));
  }

  @Test
  void showEditPostForm_WithInvalidId_ShouldReturn404() throws Exception {
    // Arrange
    when(postService.getPostForEdit(999L)).thenReturn(null);

    // Act & Assert
    mockMvc.perform(get("/posts/999/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("error/404"));
  }

  @Test
  void updatePost_WithValidDataAndPassword_ShouldRedirectToViewPost() throws Exception {
    // Arrange
    when(postService.updatePost(any(Post.class))).thenReturn(true);

    // Act & Assert
    mockMvc.perform(post("/posts/1/edit")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .param("title", "Updated Title")
          .param("author", "Test Author")
          .param("rawPassword", "password123")
          .param("content", "Updated content"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts/1?fromEdit=true"))
        .andExpect(flash().attribute("success", containsString("successfully")));
  }

  @Test
  void updatePost_WithInvalidPassword_ShouldRedirectWithError() throws Exception {
    // Arrange
    when(postService.updatePost(any(Post.class))).thenReturn(false);

    // Act & Assert
    mockMvc.perform(post("/posts/1/edit")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("title", "Updated Title")
            .param("author", "Test Author")
            .param("rawPassword", "wrongpassword")
            .param("content", "Updated content"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts/1/edit"))
        .andExpect(flash().attribute("error", containsString("Invalid password")));
  }

  @Test
  void showDeletePostForm_WithValidId_ShouldShowDeleteFormWithPost() throws Exception {
    // Arrange
    when(postService.getPostForEdit(1L)).thenReturn(mockPost);

    // Act & Assert
    mockMvc.perform(get("/posts/1/delete"))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/delete"))
        .andExpect(model().attribute("post", hasProperty("id", is(1L))))
        .andExpect(model().attributeExists("deletePostDto"));
  }

  @Test
  void deletePost_WithCorrectPassword_ShouldRedirectToPostsList() throws Exception {
    // Arrange
    when(postService.getPostForEdit(1L)).thenReturn(mockPost);
    when(postService.deletePost(anyLong(), anyString())).thenReturn(true);

    // Act & Assert
    mockMvc.perform(post("/posts/1/delete")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("rawPassword", "correctpassword"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts"))
        .andExpect(flash().attribute("success", containsString("deleted successfully")));
  }

  @Test
  void deletePost_WithIncorrectPassword_ShouldRedirectWithError() throws Exception {
    // Arrange
    when(postService.getPostForEdit(1L)).thenReturn(mockPost);
    when(postService.deletePost(anyLong(), anyString())).thenReturn(false);

    // Act & Assert
    mockMvc.perform(post("/posts/1/delete")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("rawPassword", "wrongpassword"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts/1/delete"))
        .andExpect(flash().attribute("error", containsString("Invalid password")));
  }
}
