package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.EvaluationResult;

public class NotEqualsOperator<T> extends ComparisonOperator<T> {

    public NotEqualsOperator(String jsonPath, T expectedValue) {
        super(ComparisonOperatorEnum.NE);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    EvaluationResult compare(String json, String jsonPath, T expectedValue) {
        try {
            if (!getActualValue(json, jsonPath).equals(expectedValue)) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value was equal to unexpected value.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }
}
