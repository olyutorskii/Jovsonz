/*
 * JSON utilities
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

/**
 * JSON utilities.
 */
public final class Json {

    /** MIME type of JSON. */
    public static final String MIME_TYPE = "application/json";


    /**
     * Hidden constructor.
     */
    private Json() {
        assert false;
        throw new AssertionError();
    }

    /**
     * Dump JSON text sequence from JSON root value.
     *
     * @param appout target output
     * @param topValue OBJECT or ARRAY root Value
     * @throws JsVisitException Traversing is suspended at the discretion of the visitor
     * @throws IOException I/O error
     * @throws NullPointerException argument is null
     */
    public static void dumpJson(Appendable appout, JsComposition<?> topValue)
            throws JsVisitException, IOException {
        Objects.requireNonNull(appout);
        Objects.requireNonNull(topValue);

        JsonAppender appender = new JsonAppender(appout);

        try {
            topValue.traverse(appender);
        } catch (JsVisitException e) {
            throw appender.getIOException();
        }

        return;
    }

    /**
     * Read any JSON Value from input source.
     *
     * @param source input source
     * @return any JSON Value.
     *     null when the end of the source is reached with zero or more consecutive white spaces.
     * @throws IOException I/O error
     * @throws JsParseException invalid token
     * @throws NullPointerException argument is null
     */
    static JsValue parseValue(JsonSource source)
            throws IOException, JsParseException {
        source.skipWhiteSpace();
        if ( !source.hasMore() ) return null;

        JsValue result;
        result = JsObject.parseObject(source);
        if (result == null) {
            result = JsArray.parseArray(source);
        }
        if (result == null) {
            result = JsString.parseString(source);
        }
        if (result == null) {
            result = JsNull.parseNull(source);
        }
        if (result == null) {
            result = JsBoolean.parseBoolean(source);
        }
        if (result == null) {
            result = JsNumber.parseNumber(source);
        }

        if (result == null) {
            throw new JsParseException(JsParseException.ERRMSG_INVALIDTOKEN,
                                       source.getLineNumber() );
        }

        return result;
    }

    /**
     * Read JSON root Value from input source.
     *
     * @param source input source
     * @return JSON root Value. (OBJECT or ARRAY)
     *     null when the end of the source is reached with zero or more consecutive white spaces.
     * @throws IOException I/O error
     * @throws JsParseException invalid token
     */
    private static JsComposition<?> parseJson(JsonSource source)
            throws IOException, JsParseException {
        JsValue topValue = parseValue(source);
        if (topValue == null) return null;

        if ( !(topValue instanceof JsComposition) ) {
            throw new JsParseException(JsParseException.ERRMSG_INVALIDROOT,
                                       source.getLineNumber() );
        }
        JsComposition<?> result = (JsComposition) topValue;

        return result;
    }

    /**
     * Read JSON root Value from {@link java.io.Reader}.
     *
     * @param source input Reader
     * @return OBJECT or ARRAY root Value.
     *     null when the end of the source is reached with zero or more consecutive white spaces.
     * @throws IOException I/O error
     * @throws JsParseException invalid token
     * @throws NullPointerException argument is null
     */
    public static JsComposition<?> parseJson(Reader source)
            throws IOException, JsParseException {
        JsonSource jsonSource = new JsonSource(source);
        return parseJson(jsonSource);
    }

}
