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
import javax.swing.AbstractListModel;

/**
 * Component parents list model.
 *
 * @version 0.1.0 2019/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class ComponentParentsListModel extends AbstractListModel<String> {

    private final List<RowRecord> items = new ArrayList<>();

    public void clear() {
        int size = items.size();
        items.clear();
        if (size > 0) {
            fireIntervalRemoved(this, 0, size - 1);
        }
    }

    public void add(String name, Object component) {
        int size = items.size();
        items.add(new RowRecord(name, component));
        fireIntervalAdded(this, size, size);
    }

    public Object getItemObject(int index) {
        return items.get(index).object;
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public String getElementAt(int index) {
        return items.get(index).name;
    }

    private static class RowRecord {

        String name;
        Object object;

        public RowRecord(String name, Object object) {
            this.name = name;
            this.object = object;
        }
    }
}
