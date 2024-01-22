package de.telekom.jsonfilter.operator.comparison;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.path.PathCompiler;
import de.telekom.jsonfilter.operator.EvaluationResult;
import de.telekom.jsonfilter.operator.ValidationResult;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexOperator<T> extends ComparisonOperator<T> {

    /**
     * @param jsonPath      Given path
     * @param expectedValue is the Regex that the value at jsonPath is validated against
     */
    public RegexOperator(String jsonPath, T expectedValue) {
        super(ComparisonOperatorEnum.RX);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    EvaluationResult compare(String json, String jsonPath, T expectedValue) {
        try {
            if (Pattern.matches((String) expectedValue, (CharSequence) getActualValue(json, jsonPath))) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value did not match regex.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }

    @Override
    public ValidationResult validate() {
        try {
            PathCompiler.compile(jsonPath);
            Pattern.compile((String) expectedValue);
            return ValidationResult.valid();
        } catch (InvalidPathException | PatternSyntaxException ex) {
            return ValidationResult.withError(ex.getLocalizedMessage(), getOperator());
        }
    }
}
