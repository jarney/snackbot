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

package org.ensor.io.tty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * This class is a TTY device for the Linux platform
 * and takes care of putting the TTY device into RAW mode
 * so that it is suitable for sending binary commands to
 * for external device driver type operations.
 * @author jona
 */
class TTYLinux implements ITTY {

    private final OutputStream mOutputStream;
    private final InputStream mInputStream;

    private SerialPort mSerialPort;
    
    class SerialOutputStream extends OutputStream {

        @Override
        public void write(int i) throws IOException {
            try {
                mSerialPort.writeByte((byte) i);
            } catch (SerialPortException ex) {
                throw new IOException("Exception writing", ex);
            }
        }
    }
    class SerialInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            byte[] b;
            try {
                b = mSerialPort.readBytes(1);
            } catch (SerialPortException ex) {
                throw new IOException("Exception writing", ex);
            }
            if (b != null) {
                return b[0];
            }
            return -1;
        }
        
    }
    
    
    protected TTYLinux(final String aTTY) throws Exception {

        init(aTTY);

//        mSerialPort = new SerialPort(aTTY);
//        mSerialPort.openPort();
//        mSerialPort.setParams(SerialPort.BAUDRATE_9600,
//                SerialPort.DATABITS_8,
//                SerialPort.STOPBITS_1,
//                SerialPort.PARITY_NONE);
//
//        mOutputStream = new SerialOutputStream();
//        mInputStream = new SerialInputStream();
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
        commandList.add("-hupcl");
        commandList.add("eof");
        commandList.add("");
        commandList.add("intr");
        commandList.add("");
        commandList.add("erase");
        commandList.add("");
        commandList.add("kill");
        commandList.add("");
        commandList.add("start");
        commandList.add("");
        commandList.add("stop");
        commandList.add("");
        commandList.add("susp");
        commandList.add("");
        commandList.add("flush");
        commandList.add("");
        commandList.add("lnext");
        commandList.add("");
        commandList.add("werase");
        commandList.add("");
        commandList.add("rprnt");
        commandList.add("");
        commandList.add("quit");
        commandList.add("");
        commandList.add("crtscts");
        commandList.add("time");
        commandList.add("10");
        commandList.add("min");
        commandList.add("1");

        int rc = runCommand(commandList);

        if (rc != 0) {
            System.out.println("Return code " + rc);
            throw new
                IOException("Could not execute stty command to prepare device");
        }

    }

    private int runCommand(final List<String> s)
            throws IOException, InterruptedException {
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
