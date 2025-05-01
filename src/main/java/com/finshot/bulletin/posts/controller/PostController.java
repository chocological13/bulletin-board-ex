package com.finshot.bulletin.posts.controller;

import com.finshot.bulletin.posts.entity.Post;
import com.finshot.bulletin.posts.entity.dto.DeletePostDto;
import com.finshot.bulletin.posts.service.PostService;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
@Data
public class PostController {

  private static final Logger log = LoggerFactory.getLogger(PostController.class);
  private final PostService postService;

  @GetMapping
  public String listPosts(Model model) {
   model.addAttribute("posts", postService.getAllPosts());
   return "posts/list";
  }
  @GetMapping("/{id}")
  public String viewPost(@PathVariable Long id,
      @RequestParam(required = false) boolean fromEdit, Model model) {

    Post post;
    if (fromEdit) {
      post = postService.getPostForEdit(id);
    } else {
      post = postService.getPostById(id);
    }

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
  public String createPost(@Valid @ModelAttribute Post post, BindingResult result, RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      return "posts/form";
    }

    boolean created = postService.createPost(post);

    if (!created) {
      redirectAttributes.addFlashAttribute("error", "Something went wrong :(");
      return "redirect:/posts/form";
    }

    return "redirect:/posts";
  }

  @GetMapping("/{id}/edit")
  public String showEditPostForm(@PathVariable Long id, Model model) {
    Post post = postService.getPostForEdit(id);
    if (post == null) {
      return "error/404";
    }
    model.addAttribute("post", post);
    model.addAttribute("isEdit", true);
    return "posts/form";
  }

  @PostMapping("/{id}/edit")
  public String updatePost(@PathVariable Long id, @Valid @ModelAttribute Post post, BindingResult result,
      RedirectAttributes redirectAttributes) {

    post.setId(id); // Ensure ID is set

    if (result.hasErrors()) {

      String errorMessages = "Error updating post: " + result.getFieldErrors().stream()
          .map(DefaultMessageSourceResolvable::getDefaultMessage)
          .collect(Collectors.joining(", "));

      redirectAttributes.addFlashAttribute("error", errorMessages);

      return "redirect:/posts/" + id + "/edit";
    }

    boolean updated = postService.updatePost(post);

    if (!updated) {
      redirectAttributes.addFlashAttribute("error", "Failed to update post: Invalid password");
      return "redirect:/posts/" + id + "/edit";
    }

    redirectAttributes.addFlashAttribute("success", "Post modified successfully!");
    return "redirect:/posts/" + id + "?fromEdit=true";
  }

  @GetMapping("/{id}/delete")
  public String showDeletePostForm(@PathVariable Long id, Model model) {
    Post post = postService.getPostForEdit(id);
    if (post == null) {
      return "error/404";
    }
    model.addAttribute("post", post);
    model.addAttribute("deletePostDto", new DeletePostDto());
    return "posts/delete";
  }

  @PostMapping("/{id}/delete")
  public String deletePost(@PathVariable Long id, @Valid @ModelAttribute DeletePostDto deletePostDto,
      BindingResult result, Model model, RedirectAttributes redirectAttributes) {

    Post post = postService.getPostForEdit(id);

    if (result.hasErrors()) {
      model.addAttribute("post", post);
      model.addAttribute("deletePost", deletePostDto);
      return "posts/delete";
    }

    boolean deleted = postService.deletePost(id, deletePostDto.getRawPassword());

    if (!deleted) {
      redirectAttributes.addFlashAttribute("error", "Failed to delete post: Invalid password");
      return "redirect:/posts/" + id + "/delete";
    }

    redirectAttributes.addFlashAttribute("success", "Post deleted successfully!");
    return "redirect:/posts";
  }
}
