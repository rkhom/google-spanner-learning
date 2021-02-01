package com.rkhom.spanner.validator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = DateRangeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {

  String message() default "Invalid date range";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
