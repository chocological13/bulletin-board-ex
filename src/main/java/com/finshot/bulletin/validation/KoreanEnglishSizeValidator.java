package com.finshot.bulletin.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class KoreanEnglishSizeValidator implements ConstraintValidator<KoreanEnglishSize, String> {

  private static final Pattern KOREAN_PATTERN = Pattern.compile("[가-힣]+");
  private int maxKorean;
  private int maxEnglish;

  @Override
  public void initialize(KoreanEnglishSize constraintAnnotation) {
    this.maxKorean = constraintAnnotation.maxKorean();
    this.maxEnglish = constraintAnnotation.maxEnglish();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true; // @NotBlank will handle this
    }

    int koreanCount = 0;
    int nonKoreanCount = 0;
    for (char c : value.toCharArray()) {
      if (KOREAN_PATTERN.matcher(String.valueOf(c)).matches()) {
        koreanCount++;
      } else {
        nonKoreanCount++;
      }
    }

    // Check ratio of each char types and return false if it exceeds the normalization threshold
    double koreanRatio = (double) koreanCount / maxKorean;
    double nonKoreanRatio = (double) nonKoreanCount / maxEnglish;

    return (koreanRatio + nonKoreanRatio) <= 1.0;
  }


}
