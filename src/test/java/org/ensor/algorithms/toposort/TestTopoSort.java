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

package org.ensor.algorithms.toposort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class TestTopoSort {
    
    class KLazz {
        private Class mClass;
        public KLazz(final Class aClass) {
            mClass = aClass;
        }
        public boolean equals(Object aObject) {
            if (aObject instanceof KLazz) {
                return mClass.equals(((KLazz)aObject).mClass);
            }
            else if (aObject instanceof Class) {
                return mClass.equals(aObject);
            }
            return false;
        }
        public int hashCode() {
            return 0;
        }
    }
    
    abstract class Module implements INode<KLazz> {
        public KLazz getNode() {
            return new KLazz(this.getClass());
        }
        public abstract KLazz[] getDeps();
        public List<KLazz> getDependencies() {
            return Arrays.asList(getDeps());
        }
    }

    class AModule extends Module {
        public final KLazz[] mDependentModules = { };
        public KLazz[] getDeps() {
            return mDependentModules;
        }
    }    
    class BModule extends Module {
        public final KLazz[] mDependentModules = { 
            new KLazz(AModule.class)
        };
        public KLazz[] getDeps() {
            return mDependentModules;
        }
    }    

    class CModule extends Module {
        public final KLazz[] mDependentModules = { 
            new KLazz(AModule.class),
            new KLazz(BModule.class)
        };
        public KLazz[] getDeps() {
            return mDependentModules;
        }
    }
    
    class C1Module extends Module {
        public final KLazz[] mDependentModules = { 
            new KLazz(C2Module.class)
        };
        public KLazz[] getDeps() {
            return mDependentModules;
        }
    }
    class C2Module extends Module {
        public final KLazz[] mDependentModules = { 
            new KLazz(C1Module.class)
        };
        public KLazz[] getDeps() {
            return mDependentModules;
        }
    }
    
    @Test
    public void testNormalSort() {
        AModule m1 = new AModule();
        BModule m2 = new BModule();
        CModule m3 = new CModule();
        
        TopologicalSort<KLazz> toposort = new TopologicalSort<KLazz>();
        List<INode<KLazz>> list1 = new ArrayList<INode<KLazz>>();
        list1.add(m1);
        list1.add(m2);
        list1.add(m3);
        
        List<KLazz> sort1 = toposort.getTopologicalSort(list1);
        
        Assert.assertEquals(sort1.get(0), AModule.class);
        Assert.assertEquals(sort1.get(1), BModule.class);
        Assert.assertEquals(sort1.get(2), CModule.class);
        
        List<INode<KLazz>> list2 = new ArrayList<INode<KLazz>>();
        list2.add(m3);
        list2.add(m2);
        list2.add(m1);
        
        List<KLazz> sort2 = toposort.getTopologicalSort(list2);
        
        Assert.assertEquals(sort2.get(0), AModule.class);
        Assert.assertEquals(sort2.get(1), BModule.class);
        Assert.assertEquals(sort2.get(2), CModule.class);
        
        List<INode<KLazz>> list3 = new ArrayList<INode<KLazz>>();
        list3.add(m2);
        list3.add(m3);
        list3.add(m1);
        
        List<KLazz> sort3 = toposort.getTopologicalSort(list3);
        
        Assert.assertEquals(sort3.get(0), AModule.class);
        Assert.assertEquals(sort3.get(1), BModule.class);
        Assert.assertEquals(sort3.get(2), CModule.class);
        
        List<INode<KLazz>> listWithCycles = new ArrayList<INode<KLazz>>();
        listWithCycles.add(new C1Module());
        listWithCycles.add(new C2Module());
        
        List<KLazz> cycleSort = toposort.getTopologicalSort(listWithCycles);
        
        Assert.assertNull(cycleSort);
        
        
    }
    
}
