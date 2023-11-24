/*
 * JSON pair in object
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.util.Objects;

/**
 * PAIR with a named Value enumerated within an OBJECT typed Value.
 *
 * <p>PAIR is not Value.
 *
 * <p>example of notation
 *
 * <pre>
 * {
 *     "PairName1" : 99.9 ,
 *     "PairName2" : "textValue"
 * }
 * </pre>
 *
 */
public class JsPair {

    private final String name;
    private final JsValue value;


    /**
     * Constructor.
     *
     * <p>A PAIR associated with Value is generated.
     *
     * @param name name of PAIR
     * @param value associated Value
     * @throws NullPointerException argument is null
     */
    public JsPair(String name, JsValue value) {
        super();
        this.name  = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
        return;
    }

    /**
     * Constructor.
     *
     * <p>A PAIR associated with STRING typed Value is generated.
     *
     * @param name name of PAIR
     * @param text associated text
     * @throws NullPointerException name is null
     */
    public JsPair(String name, CharSequence text) {
        this(name, (JsValue) new JsString(text));
        return;
    }

    /**
     * Constructor.
     *
     * <p>A PAIR associated with BOOLEAN typed Value is generated.
     *
     * @param name name of PAIR
     * @param bool associated boolean
     * @throws NullPointerException name is null
     */
    public JsPair(String name, boolean bool) {
        this(name, JsBoolean.valueOf(bool));
        return;
    }

    /**
     * Constructor.
     *
     * <p>A PAIR associated with NUMBER typed Value is generated.
     *
     * @param name name of PAIR
     * @param number associated number
     * @throws NullPointerException name is null
     */
    public JsPair(String name, long number) {
        this(name, new JsNumber(number));
        return;
    }

    /**
     * Constructor.
     *
     * <p>A PAIR associated with NUMBER typed Value is generated.
     *
     * @param name name of PAIR
     * @param number associated number
     * @throws NullPointerException name is null
     */
    public JsPair(String name, double number) {
        this(name, new JsNumber(number));
        return;
    }


    /**
     * Return name of PAIR.
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return associated Value.
     *
     * @return Value
     */
    public JsValue getValue() {
        return this.value;
    }

    /**
     * Return hash code.
     *
     * <p>Synthesized from hash values of both PAIR name and Value.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        int nameHash = this.name.hashCode();
        int valHash = this.value.hashCode();
        return nameHash ^ valHash;
    }

    /**
     * Indicates whether some other PAIR is "equal to" this PAIR Value.
     *
     * <p>True only if both PAIR name and Value equivalent.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if ( !(obj instanceof JsPair) ) return false;
        JsPair target = (JsPair) obj;

        boolean result;
        if (this.name.equals(target.name)) {
            result = this.value.equals(target.value);
        } else {
            result = false;
        }

        return result;
    }

    /**
     * Returns JSON notation.
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        try {
            JsString.dumpString(result, this.name);
        } catch (IOException e) {
            assert false;
            throw new AssertionError(e);
        }

        result.append(':')
            .append(this.value.toString());

        return result.toString();
    }

}
