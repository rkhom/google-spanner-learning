package com.rkhom.spanner.validator;

import com.rkhom.spanner.model.DateRange;
import java.time.LocalDateTime;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRange> {

  @Override
  public boolean isValid(DateRange dateRange, ConstraintValidatorContext context) {

    LocalDateTime startDate = dateRange.getStartDate();
    LocalDateTime endDate = dateRange.getEndDate();

    if (startDate == null || endDate == null) {
      buildConstraintViolation(context, "start and end dates of the date range must not be null");
      return false;
    }

    if (startDate.isAfter(endDate)) {
      buildConstraintViolation(context, "end date in the date range must not precede a start date");
      return false;
    }

    return true;

  }

  private void buildConstraintViolation(ConstraintValidatorContext validatorContext,
      String message) {
    validatorContext.disableDefaultConstraintViolation();
    validatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }

}
