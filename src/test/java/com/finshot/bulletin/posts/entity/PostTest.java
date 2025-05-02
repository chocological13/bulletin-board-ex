package com.finshot.bulletin.posts.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PostTest {

  private Validator validator;
  private Post post;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    post = new Post();
    post.setTitle("Valid Title");
    post.setAuthor("Valid Author");
    post.setRawPassword("validPassword");
    post.setContent("Valid content");
  }

  @Test
  void validPost_ShoudlHaveNoViolations() {
    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertTrue(violations.isEmpty());
  }

  @Test
  void emptyTitle_ShouldViolateValidation() {
    // Arrange
    post.setTitle("");

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    // Verify violations
    ConstraintViolation<Post> violation = violations.iterator().next();
    assertEquals("title", violation.getPropertyPath().toString());
  }

  @Test
  void nullTitle_ShouldViolateValidation() {
    // Arrange
    post.setTitle(null);

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
  }

  @Test
  void longKoreanTitle_ShouldViolateValidation() {
    // Arrange
    // This korean title contains 78 chars
    post.setTitle("아주 긴 한국어 제목입니다. 이 제목은 한글 글자 수 제한을 초과하기 위해 의도적으로 만들어졌습니다. 정말 정말 정말 긴 제목입니다. 좋은 하루 보내시길 바랍니다!");

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());

    // Verify violations
    ConstraintViolation<Post> violation = violations.iterator().next();
    assertEquals("Title must be less than 50 characters in Korean or "
                 + "100 characters in English", violation.getMessage());
    assertEquals("title", violation.getPropertyPath().toString());
  }

  @Test
  void longEnglishTitle_ShouldViolateValidation() {
    // Arrange
    // This English title contains 163 chars
    post.setTitle("This is a very long Korean title. This title was intentionally created to exceed the Korean character limit. Very very very long title. I hope you have a nice day!");

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());

    // Verify violations
    ConstraintViolation<Post> violation = violations.iterator().next();
    assertEquals("Title must be less than 50 characters in Korean or "
                 + "100 characters in English", violation.getMessage());
    assertEquals("title", violation.getPropertyPath().toString());
  }

  @Test
  void emptyAuthor_ShouldViolateValidation() {
    // Arrange
    post.setAuthor("");

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("author")));
  }

  @Test
  void longKoreanAuthor_ShouldViolateValidation() {
    // Arrange
    post.setAuthor("블랙 쇼어의 창립자이자 리더");

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());

    // Verify violations
    ConstraintViolation<Post> violation = violations.iterator().next();
    assertEquals("Author name must be less than 10 characters in "
                 + "Korean or 20 characters in English", violation.getMessage());
    assertEquals("author", violation.getPropertyPath().toString());
  }

  @Test
  void longEnglishAuthor_ShouldViolateValidation() {
    // Arrange
    post.setAuthor("Founder and Leader of the Black Shores");

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());

    // Verify violations
    ConstraintViolation<Post> violation = violations.iterator().next();
    assertEquals("Author name must be less than 10 characters in "
                 + "Korean or 20 characters in English", violation.getMessage());
    assertEquals("author", violation.getPropertyPath().toString());
  }

  @Test
  void emptyRawPassword_ShouldViolateValidation() {
    // Arrange
    post.setRawPassword("");

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("rawPassword")));
  }

  @Test
  void emptyContent_ShouldViolateValidation() {
    // Arrange
    post.setContent("");

    // Act
    Set<ConstraintViolation<Post>> violations = validator.validate(post);

    // Assert
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("content")));
  }

}
