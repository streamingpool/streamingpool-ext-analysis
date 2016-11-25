/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

public enum AssertionStatus {
    SUCCESSFUL,
    FAILURE,
    ERROR,
    NONAPPLICABLE;

    public static final AssertionStatus fromBooleanSuccessful(boolean isOk) {
        if (isOk) {
            return SUCCESSFUL;
        } else {
            return FAILURE;
        }
    }
}
