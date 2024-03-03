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
import java.util.Objects;

/**
 * JSON ARRAY Value.
 *
 * <p>Implements list of childs.
 *
 * <p>example of notation
 *
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


    private final List<JsValue> valueList = new LinkedList<>();
    private boolean changed = false;


    /**
     * Constructor.
     */
    public JsArray() {
        super();
        return;
    }


    /**
     * Try parsing ARRAY Value from JSON source.
     *
     * <p>If a leading character of another possible type is read,
     * null is returned after push-back character into the source.
     *
     * <p>In addition, the parsing process may proceed recursively to child Values.
     *
     * @param source input source
     * @return ARRAY typed Value. null if another possible type.
     * @throws IOException I/O error
     * @throws JsParseException invalid token or EOF
     * @throws NullPointerException argument is null
     */
    static JsArray parseArray(JsonSource source)
            throws IOException, JsParseException {
        char charHead = source.readOrDie();
        if (charHead != '[') {
            source.unread(charHead);
            return null;
        }

        JsArray result = new JsArray();

        for (;;) {
            source.skipWhiteSpace();
            char chData = source.readOrDie();
            if (chData == ']') break;

            if (result.isEmpty()) {
                source.unread(chData);
            } else {
                if (chData != ',') {
                    throw new JsParseException(ERRMSG_NOARRAYCOMMA,
                                               source.getLineNumber() );
                }
            }

            JsValue value = Json.parseValue(source);
            if (value == null) {
                throw new JsParseException(ERRMSG_NOELEM,
                                           source.getLineNumber() );
            }

            result.add(value);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Always return {@link JsTypes#ARRAY}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes() {
        return JsTypes.ARRAY;
    }

    /**
     * Determine if this Value and its descendants have changed.
     *
     * <p>A change is considered to have occurred to this ARRAY Value
     * if a child element is added or deleted,
     * or if a change is recognized in any of the child elements.
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasChanged() {
        if (this.changed) return true;

        for (JsValue value : this.valueList) {
            if ( !(value instanceof JsComposition) ) continue;
            JsComposition<?> composition = (JsComposition) value;
            if (composition.hasChanged()) return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnchanged() {
        this.changed = false;

        for (JsValue value : this.valueList) {
            if ( !(value instanceof JsComposition) ) continue;
            JsComposition<?> composition = (JsComposition) value;
            composition.setUnchanged();
        }

        return;
    }

    /**
     * {@inheritDoc}
     *
     * <p>After notifying this object, the child Values are visited in sequence,
     * and finally the closing bracket is notified.
     *
     * @param visitor {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void traverse(ValueVisitor visitor) throws JsVisitException {
        visitor.visitValue(this);

        for (JsValue value : this.valueList) {
            value.traverse(visitor);
        }

        visitor.visitCompositionClose(this);

        return;
    }

    /**
     * Return number of childs.
     *
     * <p>For ARRAY type, the total number of JsValue directly below.
     *
     * @return {@inheritDoc}
     */
    @Override
    public int size() {
        return this.valueList.size();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return this.valueList.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        if (!this.valueList.isEmpty()) this.changed = true;
        this.valueList.clear();
        return;
    }

    /**
     * Add Value to child.
     *
     * <p>The same JsValue instance can be added multiple times.
     *
     * @param value JSON Value
     * @throws NullPointerException argument is null
     */
    public void add(JsValue value) {
        Objects.requireNonNull(value);
        this.valueList.add(value);
        this.changed = true;
        return;
    }

    /**
     * Returns the Value at the specified position in this childs.
     *
     * @param index index of the child to return. Starting from 0.
     * @return Value JSON Value
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public JsValue get(int index) {
        return this.valueList.get(index);
    }

    /**
     * Remove Value from childs.
     *
     * <p>Unlike {@link java.util.List#remove(Object)},
     * {@link java.lang.Object#equals(Object)} is not used to search
     * for deletion targets.
     *
     * <p>If there are multiple same instances,
     * only the instance closest to the top is deleted.
     * If there are no same instances, nothing is done.
     *
     * @param value JSON Value
     * @return true if the existing Value is deleted
     */
    // TODO Is it really necessary?
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public boolean remove(JsValue value) {
        boolean removed = false;

        Iterator<JsValue> it = this.valueList.iterator();
        while (it.hasNext()) {
            JsValue elem = it.next();
            if (elem == value) {
                it.remove();
                this.changed = true;
                removed = true;
                break;
            }
        }

        return removed;
    }

    /**
     * Remove Value from childs by index.
     *
     * @param index the index of the child Value to be removed starting 0
     * @return Removed child Value
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public JsValue remove(int index) {
        JsValue removed = this.valueList.remove(index);
        this.changed = true;
        return removed;
    }

    /**
     * Returns an iterator over childs Value.
     *
     * <p>Remove operation is not possible with this iterator.
     *
     * @return Iterator
     * @see UnmodIterator
     */
    @Override
    public Iterator<JsValue> iterator() {
        return UnmodIterator.unmodIterator(this.valueList);
    }

    /**
     * Return hash code.
     *
     * <p>It is synthesized each time from the hash values of all descendant Values.
     * It is a high cost process.
     *
     * @return a hash code value for this object
     * @see java.util.List#hashCode()
     */
    @Override
    public int hashCode() {
        return this.valueList.hashCode();
    }

    /**
     * Indicates whether some other ARRAY Value is "equal to" this ARRAY Value.
     *
     * <p>Only if both array sizes match
     * and equals() on all its children
     * is determined to be equivalent is it determined to be equivalent.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     * @see java.util.List#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if ( !(obj instanceof JsArray) ) return false;
        JsArray array = (JsArray) obj;

        return this.valueList.equals(array.valueList);
    }

    /**
     * Returns JSON notation.
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();

        text.append('[');
        boolean hasElem = false;
        for (JsValue value : this.valueList) {
            if (hasElem) text.append(',');
            text.append(value);
            hasElem = true;
        }
        text.append(']');

        return text.toString();
    }

}
