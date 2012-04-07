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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
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
    private static final int bufferSize = 16;
    private final LinkedList<Short> keyboardBuffer = new LinkedList<Short>();
    private final Memory textRAM = new ArrayMemory(grid) {

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
    private final Memory keyboardRAM = new ArrayMemory(1) {

        @Override
        public short get(int address) {
            if (keyboardBuffer.isEmpty()) {
                return 0;
            } else {
                return keyboardBuffer.removeFirst();
            }
        }
    };
    private final Peripheral keyboard = new Peripheral() {

        @Override
        public Memory memory() {
            return keyboardRAM;
        }

        @Override
        public String name() {
            return "keyboard";
        }
    };

    public Console() {
        textArea = new JTextArea(numRows, numColumns);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
        textArea.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }

            @Override
            public void keyTyped(KeyEvent ke) {
                if (keyboardBuffer.size() < bufferSize) {
                    // FIXME: Not sure if this is how this is supposed to work.
                    keyboardBuffer.add((short) ke.getKeyChar());
                }
            }
        });
    }

    public JComponent getWidget() {
        return textArea;
    }

    private void update() {
        if (SwingUtilities.isEventDispatchThread()) {
            updateOnSwingThread();
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        updateOnSwingThread();
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateOnSwingThread() {
        // FIXME: Be smarter than redrawing whole screen.
        StringBuilder buf = new StringBuilder();
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

        textArea.setText(buf.toString());
    }

    public Peripheral getScreen() {
        return screen;
    }

    public Peripheral getKeyboard() {
        return keyboard;
    }
}
