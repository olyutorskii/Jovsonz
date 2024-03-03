/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class JsParseExceptionTest {

    public JsParseExceptionTest() {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
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

        ex = new JsParseException("abc", 99);
        assertEquals(99, ex.getLineNumber());

        ex = new JsParseException("abc", new Throwable(), 99);
        assertEquals(99, ex.getLineNumber());

        return;
    }

    /**
     * Test of hasValidLineNumber method, of class JsParseException.
     */
    @Test
    public void testHasValidLineNumber() {
        System.out.println("hasValidLineNumber");

        JsParseException ex;

        ex = new JsParseException();
        assertFalse(ex.hasValidLineNumber());

        ex = new JsParseException("abc", 99);
        assertTrue(ex.hasValidLineNumber());

        ex = new JsParseException("abc", new Throwable(), 99);
        assertTrue(ex.hasValidLineNumber());

        ex = new JsParseException("abc", 1);
        assertTrue(ex.hasValidLineNumber());

        ex = new JsParseException("abc", 0);
        assertFalse(ex.hasValidLineNumber());

        ex = new JsParseException("abc", -1);
        assertFalse(ex.hasValidLineNumber());

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

        ex = new JsParseException("abc", new Throwable("xyz"), 0);
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
        assertSame(cause, ex.getCause());

        return;
    }

}
