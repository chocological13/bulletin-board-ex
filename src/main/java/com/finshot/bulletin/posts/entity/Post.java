package com.finshot.bulletin.posts.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

  private Long id;

  @NotBlank(message = "Title is required")
  @Size(max = 100, message = "Title must be less than 50 characters in Korean and 100 characters in English")
  private String title;

  @NotBlank(message = "Author name is required")
  @Size(max = 10, message = "Author name must be less than 10 characters")
  private String author;

  @NotBlank(message = "Password is required")
  private String password;

  @NotBlank(message = "Content is required")
  private String content;

  private Integer viewCount;

  private LocalDateTime createdAt;

  private LocalDateTime modifiedAt;

  private LocalDateTime deletedAt;

  // Non-persistent field for password confirmation
  private String rawPassword;
}
