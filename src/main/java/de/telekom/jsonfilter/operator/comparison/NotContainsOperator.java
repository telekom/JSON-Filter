// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.EvaluationResult;

public class NotContainsOperator<T> extends ComparisonOperator<T> {

    public NotContainsOperator(String jsonPath, T expectedValue) {
        super(ComparisonOperatorEnum.NCT);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    public EvaluationResult compare(String json, String jsonPath, T expectedValue) {
        try {
            if (getActualValues(json, jsonPath).stream().noneMatch(expectedValue::equals)) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value did contain expected value.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }
}
