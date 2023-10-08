/*
 * JSON value visitor
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * JSONツリー上の各Valueへの深さ優先ビジター共通インタフェース。
 */
public interface ValueVisitor {

    /**
     * Value登場の通知を受け取る。
     *
     * @param value JSON Value
     * @throws JsVisitException ビジターがトラバース中止を判断した際に
     *     投げられる。
     */
    public abstract void visitValue(JsValue value) throws JsVisitException;

    /**
     * OBJECT型内部のPAIR名登場の通知を受け取る。
     *
     * <p>PAIRの示すValueの出現する直前に通知が行われる。
     *
     * @param pairName PAIR名
     * @throws JsVisitException ビジターがトラバース中止を判断した際に
     *     投げられる。
     */
    public abstract void visitPairName(String pairName) throws JsVisitException;

    /**
     * 括弧構造終了の通知を受け取る。
     *
     * <p>括弧構造を持つJSON型は、OBJECT型かARRAY型のみ。
     *
     * @param composition OBJECT型かARRAY型のいずれかのValue
     * @throws JsVisitException ビジターがトラバース中止を判断した際に
     *     投げられる。
     */
    public abstract void visitCompositionClose(JsComposition<?> composition)
            throws JsVisitException;

}
