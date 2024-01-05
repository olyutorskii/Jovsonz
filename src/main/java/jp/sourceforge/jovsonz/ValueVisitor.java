/*
 * JSON value visitor
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * JSON visitor interface.
 *
 * <p>Depth-first search is performed
 * and the appearance of various JSON Value is notified.
 */
public interface ValueVisitor {

    /**
     * Receive notification of the appearance of Value.
     *
     * @param value JSON Value
     * @throws JsVisitException Traversing is suspended at the discretion of the visitor.
     * @throws NullPointerException argument is null
     */
    public abstract void visitValue(JsValue value) throws JsVisitException;

    /**
     * Receive notification of the appearance of PAIR in OBJECT.
     *
     * @param pairName name of PAIR
     * @throws JsVisitException Traversing is suspended at the discretion of the visitor.
     * @throws NullPointerException argument is null
     */
    public abstract void visitPairName(String pairName) throws JsVisitException;

    /**
     * Receive notification of the appearance of bracket closing Composition.
     *
     * <p>OBJECT or ARRAY
     *
     * @param composition OBJECT or ARRAY Value
     * @throws JsVisitException Traversing is suspended at the discretion of the visitor.
     * @throws NullPointerException argument is null
     */
    public abstract void visitCompositionClose(JsComposition<?> composition)
            throws JsVisitException;

}
