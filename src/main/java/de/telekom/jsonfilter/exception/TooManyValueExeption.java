package de.telekom.jsonfilter.exception;

public class TooManyValueExeption extends Exception {

    public TooManyValueExeption(String jsonPath, int numberOfActualValues) {
        super("The evaluation of \"" + jsonPath + "\" contained too many values. Expected 1, got " + numberOfActualValues + ".");
    }
}
