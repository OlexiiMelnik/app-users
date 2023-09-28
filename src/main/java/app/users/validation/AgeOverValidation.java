package app.users.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class AgeOverValidation implements ConstraintValidator<AgeOver, LocalDate> {
    private static final int MIN_AGE = 18;

    @Override
    public boolean isValid(LocalDate dateOfBirth,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (dateOfBirth == null) {
            return true;
        }
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateOfBirth, currentDate);
        int age = period.getYears();
        return age >= MIN_AGE;
    }
}
