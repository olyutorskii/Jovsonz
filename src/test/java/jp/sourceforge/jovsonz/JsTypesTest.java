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
public class JsTypesTest {

    public JsTypesTest() {
    }

    /**
     * Test of values method, of class JsTypes.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        JsTypes[] result = JsTypes.values();
        assertEquals(6, result.length);
        assertEquals(JsTypes.NUMBER,  result[0]);
        assertEquals(JsTypes.STRING,  result[1]);
        assertEquals(JsTypes.BOOLEAN, result[2]);
        assertEquals(JsTypes.ARRAY,   result[3]);
        assertEquals(JsTypes.OBJECT,  result[4]);
        assertEquals(JsTypes.NULL,    result[5]);
        return;
    }

    /**
     * Test of valueOf method, of class JsTypes.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");

        assertEquals(JsTypes.NUMBER,  JsTypes.valueOf("NUMBER"));
        assertEquals(JsTypes.STRING,  JsTypes.valueOf("STRING"));
        assertEquals(JsTypes.BOOLEAN, JsTypes.valueOf("BOOLEAN"));
        assertEquals(JsTypes.ARRAY,   JsTypes.valueOf("ARRAY"));
        assertEquals(JsTypes.OBJECT,  JsTypes.valueOf("OBJECT"));
        assertEquals(JsTypes.NULL,    JsTypes.valueOf("NULL"));

        return;
    }

    /**
     * Test of getJsTypes method, of class JsTypes.
     */
    @Test
    public void testGetJsTypes() {
        System.out.println("getJsTypes");

        assertEquals(JsTypes.NUMBER,  JsTypes.getJsTypes(JsNumber.class));
        assertEquals(JsTypes.STRING,  JsTypes.getJsTypes(JsString.class));
        assertEquals(JsTypes.BOOLEAN, JsTypes.getJsTypes(JsBoolean.class));
        assertEquals(JsTypes.ARRAY,   JsTypes.getJsTypes(JsArray.class));
        assertEquals(JsTypes.OBJECT,  JsTypes.getJsTypes(JsObject.class));
        assertEquals(JsTypes.NULL,    JsTypes.getJsTypes(JsNull.class));

        assertNull(JsTypes.getJsTypes(Object.class));

        try{
            JsTypes.getJsTypes(null);
            fail();
        }catch(NullPointerException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of getJsClass method, of class JsTypes.
     */
    @Test
    public void testGetJsClass() {
        System.out.println("getJsClass");

        assertEquals(JsNumber.class, JsTypes.NUMBER.getJsClass());
        assertEquals(JsString.class, JsTypes.STRING.getJsClass());
        assertEquals(JsBoolean.class, JsTypes.BOOLEAN.getJsClass());
        assertEquals(JsArray.class, JsTypes.ARRAY.getJsClass());
        assertEquals(JsObject.class, JsTypes.OBJECT.getJsClass());
        assertEquals(JsNull.class, JsTypes.NULL.getJsClass());

        return;
    }

    /**
     * Test of isComposition method, of class JsTypes.
     */
    @Test
    public void testIsComposition(){
        System.out.println("isComposition");

        assertTrue(JsTypes.OBJECT.isComposition());
        assertTrue(JsTypes.ARRAY.isComposition());

        assertFalse(JsTypes.NUMBER.isComposition());
        assertFalse(JsTypes.STRING.isComposition());
        assertFalse(JsTypes.BOOLEAN.isComposition());
        assertFalse(JsTypes.NULL.isComposition());

        return;
    }

}
