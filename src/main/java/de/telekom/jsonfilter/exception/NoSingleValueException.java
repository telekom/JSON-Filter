// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.exception;

public class NoSingleValueException extends Exception {

    public NoSingleValueException(String jsonPath, int numberOfActualResults) {
        super("The evaluation of \"" + jsonPath + "\" did not return a single value. Expected 1 value, got " + numberOfActualResults + ".");
    }
}
