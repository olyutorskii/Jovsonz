/*
 * JSON number value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * JSON NUMBER Value.
 *
 * <p>Reflects numeric types, including integers and decimals.
 *
 * <p>{@link java.math.BigDecimal} as the base of the implementation.
 * ※ Not IEEE754 floating point number.
 *
 * <p>{@code (1)} and {@code (1.0)} are distinguished by scale parameter.
 *
 * <p>example of notation
 *
 * <pre>
 * -43
 * 0.56
 * 3.23E-06
 * </pre>
 *
 * @see java.math.BigDecimal
 */
public class JsNumber
        implements JsValue, Comparable<JsNumber> {

    private static final MathContext DEF_MC =
            new MathContext(0, RoundingMode.UNNECESSARY);

    private static final String ERRMSG_INVFRAC =
            "invalid fractional number";
    private static final String ERRMSG_NONUMBER =
            "no number";
    private static final String ERRMSG_EXTRAZERO =
            "extra zero found";


    private final BigDecimal decimal;


    /**
     * Constructor.
     *
     * @param val initial integer value
     */
    public JsNumber(long val) {
        this(BigDecimal.valueOf(val));
        return;
    }

    /**
     * Constructor.
     *
     * <p>Equivalent to {@link java.math.BigDecimal#valueOf(double)}, rounding is performed.
     *
     * <p>if {@code (1.0/10.0)} double value given, it rounds to &quot;{@code 0.1}&quot;.
     *
     * <p>Use {@link JsNumber#JsNumber(java.math.BigDecimal)} and this constructor as needed.
     *
     * @param val initial floating-point value
     * @see java.math.BigDecimal#valueOf(double)
     */
    public JsNumber(double val) {
        this(BigDecimal.valueOf(val));
        return;
    }

    /**
     * Constructor.
     *
     * @param val initial integer value
     * @throws NullPointerException argument is null
     */
    public JsNumber(BigInteger val) {
        this(new BigDecimal(val, DEF_MC));
        return;
    }

    /**
     * Constructor.
     *
     * <p>Formatting is the same as {@link java.math.BigDecimal#BigDecimal(String)}.
     *
     * @param val number notation
     * @throws NumberFormatException invalid number format
     * @throws NullPointerException argument is null
     *
     * @see java.math.BigDecimal#BigDecimal(String)
     */
    public JsNumber(CharSequence val) {
        this(new BigDecimal(val.toString(), DEF_MC));
        return;
    }

    /**
     * Constructor.
     *
     * @param val initial decimal value
     * @throws NullPointerException argument is null
     */
    public JsNumber(BigDecimal val) {
        super();
        this.decimal = Objects.requireNonNull(val);
        return;
    }


    /**
     * Determine if any character is Unicode Basic-Latin number or not.
     *
     * @param ch character
     * @return true if [0-9]
     *
     * @see java.lang.Character#isDigit(char)
     */
    public static boolean isLatinDigit(char ch) {
        boolean result;
        result = '0' <= ch && ch <= '9';
        return result;
    }

    /**
     * Read a sequence of digits with leading sign from input source.
     *
     * <p>The leading sign '+' is skipped.
     * Can specify whether or not to allow digits following '0' at the beginning.
     *
     * @param source input source
     * @param app output target
     * @param allowZeroTrail true if allow digits following '0' at the beginning.
     * @return Same as output argument
     * @throws IOException I/O error
     * @throws JsParseException invalid token or EOF
     */
    private static Appendable appendDigitText(JsonSource source,
                                               Appendable app,
                                               boolean allowZeroTrail)
            throws IOException, JsParseException {
        char head = source.readOrDie();
        if (head == '-') {
            app.append('-');
        } else if (head != '+') {
            source.unread(head);
        }

        boolean hasAppended = false;
        boolean zeroStarted = false;    // leading 0
        for (;;) {
            if ( !source.hasMore() && hasAppended ) break;

            char readedCh = source.readOrDie();

            if ( !isLatinDigit(readedCh) ) {
                if ( !hasAppended ) {
                    throw new JsParseException(ERRMSG_NONUMBER,
                                               source.getLineNumber() );
                }
                source.unread(readedCh);
                break;
            }

            if (hasAppended) {
                if (zeroStarted && !allowZeroTrail) {
                    throw new JsParseException(ERRMSG_EXTRAZERO,
                                               source.getLineNumber() );
                }
            } else {                       // 1st char
                if (readedCh == '0') {
                    zeroStarted = true;
                }
            }

            app.append(readedCh);
            hasAppended = true;
        }

        return app;
    }

    /**
     * Read a sequence of fraction digits with leading dot'.' from input source.
     *
     * <p>If no fraction digits, do nothing and return.
     *
     * @param source input source
     * @param app output target
     * @return Same as output argument
     * @throws IOException I/O error
     * @throws JsParseException invalid token or EOF
     */
    private static Appendable appendFractionPart(JsonSource source,
                                                  Appendable app )
            throws IOException, JsParseException {
        if ( !source.hasMore() ) return app;

        char chData;

        chData = source.readOrDie();
        if (chData != '.') {
            source.unread(chData);
            return app;
        }

        app.append('.');

        boolean hasAppended = false;
        for (;;) {
            if ( !source.hasMore() && hasAppended ) break;

            chData = source.readOrDie();

            if ( !isLatinDigit(chData) ) {
                if ( !hasAppended ) {
                    throw new JsParseException(ERRMSG_INVFRAC,
                                               source.getLineNumber());
                }
                source.unread(chData);
                break;
            }

            app.append(chData);
            hasAppended = true;
        }

        return app;
    }

    /**
     * Read a sequence of exponential digits with leading 'e' or 'E' from input source.
     *
     * <p>If no exponential digits, do nothing and return.
     *
     * @param source input source
     * @param app output target
     * @return Same as output argument
     * @throws IOException I/O error
     * @throws JsParseException invalid token or EOF
     */
    private static Appendable appendExpPart(JsonSource source,
                                            Appendable app )
            throws IOException, JsParseException {
        if ( !source.hasMore() ) return app;

        char chData = source.readOrDie();
        if (chData != 'e' && chData != 'E') {
            source.unread(chData);
            return app;
        }

        app.append('E');

        appendDigitText(source, app, true);

        return app;
    }

    /**
     * Try parsing NUMBER Value from JSON source.
     *
     * <p>If a leading character of another possible type is read,
     * null is returned after push-back character into the source.
     *
     * @param source input source
     * @return NUMBER typed Value. null if another possible type.
     * @throws IOException I/O error
     * @throws JsParseException invalid token or EOF
     * @throws NullPointerException argument is null
     */
    static JsNumber parseNumber(JsonSource source)
            throws IOException, JsParseException {
        char charHead = source.readOrDie();
        source.unread(charHead);
        if ( charHead != '-' && !JsNumber.isLatinDigit(charHead) ) {
            return null;
        }

        StringBuilder numText = new StringBuilder();

        appendDigitText(    source, numText, false);
        appendFractionPart( source, numText );
        appendExpPart(      source, numText );

        JsNumber result = new JsNumber(numText);

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Always return {@link JsTypes#NUMBER}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes() {
        return JsTypes.NUMBER;
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
     * Return a hash code.
     *
     * <p>Equivalent to {@link java.math.BigDecimal#hashCode()}.
     *
     * @return hash code
     *
     * @see java.math.BigDecimal#hashCode()
     */
    @Override
    public int hashCode() {
        return this.decimal.hashCode();
    }

    /**
     * Indicates whether some other NUMBER Value is "equal to" this NUMBER Value.
     *
     * <p>The same decision is made as for {@link java.math.BigDecimal#equals(Object)}.
     *
     * <p>Values that do not match the scale, such as {@code 1.2} and {@code 0.12E+1},
     * are considered different values.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     *
     * @see java.math.BigDecimal#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ( !(obj instanceof JsNumber) ) return false;
        JsNumber number = (JsNumber) obj;
        return this.decimal.equals(number.decimal);
    }

    /**
     * Compare between NUMBER typed Value.
     *
     * <p>For example, {@code 1.2} and {@code 0.12E+1} are considered equal
     * if they have the same value even
     * if they are on different scales.
     *
     * @param value the object to be compared
     * @return a negative integer, zero, or a positive integer
     *     as this object is less than, equal to, or greater than the specified object.
     *
     * @see java.math.BigDecimal#compareTo(BigDecimal)
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    public int compareTo(JsNumber value) {
        if (this == value) return 0;
        return this.decimal.compareTo(value.decimal);
    }

    /**
     * Return int value.
     *
     * <p>Potential loss of information.
     *
     * @return int value
     *
     * @see java.lang.Number#intValue()
     * @see java.math.BigDecimal#intValue()
     */
    public int intValue() {
        return this.decimal.intValue();
    }

    /**
     * Return long value.
     *
     * <p>Potential loss of information.
     *
     * @return long value
     *
     * @see java.lang.Number#longValue()
     * @see java.math.BigDecimal#longValue()
     */
    public long longValue() {
        return this.decimal.longValue();
    }

    /**
     * Return float value.
     *
     * <p>Potential loss of information.
     *
     * @return float value
     *
     * @see java.lang.Number#floatValue()
     * @see java.math.BigDecimal#floatValue()
     */
    public float floatValue() {
        return this.decimal.floatValue();
    }

    /**
     * Return double value.
     *
     * <p>Potential loss of information.
     *
     * @return double value
     *
     * @see java.lang.Number#doubleValue()
     * @see java.math.BigDecimal#doubleValue()
     */
    public double doubleValue() {
        return this.decimal.doubleValue();
    }

    /**
     * Return {@link java.math.BigDecimal} value.
     *
     * @return BigDecimal value
     */
    public BigDecimal decimalValue() {
        return this.decimal;
    }

    /**
     * Return scale of NUMBER.
     *
     * <ul>
     * <li>Scale of "99" is 0
     * <li>Scale of "99.0" is 1
     * <li>Scale of "99.01" is 2
     * <li>Scale of "99E+3" is -3
     * <li>Scale of "99.0E+3" is -2
     * </ul>
     *
     * @return scale
     *
     * @see java.math.BigDecimal#scale()
     */
    public int scale() {
        return this.decimal.scale();
    }

    /**
     * Returns JSON notation.
     *
     * <p>like {@link java.math.BigDecimal#toString()}.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return this.decimal.toString();
    }

}
