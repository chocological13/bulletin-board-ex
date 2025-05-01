package com.finshot.bulletin.posts.entity;

import com.finshot.bulletin.validation.KoreanEnglishSize;
import jakarta.validation.constraints.NotBlank;
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
  @KoreanEnglishSize(maxKorean = 50, maxEnglish = 100, message = "Title must be less than 50 characters in Korean or "
                                                                 + "100 characters in English")
  private String title;

  @NotBlank(message = "Author name is required")
  @KoreanEnglishSize(maxKorean = 10, maxEnglish = 20, message = "Author name must be less than 10 characters in "
                                                                + "Korean or 20 characters in English")
  private String author;

  // Non-persistent field for password confirmation
  @NotBlank(message = "Password is required")
  private String rawPassword;

  private String password;

  @NotBlank(message = "Content is required")
  private String content;

  private int viewCount;

  private LocalDateTime createdAt;

  private LocalDateTime modifiedAt;

  private LocalDateTime deletedAt;
}
