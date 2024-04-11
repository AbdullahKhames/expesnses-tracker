package name.expenses.utils.validators.validatorclasses;


import jakarta.inject.Singleton;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import name.expenses.utils.validators.validatoranootations.UserNameValidator;

@Singleton
public class UniqueUserNameValidator implements ConstraintValidator<UserNameValidator, String> {

//    public UniqueUserNameValidator() {
//        this.userRepo = (UserRepo) ContextProvider.getBean(UserRepo.class);
//    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return true;
    }

    @Override
    public void initialize(UserNameValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
