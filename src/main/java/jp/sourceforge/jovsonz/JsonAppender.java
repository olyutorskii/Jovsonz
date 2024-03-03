/*
 * JSON output
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.Flushable;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Objects;
import java.util.Stack;

/**
 * Visitor for JSON text output.
 *
 * <p>If this visitor is specified when traversing JSON Value,
 * output will be in JSON format to the specified text output.
 *
 * <p>{@link java.io.IOException} at output
 * becomes a chained exception for {@link JsVisitException}.
 *
 * <p>Regardless of the success/failure of the previous parse,
 * Behavior is not guaranteed when reusing instances
 */
class JsonAppender implements ValueVisitor {

    /** New line. */
    public static final String NEWLINE = "\n";
    /** Indent unit. */
    public static final String INDENT_UNIT = "\u0020\u0020";
    /** PAIR separator. */
    public static final String PAIR_SEPARATOR = "\u0020:\u0020";
    /** Comma separator. */
    public static final String COMMA = "\u0020,";
    /** White space. */
    public static final String EMPTY = "\u0020";


    private final Appendable appout;

    private final Stack<DumpContext> contextStack =
            new Stack<>();

    private IOException ioException = null;


    /**
     * Constructor.
     *
     * @param appout output
     * @throws NullPointerException argument is null
     */
    public JsonAppender(Appendable appout) {
        super();
        this.appout = Objects.requireNonNull(appout);
        return;
    }


    /**
     * Push current context.
     *
     * <p>Context must be parent Value. (OBJECT or ARRAY)
     *
     * @param composition current context (parent Value)
     */
    protected void pushComposition(JsComposition<?> composition) {
        DumpContext context = new DumpContext(composition);
        this.contextStack.push(context);
        return;
    }

    /**
     * Pop last context.
     *
     * <p>Context must be parent Value. (OBJECT or ARRAY)
     *
     * @return top of context stack
     * @throws EmptyStackException stack is empty
     */
    protected JsComposition<?> popComposition() {
        DumpContext context = this.contextStack.pop();
        JsComposition<?> composition = context.getComposition();
        return composition;
    }

    /**
     * Return depth of context stack.
     *
     * @return depth of nest starts with 0
     */
    protected int nestDepth() {
        return this.contextStack.size();
    }

    /**
     * Determin if the context stack is empty or not.
     *
     * @return true if empty
     */
    protected boolean isNestEmpty() {
        return this.contextStack.isEmpty();
    }

    /**
     * Determin if child Value has already been appended
     * after last stack context.
     *
     * @return true if child Value has already been appended
     */
    protected boolean hasChildDumped() {
        if (isNestEmpty()) return false;
        boolean result = this.contextStack.peek().hasChildDumped();
        return result;
    }

    /**
     * State that child Value has already been appended
     * in the current context.
     */
    protected void setChildDumped() {
        if (isNestEmpty()) return;
        this.contextStack.peek().setChildDumped();
        return;
    }

    /**
     * Determin if the current context is appending ARRAY or not.
     *
     * @return true if the current context is appending ARRAY
     */
    protected boolean isArrayContext() {
        if (isNestEmpty()) return false;

        DumpContext context = this.contextStack.peek();
        JsComposition<?> composition = context.getComposition();
        JsTypes type = composition.getJsTypes();

        boolean result = type == JsTypes.ARRAY;
        return result;
    }

    /**
     * Append 1 char.
     *
     * @param ch char
     * @throws JsVisitException output error
     * @see java.lang.Appendable#append(char)
     */
    protected void append(char ch) throws JsVisitException {
        try {
            this.appout.append(ch);
        } catch (IOException e) {
            this.ioException = e;
            throw new JsVisitException(e);
        }
        return;
    }

    /**
     * Append text.
     *
     * @param seq text
     * @throws JsVisitException output error
     * @see java.lang.Appendable#append(CharSequence)
     */
    protected void append(CharSequence seq) throws JsVisitException {
        try {
            this.appout.append(seq);
        } catch (IOException e) {
            this.ioException = e;
            throw new JsVisitException(e);
        }
        return;
    }

    /**
     * Flush output.
     *
     * @throws JsVisitException output error
     * @see java.io.Flushable
     */
    protected void flush() throws JsVisitException {
        try {
            if (this.appout instanceof Flushable) {
                ((Flushable) this.appout).flush();
            }
        } catch (IOException e) {
            this.ioException = e;
            throw new JsVisitException(e);
        }
        return;
    }

    /**
     * Return {@link IOException} that caused the traverse interruption.
     *
     * @return IOException that caused the traverse interruption.
     *     Return null if no IOException.
     */
    public IOException getIOException() {
        return this.ioException;
    }

    /**
     * Determine if there is an IOException
     * that caused the traverse interruption.
     *
     * @return true if there is an IOException
     *     that caused the traverse interruption
     */
    public boolean hasIOException() {
        boolean result = this.ioException != null;
        return result;
    }

    /**
     * Append name of PAIR.
     *
     * @param name name of PAIR
     * @throws JsVisitException output error
     */
    protected void putPairName(String name) throws JsVisitException {
        try {
            JsString.dumpString(this.appout, name);
        } catch (IOException e) {
            this.ioException = e;
            throw new JsVisitException(e);
        }
        return;
    }

    /**
     * append PAIR sepatartor colon. (:)
     *
     * @throws JsVisitException output error
     */
    protected void putPairSeparator() throws JsVisitException {
        append(PAIR_SEPARATOR);
        return;
    }

    /**
     * append OBJECT separator commma. (,)
     *
     * @throws JsVisitException output error
     */
    protected void putComma() throws JsVisitException {
        append(COMMA);
        return;
    }

    /**
     * append newline.
     *
     * @throws JsVisitException output error
     */
    protected void putNewLine() throws JsVisitException {
        append(NEWLINE);
        return;
    }

    /**
     * append indent spaces.
     *
     * @throws JsVisitException output error
     */
    protected void putIndent() throws JsVisitException {
        int level = nestDepth();
        for (int ct = 1; ct <= level; ct++) {
            append(INDENT_UNIT);
        }
        return;
    }

    /**
     * append before 1st element of OBJECT or ARRAY.
     *
     * @throws JsVisitException output error
     */
    protected void putBefore1stElement() throws JsVisitException {
        putNewLine();
        putIndent();
        return;
    }

    /**
     * append delimiter between elements of OBJECT or ARRAY.
     *
     * @throws JsVisitException output error
     */
    protected void putBetweenElement() throws JsVisitException {
        putComma();
        putNewLine();
        putIndent();
        return;
    }

    /**
     * append after last element of OBJECT or ARRAY.
     *
     * @throws JsVisitException output error
     */
    protected void putAfterLastElement() throws JsVisitException {
        putNewLine();
        putIndent();
        return;
    }

    /**
     * append empty element of OBJECT or ARRAY.
     *
     * @throws JsVisitException output error
     */
    protected void putEmptyElement() throws JsVisitException {
        append(EMPTY);
        return;
    }

    /**
     * append something before JSON.
     *
     * @throws JsVisitException output error
     */
    protected void putBeforeParse() throws JsVisitException {
        //NOTHING
        return;
    }

    /**
     * append something after JSON.
     *
     * @throws JsVisitException output error
     */
    protected void putAfterParse() throws JsVisitException {
        putNewLine();
        return;
    }

    /**
     * {@inheritDoc}
     *
     * <p>append Value.
     *
     * @param value {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     * @throws NullPointerException argument is null
     */
    @Override
    public void visitValue(JsValue value)
            throws JsVisitException {
        Objects.requireNonNull(value);

        if (isNestEmpty()) putBeforeParse();

        if (isArrayContext()) {
            if (hasChildDumped()) putBetweenElement();
            else                  putBefore1stElement();
        }

        String txt;
        JsTypes type = value.getJsTypes();
        switch (type) {
        case OBJECT:
            txt = "{";
            break;
        case ARRAY:
            txt = "[";
            break;
        default:
            txt = value.toString();
            break;
        }
        append(txt);
        setChildDumped();

        if (type.isComposition()) {
            assert value instanceof JsComposition;
            JsComposition<?> composition = (JsComposition) value;
            pushComposition(composition);
        }

        return;
    }

    /**
     * {@inheritDoc}
     *
     * <p>append PAIR name in OBJECT.
     *
     * @param pairName {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     * @throws NullPointerException argument is null
     */
    @Override
    public void visitPairName(String pairName)
            throws JsVisitException {
        Objects.requireNonNull(pairName);

        if (hasChildDumped()) putBetweenElement();
        else                  putBefore1stElement();

        putPairName(pairName);
        putPairSeparator();

        setChildDumped();

        return;
    }

    /**
     * {@inheritDoc}
     *
     * <p>append close bracket.
     *
     * @param closed {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     * @throws NullPointerException argument is null
     */
    @Override
    public void visitCompositionClose(JsComposition<?> closed)
            throws JsVisitException {
        Objects.requireNonNull(closed);

        boolean hasDumped = hasChildDumped();
        JsComposition<?> composition = popComposition();

        if (hasDumped) putAfterLastElement();
        else           putEmptyElement();

        char closeBrace;
        JsTypes type = composition.getJsTypes();
        switch (type) {
        case OBJECT:
            closeBrace = '}';
            break;
        case ARRAY:
            closeBrace = ']';
            break;
        default:
            assert false;
            throw new AssertionError();
        }
        append(closeBrace);

        if (isNestEmpty()) {
            putAfterParse();
            flush();
        }

        return;
    }

    /**
     * Output status of each nested JSON aggregate type context.
     */
    private static class DumpContext {
        private final JsComposition<?> composition;
        private boolean childDumped;

        /**
         * Constructor.
         *
         * <p>Starts with no fact that the child element was output.
         *
         * @param composition OBJECT or ARRAY
         */
        DumpContext(JsComposition<?> composition) {
            this.composition = composition;
            this.childDumped = false;
            return;
        }

        /**
         * Return composition Value.
         *
         * @return OBJECT or ARRAY
         */
        JsComposition<?> getComposition() {
            return this.composition;
        }

        /**
         * Determine if child element output has been performed.
         *
         * @return true if child element output has been performed
         */
        boolean hasChildDumped() {
            return this.childDumped;
        }

        /**
         * Set the fact that the child element output was.
         */
        void setChildDumped() {
            this.childDumped = true;
            return;
        }

    }

}
