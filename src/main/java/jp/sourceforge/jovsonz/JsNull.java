/*
 * JSON null value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;

/**
 * JSON NULL型Valueを表す。
 * Javaのnullとは一切無関係。
 * その実体はシングルトン。
 * <h1>表記例</h1>
 * <pre>
 * null
 * </pre>
 */
public final class JsNull
        implements JsValue, Comparable<JsNull> {

    /** ただ唯一のインスタンス。 */
    public static final JsNull NULL = new JsNull();

    /** 唯一の文字列表現。 */
    public static final String TEXT = "null";

    /** 唯一のハッシュ値。 */
    public static final int ONLYHASH = 982451653; // 大きな素数;

    /**
     * 隠しコンストラクタ。
     * 1回しか呼ばれないはず
     */
    private JsNull(){
        super();
        return;
    }

    /**
     * JSON文字列ソースからNULL型Valueを読み込む。
     * 別型の可能性のある先頭文字を読み込んだ場合、
     * ソースに文字を読み戻した後nullが返される。
     * @param source 文字列ソース
     * @return NULL型Value。別型の可能性がある場合はnull。
     * @throws IOException 入力エラー
     * @throws JsParseException 不正トークンもしくは意図しない入力終了
     */
    static JsNull parseNull(JsonSource source)
            throws IOException, JsParseException{
        char charHead = source.readOrDie();

        if(charHead != 'n'){
            source.unread(charHead);
            return null;
        }

        if( ! source.matchOrDie("ull") ){
            throw new JsParseException(JsParseException.ERRMSG_INVALIDTOKEN,
                                       source.getLineNumber() );
        }

        return JsNull.NULL;
    }

    /**
     * {@inheritDoc}
     * 常に{@link JsTypes#NULL}を返す。
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes(){
        return JsTypes.NULL;
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
     * 常に{@value ONLYHASH}を返す。
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode(){
        return ONLYHASH;
    }

    /**
     * {@inheritDoc}
     * 等価判定を行う。
     * {@link #NULL}が渡された時のみtrueを返す。
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj instanceof JsNull) return true;  // シングルトンには冗長
        return false;
    }

    /**
     * {@inheritDoc}
     * NULL型Valueを順序付ける。シングルトン相手にほぼ無意味。
     * null以外の引数には必ず0を返す。
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException 引数がnull
     */
    @Override
    public int compareTo(JsNull value) throws NullPointerException{
        if(value == null) throw new NullPointerException();
        return 0;
    }

    /**
     * {@inheritDoc}
     * 文字列表現を返す。
     * 常に文字列 {@value TEXT} を返す。
     * JSON表記の一部としての利用も可能。
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        return TEXT;
    }

}