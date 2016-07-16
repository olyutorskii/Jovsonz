/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class JsVisitExceptionTest {

    public JsVisitExceptionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception{
    }

    @AfterClass
    public static void tearDownClass() throws Exception{
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
