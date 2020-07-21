/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.utils.guipopup.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Parameters list table model for item editing.
 *
 * @version 0.1.0 2019/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class PropertyTableModel extends AbstractTableModel {

    private List<PropertyTableItem> items;

    private final String[] columnNames;
    private Class[] columnTypes = new Class[]{
        java.lang.String.class, java.lang.Object.class
    };
    private final boolean[] columnsEditable = new boolean[]{false, true};

    public PropertyTableModel() {
        columnNames = new String[]{"Property", "Value"};
        items = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getTypes()[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnsEditable[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return getRow(rowIndex).getValueName();
            case 1:
                return getRow(rowIndex);
            default:
                return "";
        }
    }

    public PropertyTableItem getRow(int rowIndex) {
        return items.get(rowIndex);
    }

    public void removeAll() {
        int size = items.size();
        items.clear();
        if (size > 0) {
            fireTableRowsDeleted(0, size - 1);
        }
    }

    public void removeRow(int rowIndex) {
        items.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void addRow(PropertyTableItem rowData) {
        items.add(rowData);
        fireTableRowsInserted(items.size() - 1, items.size() - 1);
    }

    public List<PropertyTableItem> getItems() {
        return items;
    }

    public void setItems(List<PropertyTableItem> attributes) {
        this.items = attributes;
    }

    public Class[] getTypes() {
        return columnTypes;
    }

    public void setTypes(Class[] types) {
        this.columnTypes = types;
    }
}
