package com.finshot.bulletin.posts.mapper;

import com.finshot.bulletin.posts.entity.Post;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper {
  @Select("SELECT * FROM posts WHERE deleted_at IS NULL ORDER BY id DESC")
  List<Post> findAll();

  @Select("SELECT * FROM posts WHERE id = #{id}")
  Post findById(Long id);

  @Select("SELECT * FROM post WHERE id = #{id} AND deleted_at IS NULL")
  Post findActiveById(Long id);

  @Insert("INSERT INTO posts (title, author, password, content)"
          + "VALUES (#{title}, #{author}, #{password}, #{content})")
  @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
  int insert(Post post);

  @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id}")
  int incrementViewCount(Long id);

}
