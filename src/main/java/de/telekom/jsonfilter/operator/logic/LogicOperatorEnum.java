// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.logic;

import de.telekom.jsonfilter.operator.OperatorEnum;
import lombok.Getter;

public enum LogicOperatorEnum implements OperatorEnum {

    AND("and"),

    OR("or");
    @Getter
    private final String value;

    LogicOperatorEnum(String value) {
        this.value = value;
    }

}
