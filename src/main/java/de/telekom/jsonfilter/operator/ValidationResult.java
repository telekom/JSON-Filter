package de.telekom.jsonfilter.operator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationResult {
    boolean valid;
    String validationError;
    OperatorEnum operator;

    /**
     * Creates and returns a new instance of ValidationResult with an error message
     *
     * @param error the error message to be associated with the validation result
     * @return ValidationResult an instance of ValidationResult with the 'valid' attribute set to false
     * and the 'validationError' set to the provided error message
     */
    public static ValidationResult withError(String error) {
        return new ValidationResult(false, error, null);
    }

    /**
     * Creates and returns a new instance of ValidationResult with an error message and an operator
     *
     * @param error the error message to be associated with the validation result
     * @param operator the OperatorEnum object to be associated with the validation result
     *
     * @return ValidationResult an instance of ValidationResult with the 'valid' attribute set to false,
     *                          the 'validationError' set to the provided error message and
     *                          the 'operator' set to the provided operator
     */
    public static ValidationResult withError(String error, OperatorEnum operator) {
        return new ValidationResult(false, error, operator);
    }

    /**
     * Creates and returns a new instance of ValidationResult indicating a successful validation
     *
     * @return ValidationResult an instance of ValidationResult with the 'valid' attribute set to true
     */
    public static ValidationResult valid() {
        return new ValidationResult(true, null, null);
    }
}
