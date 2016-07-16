/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class JsStringTest {

    public JsStringTest() {
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

    /**
     * Test of Constructor, of class JsString.
     */
    @Test
    public void testConstructor() throws Exception{
        System.out.println("constructor");

        JsString string;

        string = new JsString();
        assertEquals("", string.toRawString());

        string = new JsString("");
        assertEquals("", string.toRawString());

        string = new JsString("x");
        assertEquals("x", string.toRawString());

        string = new JsString("\u001f");
        assertEquals("\u001f", string.toRawString());
        assertEquals("\"\\u001f\"", string.toString());

        try{
            string = new JsString(null);
            fail();
        }catch(NullPointerException e){
            //NOTHING
        }

        return;
    }

    /**
     * Test of parseHexChar method, of class JsString.
     */
    @Test
    public void testParseHexChar() throws Exception{
        System.out.println("parseHexChar");

        JsonSource source;
        char ch;

        source = new JsonSource("0000");
        ch = JsString.parseHexChar(source);
        assertEquals('\u0000', ch);

        source = new JsonSource("ffff");
        ch = JsString.parseHexChar(source);
        assertEquals('\uffff', ch);

        source = new JsonSource("FFFF");
        ch = JsString.parseHexChar(source);
        assertEquals('\uffff', ch);

        source = new JsonSource("dead");
        ch = JsString.parseHexChar(source);
        assertEquals('\udead', ch);

        source = new JsonSource("abcde");
        ch = JsString.parseHexChar(source);
        assertEquals('\uabcd', ch);

        try{
            source = new JsonSource("000,");
            ch = JsString.parseHexChar(source);
            fail();
        }catch(JsParseException e){
            // NOTHING
        }

        return;
    }

    /**
     * Test of parseString method, of class JsString.
     */
    @Test
    public void testParseString() throws Exception{
        System.out.println("parseString");

        JsonSource source;
        JsString string;

        source = new JsonSource("\"abc\"");
        string = JsString.parseString(source);
        assertEquals("abc", string.toRawString());

        source = new JsonSource("\"あいう\"");
        string = JsString.parseString(source);
        assertEquals("あいう", string.toRawString());

        source = new JsonSource("\"\\\"\\\\\\/\"");
        string = JsString.parseString(source);
        assertEquals("\"\\/", string.toRawString());

        source = new JsonSource("\"\\b\\f\\n\\r\\t\"");
        string = JsString.parseString(source);
        assertEquals("\b\f\n\r\t", string.toRawString());

        source = new JsonSource("\"\\uabcd\\uCDEF\"");
        string = JsString.parseString(source);
        assertEquals("\uabcd\ucdef", string.toRawString());

        source = new JsonSource("abc\"");
        string = JsString.parseString(source);
        assertNull(string);

        try{
            source = new JsonSource("\"abc");
            string = JsString.parseString(source);
            fail();
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("\"\\#\"");
            string = JsString.parseString(source);
            fail();
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("\"\\u#999\"");
            string = JsString.parseString(source);
            fail();
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("\"\\u9#99\"");
            string = JsString.parseString(source);
            fail();
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("\"\\u99#9\"");
            string = JsString.parseString(source);
            fail();
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("\"\\u999#\"");
            string = JsString.parseString(source);
            fail();
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("\"abc\nxyz\"");
            string = JsString.parseString(source);
            fail();
        }catch(JsParseException e){
            // NOTHING
        }

        return;
    }

    /**
     * Test of dumpString method, of class JsString.
     */
    @Test
    public void testDumpString() throws Exception{
        System.out.println("writeText");

        Appendable appout;
        JsString string;

        appout = new StringBuilder();
        string = new JsString();
        JsString.dumpString(appout, string);
        assertEquals("\"\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("abc");
        JsString.dumpString(appout, string);
        assertEquals("\"abc\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("\"");
        JsString.dumpString(appout, string);
        assertEquals("\"\\\"\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("\\");
        JsString.dumpString(appout, string);
        assertEquals("\"\\\\\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("/");
        JsString.dumpString(appout, string);
        assertEquals("\"\\/\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("\b");
        JsString.dumpString(appout, string);
        assertEquals("\"\\b\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("\f");
        JsString.dumpString(appout, string);
        assertEquals("\"\\f\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("\n");
        JsString.dumpString(appout, string);
        assertEquals("\"\\n\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("\r");
        JsString.dumpString(appout, string);
        assertEquals("\"\\r\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("\t");
        JsString.dumpString(appout, string);
        assertEquals("\"\\t\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("\u0001");
        JsString.dumpString(appout, string);
        assertEquals("\"\\u0001\"", appout.toString());

        appout = new StringBuilder();
        string = new JsString("あ");
        JsString.dumpString(appout, string);
        assertEquals("\"あ\"", appout.toString());

        return;
    }

    /**
     * Test of escapeText method, of class JsString.
     */
    @Test
    public void testEscapeText(){
        System.out.println("escapeText");

        assertEquals("\"A\"", JsString.escapeText("A").toString());

        return;
    }

    /**
     * Test of traverse method, of class JsString.
     */
    @Test
    public void testTraverse(){
        System.out.println("traverse");

        JsString string = new JsString("A");

        try{
            string.traverse(new ValueVisitor(){
                int ct = 0;

                public void visitValue(JsValue value)
                        throws JsVisitException{
                    assertEquals(new JsString("A"), value);
                    assertTrue(this.ct++ <= 0);
                }

                public void visitPairName(String name)
                        throws JsVisitException{
                    throw new JsVisitException();
                }

                public void visitCompositionClose(JsComposition<?> composite)
                        throws JsVisitException{
                    throw new JsVisitException();
                }
            });
        }catch(JsVisitException e){
            fail();
        }

        return;
    }

    /**
     * Test of charAt method, of class JsString.
     */
    @Test
    public void testCharAt(){
        System.out.println("charAt");

        JsString string;

        string = new JsString("abcde");
        assertEquals('b', string.charAt(1));

        try{
            string.charAt(999);
            fail();
        }catch(IndexOutOfBoundsException e){
            // NOTHING
        }

        return;
    }

    /**
     * Test of length method, of class JsString.
     */
    @Test
    public void testLength(){
        System.out.println("length");

        assertEquals(0, new JsString().length());
        assertEquals(0, new JsString("").length());
        assertEquals(1, new JsString("A").length());
        assertEquals(2, new JsString("AB").length());
        assertEquals(3, new JsString("A\"B").length());

        return;
    }

    /**
     * Test of subSequence method, of class JsString.
     */
    @Test
    public void testSubSequence(){
        System.out.println("subSequence");

        JsString string;

        string = new JsString("abcde");
        assertEquals("bcd", string.subSequence(1, 4).toString());
        assertEquals("", string.subSequence(1, 1).toString());

        try{
            string.subSequence(1,999);
            fail();
        }catch(IndexOutOfBoundsException e){
            // NOTHING
        }

        return;
    }

    /**
     * Test of hashCode method, of class JsString.
     */
    @Test
    public void testHashCode(){
        System.out.println("hashCode");
        assertEquals(new JsString("A").hashCode(), new JsString("A").hashCode());
        return;
    }

    /**
     * Test of equals method, of class JsString.
     */
    @Test
    public void testEquals(){
        System.out.println("equals");

        assertTrue(new JsString("A").equals(new JsString("A")));
        assertFalse(new JsString("A").equals(new JsString("a")));
        JsString nullVal = null;
        assertFalse(new JsString("A").equals(nullVal));

        assertFalse(new JsString("A").equals(""));

        return;
    }

    /**
     * Test of compareTo method, of class JsString.
     */
    @Test
    public void testCompareTo(){
        System.out.println("compareTo");

        assertTrue(0 == new JsString("A").compareTo(new JsString("A")));
        assertTrue(0 > new JsString("A").compareTo(new JsString("a")));
        assertTrue(0 < new JsString("a").compareTo(new JsString("A")));
        assertTrue(0 < new JsString("A").compareTo(null));

        SortedSet<JsString> set = new TreeSet<JsString>();

        set.clear();
        set.add(new JsString("A"));
        set.add(new JsString("a"));
        assertEquals(new JsString("A"), set.first());
        assertEquals(new JsString("a"), set.last());

        set.clear();
        set.add(new JsString("a"));
        set.add(new JsString("A"));
        assertEquals(new JsString("A"), set.first());
        assertEquals(new JsString("a"), set.last());

        JsString string = new JsString("A");
        assertEquals(0, string.compareTo(string));

        return;
    }

    /**
     * Test of toString method, of class JsString.
     */
    @Test
    public void testToString(){
        System.out.println("toString");

        assertEquals("\"\"", new JsString("").toString());
        assertEquals("\"abc\"", new JsString("abc").toString());
        assertEquals("\"\\\"\"", new JsString("\"").toString());
        assertEquals("\"\\\\\"", new JsString("\\").toString());
        assertEquals("\"\\/\"", new JsString("/").toString());
        assertEquals("\"\\b\"", new JsString("\b").toString());
        assertEquals("\"\\f\"", new JsString("\f").toString());
        assertEquals("\"\\n\"", new JsString("\n").toString());
        assertEquals("\"\\r\"", new JsString("\r").toString());
        assertEquals("\"\\t\"", new JsString("\t").toString());
        assertEquals("\"\\u0001\"", new JsString("\u0001").toString());
        assertEquals("\"あ\"", new JsString("あ").toString());

        return;
    }

    /**
     * Test of toRawString method, of class JsString.
     */
    @Test
    public void testToRawString(){
        System.out.println("toRawString");

        assertEquals("", new JsString("").toRawString());
        assertEquals("abc", new JsString("abc").toRawString());
        assertEquals("\"", new JsString("\"").toRawString());
        assertEquals("\\", new JsString("\\").toRawString());
        assertEquals("/", new JsString("/").toRawString());
        assertEquals("\b", new JsString("\b").toRawString());
        assertEquals("\f", new JsString("\f").toRawString());
        assertEquals("\n", new JsString("\n").toRawString());
        assertEquals("\r", new JsString("\r").toRawString());
        assertEquals("\t", new JsString("\t").toRawString());
        assertEquals("\u0001", new JsString("\u0001").toRawString());
        assertEquals("あ", new JsString("あ").toRawString());

        return;
    }

    /**
     * Test of getJsTypes method, of class JsString.
     */
    @Test
    public void testGetJsTypes() {
        System.out.println("getJsTypes");

        JsString instance = new JsString();

        assertEquals(JsTypes.STRING, instance.getJsTypes());

        return;
    }

}
