/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.Reader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class JsonTest {

    public JsonTest() {
    }

    /**
     * Test of dumpJson method, of class Json.
     * @throws java.lang.Exception
     */
    @Test
    public void testDumpJson() throws Exception{
        System.out.println("dumpJson");

        String SP2 = "\u0020\u0020";
        String SP4 = SP2 + SP2;
        String HASHSEP = "\u0020:\u0020";

        Reader reader;
        JsComposition<?> root;
        StringBuilder dump = new StringBuilder();

        reader = new StringReader("{}");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("{ }\n", dump.toString());

        reader = new StringReader("[]");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("[ ]\n", dump.toString());

        reader = new StringReader("[1]");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("[\n"
                + SP2+"1\n"
                + "]\n", dump.toString());

        reader = new StringReader("[1,2]");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("[\n"
                + SP2+"1 ,\n"
                + SP2+"2\n"
                + "]\n", dump.toString());

        reader = new StringReader("[1,[2],3]");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("[\n"
                + SP2+"1 ,\n"
                + SP2+"[\n"
                + SP4+"2\n"
                + SP2+"] ,\n"
                + SP2+"3\n"
                + "]\n", dump.toString());

        reader = new StringReader("[1,{\"A\":\"a\"}]");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("[\n"
                + SP2+"1 ,\n"
                + SP2+"{\n"
                + SP4+"\"A\""+HASHSEP+"\"a\"\n"
                + SP2+"}\n"
                + "]\n", dump.toString());

        reader = new StringReader("{\"A\":\"a\"}");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("{\n"
                + SP2+"\"A\""+HASHSEP+"\"a\"\n"
                + "}\n", dump.toString());

        reader = new StringReader("{\"A\":\"a\",\"B\":\"b\"}");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("{\n"
                + SP2+"\"A\""+HASHSEP+"\"a\" ,\n"
                + SP2+"\"B\""+HASHSEP+"\"b\"\n"
                + "}\n", dump.toString());

        reader = new StringReader("{\"A\":{}}");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("{\n"
                + SP2+"\"A\""+HASHSEP+"{\u0020}\n"
                + "}\n", dump.toString());

        reader = new StringReader("{\"A\":{\"B\":\"b\"}}");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("{\n"
                + SP2+"\"A\""+HASHSEP+"{\n"
                + SP4+"\"B\""+HASHSEP+"\"b\"\n"
                + SP2+"}\n"
                + "}\n", dump.toString());

        reader = new StringReader("{\"A\":[]}");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("{\n"
                + SP2+"\"A\""+HASHSEP+"[\u0020]\n"
                + "}\n", dump.toString());

        reader = new StringReader("{\"A\":[1,2]}");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("{\n"
                + SP2+"\"A\""+HASHSEP+"[\n"
                + SP4+"1 ,\n"
                + SP4+"2\n"
                + SP2+"]\n"
                + "}\n", dump.toString());

        reader = new StringReader("["
                + "true,false,null,\"string\",-0.5"
                + "]");
        root = Json.parseJson(reader);
        dump.setLength(0);
        Json.dumpJson(dump, root);
        assertEquals("[\n"
                + SP2+"true ,\n"
                + SP2+"false ,\n"
                + SP2+"null ,\n"
                + SP2+"\"string\" ,\n"
                + SP2+"-0.5\n"
                + "]\n", dump.toString());

        try{
            Json.dumpJson(null, new JsObject());
            fail();
        }catch(NullPointerException e){
            //GOOD
        }

        try{
            Json.dumpJson(new StringBuilder(), null);
            fail();
        }catch(NullPointerException e){
            //GOOD
        }

        reader = new StringReader("[1,2,3]");
        root = Json.parseJson(reader);
        TroubleAppender app = new TroubleAppender(3);
        try{
            Json.dumpJson(app, root);
            fail();
        }catch(IOException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of parseValue method, of class Json.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseValue() throws Exception{
        System.out.println("parseValue");

        JsonSource source;
        JsValue value;

        source = new JsonSource("true");
        value = Json.parseValue(source);
        assertEquals(JsBoolean.TRUE, value);

        source = new JsonSource("false");
        value = Json.parseValue(source);
        assertEquals(JsBoolean.FALSE, value);

        source = new JsonSource("null");
        value = Json.parseValue(source);
        assertEquals(JsNull.NULL, value);

        source = new JsonSource("-0.5");
        value = Json.parseValue(source);
        assertEquals(JsTypes.NUMBER, value.getJsTypes());
        assertEquals(-0.5, ((JsNumber)value).doubleValue(), 0.0);

        source = new JsonSource("\"ABC\"");
        value = Json.parseValue(source);
        assertEquals(JsTypes.STRING, value.getJsTypes());
        assertEquals("ABC", ((JsString)value).toRawString());

        source = new JsonSource("[1,2,3]");
        value = Json.parseValue(source);
        assertEquals(JsTypes.ARRAY, value.getJsTypes());
        assertEquals(3, ((JsArray)value).size());

        source = new JsonSource("{\"A\":1,\"B\":2,\"C\":3}");
        value = Json.parseValue(source);
        assertEquals(JsTypes.OBJECT, value.getJsTypes());
        assertEquals(3, ((JsObject)value).size());

        source = new JsonSource("");
        value = Json.parseValue(source);
        assertNull(value);

        source = new JsonSource(" ");
        value = Json.parseValue(source);
        assertNull(value);

        try{
            source = new JsonSource("#");
            value = Json.parseValue(source);
            fail();
            assert value == value;
        }catch(JsParseException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of parseJson method, of class Json.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseJson() throws Exception{
        System.out.println("parseJson");

        Reader reader;
        JsComposition<?> root;

        reader = new StringReader("{}");
        root = Json.parseJson(reader);
        assertNotNull(root);
        assertEquals(JsTypes.OBJECT, root.getJsTypes());
        assertEquals(0, root.size());

        reader = new StringReader("{\"name\":\"value\"}");
        root = Json.parseJson(reader);
        assertNotNull(root);
        assertEquals(JsTypes.OBJECT, root.getJsTypes());
        assertEquals(1, root.size());

        reader = new StringReader(" { \"name\" : \"value\" } ");
        root = Json.parseJson(reader);
        assertNotNull(root);
        assertEquals(JsTypes.OBJECT, root.getJsTypes());
        assertEquals(1, root.size());

        reader = new StringReader("[]");
        root = Json.parseJson(reader);
        assertNotNull(root);
        assertEquals(JsTypes.ARRAY, root.getJsTypes());
        assertEquals(0, root.size());

        reader = new StringReader("[1,2,3]");
        root = Json.parseJson(reader);
        assertNotNull(root);
        assertEquals(JsTypes.ARRAY, root.getJsTypes());
        assertEquals(3, root.size());

        reader = new StringReader(" [ 1 , 2 , 3 ] ");
        root = Json.parseJson(reader);
        assertNotNull(root);
        assertEquals(JsTypes.ARRAY, root.getJsTypes());
        assertEquals(3, root.size());

        reader = new StringReader("");
        root = Json.parseJson(reader);
        assertNull(root);

        reader = new StringReader(" ");
        root = Json.parseJson(reader);
        assertNull(root);

        try{
            reader = new StringReader("true");
            root = Json.parseJson(reader);
            fail();
            assert root == root;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            reader = new StringReader("false");
            root = Json.parseJson(reader);
            fail();
            assert root == root;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            reader = new StringReader("null");
            root = Json.parseJson(reader);
            fail();
            assert root == root;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            reader = new StringReader("\"ABC\"");
            root = Json.parseJson(reader);
            fail();
            assert root == root;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            reader = new StringReader("-0.5");
            root = Json.parseJson(reader);
            fail();
            assert root == root;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            reader = new StringReader("#");
            root = Json.parseJson(reader);
            fail();
            assert root == root;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            reader = new StringReader(" [ 1 , 2 , 3 ");
            root = Json.parseJson(reader);
            fail();
            assert root == root;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            reader = new TroubleReader(" [ 1 , 2 , 3 ] ", 3);
            root = Json.parseJson(reader);
            fail();
            assert root == root;
        }catch(IOException e){
            //GOOD
        }

        return;
    }

}
