package com.neodem.orleans.engine.core;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class ActionProcessorException extends IllegalArgumentException {
    public ActionProcessorException() {
    }

    public ActionProcessorException(String s) {
        super(s);
    }

    public ActionProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionProcessorException(Throwable cause) {
        super(cause);
    }
}
