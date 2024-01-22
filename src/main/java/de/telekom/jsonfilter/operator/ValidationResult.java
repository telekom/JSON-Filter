package de.telekom.jsonfilter.operator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationResult {
    boolean valid;
    String validationError;
    OperatorEnum operator;

    public static ValidationResult withError(String error) {
        return new ValidationResult(false, error, null);
    }

    public static ValidationResult withError(String error, OperatorEnum operator) {
        return new ValidationResult(false, error, operator);
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, null, null);
    }
}
