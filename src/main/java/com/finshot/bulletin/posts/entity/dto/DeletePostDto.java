package com.finshot.bulletin.posts.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletePostDto {

  @NotBlank(message = "Password is required")
  private String rawPassword;
}
