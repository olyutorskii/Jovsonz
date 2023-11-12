/*
 * JSON stream source
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

/**
 * Input source for JSON data stream.
 *
 * <p>Push-back function for the character read ahead and line number counting function.
 *
 * <p>Line numbers begin with 1.
 * LF('\n') shall be used to separate lines. (※CR is ignored)
 *
 * @see java.io.PushbackReader
 * @see java.io.LineNumberReader
 */
class JsonSource implements Closeable {

    /** chars max for push-back. */
    private static final int PUSHBACK_TOKENS = 10;

    private static final char LINEFEED = '\n';  // LF(0x0a)

    private static final String ERRMSG_OVERFLOW =
            "Pushback buffer overflow";
    private static final String ERRMSG_CLOSED =
            "Stream closed";

    static {
        boolean enoughPushBack = "\\uXXXX".length() < PUSHBACK_TOKENS;
        assert enoughPushBack;
    }


    private final Reader reader;

    // stack for push-back
    private final char[] charStack = new char[PUSHBACK_TOKENS];
    private int stackPt = 0;

    private int lineNumber = 1;

    private boolean closed = false;


    /**
     * Constructor.
     *
     * @param reader character reader
     * @throws NullPointerException argument is null
     */
    public JsonSource(Reader reader) {
        super();
        this.reader = Objects.requireNonNull(reader);
        return;
    }

    /**
     * Constructor.
     *
     * <p>Text as input source.
     *
     * @param text text
     * @throws NullPointerException argument is null
     * @see java.io.StringReader
     */
    public JsonSource(CharSequence text) {
        this(new StringReader(text.toString()));
        return;
    }

    /**
     * Determine the whitespace character in the JSON standard.
     *
     * @param ch target char
     * @return true if whitespace
     */
    public static boolean isWhitespace(char ch) {
        switch (ch) {
        case '\u0020':
        case '\t':
        case '\r':
        case '\n':
            return true;
        default:
            break;
        }
        return false;
    }

    /**
     * Determine the whitespace character in the JSON standard.
     *
     * @param ch target
     * @return true if whitespace
     */
    public static boolean isWhitespace(int ch) {
        if ((int) Character.MIN_VALUE > ch) return false;
        if ((int) Character.MAX_VALUE < ch) return false;
        return isWhitespace((char) ch);
    }

    /**
     * Returns the number of remaining characters that can be push-back.
     *
     * @return number of remaining characters that can be push-back
     */
    public int getPushBackSpared() {
        return PUSHBACK_TOKENS - this.stackPt;
    }

    /**
     * Return current line-number.
     *
     * @return line-number starting from 1
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Read 1char.
     *
     * <p>Line numbers will be updated depending on the situation.
     *
     * @return Lower 16 bits are 1char data loaded.
     *     Or negative value if the end of the stream has been reached.
     * @throws IOException I/O error
     *
     * @see java.io.Reader#read()
     */
    public int read() throws IOException {
        if (this.closed) throw new IOException(ERRMSG_CLOSED);

        int chData;
        if (this.stackPt > 0) {
            chData = (int) this.charStack[--this.stackPt];
        } else {
            chData = this.reader.read();
        }

        if (chData == (int) LINEFEED) this.lineNumber++;

        return chData;
    }

    /**
     * Assuming that it is a grammatical violation to end the input here, read one character.
     *
     * @return char readed
     * @throws IOException I/O error
     * @throws JsParseException Grammar violation due to terminated input
     */
    public char readOrDie() throws IOException, JsParseException {
        int chData = read();
        if (chData < 0) {
            throw new JsParseException(JsParseException.ERRMSG_NODATA,
                                       this.lineNumber);
        }
        return (char) chData;
    }

    /**
     * Assuming that it is a grammatical violation to end the input here, matching text sequence.
     *
     * <p>No push-back is performed even if there is no match.
     * A zero-length string is always matched.
     *
     * @param seq target text
     * @return true if matched
     * @throws NullPointerException argument is null
     * @throws IOException I/O error
     * @throws JsParseException Input terminated without waiting for a match.
     */
    public boolean matchOrDie(CharSequence seq)
            throws IOException, JsParseException {
        int length = seq.length();
        for (int pt = 0; pt < length; pt++) {
            if (readOrDie() != seq.charAt(pt)) return false;
        }
        return true;
    }

    /**
     * Push-back 1 char.
     *
     * <p>This is also reflected in the line count.
     *
     * @param ch char
     * @throws IOException Buffer overflow or already closed.
     */
    public void unread(char ch) throws IOException {
        if (this.closed) throw new IOException(ERRMSG_CLOSED);

        if (this.stackPt >= PUSHBACK_TOKENS) {
            throw new IOException(ERRMSG_OVERFLOW);
        }

        this.charStack[this.stackPt++] = ch;

        if (ch == LINEFEED) this.lineNumber--;

        return;
    }

    /**
     * Push-back 1 char.
     *
     * <p>This is also reflected in the line count.
     *
     * @param ch char. Higher 16bits are ignored.
     * @throws IOException Buffer overflow or already closed.
     */
    public void unread(int ch) throws IOException {
        unread((char) ch);
        return;
    }

    /**
     * Skip over whitespaces.
     *
     * @throws IOException I/O error
     */
    public void skipWhiteSpace() throws IOException {
        for (;;) {
            int chData = read();
            if (chData < 0) break;
            if ( !isWhitespace(chData) ) {
                unread(chData);
                break;
            }
        }

        return;
    }

    /**
     * Determine if there is still data to be read.
     *
     * @return true if there is still data to be read
     * @throws IOException I/O error
     */
    public boolean hasMore() throws IOException {
        int chData = read();
        if (chData < 0) return false;
        unread(chData);
        return true;
    }

    /**
     * Close {@link java.io.Reader}.
     *
     * <p>All read and push-back operations after close throw an exception.
     *
     * @throws IOException I/O error
     * @see java.io.Closeable
     */
    @Override
    public void close() throws IOException {
        this.closed = true;
        this.stackPt = 0;
        this.reader.close();
        return;
    }

}
