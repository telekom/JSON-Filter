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
