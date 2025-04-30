package com.finshot.bulletin.posts.service.impl;

import com.finshot.bulletin.posts.entity.Post;
import com.finshot.bulletin.posts.mapper.PostMapper;
import com.finshot.bulletin.posts.service.PostService;
import java.util.List;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Data
public class PostServiceImpl implements PostService {

  private final PostMapper postMapper;
  private final BCryptPasswordEncoder passwordEncoder;


  @Override
  public List<Post> getAllPosts() {
    return postMapper.findAll();
  }

  @Override
  @Transactional
  public Post getPostById(Long id) {
    Post post = postMapper.findById(id);
    if (post != null) {
      postMapper.incrementViewCount(id);
    }
    return post;
  }

  @Override
  @Transactional
  public Post createPost(Post post) {
    // Hash password
    post.setPassword(passwordEncoder.encode(post.getRawPassword()));

    postMapper.insert(post);
    return post;
  }
}
