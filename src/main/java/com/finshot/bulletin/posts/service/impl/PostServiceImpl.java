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
    return postMapper.findAndIncrementViewCount(id);
  }

  // Retrieve post without incrementing view
  @Override
  public Post getPostForEdit(Long id) {
    return postMapper.findById(id);
  }

  @Override
  @Transactional
  public boolean createPost(Post newPost) {
    // Hash password
    newPost.setPassword(passwordEncoder.encode(newPost.getRawPassword()));
    return postMapper.insert(newPost) != 0;
  }

  @Override
  @Transactional
  public boolean updatePost(Post updatedPost) {
    String storedHash = postMapper.getPasswordById(updatedPost.getId());
    if (storedHash != null && passwordEncoder.matches(updatedPost.getRawPassword(), storedHash)) {
      postMapper.update(updatedPost);
      return true;
    }
    return false;
  }

  @Override
  public boolean deletePost(Long id, String rawPassword) {
    String storedHash = postMapper.getPasswordById(id);
    if (storedHash != null && passwordEncoder.matches(rawPassword, storedHash)) {
      postMapper.softDelete(id);
      return true;
    }
    return false;
  }


}
