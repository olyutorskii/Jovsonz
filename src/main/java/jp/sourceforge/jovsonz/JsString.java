/*
 * JSON string value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;

/**
 * JSON STRING型Valueを表す。
 *
 * <p>Unicode文字列データを反映する。
 *
 * <p>表記例
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
     * コンストラクタ。
     *
     * <p>長さ0の空文字が設定される。
     */
    public JsString() {
        this("");
        return;
    }

    /**
     * コンストラクタ。
     *
     * <p>引数はJSON書式ではない生文字列。
     *
     * @param rawSeq 生文字列
     * @throws NullPointerException 引数がnull
     */
    public JsString(CharSequence rawSeq) throws NullPointerException {
        super();
        if (rawSeq == null) throw new NullPointerException();
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

        if (esc1ch != '\0') {
            appout.append('\\').append(esc1ch);
        } else if (Character.isISOControl(ch)) {
            // TODO さらなる高速化が必要
            String hex = "0000" + Integer.toHexString(ch);
            hex = hex.substring(hex.length() - NIBBLES_CHAR);
            appout.append("\\u").append(hex);
        } else {
            return false;
        }

        return true;
    }

    /**
     * JSON STRING型Value形式で文字列を出力する。
     *
     * @param appout 文字出力
     * @param seq 文字列
     * @throws IOException 出力エラー
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
     * JSON STRING型Value形式の文字列を返す。
     *
     * @param seq 生文字列
     * @return STRING型表記に変換された文字列
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
     * <p>常に{@link JsTypes#STRING}を返す。
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes() {
        return JsTypes.STRING;
    }

    /**
     * 各種構造の出現をビジターに通知する。
     *
     * <p>この実装ではthisの出現のみを通知する。
     *
     * @param visitor {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void traverse(ValueVisitor visitor)
            throws JsVisitException {
        visitor.visitValue(this);
        return;
    }

    /**
     * {@inheritDoc}
     *
     * <p>ハッシュ値を返す。
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.rawText.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * <p>等価判定を行う。
     *
     * <p>{@link java.lang.String#equals(Object)}に準ずる。
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if ( !(obj instanceof JsString) ) return false;
        JsString string = (JsString) obj;

        return this.rawText.equals(string.rawText);
    }

    /**
     * {@inheritDoc}
     *
     * <p>STRING型Valueを昇順に順序付ける。
     *
     * <p>{@link java.lang.String#compareTo(String)}に準ずる。
     *
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(JsString value) {
        if (this == value) return 0;
        if (value == null) return +1;
        return this.rawText.compareTo(value.rawText);
    }

    /**
     * {@inheritDoc}
     *
     * <p>指定位置の文字を返す。
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException if the {@code index} argument is negative
     *     or not less than the length of this string.
     */
    @Override
    public char charAt(int index)
            throws IndexOutOfBoundsException {
        return this.rawText.charAt(index);
    }

    /**
     * {@inheritDoc}
     *
     * <p>文字列長(char値総数)を返す。
     *
     * @return {@inheritDoc}
     */
    @Override
    public int length() {
        return this.rawText.length();
    }

    /**
     * {@inheritDoc}
     *
     * <p>部分文字列を返す。
     *
     * @param start {@inheritDoc}
     * @param end {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException
     *     if {@code start} or {@code end} is negative,
     *     if {@code end} is greater than {@code length()},
     *     or if {@code start} is greater than {@code end}
     */
    @Override
    public CharSequence subSequence(int start, int end)
            throws IndexOutOfBoundsException {
        return this.rawText.subSequence(start, end);
    }

    /**
     * クォーテーションやエスケープ処理の施されていない生の文字列を返す。
     *
     * @return 生の文字列
     */
    public String toRawString() {
        return this.rawText;
    }

    /**
     * {@inheritDoc}
     *
     * <p>クォーテーションとエスケープ処理の施された文字列表記を生成する。
     * JSON表記の一部としての利用も可能。
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder string = escapeText(this.rawText);
        return string.toString();
    }

}
