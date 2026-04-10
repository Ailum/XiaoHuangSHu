package com.lhj.framework.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber,String> {

    @Override
    public void initialize(PhoneNumber phoneNumber) {
        //初始化操作
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context){
    return phoneNumber != null && phoneNumber.matches("\\d{11}");
    }
}
