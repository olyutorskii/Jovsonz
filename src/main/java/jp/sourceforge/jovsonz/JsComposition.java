/*
 * composition type value
 *
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * 子要素を持つJSON型の抽象インタフェース。
 * JSON最上位構造であるための必要条件。
 * 子要素を持ちうるJSON型はOBJECT型かARRAY型のみ。
 * @param <E> 反復子の要素型
 */
public interface JsComposition<E> extends JsValue, Iterable<E> {

    /**
     * 要素数を返す。
     * <p>OBJECT型の場合は直下のPAIR総数。</p>
     * <p>ARRAY型の場合は直下の子要素総数。</p>
     * @return 要素数
     */
    int size();

    /**
     * 子要素が空か否か判定する。
     * @return 要素がなければtrue
     */
    boolean isEmpty();

    /**
     * 子要素を空にする。
     */
    void clear();

    /**
     * このValueおよび子孫に変更があったか判定する。
     * Value生成直後はfalseでなければならない。
     * ロードしたデータに対し
     * 再セーブの必要があるかどうかの判定などを目的とする。
     * <p>変更が可能なValueはOBJECT型かARRAY型のみ。</p>
     * @return 変更があればtrue
     */
    boolean hasChanged();

    /**
     * このValueおよび子孫に変更がなかったことにする。
     */
    void setUnchanged();

}
