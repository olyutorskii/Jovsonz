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
 * OBJECT型Value内に列挙される、名前の付いたValueとの組(PAIR)。
 *
 * <p>PAIRはValueではない。
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
     * コンストラクタ。
     *
     * @param name PAIR名
     * @param value PAIR名に対応付けられるValue
     * @throws NullPointerException 引数のいずれかがnull
     */
    public JsPair(String name, JsValue value) {
        super();
        this.name  = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
        return;
    }

    /**
     * コンストラクタ。
     *
     * <p>STRING型をValueに持つPAIRが生成される。
     *
     * @param name PAIR名
     * @param text PAIR名に対応付けられる文字列データ。
     *     エスケープされる前段階の表記。
     * @throws NullPointerException 引数がnull
     */
    public JsPair(String name, CharSequence text) {
        this(name, (JsValue) new JsString(text));
        return;
    }

    /**
     * コンストラクタ。
     *
     * <p>BOOLEAN型をValueに持つPAIRが生成される。
     *
     * @param name PAIR名
     * @param bool PAIR名に対応付けられる真偽値
     * @throws NullPointerException PAIR名がnull
     */
    public JsPair(String name, boolean bool) {
        this(name, JsBoolean.valueOf(bool));
        return;
    }

    /**
     * コンストラクタ。
     *
     * <p>NUMBER型をValueに持つPAIRが生成される。
     *
     * @param name PAIR名
     * @param number PAIR名に対応付けられる整数値
     * @throws NullPointerException PAIR名がnull
     */
    public JsPair(String name, long number) {
        this(name, new JsNumber(number));
        return;
    }

    /**
     * コンストラクタ。
     *
     * <p>NUMBER型をValueに持つPAIRが生成される。
     *
     * @param name PAIR名
     * @param number PAIR名に対応付けられる実数値
     * @throws NullPointerException PAIR名がnull
     */
    public JsPair(String name, double number) {
        this(name, new JsNumber(number));
        return;
    }

    /**
     * PAIR名を返す。
     *
     * @return PAIR名
     */
    public String getName() {
        return this.name;
    }

    /**
     * Valueを返す。
     *
     * @return Value
     */
    public JsValue getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     *
     * <p>ハッシュ値を返す。
     *
     * <p>PAIR名とValue双方のハッシュ値から合成される。
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int nameHash = this.name.hashCode();
        int valHash = this.value.hashCode();
        return nameHash ^ valHash;
    }

    /**
     * {@inheritDoc}
     *
     * <p>等価判定を行う。
     *
     * <p>PAIR名とValue双方が一致する場合のみ真となる。
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
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
     * 文字列表現を返す。
     *
     * <p>JSON表記の一部としての利用も可能。
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
