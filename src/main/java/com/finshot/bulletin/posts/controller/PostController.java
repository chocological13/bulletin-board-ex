package com.finshot.bulletin.posts.controller;

import com.finshot.bulletin.posts.service.PostService;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/posts")
@Data
public class PostController {

  private final PostService postService;

  @GetMapping
  public String listPosts(Model model) {
   model.addAttribute("posts", postService.getAllPosts());
   return "posts/list";
  }

}
