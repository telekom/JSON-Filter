// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.EvaluationResult;

public class ContainsOperator<T> extends ComparisonOperator<T> {

    public ContainsOperator(String jsonPath, T expectedValue) {
        super(ComparisonOperatorEnum.CT);
        this.jsonPath = jsonPath;
        this.expectedValue = expectedValue;
    }

    @Override
    EvaluationResult compare(String json, String jsonPath, T expectedValue) {
        try {
            if (getActualValues(json, jsonPath).stream().anyMatch(expectedValue::equals)) {
                return EvaluationResult.valid(this);
            } else {
                return EvaluationResult.withError(this, "Actual value did not contain expected value.");
            }
        } catch (Exception ex) {
            return EvaluationResult.withError(this, "An exception occurred during the evaluation: \n" + ex.getLocalizedMessage());
        }
    }
}
