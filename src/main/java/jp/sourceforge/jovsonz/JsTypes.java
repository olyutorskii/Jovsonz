/*
 * JSON types
 *
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.util.Objects;

/**
 * JSON 各種型列挙。
 */
public enum JsTypes {

    /** NUMBER型に対応。 */
    NUMBER(  JsNumber .class ),
    /** STRING型に対応。 */
    STRING(  JsString .class ),
    /** BOOLEAN型に対応。 */
    BOOLEAN( JsBoolean.class ),
    /** ARRAY型に対応。 */
    ARRAY(   JsArray  .class ),
    /** OBJECT型に対応。 */
    OBJECT(  JsObject .class ),
    /** NULL型に対応。 */
    NULL(    JsNull   .class ),
    ;

    private static final JsTypes[] VALUE_ARRAY = values();

    private final Class<? extends JsValue> klass;
    private final boolean isJsComposition;

    /**
     * コンストラクタ。
     *
     * @param klass {@link java.lang.Class}型
     */
    private JsTypes(Class<? extends JsValue> klass) {
        this.klass = klass;

        if (JsComposition.class.isAssignableFrom(this.klass)) {
            this.isJsComposition = true;
        } else {
            this.isJsComposition = false;
        }

        return;
    }

    /**
     * {@link java.lang.Class}型から対応する型列挙を返す。
     *
     * @param carg 任意のjava.lang.Class型変数
     * @return 型列挙。JSON型に由来しないクラスが指定されたときはnull
     * @throws NullPointerException 引数がnull
     */
    public static JsTypes getJsTypes(Class<?> carg) {
        Objects.requireNonNull(carg);

        for (JsTypes types : VALUE_ARRAY) {
            if (types.klass == carg) return types;
        }

        return null;
    }

    /**
     * 対応する{@link java.lang.Class}型を返す。
     *
     * @return java.lang.Class型
     */
    public Class<? extends JsValue> getJsClass() {
        return this.klass;
    }

    /**
     * このJSON型が子要素を持ちうるか判定する。
     *
     * <p>子要素を持ちうるJSON型はOBJECT型かARRAY型のみ。
     *
     * @return 子要素を持ちうるならtrue
     */
    public boolean isComposition() {
        return this.isJsComposition;
    }

}
