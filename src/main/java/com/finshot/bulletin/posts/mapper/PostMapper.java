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

  @Select("SELECT * FROM posts WHERE id = #{id} AND deleted_at IS NULL")
  Post findById(Long id);

  @Select("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id} AND deleted_at IS NULL RETURNING *")
  Post findAndIncrementViewCount(Long id);

  @Insert("INSERT INTO posts (title, author, password, content)"
          + "VALUES (#{title}, #{author}, #{password}, #{content})")
  @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
  int insert(Post post);

  @Update("UPDATE posts "
          + "SET title = #{title}, content = #{content}, modified_at = CURRENT_TIMESTAMP "
          + "WHERE id = #{id}")
  int update(Post post);

  @Update("UPDATE posts SET deleted_at = CURRENT_TIMESTAMP WHERE id = #{id}")
  int softDelete(Long id);

  @Select("SELECT password FROM posts WHERE id = #{id} AND deleted_at IS NULL")
  String getPasswordById(Long id);

}
