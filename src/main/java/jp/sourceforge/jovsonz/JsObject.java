/*
 * JSON object value
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * JSON OBJECT型Valueを表す。
 *
 * <p>PAIR名と子要素の組(PAIR)の集合を反映する。
 * PAIR名の並び順に関しては未定義とする。
 *
 * <p>表記例
 *
 * <pre>
 * {
 *     "Name" : "Joe" ,
 *     "Age" : 19
 * }
 * </pre>
 */
public class JsObject
        implements JsComposition<JsPair> {

    private static final String ERRMSG_NOOBJECTCOMMA =
            "missing comma in OBJECT";
    private static final String ERRMSG_NOHASHNAME =
            "no hash name in OBJECT";
    private static final String ERRMSG_NOHASHSEP =
            "missing hash separator(:) in OBJECT";
    private static final String ERRMSG_NOHASHVAL =
            "no hash value in OBJECT";

    private final Map<String, JsPair> pairMap =
            new TreeMap<>();
    private final Collection<JsPair> pairCollection = this.pairMap.values();

    private boolean changed = false;


    /**
     * コンストラクタ。
     */
    public JsObject(){
        super();
        return;
    }

    /**
     * JSON文字列ソースからOBJECT型Valueを読み込む。
     *
     * <p>さらに子Valueへとパース解析が進む可能性がある。
     *
     * <p>別型の可能性のある先頭文字を読み込んだ場合、
     * ソースに文字を読み戻した後nullが返される。
     *
     * @param source 文字列ソース
     * @return OBJECT型Value。別型の可能性がある場合はnull。
     * @throws IOException 入力エラー
     * @throws JsParseException 不正な表記もしくは意図しない入力終了
     */
    static JsObject parseObject(JsonSource source)
            throws IOException, JsParseException{
        char charHead = source.readOrDie();
        if(charHead != '{'){
            source.unread(charHead);
            return null;
        }

        JsObject result = new JsObject();

        for(;;){
            source.skipWhiteSpace();
            char chData = source.readOrDie();
            if(chData == '}') break;

            if(result.isEmpty()){
                source.unread(chData);
            }else{
                if(chData != ','){
                    throw new JsParseException(ERRMSG_NOOBJECTCOMMA,
                                               source.getLineNumber() );
                }
                source.skipWhiteSpace();
            }

            JsString name = JsString.parseString(source);
            if(name == null){
                throw new JsParseException(ERRMSG_NOHASHNAME,
                                           source.getLineNumber() );
            }

            source.skipWhiteSpace();
            chData = source.readOrDie();
            if(chData != ':'){
                throw new JsParseException(ERRMSG_NOHASHSEP,
                                           source.getLineNumber() );
            }

            JsValue value = Json.parseValue(source);
            if(value == null){
                throw new JsParseException(ERRMSG_NOHASHVAL,
                                           source.getLineNumber() );
            }

            result.putValue(name.toRawString(), value);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>常に{@link JsTypes#OBJECT}を返す。
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes(){
        return JsTypes.OBJECT;
    }

    /**
     * このValueおよび子孫に変更があったか判定する。
     *
     * <p>PAIRの追加・削除が行われたか、
     * もしくはPAIRのValue値いずれかに変更が認められれば、
     * このOBJECT型Valueに変更があったとみなされる。
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasChanged(){
        if(this.changed) return true;

        for(JsPair pair : this){
            JsValue value = pair.getValue();
            if( ! (value instanceof JsComposition) ) continue;
            JsComposition<?> composition = (JsComposition) value;
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

        for(JsPair pair : this){
            JsValue value = pair.getValue();
            if( ! (value instanceof JsComposition) ) continue;
            JsComposition<?> composition = (JsComposition) value;
            composition.setUnchanged();
        }

        return;
    }

    /**
     * 深さ優先探索を行い各種構造の出現をビジターに通知する。
     *
     * <p>thisを通知した後、PAIRの各名前およびValueを順に訪問し、
     * 最後に閉じ括弧を通知する。
     *
     * <p>PAIRの訪問順に関しては未定義。
     *
     * @param visitor {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void traverse(ValueVisitor visitor) throws JsVisitException{
        visitor.visitValue(this);

        for(JsPair pair : this){
            String name   = pair.getName();
            JsValue value = pair.getValue();
            visitor.visitPairName(name);
            value.traverse(visitor);
        }

        visitor.visitCompositionClose(this);

        return;
    }

    /**
     * PAIR総数を返す。
     *
     * @return PAIR総数
     */
    @Override
    public int size(){
        return this.pairMap.size();
    }

    /**
     * PAIR集合が空か判定する。
     *
     * @return 空ならtrue
     */
    @Override
    public boolean isEmpty(){
        return this.pairMap.isEmpty();
    }

    /**
     * PAIR集合を空にする。
     */
    @Override
    public void clear(){
        if(this.pairMap.size() > 0) this.changed = true;
        this.pairMap.clear();
        return;
    }

    /**
     * ハッシュ値を返す。
     *
     * <p>全てのPAIRのハッシュ値からその都度合成される。高コスト注意！。
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode(){
        return this.pairMap.hashCode();
    }

    /**
     * 等価判定を行う。
     *
     * <p>双方のPAIR数が一致し、
     * 全てのPAIR名およびそれに対応付けられたValueが一致した場合のみ
     * 等価と判断される。
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;

        if( ! (obj instanceof JsObject) ) return false;
        JsObject composit = (JsObject) obj;

        return this.pairMap.equals(composit.pairMap);
    }

    /**
     * 名前とValueからPAIRを登録する。
     *
     * @param name 名前
     * @param value Value
     * @return 旧Value。同じ内容のPAIRがすでに存在していたらnull
     * @throws NullPointerException 引数のいずれかがnull
     */
    public JsValue putValue(String name, JsValue value)
            throws NullPointerException{
        if(name  == null) throw new NullPointerException();
        if(value == null) throw new NullPointerException();

        JsValue oldValue = null;
        JsPair oldPair = this.pairMap.get(name);
        if(oldPair != null){
            oldValue = oldPair.getValue();
            if(value.equals(oldValue)) return null;
        }

        JsPair newPair = new JsPair(name, value);
        this.pairMap.put(name, newPair);

        this.changed = true;
        return oldValue;
    }

    /**
     * PAIR名からValueを取得する。
     *
     * @param name PAIR名
     * @return 対応するValue。見つからなければnull
     */
    public JsValue getValue(String name){
        JsPair pair = this.pairMap.get(name);
        if(pair == null) return null;
        JsValue value = pair.getValue();
        return value;
    }

    /**
     * PAIRを追加する。
     *
     * <p>同じPAIR名を持つPAIRは無条件に上書きされる。
     *
     * @param pair PAIR
     */
    public void putPair(JsPair pair){
        this.pairMap.put(pair.getName(), pair);
        return;
    }

    /**
     * PAIR名からPAIRを返す。
     *
     * @param name PAIR名
     * @return PAIR。見つからなければnull
     */
    public JsPair getPair(String name){
        JsValue value = getValue(name);
        if(value == null) return null;

        return new JsPair(name, value);
    }

    /**
     * 指定した名前のPAIRを削除する。
     *
     * @param name PAIR名
     * @return 消されたPAIR。該当するPAIRがなければnull
     */
    public JsPair remove(String name){
        JsPair oldPair = this.pairMap.remove(name);
        if(oldPair != null) this.changed = true;

        return oldPair;
    }

    /**
     * 保持する全PAIRのPAIR名の集合を返す。
     *
     * @return すべての名前
     */
    public Set<String> nameSet(){
        return this.pairMap.keySet();
    }

    /**
     * PAIRのリストを返す。
     *
     * <p>このリストを上書き操作しても影響はない。
     *
     * @return PAIRリスト
     */
    public List<JsPair> getPairList(){
        List<JsPair> result = new ArrayList<>(this.pairMap.size());

        for(JsPair pair : this){
            result.add(pair);
        }

        return result;
    }

    /**
     * PAIRにアクセスするための反復子を提供する。
     *
     * <p>この反復子での削除作業はできない。
     * PAIR出現順序は未定義。
     *
     * @return 反復子イテレータ
     */
    @Override
    public Iterator<JsPair> iterator(){
        return UnmodIterator.unmodIterator(this.pairCollection);
    }

    /**
     * 文字列表現を返す。
     *
     * <p>JSON表記の全体もしくは一部としての利用も可能。
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder text = new StringBuilder();

        text.append("{");

        boolean hasElem = false;
        for(JsPair pair : this){
            if(hasElem) text.append(',');
            text.append(pair);
            hasElem = true;
        }

        text.append("}");

        return text.toString();
    }

}
