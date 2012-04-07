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

import com.stackframe.pattymelt.DCPU16.CPUEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * An implementation of TableModel that allows one to peek at memory of DCPU-16.
 *
 * @author mcculley
 */
public class MemoryTableModel implements TableModel {

    // FIXME: Add a column for a disassembled view? Or make that a separate window?
    private final Memory memory;
    private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();
    private static final int columns = 8;

    public MemoryTableModel(DCPU16 cpu) {
        cpu.addListener(new DCPU16.CPUEventListener() {

            @Override
            public void instructionExecuted(CPUEvent event) {
            }

            @Override
            public void memoryModified(CPUEvent event) {
                for (TableModelListener l : listeners) {
                    // FIXME: Notify about single cell
                    l.tableChanged(new TableModelEvent(MemoryTableModel.this));
                }
            }
        });
        this.memory = cpu.memory();
    }

    @Override
    public void addTableModelListener(TableModelListener tl) {
        listeners.add(tl);
    }

    @Override
    public Class<?> getColumnClass(int i) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return 1 + columns + 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return null;
        } else if (columnIndex == getColumnCount() - 1) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < columns; i++) {
                buf.append(String.format("%X", i));
            }

            return buf.toString();
        } else {
            return String.format("%X", columnIndex - 1);
        }
    }

    @Override
    public int getRowCount() {
        return memory.size() / columns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return String.format("%04X", rowIndex * columns);
        } else if (columnIndex == getColumnCount() - 1) {
            int address = rowIndex * columns;
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < columns; i++) {
                short value = memory.get(address + i);
                char c = (char) (value & 0x7F);
                if (c < 0x20 || c > 0x7e) {
                    c = '.';
                }

                buf.append(c);
            }

            return buf.toString();
        } else {
            columnIndex--;
            int address = rowIndex * columns + columnIndex;
            short value = memory.get(address);
            return String.format("%04X", value);
        }
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    @Override
    public void removeTableModelListener(TableModelListener tl) {
        listeners.remove(tl);
    }

    @Override
    public void setValueAt(Object o, int i, int i1) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
