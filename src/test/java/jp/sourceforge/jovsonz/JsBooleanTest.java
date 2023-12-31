/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class JsBooleanTest {

    public JsBooleanTest() {
    }

    /**
     * Test of traverse method, of class JsBoolean.
     */
    @Test
    public void testTraverse(){
        System.out.println("traverse");

        try{
            JsBoolean.TRUE.traverse(new ValueVisitor(){
                int ct = 0;

                @Override
                public void visitValue(JsValue value)
                        throws JsVisitException{
                    assertEquals(JsBoolean.TRUE, value);
                    assertTrue(this.ct++ <= 0);
                }

                @Override
                public void visitPairName(String name)
                        throws JsVisitException{
                    throw new JsVisitException();
                }

                @Override
                public void visitCompositionClose(JsComposition<?> composite)
                        throws JsVisitException{
                    throw new JsVisitException();
                }
            });
        }catch(JsVisitException e){
            fail();
        }

        try{
            JsBoolean.FALSE.traverse(new ValueVisitor(){
                int ct = 0;

                @Override
                public void visitValue(JsValue value)
                        throws JsVisitException{
                    assertEquals(JsBoolean.FALSE, value);
                    assertTrue(this.ct++ <= 0);
                }

                @Override
                public void visitPairName(String name)
                        throws JsVisitException{
                    throw new JsVisitException();
                }

                @Override
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
     * Test of valueOf method, of class JsBoolean.
     */
    @Test
    public void testValueOf(){
        System.out.println("valueOf");
        assertEquals(JsBoolean.TRUE, JsBoolean.valueOf(true));
        assertEquals(JsBoolean.FALSE, JsBoolean.valueOf(false));
        return;
    }

    /**
     * Test of booleanValue method, of class JsBoolean.
     */
    @Test
    public void testBooleanValue(){
        System.out.println("booleanValue");
        assertTrue(JsBoolean.TRUE.booleanValue());
        assertFalse(JsBoolean.FALSE.booleanValue());
        return;
    }

    /**
     * Test of isTrue method, of class JsBoolean.
     */
    @Test
    public void testIsTrue(){
        System.out.println("isTrue");
        assertTrue(JsBoolean.TRUE.isTrue());
        assertFalse(JsBoolean.FALSE.isTrue());
        return;
    }

    /**
     * Test of isFalse method, of class JsBoolean.
     */
    @Test
    public void testIsFalse(){
        System.out.println("isFalse");
        assertFalse(JsBoolean.TRUE.isFalse());
        assertTrue(JsBoolean.FALSE.isFalse());
        return;
    }

    /**
     * Test of hashCode method, of class JsBoolean.
     */
    @Test
    public void testHashCode(){
        System.out.println("hashCode");
        assertEquals(JsBoolean.TRUE.hashCode(), JsBoolean.TRUE.hashCode());
        assertEquals(JsBoolean.FALSE.hashCode(), JsBoolean.FALSE.hashCode());
        // NOTHING
        return;
    }

    /**
     * Test of equals method, of class JsBoolean.
     */
    @Test
    public void testEquals(){
        System.out.println("equals");

        JsValue jsnull = JsNull.NULL;
        JsBoolean nullVal = null;

        assertTrue(JsBoolean.TRUE.equals(JsBoolean.TRUE));
        assertFalse(JsBoolean.TRUE.equals(JsBoolean.FALSE));
        assertFalse(JsBoolean.TRUE.equals(jsnull));
        assertFalse(JsBoolean.TRUE.equals(nullVal));

        assertFalse(JsBoolean.FALSE.equals(JsBoolean.TRUE));
        assertTrue(JsBoolean.FALSE.equals(JsBoolean.FALSE));
        assertFalse(JsBoolean.TRUE.equals(jsnull));
        assertFalse(JsBoolean.FALSE.equals(nullVal));

        return;
    }

    /**
     * Test of compareTo method, of class JsBoolean.
     */
    @Test
    public void testCompareTo(){
        System.out.println("compareTo");
        assertEquals(0, JsBoolean.TRUE.compareTo(JsBoolean.TRUE));
        assertEquals(0, JsBoolean.FALSE.compareTo(JsBoolean.FALSE));
        assertTrue(0 > JsBoolean.TRUE.compareTo(JsBoolean.FALSE));
        assertTrue(0 < JsBoolean.FALSE.compareTo(JsBoolean.TRUE));

        try{
            JsBoolean.TRUE.compareTo(null);
            fail();
        }catch(NullPointerException e){
            // NOTHING
        }

        try{
            JsBoolean.FALSE.compareTo(null);
            fail();
        }catch(NullPointerException e){
            // NOTHING
        }

        SortedSet<JsBoolean> set = new TreeSet<>();

        set.clear();
        set.add(JsBoolean.TRUE);
        set.add(JsBoolean.FALSE);
        assertEquals(JsBoolean.TRUE, set.first());
        assertEquals(JsBoolean.FALSE, set.last());
        set.clear();
        set.add(JsBoolean.FALSE);
        set.add(JsBoolean.TRUE);
        assertEquals(JsBoolean.TRUE, set.first());
        assertEquals(JsBoolean.FALSE, set.last());

        return;
    }

    /**
     * Test of toString method, of class JsBoolean.
     */
    @Test
    public void testToString(){
        System.out.println("toString");
        assertEquals("true", JsBoolean.TRUE.toString());
        assertEquals("false", JsBoolean.FALSE.toString());
        return;
    }

    /**
     * Test of toString method, of class JsBoolean.
     */
    @Test
    public void testEtc(){
        System.out.println("etc.");
        assertNotNull(JsBoolean.TRUE);
        assertNotNull(JsBoolean.FALSE);
        assertTrue(JsBoolean.TRUE instanceof JsBoolean);
        assertTrue(JsBoolean.FALSE instanceof JsBoolean);
        return;
    }

    /**
     * Test of getJsTypes method, of class JsBoolean.
     */
    @Test
    public void testGetJsTypes() {
        System.out.println("getJsTypes");

        assertEquals(JsTypes.BOOLEAN, JsBoolean.TRUE.getJsTypes());

        return;
    }

    /**
     * Test of parseBoolean method, of class JsBoolean.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseBoolean() throws Exception{
        System.out.println("parseBoolean");

        JsonSource source;
        JsBoolean result;

        source = new JsonSource("true");
        result = JsBoolean.parseBoolean(source);
        assertEquals(JsBoolean.TRUE, result);

        source = new JsonSource("false");
        result = JsBoolean.parseBoolean(source);
        assertEquals(JsBoolean.FALSE, result);

        source = new JsonSource("X");
        result = JsBoolean.parseBoolean(source);
        assertNull(result);

        try{
            source = new JsonSource("tX");
            result = JsBoolean.parseBoolean(source);
            fail();
            assert result == result;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            source = new JsonSource("trX");
            result = JsBoolean.parseBoolean(source);
            fail();
            assert result == result;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            source = new JsonSource("truX");
            result = JsBoolean.parseBoolean(source);
            fail();
            assert result == result;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            source = new JsonSource("fX");
            result = JsBoolean.parseBoolean(source);
            fail();
            assert result == result;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            source = new JsonSource("faX");
            result = JsBoolean.parseBoolean(source);
            fail();
            assert result == result;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            source = new JsonSource("falX");
            result = JsBoolean.parseBoolean(source);
            fail();
            assert result == result;
        }catch(JsParseException e){
            //GOOD
        }

        try{
            source = new JsonSource("falsX");
            result = JsBoolean.parseBoolean(source);
            fail();
            assert result == result;
        }catch(JsParseException e){
            //GOOD
        }

        return;
    }

}
