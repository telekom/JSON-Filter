// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.EvaluationResult;


public class EqualsOperator<T> extends ComparisonOperator<T> {

    public EqualsOperator(String jsonPath, T expectedValue) {
        super(ComparisonOperatorEnum.EQ);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    EvaluationResult compare(String json, String jsonPath, T expectedValue) {
        try {
            if (getActualValue(json, jsonPath).equals(expectedValue)) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value did not equal expected value.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }
}
