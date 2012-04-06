/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.pattymelt;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author mcculley
 */
public class MemoryTableModel implements TableModel {

    private final short[] memory;
    private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();
    private static final int columns = 8;

    public MemoryTableModel(short[] memory) {
        this.memory = memory;
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
        return 65536 / columns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return String.format("%04X", rowIndex * columns);
        } else if (columnIndex == getColumnCount() - 1) {
            int address = rowIndex * columns;
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < columns; i++) {
                short value = memory[address + i];
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
            short value = memory[address];
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

    public void update() {
        for (TableModelListener l : listeners) {
            l.tableChanged(new TableModelEvent(this));
        }
    }
}
