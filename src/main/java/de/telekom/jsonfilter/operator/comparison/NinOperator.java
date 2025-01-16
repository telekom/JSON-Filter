package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.EvaluationResult;

import java.util.List;

public class NinOperator<T> extends ComparisonOperator<List<T>> {
    public NinOperator(String jsonPath, List<T> expectedValue) {
        super(ComparisonOperatorEnum.NIN);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    EvaluationResult compare(String json, String jsonPath, List<T> expectedValue) {
        try {
            if (expectedValue.stream().noneMatch(getActualValue(json, jsonPath)::equals)) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value was in expected values.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }
}
