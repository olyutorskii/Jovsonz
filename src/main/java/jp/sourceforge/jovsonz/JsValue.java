/*
 * JSON value common interface
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * JSON各種Value共通インタフェース。
 */
public interface JsValue {

    /**
     * 対応するJSON型列挙を返す。
     *
     * @return JSON型列挙
     */
    JsTypes getJsTypes();

    /**
     * 深さ優先探索を行い各種構造の出現をビジターに通知する。
     *
     * @param visitor ビジター
     * @throws JsVisitException ビジターにより
     *  トラバース中断が判断された時に投げられる。
     */
    void traverse(ValueVisitor visitor) throws JsVisitException;

}
