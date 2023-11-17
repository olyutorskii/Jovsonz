/*
 * JSON null value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.util.Objects;

/**
 * JSON null Value.
 *
 * <p>Not related to null in Java language.
 *
 * <p>Provided as a singleton.
 *
 * <p>example of notation
 *
 * <pre>
 * {@code null}
 * </pre>
 */
public final class JsNull
        implements JsValue, Comparable<JsNull> {

    /** Singleton. */
    public static final JsNull NULL = new JsNull();

    /** notation. */
    public static final String TEXT = "null";

    /** hash only one. */
    public static final int ONLYHASH = 982_451_653;   // large prime

    /**
     * Hidden constructor.
     */
    private JsNull() {
        super();
        return;
    }

    /**
     * Try parsing NULL value from JSON source.
     *
     * <p>If a leading character of another possible type is read,
     * null is returned after push-back character into the source.
     *
     * @param source input source
     * @return NULL typed value. null if another possible type.
     * @throws IOException I/O error
     * @throws JsParseException invalid token or EOF
     */
    static JsNull parseNull(JsonSource source)
            throws IOException, JsParseException {
        char charHead = source.readOrDie();

        if (charHead != 'n') {
            source.unread(charHead);
            return null;
        }

        if ( !source.matchOrDie("ull") ) {
            throw new JsParseException(JsParseException.ERRMSG_INVALIDTOKEN,
                                       source.getLineNumber() );
        }

        return JsNull.NULL;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Always return {@link JsTypes#NULL}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes() {
        return JsTypes.NULL;
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
     * Always return {@value ONLYHASH}.
     *
     * @return {@value ONLYHASH}
     */
    @Override
    public int hashCode() {
        return ONLYHASH;
    }

    /**
     * Return true only when {@link #NULL} is passed.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is {@link #NULL}; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = obj == NULL;
        assert (obj instanceof JsNull) == result;
        return result;
    }

    /**
     * Compare between NULL typed Value.
     *
     * <p>Returns 0 if argument is not null.
     *
     * @param value the object to be compared
     * @return always 0
     * @throws NullPointerException argument is null
     */
    @Override
    public int compareTo(JsNull value) {
        Objects.requireNonNull(value);
        return 0;
    }

    /**
     * Returns JSON notation {@value TEXT}.
     *
     * <p>It's not null in Java language.
     *
     * @return JSON notation {@value TEXT}
     */
    @Override
    public String toString() {
        return TEXT;
    }

}
