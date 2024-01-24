// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.EvaluationResult;

public class GreaterEqualOperator<T> extends ComparisonOperator<T> {

    public GreaterEqualOperator(String jsonPath, T expectedValue) {
        super(ComparisonOperatorEnum.GE);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    EvaluationResult compare(String json, String jsonPath, T expectedValue) {
        try {
            if (getActualValue(json, jsonPath).compareTo(expectedValue) >= 0) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value was not greater or equal to expected value.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }
}
