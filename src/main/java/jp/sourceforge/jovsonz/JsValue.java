/*
 * JSON value common interface
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * JSON Value common interface.
 */
public interface JsValue {

    /**
     * Return enumeration of JSON types.
     *
     * @return enumeration of JSON type
     */
    public abstract JsTypes getJsTypes();

    /**
     * Depth-first traversing is performed
     * to notify visitor of the appearance of various structures.
     *
     * @param visitor visitor
     * @throws JsVisitException Traverse aborted by visitor implementation
     * @throws NullPointerException argument is null
     */
    public abstract void traverse(ValueVisitor visitor) throws JsVisitException;

}
