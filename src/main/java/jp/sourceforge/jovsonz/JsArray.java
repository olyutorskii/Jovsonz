/*
 * JSON array value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * JSON ARRAY型Valueを表す。
 * 子要素の配列リストを反映する。
 * <h1>表記例</h1>
 * <pre>
 * [
 *     true ,
 *     "ABC" ,
 *     12.3
 * ]
 * </pre>
 */
public class JsArray
        implements JsComposition<JsValue> {

    private static final String ERRMSG_NOARRAYCOMMA =
            "missing comma in ARRAY";
    private static final String ERRMSG_NOELEM =
            "missing element in ARRAY";

    private final List<JsValue> valueList = new LinkedList<JsValue>();
    private boolean changed = false;

    /**
     * コンストラクタ。
     */
    public JsArray(){
        super();
        return;
    }

    /**
     * JSON文字列ソースからARRAY型Valueを読み込む。
     * さらに子Valueへとパース解析が進む可能性がある。
     * 別型の可能性のある先頭文字を読み込んだ場合、
     * ソースに文字を読み戻した後nullが返される。
     * @param source 文字列ソース
     * @return ARRAY型Value。別型の可能性がある場合はnull。
     * @throws IOException 入力エラー
     * @throws JsParseException 不正な表現または意図しない入力終了
     */
    static JsArray parseArray(JsonSource source)
            throws IOException, JsParseException {
        char charHead = source.readOrDie();
        if(charHead != '['){
            source.unread(charHead);
            return null;
        }

        JsArray result = new JsArray();

        for(;;){
            source.skipWhiteSpace();
            char chData = source.readOrDie();
            if(chData == ']') break;

            if(result.isEmpty()){
                source.unread(chData);
            }else{
                if(chData != ','){
                    throw new JsParseException(ERRMSG_NOARRAYCOMMA,
                                               source.getLineNumber() );
                }
            }

            JsValue value = Json.parseValue(source);
            if(value == null){
                throw new JsParseException(ERRMSG_NOELEM,
                                           source.getLineNumber() );
            }

            result.add(value);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 常に{@link JsTypes#ARRAY}を返す。
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes(){
        return JsTypes.ARRAY;
    }

    /**
     * このValueおよび子孫に変更があったか判定する。
     * 子要素の追加・削除が行われたか、
     * もしくは子要素のいずれかに変更が認められれば、
     * このARRAY型Valueに変更があったとみなされる。
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasChanged(){
        if(this.changed) return true;

        for(JsValue value : this.valueList){
            if( ! (value instanceof JsComposition) ) continue;
            JsComposition composition = (JsComposition) value;
            if(composition.hasChanged()) return true;
        }

        return false;
    }

    /**
     * このValueおよび子孫に変更がなかったことにする。
     */
    @Override
    public void setUnchanged(){
        this.changed = false;

        for(JsValue value : this.valueList){
            if( ! (value instanceof JsComposition) ) continue;
            JsComposition composition = (JsComposition) value;
            composition.setUnchanged();
        }

        return;
    }

    /**
     * 深さ優先探索を行い各種構造の出現をビジターに通知する。
     * thisを通知した後、子Valueを順に訪問し、最後に閉じ括弧を通知する。
     * @param visitor {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void traverse(ValueVisitor visitor) throws JsVisitException{
        visitor.visitValue(this);

        for(JsValue value : this.valueList){
            value.traverse(visitor);
        }

        visitor.visitCompositionClose(this);

        return;
    }

    /**
     * 配列要素数を返す。
     * @return {@inheritDoc}
     */
    @Override
    public int size(){
        return this.valueList.size();
    }

    /**
     * 配列が空か判定する。
     * @return {@inheritDoc}
     */
    @Override
    public boolean isEmpty(){
        return this.valueList.isEmpty();
    }

    /**
     * 配列を空にする。
     */
    @Override
    public void clear(){
        if(this.valueList.size() > 0) this.changed = true;
        this.valueList.clear();
        return;
    }

    /**
     * ハッシュ値を返す。
     * 全ての子孫Valueのハッシュ値からその都度合成される。高コスト注意！。
     * @return {@inheritDoc}
     * @see java.util.List#hashCode()
     */
    @Override
    public int hashCode(){
        return this.valueList.hashCode();
    }

    /**
     * 等価判定を行う。
     * 双方の配列サイズが一致し
     * その全ての子Valueでのequals()が等価と判断された場合のみ
     * 等価と判断される。
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @see java.util.List#equals(Object)
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;

        if( ! (obj instanceof JsArray) ) return false;
        JsArray array = (JsArray) obj;

        return this.valueList.equals(array.valueList);
    }

    /**
     * 配列にValueを追加する。
     * 同じJsValueインスタンスを複数回追加することも可能。
     * @param value JSON Value
     * @throws NullPointerException 引数がnull
     */
    public void add(JsValue value) throws NullPointerException{
        if(value == null) throw new NullPointerException();
        this.valueList.add(value);
        this.changed = true;
        return;
    }

    /**
     * 配列から指定された位置のValueを返す。
     * @param index 0で始まる配列上の位置
     * @return Value JSON Value
     * @throws IndexOutOfBoundsException 不正な位置指定
     */
    public JsValue get(int index) throws IndexOutOfBoundsException{
        return this.valueList.get(index);
    }

    /**
     * 配列からValueを削除する。
     * {@link java.util.List#remove(Object)}と異なり、
     * 削除対象の検索に際して
     * {@link java.lang.Object#equals(Object)}は使われない。
     * 一致するインスタンスが複数存在する場合、
     * 先頭に近いインスタンスのみ削除される。
     * 一致するインスタンスが存在しなければなにもしない。
     * @param value JSON Value
     * @return 既存のValueが削除されたならtrue
     */
    // TODO 必要？
    public boolean remove(JsValue value){
        boolean removed = false;

        Iterator<JsValue> it = this.valueList.iterator();
        while(it.hasNext()){
            JsValue elem = it.next();
            if(elem == value){
                it.remove();
                this.changed = true;
                removed = true;
                break;
            }
        }

        return removed;
    }

    /**
     * 配列から指定位置のValueを削除する。
     * @param index 0で始まる削除対象のインデックス値
     * @return 削除されたValue
     * @throws IndexOutOfBoundsException 不正なインデックス値
     */
    public JsValue remove(int index) throws IndexOutOfBoundsException{
        JsValue removed = this.valueList.remove(index);
        this.changed = true;
        return removed;
    }

    /**
     * Valueにアクセスするための反復子を提供する。
     * この反復子での削除作業はできない。
     * @return 反復子イテレータ
     * @see UnmodIterator
     */
    public Iterator<JsValue> iterator(){
        return UnmodIterator.unmodIterator(this.valueList);
    }

    /**
     * {@inheritDoc}
     * 文字列表現を返す。
     * JSON表記の全体もしくは一部としての利用も可能。
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder text = new StringBuilder();

        text.append("[");
        boolean hasElem = false;
        for(JsValue value : this.valueList){
            if(hasElem) text.append(',');
            text.append(value);
            hasElem = true;
        }
        text.append("]");

        return text.toString();
    }

}
