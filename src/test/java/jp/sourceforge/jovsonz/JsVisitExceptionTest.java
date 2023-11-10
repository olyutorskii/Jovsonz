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
public class JsVisitExceptionTest {

    public JsVisitExceptionTest() {
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

    @Test
    public void testConstructor(){
        System.out.println("constructor");

        JsVisitException ex;

        ex = new JsVisitException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());

        ex = new JsVisitException((String)null);
        assertNull(ex.getMessage());

        ex = new JsVisitException("");
        assertEquals("", ex.getMessage());

        ex = new JsVisitException("abc");
        assertEquals("abc", ex.getMessage());

        Throwable cause = new Throwable("cause");

        ex = new JsVisitException((Throwable)null);
        assertNull(ex.getMessage());

        ex = new JsVisitException(cause);
        assertEquals(cause, ex.getCause());

        ex = new JsVisitException(null, null);
        assertNull(ex.getMessage());
        assertNull(ex.getCause());

        ex = new JsVisitException("abc", null);
        assertEquals("abc", ex.getMessage());
        assertNull(ex.getCause());

        ex = new JsVisitException(null, cause);
        assertNull(ex.getMessage());
        assertEquals(cause, ex.getCause());

        ex = new JsVisitException("abc", cause);
        assertEquals("abc", ex.getMessage());
        assertEquals(cause, ex.getCause());

        return;
    }

}
