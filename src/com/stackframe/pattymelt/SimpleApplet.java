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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JComponent;

/**
 * An applet which has just a console.
 *
 * @author mcculley
 */
public class SimpleApplet extends JApplet {

    @Override
    public void init() {
        String program = getParameter("program");
        InputStream inputStream = getClass().getResourceAsStream(program);
        try {
            final DCPU16 cpu = new DCPU16Emulator();
            DCPU16Utilities.load(inputStream, cpu.memory(), 0);
            Console console = new Console();
            cpu.install(console.getScreen(), 0x8000);
            cpu.install(console.getKeyboard(), 0x9000);
            JComponent screenWidget = console.getWidget();
            screenWidget.setBorder(BorderFactory.createEtchedBorder());
            add(screenWidget);

            Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        while (true) {
                            cpu.step();
                        }
                    } catch (IllegalOpcodeException ioe) {
                        System.err.printf("Illegal opcode 0x%04x encountered.\n", ioe.opcode);
                    }
                }
            };

            new Thread(r, "DCPU-16").start();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
