/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class JsParseExceptionTest {

    public JsParseExceptionTest() {
    }

    /**
     * Test of getLineNumber method, of class JsParseException.
     */
    @Test
    public void testGetLineNumber(){
        System.out.println("getLineNumber");

        JsParseException ex;

        ex = new JsParseException();
        assertTrue(1 > ex.getLineNumber());
        assertFalse(ex.hasValidLineNumber());

        ex = new JsParseException("abc", 99);
        assertEquals(99, ex.getLineNumber());
        assertTrue(ex.hasValidLineNumber());

        ex = new JsParseException("abc", new Throwable(), 99);
        assertEquals(99, ex.getLineNumber());
        assertTrue(ex.hasValidLineNumber());

        return;
    }

    /**
     * Test of getMessage method, of class JsParseException.
     */
    @Test
    public void testGetMessage(){
        System.out.println("getMessage");

        JsParseException ex;

        ex = new JsParseException();
        assertNull(ex.getMessage());

        ex = new JsParseException("abc", 99);
        assertEquals("abc [line:99]", ex.getMessage());

        ex = new JsParseException("abc", new Throwable(), 99);
        assertEquals("abc [line:99]", ex.getMessage());

        ex = new JsParseException(null, new Throwable(), 99);
        assertEquals("[line:99]", ex.getMessage());

        ex = new JsParseException(null, new Throwable(), 0);
        assertNull(ex.getMessage());

        ex = new JsParseException("abc", new Throwable(), 0);
        assertEquals("abc", ex.getMessage());

        return;
    }

    /**
     * Test of getCause method, of class JsParseException.
     */
    @Test
    public void testGetCause(){
        System.out.println("getMessage");

        JsParseException ex;

        ex = new JsParseException();
        assertNull(ex.getCause());

        ex = new JsParseException("abc", 99);
        assertNull(ex.getCause());

        Throwable cause = new Throwable();
        ex = new JsParseException("abc", cause, 99);
        assertTrue(cause == ex.getCause());

        return;
    }

}
