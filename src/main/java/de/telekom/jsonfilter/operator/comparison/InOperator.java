package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.EvaluationResult;

import java.util.List;

public class InOperator<T> extends ComparisonOperator<List<T>> {

    public InOperator(String jsonPath, List<T> expectedValue) {
        super(ComparisonOperatorEnum.IN);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    EvaluationResult compare(String json, String jsonPath, List<T> expectedValue) {
        try {
            if (expectedValue.stream().anyMatch(getActualValue(json, jsonPath)::equals)) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value was not in expected values.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }
}
