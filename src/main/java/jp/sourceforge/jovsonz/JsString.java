/*
 * JSON string value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.util.Objects;

/**
 * JSON STRING Value.
 *
 * <p>example of notation
 *
 * <pre>
 * "xyz"
 * "漢"
 * "foo\nbar"
 * "{@literal \}u304a"
 * ""
 * </pre>
 */
public class JsString
        implements JsValue, CharSequence, Comparable<JsString> {

    private static final int HEX_BASE = 16;
    private static final int NIBBLE_WIDE = 4;
    private static final int NIBBLES_CHAR = Character.SIZE / NIBBLE_WIDE;

    private static final String ERRMSG_INVESC = "invalid escape character";
    private static final String ERRMSG_INVCTR = "invalid control character";


    private final String rawText;


    /**
     * Constructor.
     *
     * <p>init with empty text.
     */
    public JsString() {
        this("");
        return;
    }

    /**
     * Constructor.
     *
     * <p>Argument text is RAW text. (not escaped)
     *
     * @param rawSeq raw text
     * @throws NullPointerException argument is null
     */
    public JsString(CharSequence rawSeq) {
        super();
        Objects.requireNonNull(rawSeq);
        this.rawText = rawSeq.toString();
        return;
    }

    /**
     * FFFF形式4桁で16進エスケープされた文字列を読み、
     * char1文字にデコードする。
     *
     * @param source 文字列ソース
     * @return 文字
     * @throws IOException 入力エラー
     * @throws JsParseException 不正表記もしくは意図しない入力終了
     */
    static char parseHexChar(JsonSource source)
            throws IOException, JsParseException {
        char hex1Ch = source.readOrDie();
        char hex2Ch = source.readOrDie();
        char hex3Ch = source.readOrDie();
        char hex4Ch = source.readOrDie();

        int digit1 = Character.digit(hex1Ch, HEX_BASE);
        int digit2 = Character.digit(hex2Ch, HEX_BASE);
        int digit3 = Character.digit(hex3Ch, HEX_BASE);
        int digit4 = Character.digit(hex4Ch, HEX_BASE);

        if (   digit1 < 0
            || digit2 < 0
            || digit3 < 0
            || digit4 < 0 ) {
            throw new JsParseException(ERRMSG_INVESC, source.getLineNumber());
        }

        int digit = 0;
        digit += digit1;
        digit <<= NIBBLE_WIDE;
        digit += digit2;
        digit <<= NIBBLE_WIDE;
        digit += digit3;
        digit <<= NIBBLE_WIDE;
        digit += digit4;

        char result = (char) digit;

        return result;
    }

    /**
     * '\'に続くスペシャルキャラの読み込みを行う。
     *
     * @param source 文字列ソース
     * @param app スペシャルキャラ格納文字列
     * @throws IOException 入出力エラー
     * @throws JsParseException "\z"などの不正なスペシャルキャラ
     *     もしくは意図しない入力終了
     */
    private static void parseSpecial(JsonSource source, Appendable app)
            throws IOException, JsParseException {
        char special;

        char chData = source.readOrDie();
        switch (chData) {
        case '"':
            special = '"';
            break;
        case '\\':
            special = '\\';
            break;
        case '/':
            special = '/';
            break;
        case 'b':
            special = '\b';
            break;
        case 'f':
            special = '\f';
            break;
        case 'n':
            special = '\n';
            break;
        case 'r':
            special = '\r';
            break;
        case 't':
            special = '\t';
            break;
        case 'u':
            special = parseHexChar(source);
            break;
        default:
            throw new JsParseException(ERRMSG_INVESC, source.getLineNumber());
        }

        app.append(special);

        return;
    }

    /**
     * JSON文字列ソースからSTRING型Valueを読み込む。
     *
     * <p>別型の可能性のある先頭文字を読み込んだ場合、
     * ソースに文字を読み戻した後nullが返される。
     *
     * @param source 文字列ソース
     * @return STRING型Value。別型の可能性がある場合はnull。
     * @throws IOException 入力エラー
     * @throws JsParseException 不正な表記もしくは意図しない入力終了
     */
    static JsString parseString(JsonSource source)
            throws IOException, JsParseException {
        char charHead = source.readOrDie();
        if (charHead != '"') {
            source.unread(charHead);
            return null;
        }

        StringBuilder text = new StringBuilder();

        for (;;) {
            char chData = source.readOrDie();
            if (chData == '"') break;

            if (chData == '\\') {
                parseSpecial(source, text);
            } else if (Character.isISOControl(chData)) {
                throw new JsParseException(ERRMSG_INVCTR,
                                           source.getLineNumber());
            } else {
                text.append(chData);
            }
        }

        JsString result = new JsString(text);

        return result;
    }

    /**
     * 任意の文字からエスケープ出力用シンボルを得る。
     *
     * <p>このシンボルは'\'に続けて用いられる1文字である。
     * 'u'を返す事はありえない。
     *
     * @param ch 任意の文字
     * @return エスケープ出力用シンボル。
     *     1文字エスケープの必要がない場合は'\0'
     */
    private static char escapeSymbol(char ch) {
        char result;
        switch (ch) {
        case '"':
            result = '"';
            break;
        case '\\':
            result = '\\';
            break;
        case '/':
            result = '/';
            break;
        case '\b':
            result = 'b';
            break;
        case '\f':
            result = 'f';
            break;
        case '\n':
            result = 'n';
            break;
        case '\r':
            result = 'r';
            break;
        case '\t':
            result = 't';
            break;
        default:
            result = '\0';
            break;
        }
        return result;
    }

    /**
     * 特殊文字をエスケープ出力する。
     *
     * <p>特殊文字でなければなにもしない。
     *
     * @param appout 出力先
     * @param ch 文字
     * @return 特殊文字出力がエスケープされた時にtrue
     * @throws IOException 出力エラー
     */
    private static boolean dumpSpecialChar(Appendable appout, char ch)
            throws IOException {
        char esc1ch = escapeSymbol(ch);

        if (esc1ch == '\0') {
            if (!Character.isISOControl(ch)) {
                return false;
            }
            // TODO さらなる高速化が必要
            String hex = "0000" + Integer.toHexString(ch);
            hex = hex.substring(hex.length() - NIBBLES_CHAR);
            appout.append("\\u").append(hex);
        } else {
            appout.append('\\').append(esc1ch);
        }

        return true;
    }

    /**
     * Dump JSON STRING Value.
     *
     * @param appout target output
     * @param seq raw text
     * @throws IOException I/O error
     */
    public static void dumpString(Appendable appout, CharSequence seq)
            throws IOException {
        appout.append('"');

        int length = seq.length();
        for (int pos = 0; pos < length; pos++) {
            char ch = seq.charAt(pos);
            if ( !dumpSpecialChar(appout, ch) ) {
                appout.append(ch);
            }
        }

        appout.append('"');

        return;
    }

    /**
     * Return JSON STRING Value from raw text.
     *
     * @param seq raw text
     * @return JSON STRING text
     */
    // TODO いらない
    public static StringBuilder escapeText(CharSequence seq) {
        StringBuilder result = new StringBuilder();
        try {
            dumpString(result, seq);
        } catch (IOException e) {
            assert false;
            throw new AssertionError(e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Always return {@link JsTypes#STRING}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes() {
        return JsTypes.STRING;
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
     * <p>Equivalent to {@link java.lang.String#hashCode()}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.rawText.hashCode();
    }

    /**
     * Indicates whether some other STRING Value is "equal to" this STRING Value.
     *
     * <p>The same decision is made as for {@link java.lang.String#equals(Object)}.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if ( !(obj instanceof JsString) ) return false;
        JsString string = (JsString) obj;

        return this.rawText.equals(string.rawText);
    }

    /**
     * Compare between STRING typed Value.
     *
     * <p>Same as specification {@link java.lang.String#compareTo(String)}.
     *
     * @param value the object to be compared
     * @return a negative integer, zero, or a positive integer
     *     as this object is less than, equal to, or greater than the specified object.
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    public int compareTo(JsString value) {
        if (this == value) return 0;
        if (value == null) return +1;
        return this.rawText.compareTo(value.rawText);
    }

    /**
     * Returns the char value at the specified index.
     *
     * @param index char position index start with 0
     * @return char value
     * @throws IndexOutOfBoundsException if the {@code index} argument is negative
     *     or not less than the length of this string.
     */
    @Override
    public char charAt(int index) {
        return this.rawText.charAt(index);
    }

    /**
     * Returns STRING length.
     *
     * @return length of char value in text
     */
    @Override
    public int length() {
        return this.rawText.length();
    }

    /**
     * Return subsequence of text.
     *
     * @param start the begin index, inclusive
     * @param end the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException
     *     if {@code start} or {@code end} is negative,
     *     if {@code end} is greater than {@code length()},
     *     or if {@code start} is greater than {@code end}
     */
    @Override
    public CharSequence subSequence(int start, int end) throws IndexOutOfBoundsException {
        return this.rawText.subSequence(start, end);
    }

    /**
     * Return raw text.
     *
     * <p>Special chars are not escaped.
     *
     * @return raw text
     */
    public String toRawString() {
        return this.rawText;
    }

    /**
     * Returns JSON notation.
     *
     * <p>Special chars are escaped.
     *
     * <p>Text is quoted.
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder string = escapeText(this.rawText);
        return string.toString();
    }

}
