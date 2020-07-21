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

/**
 * Parameters list table item record.
 *
 * @version 0.1.0 2019/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class PropertyTableItem {

    private String valueName;
    private String typeName;
    private Object value;

    public PropertyTableItem(String valueName, String typeName, Object value) {
        this.valueName = valueName;
        this.typeName = typeName;
        this.value = value;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public Object asBasicType() {
        if (value == null)
            return null;
        
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Integer) {
            return Integer.toString((Integer) value);
        } else if (value instanceof Long) {
            return Long.toString((Long) value);
        } else if (value instanceof Float) {
            return Float.toString((Float) value);
        } else if (value instanceof Double) {
            return Double.toString((Double) value);
        } else if (value instanceof Byte) {
            return Byte.toString((Byte) value);
        } else if (value instanceof Character) {
            return Character.toString((Character) value);
        } else if (value instanceof Boolean) {
            return Boolean.toString((Boolean) value);
        } else if (value instanceof Short) {
            return Short.toString((Short) value);
        }
        
        return value;
    }
}
