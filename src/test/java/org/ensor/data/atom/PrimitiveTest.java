/*
 * The MIT License
 *
 * Copyright 2014 Jon Arney, Ensor Robotics.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.ensor.data.atom;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class PrimitiveTest {

    @Test
    public void testPair() {
        Pair<String, Integer> p = new Pair<String, Integer>("123", 123);
        Pair<String, Integer> p2 = new Pair<String, Integer>("123", 123);
        Pair<String, Integer> p3 = new Pair<String, Integer>("123", 1234);

        Assert.assertEquals("123", p.getKey());
        Assert.assertEquals((long)123, (long)p.getValue());
        
        Assert.assertEquals(p, p2);
        Assert.assertNotEquals(p, p3);

        Assert.assertEquals((long)1234, (long)p3.setValue(123));
        
        Assert.assertEquals(p, p3);
        
        Assert.assertEquals(p.hashCode(), p3.hashCode());
        
        Assert.assertNotEquals(p, "asdf");
        
        Pair<String, Integer> pnull = new Pair<String, Integer>(null, null);
        Assert.assertEquals(7*97*97, pnull.hashCode());
        
    }
    
    @Test
    public void testBoolean() {
        BoolAtom t1 = BoolAtom.newAtom(true);
        BoolAtom t2 = BoolAtom.newAtom(true);
        
        // Assert that the two booleans correspond
        // to the same value.
        Assert.assertEquals(t1, t2);
        Assert.assertTrue(t1 == t2);
        
        BoolAtom f1 = BoolAtom.newAtom(false);
        BoolAtom f2 = BoolAtom.newAtom(false);
        
        Assert.assertEquals(f1, f2);
        Assert.assertTrue(f1 == f2);
        
        // Assert that the "not" principle
        // applies.
        Assert.assertTrue(f1.getValue() == !t1.getValue());
        
        Assert.assertTrue(t1 == t1.getImmutable());
        Assert.assertTrue(f1 == f1.getImmutable());
        
        Assert.assertTrue(t1 == t1.getMutable());
        Assert.assertTrue(f1 == f1.getMutable());
        
        Assert.assertEquals("true", t1.toString());
        Assert.assertEquals("false", f1.toString());
        
    }
    
    @Test
    public void testInt() {
        IntAtom i1 = IntAtom.newAtom(1);
        IntAtom i2 = IntAtom.newAtom(1L);
        
        Assert.assertEquals(i1, i2);
        Assert.assertEquals(i1.getImmutable(), i1);
        Assert.assertTrue(i1.getImmutable() == i1);
        Assert.assertTrue(i1 == i2);
        
        IntAtom i11 = IntAtom.newAtom(18);
        IntAtom i12 = IntAtom.newAtom(18L);
        
        Assert.assertEquals(i11.getImmutable(), i11);
        Assert.assertTrue(i11 != i12);
        Assert.assertEquals(i11.getValue(), i12.getValue());
        
        IntAtom i21 = IntAtom.newAtom(-18);
        IntAtom i22 = IntAtom.newAtom(-18L);
        
        Assert.assertEquals(i21.getImmutable(), i21);
        Assert.assertTrue(i21 != i22);
        Assert.assertEquals(i21.getValue(), i22.getValue());
        
        Assert.assertEquals(i21.hashCode(), i22.hashCode());
        
        Assert.assertNotEquals(i11, i21);
        
        Assert.assertNotEquals(i1, this);
        
        Assert.assertEquals("-18", i21.toString());
        
        
    }
    
    @Test
    public void testString() {
        StringAtom sa = StringAtom.newAtom("fish");
        StringAtom sa2 = StringAtom.newAtom("fish");
        
        Assert.assertEquals(sa.getMutable(), sa);
        Assert.assertEquals(sa.getImmutable(), sa);
        Assert.assertEquals(sa, sa2);
        
        Assert.assertEquals(sa.hashCode(), "fish".hashCode());
        
        Assert.assertNotEquals(sa, "fish");
        
    }
    
    
    @Test
    public void testReal() {
        RealAtom minusone = RealAtom.newAtom((double)-1.0f);
        RealAtom minusonef = RealAtom.newAtom(-1.0f);

        Assert.assertEquals(minusone, minusonef);
        
        RealAtom zero = RealAtom.newAtom((double)0.0f);
        RealAtom zerof = RealAtom.newAtom(0.0f);

        Assert.assertEquals(zero, zerof);
        
        RealAtom one = RealAtom.newAtom((double)1.0f);
        RealAtom onef = RealAtom.newAtom(1.0f);

        Assert.assertEquals(one, onef);
        Assert.assertNotEquals(zero, one);
        
        RealAtom two = RealAtom.newAtom((double)2.0f);
        RealAtom twof = RealAtom.newAtom(2.0f);
        
        Assert.assertEquals(two, twof);
        Assert.assertEquals(2, two.getValue(), 0.0002);
        
        RealAtom other = RealAtom.newAtom((double)17.2f);
        RealAtom otherf = RealAtom.newAtom(17.2f);
        
        Assert.assertEquals(other, otherf);
        Assert.assertEquals(17.2, other.getValue(), 0.0002);
        
        Assert.assertEquals(other, other.getImmutable());
        
        Assert.assertNotEquals(other, this);

        Assert.assertTrue(other == other.getMutable());
        
        Assert.assertEquals(other.hashCode(), otherf.hashCode());
        Assert.assertNotEquals(other.hashCode(),two.hashCode());
        
        Assert.assertTrue(otherf.toString().startsWith("17.2"));
    }
    
    @Test
    public void testList() throws Exception {
        ListAtom list = ListAtom.newAtom();
        
        list.append(1244);
        list.append(false);
        list.append("fish");
        list.append(17.2);
        list.append(1244L);
        list.append(17.2f);
        
        ImmutableList ilist = ImmutableList.newAtom(list);
        ListAtom rlist = ilist.getMutable();
        
        Assert.assertEquals(ilist.getInt(0), list.getInt(0));
        Assert.assertEquals(list.getInt(0), rlist.getInt(0));
        Assert.assertEquals(1244, list.getInt(0));

        Assert.assertEquals(ilist.getBool(1), list.getBool(1));
        Assert.assertEquals(list.getBool(1), rlist.getBool(1));
        Assert.assertEquals(false, list.getBool(1));
        
        Assert.assertEquals(ilist.getString(2), list.getString(2));
        Assert.assertEquals(list.getString(2), rlist.getString(2));
        Assert.assertEquals("fish", list.getString(2));
        Assert.assertEquals(list.get(2), StringAtom.newAtom("fish"));
        
        Assert.assertEquals(ilist.getReal(3), list.getReal(3), 0.002);
        Assert.assertEquals(list.getReal(3), rlist.getReal(3), 0.002);
        Assert.assertEquals(17.2, list.getReal(3), 0.002);
        
        Assert.assertEquals(ilist.getInt(4), list.getInt(4));
        Assert.assertEquals(list.getInt(4), rlist.getInt(4));
        Assert.assertEquals(1244, list.getInt(4));
        
        Assert.assertEquals(ilist.getReal(5), list.getReal(5), 0.002);
        Assert.assertEquals(list.getReal(5), rlist.getReal(5), 0.002);
        Assert.assertEquals(17.2, list.getReal(5), 0.002);

        // Check that the list is already mutable.
        Assert.assertTrue(list == list.getMutable());
        
        // Check that the ilist is already immutable.
        Assert.assertTrue(ilist == ilist.getImmutable());
        
        final int[] count = {0};
        final List<Atom> atomlist = new ArrayList<Atom>();
        
        IListVisitor visitor = new IListVisitor() {

            public void visit(Atom aAtom) throws Exception {
                count[0]++;
                atomlist.add(aAtom);
            }
            
        };
        
        list.visitAtoms(visitor);
        
        Assert.assertEquals(6, count[0]);
        Assert.assertEquals(count[0], list.size());
        Assert.assertEquals(list.size(), atomlist.size());
        
        ImmutableList ilist1 = ImmutableList.newAtom(atomlist);
        ListAtom list1 = ListAtom.newAtom(atomlist);
        
        Assert.assertEquals(list.size(), ilist1.size());
        Assert.assertEquals(list.size(), list1.size());
        
    }
    
    
}
