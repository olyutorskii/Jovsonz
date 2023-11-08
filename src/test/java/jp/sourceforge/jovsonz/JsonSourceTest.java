/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class JsonSourceTest {

    public JsonSourceTest() {
    }

    /**
     * Test of constructor, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testConstructor() throws Exception{
        System.out.println("constructor");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        assertEquals('a', source.read());
        assertEquals('b', source.read());
        assertEquals('c', source.read());
        assertEquals(-1, source.read());

        source = new JsonSource("abc");
        assertEquals('a', source.read());
        assertEquals('b', source.read());
        assertEquals('c', source.read());
        assertEquals(-1, source.read());

        try{
            source = new JsonSource((Reader)null);
            fail();
            assert source == source;
        }catch(NullPointerException e){
            //GOOD
        }

        try{
            source = new JsonSource((String)null);
            fail();
            assert source == source;
        }catch(NullPointerException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of getLineNumber method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetLineNumber() throws Exception{
        System.out.println("getLineNumber");

        JsonSource source;

        source = new JsonSource("a\nbc\r\nd\n\n");
        assertEquals(1, source.getLineNumber());
        assertEquals('a', source.read());
        assertEquals(1, source.getLineNumber());
        assertEquals('\n', source.read());
        assertEquals(2, source.getLineNumber());
        assertEquals('b', source.read());
        assertEquals(2, source.getLineNumber());
        assertEquals('c', source.read());
        assertEquals(2, source.getLineNumber());
        assertEquals('\r', source.read());
        assertEquals(2, source.getLineNumber());
        assertEquals('\n', source.read());
        assertEquals(3, source.getLineNumber());
        assertEquals('d', source.read());
        assertEquals(3, source.getLineNumber());
        assertEquals('\n', source.read());
        assertEquals(4, source.getLineNumber());
        assertEquals('\n', source.read());
        assertEquals(5, source.getLineNumber());
        assertEquals(-1, source.read());
        assertEquals(5, source.getLineNumber());

        source = new JsonSource("\nX");
        assertEquals(1, source.getLineNumber());
        assertEquals('\n', source.read());
        assertEquals(2, source.getLineNumber());
        assertEquals('X', source.read());
        assertEquals(2, source.getLineNumber());
        assertEquals(-1, source.read());
        assertEquals(2, source.getLineNumber());

        source = new JsonSource("");
        assertEquals(1, source.getLineNumber());
        assertEquals(-1, source.read());

        return;
    }

    /**
     * Test of read method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testRead() throws Exception{
        System.out.println("read");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        assertEquals('a', source.read());
        assertEquals('b', source.read());
        assertEquals('c', source.read());
        assertEquals(-1, source.read());

        source = new JsonSource("abc");
        assertEquals('a', source.read());
        assertEquals('b', source.read());
        assertEquals('c', source.read());
        assertEquals(-1, source.read());

        source = new JsonSource("X\u0000\u3000\uffffZ");
        assertEquals('X', source.read());
        assertEquals('\u0000', source.read());
        assertEquals('\u3000', source.read());
        assertEquals('\uffff', source.read());
        assertEquals('Z', source.read());
        assertEquals(-1, source.read());

        // CJK UNIFIED IDEOGRAPH-2000B 𠀋
        source = new JsonSource("X\ud840\udc0bZ");
        assertEquals('X', source.read());
        assertEquals('\ud840', source.read());
        assertEquals('\udc0b', source.read());
        assertEquals('Z', source.read());
        assertEquals(-1, source.read());

        source = new JsonSource("");
        assertEquals(-1, source.read());

        reader = new TroubleReader("abc", 1);
        source = new JsonSource(reader);
        assertEquals('a', source.read());
        try{
            source.read();
            fail();
        }catch(IOException e){
            // GOOD!
        }catch(Throwable e){
            fail();
        }

        return;
    }

    /**
     * Test of readOrDie method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadOrDie() throws Exception{
        System.out.println("readOrDie");

        JsonSource source;

        source = new JsonSource("ab\nc");
        assertEquals('a', source.readOrDie());
        assertEquals('b', source.readOrDie());
        assertEquals('\n', source.readOrDie());
        assertEquals('c', source.readOrDie());
        try{
            source.readOrDie();
            fail();
        }catch(JsParseException e){
            assertEquals(2, e.getLineNumber());
            assertEquals("We need but no more JSON data [line:2]",
                         e.getMessage());
        }catch(IOException e){
            fail();
        }

        Reader reader = new TroubleReader("abc", 1);
        source = new JsonSource(reader);
        assertEquals('a', source.readOrDie());
        try{
            source.readOrDie();
            fail();
        }catch(IOException e){
            // GOOD!
        }catch(JsParseException e){
            fail();
        }

        return;
    }

    /**
     * Test of matchOrDie method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testMatchOrDie() throws Exception{
        System.out.println("matchOrDie");

        JsonSource source;

        source = new JsonSource("ABC");
        assertTrue(source.matchOrDie("ABC"));

        source = new JsonSource("ABC");
        assertFalse(source.matchOrDie("XYZ"));

        source = new JsonSource("ABC");
        assertTrue(source.matchOrDie("A"));

        source = new JsonSource("ABC");
        assertTrue(source.matchOrDie(""));

        source = new JsonSource("ABC");
        try{
            source.matchOrDie("ABCD");
            fail();
        }catch(JsParseException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of unread method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testUnread() throws Exception{
        System.out.println("unread");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        assertEquals('a', source.read());
        assertEquals('b', source.read());
        source.unread('X');
        source.unread('Y');
        assertEquals('Y', source.read());
        assertEquals('X', source.read());
        assertEquals('c', source.read());
        assertEquals(-1, source.read());

        reader = new StringReader("a\nb\nc");
        source = new JsonSource(reader);
        assertEquals('a', source.read());
        assertEquals('\n', source.read());
        assertEquals('b', source.read());
        assertEquals('\n', source.read());
        assertEquals(3, source.getLineNumber());
        source.unread('\n');
        assertEquals(2, source.getLineNumber());
        assertEquals('\n', source.read());
        assertEquals(3, source.getLineNumber());

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        assertEquals('a', source.read());
        assertEquals('b', source.read());
        source.unread(-1);
        assertEquals((char)-1, source.read());
        assertEquals('c', source.read());
        assertEquals(-1, source.read());

        reader = new StringReader("X");
        source = new JsonSource(reader);
        source.unread('Y');
        assertEquals('Y', source.read());
        assertEquals('X', source.read());
        assertEquals(-1, source.read());

        reader = new StringReader("X");
        source = new JsonSource(reader);
        int spared = source.getPushBackSpared();
        for(int ct = 1; ct <= spared; ct++){
            source.unread('Y');
        }
        for(int ct = 1; ct <= spared; ct++){
            assertEquals('Y', source.read());
        }
        assertEquals('X', source.read());
        assertEquals(-1, source.read());

        reader = new StringReader("X");
        source = new JsonSource(reader);
        while(source.getPushBackSpared() > 0){
            source.unread('Y');
        }
        try{
            source.unread('Y');
            fail();
        }catch(IOException e){
            assertEquals("Pushback buffer overflow", e.getMessage());
        }catch(Throwable e){
            fail();
        }

        return;
    }

    /**
     * Test of unread method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testUnread_int() throws Exception{
        System.out.println("unread");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        assertEquals('a', source.readOrDie());
        assertEquals('b', source.readOrDie());
        source.unread((int) 'X');
        assertEquals('X', source.readOrDie());
        assertEquals('c', source.readOrDie());
        assertEquals(-1, source.read());

        reader = new StringReader("");
        source = new JsonSource(reader);
        assertEquals(-1, source.read());
        source.unread((int) 'X');
        assertEquals('X', source.readOrDie());

        reader = new StringReader("ab");
        source = new JsonSource(reader);
        assertEquals('a', source.readOrDie());
        source.unread((int) 'X');
        source.unread((int) 'Y');
        assertEquals('Y', source.readOrDie());
        assertEquals('X', source.readOrDie());
        assertEquals('b', source.readOrDie());
        assertEquals(-1, source.read());

        reader = new StringReader("");
        source = new JsonSource(reader);
        source.unread((int) '\0');
        assertEquals('\0', source.readOrDie());
        source.unread(0xffff);
        assertEquals('\uffff', source.readOrDie());
        source.unread(0x1ffff);
        assertEquals('\uffff', source.readOrDie());
        source.unread(0x1ffff);
        assertEquals(0xffff, source.read());
        source.unread(0xffffffff);
        assertEquals(0xffff, source.read());
        source.unread(-1);
        assertEquals(0xffff, source.read());

        source.unread(-1);
        assertEquals(0xffff, source.readOrDie());

        return;
    }

    /**
     * Test of unread method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testUnread_char() throws Exception{
        System.out.println("unread");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        assertEquals('a', source.readOrDie());
        assertEquals('b', source.readOrDie());
        source.unread('X');
        assertEquals('X', source.readOrDie());
        assertEquals('c', source.readOrDie());
        assertEquals(-1, source.read());

        reader = new StringReader("");
        source = new JsonSource(reader);
        assertEquals(-1, source.read());
        source.unread('X');
        assertEquals('X', source.readOrDie());

        reader = new StringReader("ab");
        source = new JsonSource(reader);
        assertEquals('a', source.readOrDie());
        source.unread('X');
        source.unread('Y');
        assertEquals('Y', source.readOrDie());
        assertEquals('X', source.readOrDie());
        assertEquals('b', source.readOrDie());
        assertEquals(-1, source.read());

        reader = new StringReader("");
        source = new JsonSource(reader);
        source.unread('\0');
        assertEquals('\0', source.readOrDie());
        source.unread((char) 0xffff);
        assertEquals('\uffff', source.readOrDie());

        return;
    }

    /**
     * Test of close method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testClose() throws Exception{
        System.out.println("close");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        source.close();

        try{
            source.read();
            fail();
        }catch(IOException e){
            assertEquals("Stream closed", e.getMessage());
        }

        try{
            source.unread('X');
            fail();
        }catch(IOException e){
            assertEquals("Stream closed", e.getMessage());
        }

        return;
    }

    /**
     * Test of getPushBackSpared method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetPushBackSpared() throws Exception{
        System.out.println("getPushBackSpared");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);

        assertTrue(source.getPushBackSpared() > 0);

        while(source.getPushBackSpared() > 0){
            source.unread('X');
        }

        try{
            source.unread('X');
            fail();
        }catch(IOException e){
            //NOTHING
        }

        assertEquals(0, source.getPushBackSpared());
        source.close();
        assertTrue(source.getPushBackSpared() > 0);

        return;
    }

    /**
     * Test of isWhitespace method, of class JsonSource.
     */
    @Test
    public void testIsWhitespace_char(){
        System.out.println("isWhitespace");

        assertTrue(JsonSource.isWhitespace('\t'));
        assertTrue(JsonSource.isWhitespace('\r'));
        assertTrue(JsonSource.isWhitespace('\n'));
        assertTrue(JsonSource.isWhitespace('\u0020'));

        assertFalse(JsonSource.isWhitespace('A'));
        assertFalse(JsonSource.isWhitespace('\u3000'));
        assertFalse(JsonSource.isWhitespace('\0'));
        assertFalse(JsonSource.isWhitespace((char) -1));

        return;
    }

    /**
     * Test of isWhitespace method, of class JsonSource.
     */
    @Test
    public void testIsWhitespace_int(){
        System.out.println("isWhitespace");

        assertTrue(JsonSource.isWhitespace((int) '\t'));
        assertTrue(JsonSource.isWhitespace((int) '\r'));
        assertTrue(JsonSource.isWhitespace((int) '\n'));
        assertTrue(JsonSource.isWhitespace((int) '\u0020'));
        assertTrue(JsonSource.isWhitespace(0x0020));

        assertFalse(JsonSource.isWhitespace((int) 'A'));
        assertFalse(JsonSource.isWhitespace((int) '\u3000'));
        assertFalse(JsonSource.isWhitespace((int) '\0'));
        assertFalse(JsonSource.isWhitespace(-1));

        assertFalse(JsonSource.isWhitespace(0xffff));
        assertFalse(JsonSource.isWhitespace(0x1ffff));
        assertFalse(JsonSource.isWhitespace(0xffff0020));

        return;
    }

    /**
     * Test of skipWhiteSpace method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testSkipWhiteSpace() throws Exception{
        System.out.println("skipWhiteSpace");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        source.skipWhiteSpace();
        assertEquals('a', source.read());

        reader = new StringReader(" abc");
        source = new JsonSource(reader);
        source.skipWhiteSpace();
        assertEquals('a', source.read());

        reader = new StringReader("\t\r\n\u0020abc");
        source = new JsonSource(reader);
        source.skipWhiteSpace();
        assertEquals('a', source.read());

        reader = new StringReader(" ");
        source = new JsonSource(reader);
        source.skipWhiteSpace();
        assertEquals(-1, source.read());

        reader = new StringReader("");
        source = new JsonSource(reader);
        source.skipWhiteSpace();
        assertEquals(-1, source.read());

        return;
    }

    /**
     * Test of hasMore method, of class JsonSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testHasMore() throws Exception{
        System.out.println("hasMore");

        JsonSource source;
        Reader reader;

        reader = new StringReader("abc");
        source = new JsonSource(reader);
        assertTrue(source.hasMore());
        assertEquals('a', source.read());
        assertTrue(source.hasMore());
        assertEquals('b', source.read());
        assertTrue(source.hasMore());
        assertEquals('c', source.read());
        assertFalse(source.hasMore());
        source.unread('X');
        assertTrue(source.hasMore());
        assertEquals('X', source.read());
        assertFalse(source.hasMore());

        reader = new StringReader("");
        source = new JsonSource(reader);
        assertFalse(source.hasMore());

        return;
    }

}
