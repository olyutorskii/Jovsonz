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
public class JsPairTest {

    public JsPairTest() {
    }

    /**
     * Test of Constructor, of class JsPair.
     */
    @Test
    public void testConstructor(){
        System.out.println("constructor");

        JsPair pair = null;

        try{
            pair = new JsPair(null, 3);
            fail();
        }catch(NullPointerException e){
            // NOTHING
        }

        try{
            pair = new JsPair("three", (String)null);
            fail();
        }catch(NullPointerException e){
            // NOTHING
        }

        try{
            pair = new JsPair(null, (String)null);
            fail();
        }catch(NullPointerException e){
            // NOTHING
        }

        try{
            pair = new JsPair("three", (JsValue)null);
            fail();
        }catch(NullPointerException e){
            // NOTHING
        }

        try{
            pair = new JsPair(null, (JsValue)null);
            fail();
        }catch(NullPointerException e){
            // NOTHING
        }

        assertNull(pair);

        return;
    }

    /**
     * Test of getName method, of class JsPair.
     */
    @Test
    public void testGetName(){
        System.out.println("getName");

        JsPair pair;

        pair = new JsPair("", JsNull.NULL);
        assertEquals("", pair.getName());

        pair = new JsPair("a", JsNull.NULL);
        assertEquals("a", pair.getName());

        return;
    }

    /**
     * Test of getValue method, of class JsPair.
     */
    @Test
    public void testGetValue(){
        System.out.println("getValue");

        JsPair pair;

        pair = new JsPair("x", JsNull.NULL);
        assertEquals(JsNull.NULL, pair.getValue());

        pair = new JsPair("x", "abc");
        assertEquals(new JsString("abc"), pair.getValue());

        pair = new JsPair("x", true);
        assertEquals(JsBoolean.TRUE, pair.getValue());

        pair = new JsPair("x", false);
        assertEquals(JsBoolean.FALSE, pair.getValue());

        pair = new JsPair("x", 999999999999L);
        assertEquals(new JsNumber("999999999999"), pair.getValue());

        pair = new JsPair("x", 1.25);
        assertEquals(new JsNumber("1.25"), pair.getValue());

        return;
    }

    /**
     * Test of toString method, of class JsPair.
     */
    @Test
    public void testToString(){
        System.out.println("toString");

        JsPair pair;

        pair = new JsPair("x", JsNull.NULL);
        assertEquals("\"x\":null", pair.toString());

        pair = new JsPair("", JsNull.NULL);
        assertEquals("\"\":null", pair.toString());

        return;
    }

    /**
     * Test of equals method, of class JsPair.
     */
    @Test
    public void testEquals(){
        System.out.println("equals");

        JsPair pair1;
        JsPair pair2;
        JsPair nullVal = null;

        pair1 = new JsPair("three", 3);
        pair2 = new JsPair("three", 3);

        assertFalse(pair1.equals(nullVal));
        assertTrue(pair1.equals(pair1));
        assertTrue(pair1.equals(pair2));
        assertFalse(pair1.equals(new Object()));

        pair2 = new JsPair("three", 4);
        assertFalse(pair1.equals(pair2));

        pair2 = new JsPair("two", 3);
        assertFalse(pair1.equals(pair2));

        pair2 = new JsPair("four", 4);
        assertFalse(pair1.equals(pair2));

        return;
    }

    /**
     * Test of hashCode method, of class JsPair.
     */
    @Test
    public void testHashCode(){
        System.out.println("hashCode");

        JsPair pair1;
        JsPair pair2;

        pair1 = new JsPair("three", 3);
        pair2 = new JsPair("three", 3);

        assertEquals(pair1.hashCode(), pair2.hashCode());

        int hash1 = pair1.hashCode();
        int hash2 = pair1.hashCode();

        assertEquals(hash1, hash2);

        return;
    }

}
