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

/**
 * JSON NUMBER型Valueを表す。
 * 整数、実数を含めた数値を反映する。
 * <p>
 * 10を基数とした{@link java.math.BigDecimal}を実装ベースとする。
 * ※ IEEE754浮動小数ではない。
 * </p>
 * <p>(1)と(1.0)はスケール値によって区別される</p>
 * <h1>表記例</h1>
 * <pre>
 * -43
 * 0.56
 * 3.23E-06
 * </pre>
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
     * コンストラクタ。
     * @param val 初期整数値
     */
    public JsNumber(long val){
        this(BigDecimal.valueOf(val));
        return;
    }

    /**
     * コンストラクタ。
     * <p>
     * {@link java.math.BigDecimal#valueOf(double)}と同等の丸めが行われる。
     * (1.0/10.0)を渡すと0.1相当になる。
     * 必要に応じて{@link java.math.BigDecimal}を
     * 引数に持つコンストラクタと使い分けること。
     * </p>
     * @param val 初期実数値
     * @see java.math.BigDecimal#valueOf(double)
     */
    public JsNumber(double val){
        this(BigDecimal.valueOf(val));
        return;
    }

    /**
     * コンストラクタ。
     * @param val 初期整数値
     * @throws ArithmeticException 正確な結果を
     * {@link java.math.BigDecimal}に納め切れない
     */
    public JsNumber(BigInteger val) throws ArithmeticException{
        this(new BigDecimal(val, DEF_MC));
        return;
    }

    /**
     * コンストラクタ。
     * 書式は{@link java.math.BigDecimal#BigDecimal(String)}に準ずる。
     * @param val 初期数値の文字列表記
     * @throws NumberFormatException 不正な数値表記
     * @throws ArithmeticException 正確な結果を
     * {@link java.math.BigDecimal}に納め切れない
     * @see java.math.BigDecimal#BigDecimal(String)
     */
    public JsNumber(CharSequence val)
            throws NumberFormatException, ArithmeticException{
        this(new BigDecimal(val.toString(), DEF_MC));
        return;
    }

    /**
     * コンストラクタ。
     * @param val 初期数値
     * @throws NullPointerException 引数がnull
     */
    public JsNumber(BigDecimal val) throws NullPointerException{
        super();
        if(val == null) throw new NullPointerException();
        this.decimal = val;
        return;
    }

    /**
     * 任意の文字がUnicodeのBasic-Latinの数字か否か判定する。
     * @param ch 文字
     * @return 数字ならtrue
     * @see java.lang.Character#isDigit(char)
     */
    public static boolean isLatinDigit(char ch){
        if('0' <= ch && ch <= '9') return true;
        return false;
    }

    /**
     * 文字ソースから符号付きの数字並びを読み込む。
     * 先頭'+'符号は読み飛ばされる。
     * 冒頭のゼロ'0'に続く数字を許すか否か指定が可能。
     * <p>NUMBER型表記の整数部、小数部、指数部読み込みの下請けメソッド。</p>
     * @param source 文字列ソース
     * @param app 出力先
     * @param allowZeroTrail 冒頭のゼロ'0'に続く数字を許すならtrue
     * @return 引数と同じ出力先
     * @throws IOException 入出力エラー
     * @throws JsParseException 不正な書式もしくは意図しない入力終了
     */
    private static Appendable appendDigitText(JsonSource source,
                                              Appendable app,
                                              boolean allowZeroTrail)
            throws IOException, JsParseException{
        char head = source.readOrDie();
        if     (head == '-') app.append('-');
        else if(head != '+') source.unread(head);

        boolean hasAppended = false;
        boolean zeroStarted = false;    // 先頭は0か
        for(;;){
            if( ! source.hasMore() && hasAppended) break;

            char readedCh = source.readOrDie();

            if( ! isLatinDigit(readedCh) ){
                if( ! hasAppended ){
                    throw new JsParseException(ERRMSG_NONUMBER,
                                               source.getLineNumber() );
                }
                source.unread(readedCh);
                break;
            }

            if(hasAppended){
                if(zeroStarted && ! allowZeroTrail){
                    throw new JsParseException(ERRMSG_EXTRAZERO,
                                               source.getLineNumber() );
                }
            }else{                       // 1st char
                if(readedCh == '0'){
                    zeroStarted = true;
                }
            }

            app.append(readedCh);
            hasAppended = true;
        }

        return app;
    }

    /**
     * 文字ソースから、ピリオド「.」で始まるNUMBER型小数部を読み込む。
     * 小数部がなければなにもせずに戻る。
     * @param source 文字列ソース
     * @param app 出力先
     * @return 引数と同じ出力先
     * @throws IOException 入出力エラー
     * @throws JsParseException 不正な書式もしくは意図しない入力終了
     */
    private static Appendable appendFractionPart(JsonSource source,
                                                 Appendable app )
            throws IOException, JsParseException{
        if( ! source.hasMore() ) return app;

        char chData;

        chData = source.readOrDie();
        if(chData != '.'){
            source.unread(chData);
            return app;
        }

        app.append(".");

        boolean hasAppended = false;
        for(;;){
            if( ! source.hasMore() && hasAppended) break;

            chData = source.readOrDie();

            if( ! isLatinDigit(chData) ){
                if( ! hasAppended ){
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
     * 文字ソースから「e」もしくは「E」で始まるNUMBER型指数部を読み込む。
     * 指数部がなければなにもせずに戻る。
     * @param source 文字列ソース
     * @param app 出力先
     * @return 引数と同じ出力先
     * @throws IOException 入出力エラー
     * @throws JsParseException 不正な書式もしくは意図しない入力終了
     */
    private static Appendable appendExpPart(JsonSource source,
                                            Appendable app )
            throws IOException, JsParseException{
        if( ! source.hasMore() ) return app;

        char chData = source.readOrDie();
        if(chData != 'e' && chData != 'E'){
            source.unread(chData);
            return app;
        }

        app.append('E');

        appendDigitText(source, app, true);

        return app;
    }

    /**
     * JSON文字列ソースからNUMBER型Valueを読み込む。
     * 別型の可能性のある先頭文字を読み込んだ場合、
     * ソースに文字を読み戻した後nullが返される。
     * @param source 文字列ソース
     * @return NUMBER型Value。別型の可能性がある場合はnull。
     * @throws IOException 入力エラー
     * @throws JsParseException 不正な表記もしくは意図しない入力終了
     */
    static JsNumber parseNumber(JsonSource source)
            throws IOException, JsParseException{
        char charHead = source.readOrDie();
        source.unread(charHead);
        if( charHead != '-' && ! JsNumber.isLatinDigit(charHead) ){
            return null;
        }

        StringBuilder numText = new StringBuilder();

        appendDigitText   (source, numText, false);
        appendFractionPart(source, numText);
        appendExpPart     (source, numText);

        JsNumber result = new JsNumber(numText);

        return result;
    }

    /**
     * {@inheritDoc}
     * 常に{@link JsTypes#NUMBER}を返す。
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes(){
        return JsTypes.NUMBER;
    }

    /**
     * 各種構造の出現をビジターに通知する。
     * この実装ではthisの出現のみを通知する。
     * @param visitor {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void traverse(ValueVisitor visitor)
            throws JsVisitException{
        visitor.visitValue(this);
        return;
    }

    /**
     * {@inheritDoc}
     * ハッシュ値を返す。
     * {@link java.math.BigDecimal#hashCode()}と同じ値を返す。
     * @return {@inheritDoc}
     * @see java.math.BigDecimal#hashCode()
     */
    @Override
    public int hashCode(){
        return this.decimal.hashCode();
    }

    /**
     * {@inheritDoc}
     * 等価判定を行う。
     * {@link java.math.BigDecimal#equals(Object)}と同等の判断が行われる。
     * 「1.2」と「0.12E+1」など、
     * スケールの一致しない値は異なる値と見なされる。
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @see java.math.BigDecimal#equals(Object)
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if( ! (obj instanceof JsNumber) ) return false;
        JsNumber number = (JsNumber) obj;
        return this.decimal.equals(number.decimal);
    }

    /**
     * {@inheritDoc}
     * NUMBER型Valueを昇順に順序付ける。
     * 「1.2」と「0.12E+1」など、スケールが異なっても値が同じであれば
     * 等しい値と見なされる。
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @see java.math.BigDecimal#compareTo(BigDecimal)
     */
    @Override
    public int compareTo(JsNumber value){
        if(this == value) return 0;
        return this.decimal.compareTo(value.decimal);
    }

    /**
     * int型の数値を返す。
     * 情報が失われる可能性がある。
     * @return int型数値
     * @see java.lang.Number#intValue()
     * @see java.math.BigDecimal#intValue()
     */
    public int intValue(){
        return this.decimal.intValue();
    }

    /**
     * long型の数値を返す。
     * 情報が失われる可能性がある。
     * @return long型数値
     * @see java.lang.Number#longValue()
     * @see java.math.BigDecimal#longValue()
     */
    public long longValue(){
        return this.decimal.longValue();
    }

    /**
     * float型の数値を返す。
     * 情報が失われる可能性がある。
     * @return float型数値
     * @see java.lang.Number#floatValue()
     * @see java.math.BigDecimal#floatValue()
     */
    public float floatValue(){
        return this.decimal.floatValue();
    }

    /**
     * double型の数値を返す。
     * 情報が失われる可能性がある。
     * @return double型数値
     * @see java.lang.Number#doubleValue()
     * @see java.math.BigDecimal#doubleValue()
     */
    public double doubleValue(){
        return this.decimal.doubleValue();
    }

    /**
     * {@link java.math.BigDecimal}型の数値表現を返す。
     * @return BigDecimal型数値
     */
    public BigDecimal decimalValue(){
        return this.decimal;
    }

    /**
     * スケール値を返す。
     * このインスタンスが整数文字列表記に由来する場合、
     * スケール値は0になるはず。
     * 
     * <ul>
     * <li>"99"のスケール値は0
     * <li>"99.0"のスケール値は1
     * <li>"99.01"のスケール値は2
     * <li>"99E+3"のスケール値は-3
     * <li>"99.0E+3"のスケール値は-2
     * </ul>
     * 
     * @return スケール値
     * @see java.math.BigDecimal#scale()
     */
    public int scale(){
        return this.decimal.scale();
    }

    /**
     * 文字列表現を返す。
     * {@link java.math.BigDecimal#toString()}に準ずる。
     * JSON表記の一部としての利用も可能。
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        return this.decimal.toString();
    }

}
