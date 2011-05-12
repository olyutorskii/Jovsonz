/*
 * JSON pair in object
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;

/**
 * OBJECT型Value内に列挙される、名前の付いたValueとの組(PAIR)。
 * PAIRはValueではない。
 * <p>
 * <code>
 * <pre>
 * {
 *     "PairName1" : 99.9 ,
 *     "PairName2" : "textValue"
 * }
 * </pre>
 * </code>
 * </p>
 */
public class JsPair {

    private final String name;
    private final JsValue value;

    /**
     * コンストラクタ。
     * @param name PAIR名
     * @param value PAIR名に対応付けられるValue
     * @throws NullPointerException 引数のいずれかがnull
     */
    public JsPair(String name, JsValue value)
            throws NullPointerException{
        super();

        if(name  == null || value == null) throw new NullPointerException();

        this.name = name;
        this.value = value;

        return;
    }

    /**
     * コンストラクタ。
     * STRING型をValueに持つPAIRが生成される。
     * @param name PAIR名
     * @param text PAIR名に対応付けられる文字列データ。
     * エスケープされる前段階の表記。
     * @throws NullPointerException 引数がnull
     */
    public JsPair(String name, CharSequence text)
            throws NullPointerException{
        this(name, (JsValue) new JsString(text) );
        return;
    }

    /**
     * コンストラクタ。
     * BOOLEAN型をValueに持つPAIRが生成される。
     * @param name PAIR名
     * @param bool PAIR名に対応付けられる真偽値
     * @throws NullPointerException PAIR名がnull
     */
    public JsPair(String name, boolean bool)
            throws NullPointerException{
        this(name, JsBoolean.valueOf(bool));
        return;
    }

    /**
     * コンストラクタ。
     * NUMBER型をValueに持つPAIRが生成される。
     * @param name PAIR名
     * @param number PAIR名に対応付けられる整数値
     * @throws NullPointerException PAIR名がnull
     */
    public JsPair(String name, long number)
            throws NullPointerException{
        this(name, new JsNumber(number));
        return;
    }

    /**
     * コンストラクタ。
     * NUMBER型をValueに持つPAIRが生成される。
     * @param name PAIR名
     * @param number PAIR名に対応付けられる実数値
     * @throws NullPointerException PAIR名がnull
     */
    public JsPair(String name, double number)
            throws NullPointerException{
        this(name, new JsNumber(number));
        return;
    }

    /**
     * PAIR名を返す。
     * @return PAIR名
     */
    public String getName(){
        return this.name;
    }

    /**
     * Valueを返す。
     * @return Value
     */
    public JsValue getValue(){
        return this.value;
    }

    /**
     * {@inheritDoc}
     * ハッシュ値を返す。
     * PAIR名とValue双方のハッシュ値から合成される。
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode(){
        int nameHash = this.name.hashCode();
        int valHash = this.value.hashCode();
        return nameHash ^ valHash;
    }

    /**
     * {@inheritDoc}
     * 等価判定を行う。
     * PAIR名とValue双方が一致する場合のみ真となる。
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;

        if( ! (obj instanceof JsPair) ) return false;
        JsPair target = (JsPair) obj;

        if( ! this.name .equals(target.name)  ) return false;
        if( ! this.value.equals(target.value) ) return false;

        return true;
    }

    /**
     * 文字列表現を返す。
     * JSON表記の一部としての利用も可能。
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        try{
            JsString.dumpString(result, this.name);
        }catch(IOException e){
            assert false;
            throw new AssertionError(e);
        }

        result.append(':')
            .append(this.value.toString());

        return result.toString();
    }

}
