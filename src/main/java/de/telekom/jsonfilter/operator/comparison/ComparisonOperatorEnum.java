// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.comparison;

import de.telekom.jsonfilter.operator.OperatorEnum;
import lombok.Getter;

@Getter
public enum ComparisonOperatorEnum implements OperatorEnum {

    EQ("equal"),
    NE("not equal"),
    RX("regex"),
    LT("less than"),
    LE("less or equal"),
    GT("greater than"),
    GE("greater or equal"),
    IN("in"),
    CT("contains"),
    NCT("not contains");

    private final String value;

    ComparisonOperatorEnum(String value) {
        this.value = value;
    }

}
