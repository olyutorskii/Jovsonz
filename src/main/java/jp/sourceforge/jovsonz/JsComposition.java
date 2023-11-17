/*
 * composition type value
 *
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jovsonz;

/**
 * JSON value which includes childs interface.
 *
 * <p>JSON root structure must be implement it.
 *
 * <p>JSON types which includes childs are OBJECT type or ARRAY type only.
 *
 * @param <E> type of childs
 */
public interface JsComposition<E> extends JsValue, Iterable<E> {

    /**
     * Return number of childs.
     *
     * <ul>
     * <li>For OBJECT type, the total number of JsPair directly below.
     * <li>For ARRAY type, the total number of JsValue directly below.
     * </ul>
     *
     * @return number of childs
     */
    public abstract int size();

    /**
     * Determine whether or not a child is present.
     *
     * @return true if no childs
     */
    public abstract boolean isEmpty();

    /**
     * Clear childs.
     */
    public abstract void clear();

    /**
     * Determine if this Value and its descendants have changed.
     *
     * <p>Must be false(no change) immediately after generation.
     *
     * <p>The purpose is to determine whether the loaded data needs to be resaved or not.
     *
     * <p>Only OBJECT or ARRAY types can be changed.
     *
     * @return true if has changed
     */
    public abstract boolean hasChanged();

    /**
     * Assume no changes were made to this Value and its descendants.
     */
    public abstract void setUnchanged();

}
