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

package org.ensor.robots.comm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/** 
 *
 * @author jona
 */
class TTYLinux implements ITTY {
    
    private final FileOutputStream mOutputStream;
    private final FileInputStream mInputStream;
    
    protected TTYLinux(final String aTTY) throws Exception {
        
        init(aTTY);
        
        mOutputStream = new FileOutputStream(aTTY);
        mInputStream = new FileInputStream(aTTY);
    }
    
    /**
     * Set the TTY to "raw" and give it a 500ms timeout.
     */
    private void init(final String aTTY)
            throws IOException,
            InterruptedException {
        List<String> commandList = new ArrayList<String>();
        
        commandList.add("stty");
        commandList.add("-F");
        commandList.add(aTTY);
        commandList.add("raw");
        commandList.add("time");
        commandList.add("10");
        commandList.add("min");
        commandList.add("0");
        
        int rc = runCommand(commandList);
        
        if (rc != 0) {
            System.out.println("Return code " + rc);
            throw new
                IOException("Could not execute stty command to prepare device");
        }
        
    }
    
    private int runCommand(List<String> s) throws IOException, InterruptedException {
        String [] command = new String[s.size()];
        command = s.toArray(command);
        Process p = Runtime.getRuntime().exec(command);
        int rc = p.waitFor();
        
        return rc;
    }
    
    
    public InputStream getInputStream() {
        return mInputStream;
    }
    
    public OutputStream getOutputStream() {
        return mOutputStream;
    }
    
}
