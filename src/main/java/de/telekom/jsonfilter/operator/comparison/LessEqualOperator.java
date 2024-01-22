package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.EvaluationResult;

public class LessEqualOperator<T> extends ComparisonOperator<T> {

    public LessEqualOperator(String jsonPath, T expectedValue) {
        super(ComparisonOperatorEnum.LE);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    EvaluationResult compare(String json, String jsonPath, T expectedValue) {
        try {
            if (getActualValue(json, jsonPath).compareTo(expectedValue) <= 0) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value was not less or equal than expected value.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }
}
