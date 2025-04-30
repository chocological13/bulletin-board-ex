package com.finshot.bulletin.posts.controller;

import com.finshot.bulletin.posts.entity.Post;
import com.finshot.bulletin.posts.service.PostService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @GetMapping("/create")
  public String showCreatePostForm(Model model) {
    model.addAttribute("post", new Post());
    model.addAttribute("isEdit", false);
    return "posts/form";
  }

  @PostMapping("/create")
  public String createPost(@Valid @ModelAttribute Post post, BindingResult result) {
    if (result.hasErrors()) {
      return "posts/form";
    }
    postService.createPost(post);
    return "redirect:/posts";
  }

}
