/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.Reader;
import java.io.StringReader;
import java.util.EmptyStackException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class JsonAppenderTest {

    public JsonAppenderTest() {
    }

    /**
     * Test of Constructor, of class JsonAppender.
     */
    @Test
    public void testConstructor(){
        System.out.println("constructor");

        JsonAppender appender;

        try{
            appender = new JsonAppender(null);
            fail();
            assert appender == appender;
        }catch(NullPointerException e){
            //GOOD
        }

        appender = new JsonAppender(new StringBuilder());
        assertFalse(appender.hasIOException());
        assertNull(appender.getIOException());

        return;
    }

    /**
     * Test of pushComposition method, of class JsonAppender.
     */
    @Test
    public void testPushPopComposition(){
        System.out.println("pushComposition");

        JsonAppender appender = new JsonAppender(new StringBuilder());

        try{
            appender.popComposition();
            fail();
        }catch(EmptyStackException e){
            //GOOD
        }
        assertEquals(0, appender.nestDepth());
        assertTrue(appender.isNestEmpty());

        JsObject obj1 = new JsObject();
        JsObject obj2 = new JsObject();
        JsArray array1 = new JsArray();
        JsArray array2 = new JsArray();

        appender.pushComposition(obj1);
        appender.pushComposition(array1);
        appender.pushComposition(obj2);
        appender.pushComposition(array2);

        assertEquals(4, appender.nestDepth());
        assertFalse(appender.isNestEmpty());

        assertEquals(array2, appender.popComposition());
        assertEquals(obj2, appender.popComposition());
        assertEquals(array1, appender.popComposition());
        assertEquals(obj1, appender.popComposition());

        try{
            appender.popComposition();
            fail();
        }catch(EmptyStackException e){
            //GOOD
        }
        assertEquals(0, appender.nestDepth());
        assertTrue(appender.isNestEmpty());

        return;
    }

    /**
     * Test of hasChildDumped setChildDumped method, of class JsonAppender.
     */
    @Test
    public void testHasSetChildDumped(){
        System.out.println("hasChildDumped");

        JsonAppender appender;

        appender = new JsonAppender(new StringBuilder());
        assertFalse(appender.hasChildDumped());
        appender.setChildDumped();
        assertFalse(appender.hasChildDumped());

        appender.pushComposition(new JsObject());
        assertFalse(appender.hasChildDumped());
        appender.setChildDumped();
        assertTrue(appender.hasChildDumped());

        appender.pushComposition(new JsObject());
        assertFalse(appender.hasChildDumped());

        appender.popComposition();
        assertTrue(appender.hasChildDumped());

        return;
    }

    /**
     * Test of isArrayContext method, of class JsonAppender.
     */
    @Test
    public void testIsArrayContext(){
        System.out.println("isArrayContext");
        return;
    }

    /**
     * Test of getIOException method, of class JsonAppender.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetIOException() throws Exception{
        System.out.println("getIOException");

        Reader reader = new StringReader("[1,2,3,4,5]");
        JsComposition<?> root = Json.parseJson(reader);

        Appendable app = new TroubleAppender(3);
        JsonAppender appender = new JsonAppender(app);

        assertFalse(appender.hasIOException());
        assertNull(appender.getIOException());

        try{
            appender.append("abcde");
            fail();
        }catch(JsVisitException e){
            assertTrue(appender.hasIOException());
            assertEquals(e.getCause(), appender.getIOException());
        }

        return;
    }

    /**
     * Test of putPairName method, of class JsonAppender.
     * @throws java.lang.Exception
     */
    @Test
    public void testPutPairName() throws Exception{
        System.out.println("putPairName");

        Appendable app;
        JsonAppender appender;

        app = new TroubleAppender(1);
        appender = new JsonAppender(app);

        try{
            appender.putPairName("A");
        }catch(JsVisitException e){
            assertTrue(appender.hasIOException());
            assertEquals(e.getCause(), appender.getIOException());
        }

        return;
    }

    /**
     * Test of append method, of class JsonAppender.
     * @throws java.lang.Exception
     */
    @Test
    public void testAppend() throws Exception{
        System.out.println("append");

        Appendable app;
        JsonAppender appender;

        app = new TroubleAppender(3);
        appender = new JsonAppender(app);
        appender.append('0');
        appender.append('1');
        appender.append('2');
        try{
            appender.append('3');
        }catch(JsVisitException e){
            assertTrue(appender.hasIOException());
            assertEquals(e.getCause(), appender.getIOException());
        }

        app = new TroubleAppender(3);
        appender = new JsonAppender(app);
        try{
            appender.append("1234");
        }catch(JsVisitException e){
            assertTrue(appender.hasIOException());
            assertEquals(e.getCause(), appender.getIOException());
        }

        return;
    }

    /**
     * Test of flush method, of class JsonAppender.
     * @throws java.lang.Exception
     */
    @Test
    public void testFlush() throws Exception{
        System.out.println("flush");

        Appendable app = new TroubleAppender(3);
        JsonAppender appender = new JsonAppender(app);

        try{
            appender.flush();
            fail();
        }catch(JsVisitException e){
            assertTrue(appender.hasIOException());
            assertEquals(e.getCause(), appender.getIOException());
        }

        return;
    }

    /**
     * Test of hasIOException method, of class JsonAppender.
     */
    @Test
    public void testHasIOException(){
        System.out.println("hasIOException");
        return;
    }

}
