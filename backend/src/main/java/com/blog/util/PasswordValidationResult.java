package com.blog.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 密码验证结果类
 * 封装密码验证的结果和错误信息
 */
public class PasswordValidationResult {
    
    private boolean valid;
    private List<String> errors;
    
    public PasswordValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }
    
    public String getErrorMessage() {
        if (errors.isEmpty()) {
            return null;
        }
        return String.join("; ", errors);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}