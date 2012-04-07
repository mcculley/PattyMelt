/*
 * Copyright 2012, Gene McCulley
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright 
 *   notice, this list of conditions and the following disclaimer in the 
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.stackframe.pattymelt;

import java.awt.Font;
import java.nio.ShortBuffer;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * A console for DCPU-16.
 *
 * @author mcculley
 */
public class Console {

    // FIXME: Add keyboard support
    private final ArrayMemory textRAM = new ArrayMemory(grid) {

        @Override
        public void put(int address, short value) {
            super.put(address, value);
            update();
        }
    };
    private final JTextArea textArea;
    private static final int numRows = 16, numColumns = 32, grid = numRows * numColumns;
    private final Peripheral screen = new Peripheral() {

        @Override
        public Memory memory() {
            return textRAM;
        }

        @Override
        public String name() {
            return "screen";
        }
    };

    public Console() {
        textArea = new JTextArea(numRows, numColumns);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
    }

    public JComponent getWidget() {
        return textArea;
    }

    private void update() {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0, col = 0; i < grid; i++, col++) {
            short word = textRAM.get(i);
            if (word != 0) {
                char c = (char) (word & 0x7f);
                int attributes = (word >> 7) & 0x1ff;
                // FIXME: We have no idea yet how to correctly interpret the color information.
                buf.append(c);
            } else {
                buf.append(' ');
            }

            if (col == numColumns - 1) {
                buf.append('\n');
                col = 0;
            }
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    textArea.setText(buf.toString());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Peripheral getScreen() {
        return screen;
    }
}
