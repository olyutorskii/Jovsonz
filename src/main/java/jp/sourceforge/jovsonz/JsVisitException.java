/*
 * JSON traverse error exception
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * Exception when a traverse is aborted.
 *
 * <p>Thrown when the traversing process of the JSON tree Objects is aborted.
 */
@SuppressWarnings("serial")
public class JsVisitException extends Exception {

    /**
     * Constructor.
     */
    public JsVisitException() {
        super();
        return;
    }

    /**
     * Constructor.
     *
     * @param message the detail message. (A {@code null} value is permitted)
     */
    public JsVisitException(String message) {
        super(message);
        return;
    }

    /**
     * Constructor.
     *
     * @param message the detail message. (A {@code null} value is permitted)
     * @param cause the cause.  (A {@code null} value is permitted,
     *     and indicates that the cause is nonexistent or unknown.)
     */
    public JsVisitException(String message, Throwable cause) {
        super(message, cause);
        return;
    }

    /**
     * Constructor.
     *
     * @param cause the cause.  (A {@code null} value is permitted,
     *     and indicates that the cause is nonexistent or unknown.)
     */
    public JsVisitException(Throwable cause) {
        super(cause);
        return;
    }

}
