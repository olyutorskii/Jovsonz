/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class JsArrayTest {

    public JsArrayTest() {
    }

    /**
     * Test of parseArray method, of class JsArray.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseArray() throws Exception{
        System.out.println("parseArray");

        JsonSource source;
        JsArray array;

        source = new JsonSource("[]");
        array = JsArray.parseArray(source);
        assertEquals(0, array.size());

        source = new JsonSource("[true]");
        array = JsArray.parseArray(source);
        assertEquals(1, array.size());
        assertEquals(JsBoolean.TRUE, array.get(0));

        source = new JsonSource("[true,false]");
        array = JsArray.parseArray(source);
        assertEquals(2, array.size());
        assertEquals(JsBoolean.TRUE, array.get(0));
        assertEquals(JsBoolean.FALSE, array.get(1));

        source = new JsonSource("\n[\rtrue\t, false\n]\r");
        array = JsArray.parseArray(source);
        assertNull(array);

        source = new JsonSource("[\rtrue\t, false\n]\r");
        array = JsArray.parseArray(source);
        assertEquals(2, array.size());
        assertEquals(JsBoolean.TRUE, array.get(0));
        assertEquals(JsBoolean.FALSE, array.get(1));

        try{
            source = new JsonSource("[,]");
            array = JsArray.parseArray(source);
            fail();
            assert array == array;
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("[true,]");
            array = JsArray.parseArray(source);
            fail();
            assert array == array;
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("[true#]");
            array = JsArray.parseArray(source);
            fail();
            assert array == array;
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("[true,");
            array = JsArray.parseArray(source);
            fail();
            assert array == array;
        }catch(JsParseException e){
            // NOTHING
        }

        try{
            source = new JsonSource("[true");
            array = JsArray.parseArray(source);
            fail();
            assert array == array;
        }catch(JsParseException e){
            // NOTHING
        }

        source = new JsonSource("true]");
        array = JsArray.parseArray(source);
        assertNull(array);

        return;
    }

    /**
     * Test of add method, of class JsArray.
     */
    @Test
    public void testAdd(){
        System.out.println("add");

        JsArray array = new JsArray();

        JsNumber number = new JsNumber("1.23");
        assertEquals(0, array.size());
        array.add(number);
        assertEquals(1, array.size());
        array.add(number);
        assertEquals(2, array.size());

        return;
    }

    /**
     * Test of get method, of class JsArray.
     */
    @Test
    public void testGet(){
        System.out.println("get");

        JsArray array = new JsArray();

        JsValue val1 = new JsNumber("1.23");
        JsValue val2 = new JsString("abc");

        array.add(val1);
        array.add(val2);

        assertEquals(val1, array.get(0));
        assertEquals(val2, array.get(1));

        try{
            array.get(2);
            fail();
        }catch(IndexOutOfBoundsException e){
            // NOTHING
        }

        return;
    }

    /**
     * Test of clear method, of class JsArray.
     */
    @Test
    public void testClear(){
        System.out.println("clear");

        JsArray array = new JsArray();

        JsValue val1 = new JsNumber("1.23");
        JsValue val2 = new JsString("abc");

        array.add(val1);
        array.add(val2);
        assertEquals(2, array.size());

        array.clear();
        assertEquals(0, array.size());

        try{
            array.get(0);
            fail();
        }catch(IndexOutOfBoundsException e){
            // NOTHING
        }

        array = new JsArray();
        array.add(JsNull.NULL);
        assertEquals(1, array.size());
        assertTrue(array.hasChanged());
        array.setUnchanged();
        assertFalse(array.hasChanged());
        array.clear();
        assertEquals(0, array.size());
        assertTrue(array.hasChanged());
        array.setUnchanged();
        array.clear();
        assertEquals(0, array.size());
        assertFalse(array.hasChanged());

        return;
    }

    /**
     * Test of remove method, of class JsArray.
     */
    @Test
    public void testRemove_JsValue(){
        System.out.println("remove");

        JsArray array = new JsArray();

        JsValue val1 = new JsNumber("1.23");
        JsValue val2 = new JsString("abc");
        JsValue val3 = JsBoolean.TRUE;

        array.add(val1);
        array.add(val2);
        assertEquals(2, array.size());

        assertTrue(array.remove(val1));
        assertEquals(1, array.size());
        assertEquals(val2, array.get(0));

        assertFalse(array.remove(val3));
        assertEquals(1, array.size());

        return;
    }

    /**
     * Test of remove method, of class JsArray.
     */
    @Test
    public void testRemove_int(){
        System.out.println("remove");

        JsArray array = new JsArray();

        JsValue val1 = new JsNumber("1.23");
        JsValue val2 = new JsString("abc");
        JsValue val3 = JsBoolean.TRUE;

        array.add(val1);
        array.add(val2);
        array.add(val3);
        assertEquals(3, array.size());

        assertEquals(val1, array.remove(0));
        assertEquals(2, array.size());
        assertEquals(val2, array.get(0));

        assertEquals(val3, array.remove(1));
        assertEquals(1, array.size());
        assertEquals(val2, array.get(0));

        return;
    }

    /**
     * Test of size method, of class JsArray.
     */
    @Test
    public void testSize(){
        System.out.println("size");

        JsArray array = new JsArray();
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());

        JsValue val1 = new JsNumber("1.23");

        array.add(val1);
        assertEquals(1, array.size());
        assertFalse(array.isEmpty());

        return;
    }

    /**
     * Test of iterator method, of class JsArray.
     */
    @Test
    public void testIterator(){
        System.out.println("iterator");

        JsArray array = new JsArray();

        JsValue val1 = new JsNumber("1.23");
        JsValue val2 = new JsString("abc");

        array.add(val1);
        array.add(val2);

        Iterator<JsValue> it = array.iterator();

        assertTrue(it.hasNext());
        assertEquals(val1, it.next());

        assertTrue(it.hasNext());
        assertEquals(val2, it.next());

        assertFalse(it.hasNext());

        return;
    }

    /**
     * Test of hashCode method, of class JsArray.
     */
    @Test
    public void testHashCode(){
        System.out.println("hashCode");

        JsArray array1 = new JsArray();
        JsArray array2 = new JsArray();

        assertEquals(array1.hashCode(), array2.hashCode());

        array1.add(new JsString("abc"));
        array2.add(new JsString("abc"));

        assertEquals(array1.hashCode(), array2.hashCode());

        return;
    }

    /**
     * Test of equals method, of class JsArray.
     */
    @Test
    public void testEquals(){
        System.out.println("equals");

        JsArray array1 = new JsArray();
        JsArray array2 = new JsArray();

        assertTrue(array1.equals(array2));

        array1.add(new JsString("abc"));
        array2.add(new JsString("abc"));

        assertTrue(array1.equals(array2));

        array1.add(new JsString("xyz"));
        array2.add(new JsString("XYZ"));

        assertFalse(array1.equals(array2));

        JsArray nullVal = null;

        assertFalse(array1.equals(nullVal));

        assertFalse(array1.equals(""));

        return;
    }

    /**
     * Test of toString method, of class JsArray.
     */
    @Test
    public void testToString(){
        System.out.println("toString");

        JsArray array = new JsArray();

        assertEquals("[]", array.toString());

        array.add(JsBoolean.TRUE);
        assertEquals("[true]", array.toString());

        array.add(JsBoolean.FALSE);
        assertEquals("[true,false]", array.toString());

        array.add(new JsArray());
        assertEquals("[true,false,[]]", array.toString());

        return;
    }

    /**
     * Test of traverse method, of class JsArray.
     * @throws java.lang.Exception
     */
    @Test
    public void testTraverse() throws Exception{
        System.out.println("traverse");

        JsArray array = new JsArray();
        JsValue val1 = new JsNumber("12");
        JsValue val2 = new JsString("AB");
        array.add(val1);
        array.add(val2);

        final List<Object> visited = new LinkedList<>();

        try{
            array.traverse(new ValueVisitor(){
                @Override
                public void visitValue(JsValue value)
                        throws JsVisitException{
                    visited.add(value);
                    return;
                }

                @Override
                public void visitPairName(String name)
                        throws JsVisitException{
                    visited.add(name);
                    return;
                }

                @Override
                public void visitCompositionClose(JsComposition<?> composite)
                        throws JsVisitException{
                    visited.add(composite);
                    return;
                }
            });
        }catch(JsVisitException e){
            fail();
        }

        assertEquals(4, visited.size());
        assertEquals(array, visited.get(0));
        assertEquals(val1, visited.get(1));
        assertEquals(val2, visited.get(2));
        assertEquals(array, visited.get(3));

        return;
    }

    /**
     * Test of hasChanged method, of class JsArray.
     */
    @Test
    public void testHasChanged(){
        System.out.println("hasChanged");

        JsArray array = new JsArray();
        assertFalse(array.hasChanged());

        array.add(new JsNumber("0"));
        assertTrue(array.hasChanged());

        array.setUnchanged();
        assertFalse(array.hasChanged());

        JsArray child = new JsArray();
        array.add(child);
        array.setUnchanged();
        assertFalse(array.hasChanged());

        child.add(JsNull.NULL);
        assertTrue(array.hasChanged());
        array.setUnchanged();
        assertFalse(array.hasChanged());

        return;
    }

    /**
     * Test of setUnchanged method, of class JsArray.
     */
    @Test
    public void testSetUnchanged(){
        System.out.println("setUnchanged");

        JsArray array = new JsArray();
        JsArray child = new JsArray();
        array.add(child);

        child.add(JsNull.NULL);
        assertTrue(child.hasChanged());

        array.setUnchanged();
        assertFalse(child.hasChanged());

        return;
    }

    /**
     * Test of getJsTypes method, of class JsArray.
     */
    @Test
    public void testGetJsTypes() {
        System.out.println("getJsTypes");

        JsArray instance = new JsArray();

        assertEquals(JsTypes.ARRAY, instance.getJsTypes());

        return;
    }

}
