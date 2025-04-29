package com.finshot.bulletin.posts.mapper;

import com.finshot.bulletin.posts.entity.Post;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper {
  @Select("SELECT * FROM posts WHERE deleted_at IS NULL ORDER BY id DESC")
  List<Post> findAll();

  @Select("SELECT * FROM posts WHERE id = #{id}")
  Post findById(Long id);

  @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id}")
  int incrementViewCount(Long id);
}
