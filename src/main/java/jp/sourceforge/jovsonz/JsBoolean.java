/*
 * JSON boolean value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;

/**
 * JSON BOOLEAN型Valueを表す。
 *
 * <p>真偽値を反映する。
 * インスタンスは2つしか存在しえない。
 *
 * <p>表記例
 *
 * <pre>
 * true
 * false
 * </pre>
 */
public final class JsBoolean
        implements JsValue, Comparable<JsBoolean> {

    /** 唯一の真値。 */
    public static final JsBoolean TRUE  = new JsBoolean();
    /** 唯一の偽値。 */
    public static final JsBoolean FALSE = new JsBoolean();

    /** 真の文字列表現。 */
    public static final String TEXT_TRUE  = "true";
    /** 偽の文字列表現。 */
    public static final String TEXT_FALSE = "false";

    /** 真のハッシュ値。 */
    public static final int HASH_TRUE = Boolean.TRUE.hashCode();
    /** 偽のハッシュ値。 */
    public static final int HASH_FALSE = Boolean.FALSE.hashCode();

    /**
     * 隠しコンストラクタ。
     *
     * <p>2回しか呼ばれないはず。
     */
    private JsBoolean() {
        super();
        return;
    }

    /**
     * JSON文字列ソースからBOOLEAN型Valueを読み込む。
     *
     * <p>別型の可能性のある先頭文字を読み込んだ場合、
     * ソースに文字を読み戻した後nullが返される。
     *
     * @param source 文字列ソース
     * @return BOOLEAN型Value。別型の可能性がある場合はnull。
     * @throws IOException 入力エラー
     * @throws JsParseException 不正トークンもしくは意図しない入力終了
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
     * <p>常に{@link JsTypes#BOOLEAN}を返す。
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes() {
        return JsTypes.BOOLEAN;
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
     * 真なら{@link #HASH_TRUE}、偽なら{@link #HASH_FALSE}を返す。
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result;
        if (this == TRUE) result = HASH_TRUE;
        else              result = HASH_FALSE;
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>等価判定を行う。
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof JsBoolean) return false;
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>BOOLEAN型Valueを順序付ける。
     * ({@link #TRUE}、{@link #FALSE})の順に順序付けられる。
     *
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException 引数がnull
     */
    @Override
    public int compareTo(JsBoolean value) throws NullPointerException {
        if (value == null) throw new NullPointerException();

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
     * boolean値を反映したBOOLEAN型Valueを返す。
     *
     * @param bool boolean値
     * @return BOOLEAN型Value
     */
    public static JsBoolean valueOf(boolean bool) {
        if (bool) return TRUE;
        return FALSE;
    }

    /**
     * boolean値を返す。
     *
     * @return boolean値
     */
    public boolean booleanValue() {
        if (this == TRUE) return true;
        return false;
    }

    /**
     * 真か判定する。
     *
     * @return 真ならtrue
     */
    public boolean isTrue() {
        if (this == TRUE) return true;
        return false;
    }

    /**
     * 偽か判定する。
     *
     * @return 偽ならtrue
     */
    public boolean isFalse() {
        if (this != TRUE) return true;
        return false;
    }

    /**
     * 文字列表現を返す。
     *
     * <p>JSON表記の一部としての利用も可能。
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        if (this == TRUE) return TEXT_TRUE;
        return TEXT_FALSE;
    }

}
