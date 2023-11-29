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
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * JSON OBJECT Value.
 *
 * <p>Reflects the set of pairs of name and child element({@link JsPair}).
 * The order of pair names is undefined.
 *
 * <p>example of notation
 *
 * <pre>
 * {
 *     "Name" : "Joe" ,
 *     "Age" : 19
 * }
 * </pre>
 */
@SuppressWarnings("PMD.UseConcurrentHashMap")
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
     * Constructor.
     */
    public JsObject() {
        super();
        return;
    }


    /**
     * Try parsing OBJECT Value from JSON source.
     *
     * <p>If a leading character of another possible type is read,
     * null is returned after push-back character into the source.
     *
     * <p>In addition, the parsing process may proceed recursively to PAIRs.
     *
     * @param source input source
     * @return OBJECT typed Value. null if another possible type.
     * @throws IOException I/O error
     * @throws JsParseException invalid token or EOF
     * @throws NullPointerException argument is null
     */
    static JsObject parseObject(JsonSource source)
            throws IOException, JsParseException {
        char charHead = source.readOrDie();
        if (charHead != '{') {
            source.unread(charHead);
            return null;
        }

        JsObject result = new JsObject();

        for (;;) {
            source.skipWhiteSpace();
            char chData = source.readOrDie();
            if (chData == '}') break;

            if (result.isEmpty()) {
                source.unread(chData);
            } else {
                if (chData != ',') {
                    throw new JsParseException(ERRMSG_NOOBJECTCOMMA,
                                               source.getLineNumber() );
                }
                source.skipWhiteSpace();
            }

            JsString name = JsString.parseString(source);
            if (name == null) {
                throw new JsParseException(ERRMSG_NOHASHNAME,
                                           source.getLineNumber() );
            }

            source.skipWhiteSpace();
            chData = source.readOrDie();
            if (chData != ':') {
                throw new JsParseException(ERRMSG_NOHASHSEP,
                                           source.getLineNumber() );
            }

            JsValue value = Json.parseValue(source);
            if (value == null) {
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
     * <p>Always return {@link JsTypes#OBJECT}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsTypes getJsTypes() {
        return JsTypes.OBJECT;
    }

    /**
     * Determine if this Value and its descendants have changed.
     *
     * <p>A change is considered to have occurred to this OBJECT Value
     * if a PAIR is added or deleted,
     * or if a change is recognized in any of the PAIR.
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasChanged() {
        if (this.changed) return true;

        for (JsPair pair : this) {
            JsValue value = pair.getValue();
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

        for (JsPair pair : this) {
            JsValue value = pair.getValue();
            if ( !(value instanceof JsComposition) ) continue;
            JsComposition<?> composition = (JsComposition) value;
            composition.setUnchanged();
        }

        return;
    }

    /**
     * {@inheritDoc}
     *
     * <p>After notifying this object, the PAIRs name and Value are visited in sequence,
     * and finally the closing bracket is notified.
     *
     * <p>The order of PAIRs visits has not yet been determined.
     *
     * @param visitor {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void traverse(ValueVisitor visitor) throws JsVisitException {
        visitor.visitValue(this);

        for (JsPair pair : this) {
            String name   = pair.getName();
            JsValue value = pair.getValue();
            visitor.visitPairName(name);
            value.traverse(visitor);
        }

        visitor.visitCompositionClose(this);

        return;
    }

    /**
     * Return number of PAIRs.
     *
     * @return number of PAIRs
     */
    @Override
    public int size() {
        return this.pairMap.size();
    }

    /**
     * Determine whether or not a PAIR is present.
     *
     * @return true if no PAIRs
     */
    @Override
    public boolean isEmpty() {
        return this.pairMap.isEmpty();
    }

    /**
     * Clear PAIRs.
     */
    @Override
    public void clear() {
        if (!this.pairMap.isEmpty()) this.changed = true;
        this.pairMap.clear();
        return;
    }

    /**
     * Associates the specified Value with the specified name in this OBJECT.
     *
     * @param name name of PAIR
     * @param value Value
     * @return old Value, or null if there was same Value for the name.
     * @throws NullPointerException argument is null
     */
    public JsValue putValue(String name, JsValue value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        JsValue oldValue = null;
        JsPair oldPair = this.pairMap.get(name);
        if (oldPair != null) {
            oldValue = oldPair.getValue();
            if (value.equals(oldValue)) return null;
        }

        JsPair newPair = new JsPair(name, value);
        this.pairMap.put(name, newPair);

        this.changed = true;
        return oldValue;
    }

    /**
     * Returns the Value to which the specified name is mapped,
     * or null if this OBJECT contains no mapping for the name.
     *
     * @param name name of PAIR
     * @return associated Value, or null if this OBJECT contains no mapping for the name
     */
    public JsValue getValue(String name) {
        JsPair pair = this.pairMap.get(name);
        if (pair == null) return null;
        JsValue value = pair.getValue();
        return value;
    }

    /**
     * Associates the specified PAIR with the name in this OBJECT.
     *
     * <p>If the OBJECT previously contained a mapping for
     * the name, the old PAIR is replaced by the specified PAIR.
     *
     * @param pair PAIR
     */
    public void putPair(JsPair pair) {
        this.pairMap.put(pair.getName(), pair);
        return;
    }

    /**
     * Returns the PAIR to which the specified name is mapped,
     * or null if this OBJECT contains no mapping for the name.
     *
     * @param name name
     * @return PAIR, or null if there was no mapping for name.
     */
    public JsPair getPair(String name) {
        JsValue value = getValue(name);
        if (value == null) return null;

        return new JsPair(name, value);
    }

    /**
     * Removes the mapping for a name from this PAIRs if it is present.
     *
     * @param name name
     * @return removed PAIR.
     *     or null if there was no mapping for name.
     */
    public JsPair remove(String name) {
        JsPair oldPair = this.pairMap.remove(name);
        if (oldPair != null) this.changed = true;

        return oldPair;
    }

    /**
     * Returns a Set view of the PAIR names contained in this map.
     *
     * @return set of names
     */
    public Set<String> nameSet() {
        return this.pairMap.keySet();
    }

    /**
     * Return list of PAIRs.
     *
     * <p>PAIR appearance order is undefined.
     *
     * <p>Overwriting this list has no effect.
     *
     * @return list of PAIRs
     */
    public List<JsPair> getPairList() {
        List<JsPair> result = new ArrayList<>(this.pairMap.size());

        for (JsPair pair : this) {
            result.add(pair);
        }

        return result;
    }

    /**
     * Returns an iterator over PAIRs.
     *
     * <p>Remove operation is not possible with this iterator.
     *
     * <p>PAIR appearance order is undefined.
     *
     * @return iterator
     */
    @Override
    public Iterator<JsPair> iterator() {
        return UnmodIterator.unmodIterator(this.pairCollection);
    }

    /**
     * Return hash code.
     *
     * <p>It is synthesized each time from the hash values of all descendant names and Values.
     * It is a high cost process.
     *
     * @return a hash code value for this object
     * @see java.util.Map#hashCode()
     */
    @Override
    public int hashCode() {
        return this.pairMap.hashCode();
    }

    /**
     * Indicates whether some other OBJECT Value is "equal to" this OBJECT Value.
     *
     * <p>Equivalence is determined
     * only when the number of PAIRs on both sides matches
     * and all PAIR names and their associated Values match.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     * @see java.util.Map#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if ( !(obj instanceof JsObject) ) return false;
        JsObject composit = (JsObject) obj;

        return this.pairMap.equals(composit.pairMap);
    }

    /**
     * Returns JSON notation.
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();

        text.append('{');

        boolean hasElem = false;
        for (JsPair pair : this) {
            if (hasElem) text.append(',');
            text.append(pair);
            hasElem = true;
        }

        text.append('}');

        return text.toString();
    }

}
