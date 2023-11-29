/*
 * JSON boolean value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.util.Objects;

/**
 * JSON BOOLEAN Value.
 *
 * <p>Two instances only.
 *
 * <p>example of notation
 *
 * <pre>
 * true
 * false
 * </pre>
 */
public final class JsBoolean
        implements JsValue, Comparable<JsBoolean> {

    /** The only true Value instance. */
    public static final JsBoolean TRUE  = new JsBoolean();
    /** The only false Value instance. */
    public static final JsBoolean FALSE = new JsBoolean();

    /** Notation of true. */
    public static final String TEXT_TRUE  = "true";
    /** Notation of false. */
    public static final String TEXT_FALSE = "false";

    /** hash number of true. */
    public static final int HASH_TRUE = Boolean.TRUE.hashCode();
    /** hash number of false. */
    public static final int HASH_FALSE = Boolean.FALSE.hashCode();


    /**
     * Hidden Constructor.
     */
    private JsBoolean() {
        super();
        return;
    }


    /**
     * Try parsing BOOLEAN Value from JSON source.
     *
     * <p>If a leading character of another possible type is read,
     * null is returned after push-back character into the source.
     *
     * @param source input source
     * @return BOOLEAN typed Value. null if another possible type.
     * @throws IOException I/O error
     * @throws JsParseException invalid token or EOF
     * @throws NullPointerException argument is null
     */
    static JsBoolean parseBoolean(JsonSource source)
            throws IOException, JsParseException {
        JsBoolean result = null;
        boolean hasError = false;

        char charHead = source.readOrDie();
        switch (charHead) {
        case 't':
            if (source.matchOrDie("rue")) {
                result = JsBoolean.TRUE;
            } else {
                hasError = true;
            }
            break;
        case 'f':
            if (source.matchOrDie("alse")) {
                result = JsBoolean.FALSE;
            } else {
                hasError = true;
            }
            break;
        default:
            source.unread(charHead);
            break;
        }

        if (hasError) {
            throw new JsParseException(JsParseException.ERRMSG_INVALIDTOKEN,
                                       source.getLineNumber() );
        }

        return result;
    }


    /**
     * {@inheritDoc}
     *
     * <p>Always return {@link JsTypes#BOOLEAN}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes() {
        return JsTypes.BOOLEAN;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation only notifies this-object.
     *
     * @param visitor {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     * @throws NullPointerException argument is null
     */
    @Override
    public void traverse(ValueVisitor visitor)
            throws JsVisitException {
        visitor.visitValue(this);
        return;
    }

    /**
     * Returns a BOOLEAN instance representing the specified boolean value.
     *
     * @param bool boolean値
     * @return BOOLEAN instance
     */
    public static JsBoolean valueOf(boolean bool) {
        if (bool) return TRUE;
        return FALSE;
    }

    /**
     * Return boolean primitive.
     *
     * @return boolean
     */
    public boolean booleanValue() {
        boolean result;
        result = this == TRUE;
        return result;
    }

    /**
     * Determine if it is true.
     *
     * @return true if TRUE
     */
    public boolean isTrue() {
        boolean result;
        result = this == TRUE;
        return result;
    }

    /**
     * Determine if it is false.
     *
     * @return true if FALSE
     */
    public boolean isFalse() {
        boolean result;
        result = this != TRUE;
        return result;
    }

    /**
     * Return a hash code.
     *
     * <p>Return {@link #HASH_TRUE} if true.
     *
     * <p>Return {@link #HASH_FALSE} if false.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result;
        if (this == TRUE) result = HASH_TRUE;
        else              result = HASH_FALSE;
        return result;
    }

    /**
     * Indicates whether some other BOOLEAN Value is "equal to" this BOOLEAN Value.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (obj instanceof JsBoolean) {
            result = this == obj;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Compare between BOOLEAN typed Value.
     *
     * <p>Order is ( {@link #TRUE} &lt; {@link #FALSE} ).
     *
     * <p>※ Warning : The order is the reverse of
     * {@link java.lang.Boolean#compareTo(java.lang.Boolean)}
     *
     * @param value the object to be compared
     * @return a negative integer, zero, or a positive integer
     *     as this object is less than, equal to, or greater than the specified object.
     * @throws NullPointerException argument is null
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    public int compareTo(JsBoolean value) {
        Objects.requireNonNull(value);

        int result;
        if (this == value) {
            result =  0;
        } else if (this == TRUE) {
            result = -1;
        } else {
            result = +1;
        }

        return result;
    }

    /**
     * Returns JSON notation.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        if (this == TRUE) return TEXT_TRUE;
        return TEXT_FALSE;
    }

}
