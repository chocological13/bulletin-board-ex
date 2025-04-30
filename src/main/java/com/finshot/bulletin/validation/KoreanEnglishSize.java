package com.finshot.bulletin.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KoreanEnglishSizeValidator.class)
public @interface KoreanEnglishSize {

  String message() default "Field exceeds maximum length";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

  int maxKorean() default Integer.MAX_VALUE;
  int maxEnglish() default Integer.MAX_VALUE;
}
