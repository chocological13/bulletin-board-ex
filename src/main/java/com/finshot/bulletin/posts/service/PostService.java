package com.finshot.bulletin.posts.service;

import com.finshot.bulletin.posts.entity.Post;
import java.util.List;

public interface PostService {
  List<Post> getAllPosts();

  Post getPostById(Long id);

  Post getPostForEdit(Long id);

  boolean createPost(Post newPost);

  boolean updatePost(Post updatedPost);

  boolean deletePost(Long id, String rawPassword);
}
