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
    
    abstract class Module implements INode<Class> {
        public Class getNode() {
            return this.getClass();
        }
        public abstract Class[] getDeps();
        public List<Class> getDependencies() {
            return Arrays.asList(getDeps());
        }
    }
    
    class AModule extends Module {
        public final Class[] mDependentModules = { };
        public Class[] getDeps() {
            return mDependentModules;
        }
    }    
    class BModule extends Module {
        public final Class[] mDependentModules = { AModule.class };
        public Class[] getDeps() {
            return mDependentModules;
        }
    }    

    class CModule extends Module {
        public final Class[] mDependentModules = { 
            AModule.class, BModule.class 
        };
        public Class[] getDeps() {
            return mDependentModules;
        }
    }
    
    class C1Module extends Module {
        public final Class[] mDependentModules = { 
            C2Module.class
        };
        public Class[] getDeps() {
            return mDependentModules;
        }
    }
    class C2Module extends Module {
        public final Class[] mDependentModules = { 
            C1Module.class
        };
        public Class[] getDeps() {
            return mDependentModules;
        }
    }
    
    @Test
    public void testNormalSort() {
        AModule m1 = new AModule();
        BModule m2 = new BModule();
        CModule m3 = new CModule();
        
        TopologicalSort<Class> toposort = new TopologicalSort<Class>();
        List<INode<Class>> list1 = new ArrayList<INode<Class>>();
        list1.add(m1);
        list1.add(m2);
        list1.add(m3);
        
        List<Class> sort1 = toposort.getTopologicalSort(list1);
        
        Assert.assertEquals(sort1.get(0), AModule.class);
        Assert.assertEquals(sort1.get(1), BModule.class);
        Assert.assertEquals(sort1.get(2), CModule.class);
        
        List<INode<Class>> list2 = new ArrayList<INode<Class>>();
        list2.add(m3);
        list2.add(m2);
        list2.add(m1);
        
        List<Class> sort2 = toposort.getTopologicalSort(list2);
        
        Assert.assertEquals(sort2.get(0), AModule.class);
        Assert.assertEquals(sort2.get(1), BModule.class);
        Assert.assertEquals(sort2.get(2), CModule.class);
        
        List<INode<Class>> list3 = new ArrayList<INode<Class>>();
        list3.add(m2);
        list3.add(m3);
        list3.add(m1);
        
        List<Class> sort3 = toposort.getTopologicalSort(list3);
        
        Assert.assertEquals(sort3.get(0), AModule.class);
        Assert.assertEquals(sort3.get(1), BModule.class);
        Assert.assertEquals(sort3.get(2), CModule.class);
        
        List<INode<Class>> listWithCycles = new ArrayList<INode<Class>>();
        listWithCycles.add(new C1Module());
        listWithCycles.add(new C2Module());
        
        List<Class> cycleSort = toposort.getTopologicalSort(listWithCycles);
        
        Assert.assertNull(cycleSort);
        
        
    }
    
}
