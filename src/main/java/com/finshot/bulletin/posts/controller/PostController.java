package com.finshot.bulletin.posts.controller;

import com.finshot.bulletin.posts.entity.Post;
import com.finshot.bulletin.posts.service.PostService;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/posts")
@Data
public class PostController {

  private final PostService postService;

  @GetMapping
  public String listPosts(Model model) {
   model.addAttribute("posts", postService.getAllPosts());
   return "posts/list";
  }
  @GetMapping("/{id}")
  public String viewPost(@PathVariable Long id, Model model) {
    Post post = postService.getPostById(id);
    if (post == null) {
      return "error/404";
    }
    model.addAttribute("post", post);
    return "posts/view";
  }

}
