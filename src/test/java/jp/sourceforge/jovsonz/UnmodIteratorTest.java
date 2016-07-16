/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class UnmodIteratorTest {

    public UnmodIteratorTest() {
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

    private void assert3ListAndIterator(List list, Iterator unmod){
        assertEquals(3, list.size());

        assertTrue(unmod.hasNext());
        assertEquals(list.get(0), unmod.next());

        assertTrue(unmod.hasNext());
        assertEquals(list.get(1), unmod.next());

        try{
            unmod.remove();
            fail();
        }catch(UnsupportedOperationException e){
            //NOTHING
        }

        assertTrue(unmod.hasNext());
        assertEquals(list.get(2), unmod.next());

        assertFalse(unmod.hasNext());

        try{
            unmod.next();
            fail();
        }catch(NoSuchElementException e){
            //NOTHING
        }

        return;
    }

    /**
     * Test of Constructor, of class UnmodIterator.
     */
    @Test
    public void testConstructor(){
        System.out.println("constructor");

        List<String> list;
        Iterator<String> it;

        list = new LinkedList<String>();
        list.add("A");
        list.add("B");
        list.add("C");

        it = list.iterator();
        UnmodIterator<String> unmod = new UnmodIterator<String>(it);

        assert3ListAndIterator(list, unmod);

        try{
            unmod = new UnmodIterator<String>(null);
            fail();
        }catch(NullPointerException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of wrapUnmod method, of class UnmodIterator.
     */
    @Test
    public void testWrapUnmod_Iterator(){
        System.out.println("wrapUnmod");

        List<String> list;
        Iterator<String> it;

        list = new LinkedList<String>();
        list.add("A");
        list.add("B");
        list.add("C");

        it = list.iterator();
        Iterator<String> unmod = UnmodIterator.wrapUnmod(it);

        assert3ListAndIterator(list, unmod);

        try{
            unmod = UnmodIterator.wrapUnmod((Iterator<String>)null);
            fail();
        }catch(NullPointerException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of wrapUnmod method, of class UnmodIterator.
     */
    @Test
    public void testWrapUnmod_Iterable(){
        System.out.println("wrapUnmod");

        List<String> list;

        list = new LinkedList<String>();
        list.add("A");
        list.add("B");
        list.add("C");

        Iterable<String> unmod = UnmodIterator.wrapUnmod(list);

        assert3ListAndIterator(list, unmod.iterator());

        try{
            unmod = UnmodIterator.wrapUnmod((Iterable<String>)null);
            fail();
        }catch(NullPointerException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of unmodIterator method, of class UnmodIterator.
     */
    @Test
    public void testUnmodIterator(){
        System.out.println("unmodIterator");

        List<String> list;

        list = new LinkedList<String>();
        list.add("A");
        list.add("B");
        list.add("C");

        Iterator<String> unmod = UnmodIterator.unmodIterator(list);

        assert3ListAndIterator(list, unmod);

        try{
            unmod = UnmodIterator.unmodIterator(null);
            fail();
        }catch(NullPointerException e){
            //GOOD
        }

        return;
    }

    /**
     * Test of hasNext method, of class UnmodIterator.
     */
    @Test
    public void testHasNext(){
        System.out.println("hasNext");
        return;
    }

    /**
     * Test of next method, of class UnmodIterator.
     */
    @Test
    public void testNext(){
        System.out.println("next");
        return;
    }

    /**
     * Test of remove method, of class UnmodIterator.
     */
    @Test
    public void testRemove(){
        System.out.println("remove");
        return;
    }

}
