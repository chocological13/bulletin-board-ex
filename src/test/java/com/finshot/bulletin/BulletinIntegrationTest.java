package com.finshot.bulletin;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.finshot.bulletin.config.SecurityTestConfig;
import com.finshot.bulletin.config.TestConfig;
import com.finshot.bulletin.posts.entity.Post;
import com.finshot.bulletin.posts.mapper.PostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Import({SecurityTestConfig.class, TestConfig.class})
@Sql("/sql/test-schema.sql")
public class BulletinIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PostMapper postMapper;

  @Test
  void fullPostLifecycle_ShouldWorkAsExpected() throws Exception {

    String title = "Integration Test Post";
    String author = "Integration Tester";
    String rawPassword = "rawpassword123";
    String content = "This is a test post for integration testing";
    String successMsg = "successfully";

    // * Step 1: Create a new post
    mockMvc.perform(post("/posts/create")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("title", title)
        .param("author", author)
        .param("rawPassword", rawPassword)
        .param("content", content))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts"))
        .andExpect(flash().attribute("success", containsString(successMsg)));

    // * Step 2: Find the created post in the list
    String listPageContent = mockMvc.perform(get("/posts"))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/list"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    assertTrue(listPageContent.contains(title));

    // Get the new post ID
    Post createdPost = postMapper.findAll().stream()
        .filter(p -> title.equals(p.getTitle()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Created post not found"));

    Long postId = createdPost.getId();

    // * Step 3: View the post details
    mockMvc.perform(get("/posts/" + postId))
        .andExpect(status().isOk())
        .andExpect(view().name("posts/view"))
        .andExpect(model().attributeExists("post"));

    // Verify view count increase
    createdPost = postMapper.findById(postId);
    assertEquals(1, createdPost.getViewCount());

    // * Step 4: Edit the post with correct password
    String updatedTitle = "Updated Integration Test Post";
    String updatedContent = "This post has been updated through integration testing";

    mockMvc.perform(post("/posts/" + postId + "/edit")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("title", updatedTitle)
        .param("author", author)
        .param("rawPassword", rawPassword)
        .param("content", updatedContent))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts/" + postId + "?fromEdit=true"))
        .andExpect(flash().attributeExists("success"));

    // Verify post was updated
    Post updatedPost = postMapper.findById(postId);
    assertEquals(updatedTitle, updatedPost.getTitle());
    assertEquals(updatedContent, updatedPost.getContent());

    // * Step 5: Try to edit with wrong password
    String failedTitle = "This Title Should Not Update";
    String failedContent = "This content should not update";
    String invalidPassword = "notpassword";

    mockMvc.perform(post("/posts/" +postId + "/edit")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("title", failedTitle)
        .param("author", author)
        .param("rawPassword", invalidPassword)
        .param("content", failedContent))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts/" + postId + "/edit"))
        .andExpect(flash().attributeExists("error"));

    // Verify post was not updated
    updatedPost = postMapper.findById(postId);
    assertEquals(updatedTitle, updatedPost.getTitle());
    assertNotEquals(failedContent, updatedPost.getContent());

    // * Step 6: Delete the post with wrong password
    mockMvc.perform(post("/posts/" + postId + "/delete")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("rawPassword", invalidPassword))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts/" + postId + "/delete"))
        .andExpect(flash().attributeExists("error"));

    // Verify post was not deleted
    updatedPost = postMapper.findById(postId);
    assertNotNull(updatedPost);

    // * Step 7 : Delete the post with correct password
    mockMvc.perform(post("/posts/" + postId + "/delete")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("rawPassword", rawPassword))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/posts"))
        .andExpect(flash().attributeExists("success"));

    // Verify post was soft deleted
    updatedPost = postMapper.findById(postId);
    assertNull(updatedPost);
  }
}
