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
public class JsNullTest {

    public JsNullTest() {
    }

    /**
     * Test of etc of class JsNull.
     */
    @Test
    public void testEtc(){
        System.out.println("etc");
        assertNotNull(JsNull.NULL);
        assertTrue(JsNull.NULL instanceof JsNull);
        return;
    }

    /**
     * Test of traverse method, of class JsNull.
     */
    @Test
    public void testTraverse(){
        System.out.println("traverse");
        try{
            JsNull.NULL.traverse(new ValueVisitor(){
                int ct = 0;

                public void visitValue(JsValue value)
                        throws JsVisitException{
                    assertEquals(JsNull.NULL, value);
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
     * Test of compareTo method, of class JsNull.
     */
    @Test
    public void testCompareTo(){
        System.out.println("compareTo");
        assertEquals(0, JsNull.NULL.compareTo(JsNull.NULL));
        try{
            JsNull.NULL.compareTo(null);
            fail();
        }catch(NullPointerException e){
            // NOTHING
        }
        return;
    }

    /**
     * Test of toString method, of class JsNull.
     */
    @Test
    public void testToString(){
        System.out.println("toString");
        assertEquals("null", JsNull.NULL.toString());
        return;
    }

    /**
     * Test of getJsTypes method, of class JsNull.
     */
    @Test
    public void testGetJsTypes() {
        System.out.println("getJsTypes");

        JsNull instance = JsNull.NULL;

        assertEquals(JsTypes.NULL, instance.getJsTypes());

        return;
    }

    /**
     * Test of equals method, of class JsNull.
     */
    @Test
    public void testEquals(){
        System.out.println("equals");

        assertTrue(JsNull.NULL.equals(JsNull.NULL));

        JsNull nullVal = null;
        assertFalse(JsNull.NULL.equals(nullVal));

        assertFalse(JsNull.NULL.equals(""));

        return;
    }

    /**
     * Test of hashCode method, of class JsNull.
     */
    @Test
    public void testHashCode(){
        System.out.println("hashCode");

        assertEquals(JsNull.NULL.hashCode(), JsNull.NULL.hashCode());

        return;
    }

    /**
     * Test of parseNull method, of class JsNull.
     */
    @Test
    public void testParseNull() throws Exception{
        System.out.println("parseNull");

        JsonSource source;
        JsNull result;

        source = new JsonSource("null");
        result = JsNull.parseNull(source);
        assertEquals(JsNull.NULL, result);

        source = new JsonSource("X");
        result = JsNull.parseNull(source);
        assertNull(result);

        try{
            source = new JsonSource("nX");
            result = JsNull.parseNull(source);
            fail();
        }catch(JsParseException e){
            //GOOD
        }

        try{
            source = new JsonSource("nuX");
            result = JsNull.parseNull(source);
            fail();
        }catch(JsParseException e){
            //GOOD
        }

        try{
            source = new JsonSource("nulX");
            result = JsNull.parseNull(source);
            fail();
        }catch(JsParseException e){
            //GOOD
        }

        return;
    }

}
