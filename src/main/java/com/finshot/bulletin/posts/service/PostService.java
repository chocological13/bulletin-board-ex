package com.finshot.bulletin.posts.service;

import com.finshot.bulletin.posts.entity.Post;
import java.util.List;

public interface PostService {
  List<Post> getAllPosts();

  Post getPostById(Long id);

  Post createPost(Post post);
}
