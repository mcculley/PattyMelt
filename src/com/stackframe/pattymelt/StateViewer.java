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

import javax.swing.*;

/**
 * A GUI to peek into the state of the virtual machine.
 *
 * @author mcculley
 */
public class StateViewer {

    // FIXME: Add a pause/resume button
    // FIXME: Add ability to adjust registers
    private final DCPU16 cpu;
    private final Box box;
    private final JTextField pcField = new JTextField(4);
    private final JTextField spField = new JTextField(4);
    private final JTextField oField = new JTextField(4);
    private final JTextField[] registerFields = new JTextField[DCPU16.Register.values().length];

    public StateViewer(DCPU16 cpu) {
        this.cpu = cpu;
        box = Box.createVerticalBox();
        JComponent pcBox = new JPanel();
        box.add(pcBox);
        JLabel pcLabel = new JLabel("PC:");
        pcBox.add(pcLabel);
        pcField.setEditable(false);
        pcBox.add(pcField);

        pcBox.add(new JSeparator());
        pcBox.add(new JLabel("SP:"));
        spField.setEditable(false);
        pcBox.add(spField);

        pcBox.add(new JSeparator());
        pcBox.add(new JLabel("O:"));
        oField.setEditable(false);
        pcBox.add(oField);

        JComponent registerBox = new JPanel();
        box.add(registerBox);

        for (DCPU16.Register r : DCPU16.Register.values()) {
            registerBox.add(new JLabel(r.name() + ":"));
            JTextField registerField = new JTextField(4);
            registerField.setEditable(false);
            registerBox.add(registerField);
            registerFields[r.ordinal()] = registerField;
        }
    }

    public JComponent getWidget() {
        return box;
    }

    public void update() {
        pcField.setText(String.format("%04x", cpu.PC()));
        spField.setText(String.format("%04x", cpu.SP()));
        oField.setText(String.format("%04x", cpu.O()));
        for (DCPU16.Register r : DCPU16.Register.values()) {
            JTextField registerField = registerFields[r.ordinal()];
            registerField.setText(String.format("%04x", cpu.register(r)));
        }
    }
}
