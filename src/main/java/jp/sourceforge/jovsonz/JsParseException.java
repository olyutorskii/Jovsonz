/*
 * JSON parse error information
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * Exception due to JSON grammar violation.
 */
@SuppressWarnings("serial")
public class JsParseException extends Exception {

    /** ERROR MESSAGE: invalid token. */
    static final String ERRMSG_INVALIDTOKEN =
            "invalid JSON token";
    /** ERROR MESSAGE: invalid root. */
    static final String ERRMSG_INVALIDROOT =
            "top root JSON value must be OBJECT or ARRAY";
    /** ERROR MESSAGE: no more data. */
    static final String ERRMSG_NODATA =
            "We need but no more JSON data";

    private static final int LINE_UNKNOWN = 0;


    /** line number. */
    private final int lineNumber;


    /**
     * Constructor.
     */
    public JsParseException() {
        this(null, LINE_UNKNOWN);
        return;
    }

    /**
     * Constructor.
     *
     * @param message the detail message. (A {@code null} value is permitted)
     * @param lineNumber line number where the JSON grammar violation occurred.
     *     If the line number is unknown, the value is less than or equal to 0.
     */
    public JsParseException(String message, int lineNumber) {
        this(message, (Throwable) null, lineNumber);
        return;
    }

    /**
     * Constructor.
     *
     * @param message the detail message. (A {@code null} value is permitted)
     * @param cause the cause.  (A {@code null} value is permitted,
     *     and indicates that the cause is nonexistent or unknown.)
     * @param lineNumber line number where the JSON grammar violation occurred.
     *     If the line number is unknown, the value is less than or equal to 0.
     */
    public JsParseException(String message, Throwable cause, int lineNumber) {
        super(message, cause);
        this.lineNumber = lineNumber;
        return;
    }


    /**
     * Returns the line number where the JSON grammar violation occurred.
     *
     * <p>If the line number is unknown, the value is less than or equal to 0.
     *
     * @return line number.
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Returns {@code true} if line number is valid.
     *
     * <p>A valid line number is when it is greater than or equal to 1.
     *
     * @return true if line number is valid.
     */
    public boolean hasValidLineNumber() {
        boolean result = this.lineNumber > 0;
        return result;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * <p>If there are valid line numbers, they are output together.
     *
     * @return the detail message string of this {@code Throwable} instance
     *     (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();

        String superMessage = super.getMessage();
        if (superMessage != null) {
            message.append(superMessage);
        }

        if (hasValidLineNumber()) {
            if (message.length() > 0) message.append(' ');
            message .append("[line:")
                    .append(this.lineNumber)
                    .append(']');
        }

        if (message.length() <= 0) {
            return null;
        }

        String result = message.toString();
        return result;
    }

}
