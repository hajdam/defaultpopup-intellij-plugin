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

import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonPainter;
import org.exbin.framework.utils.WindowUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Empty property column gui with operation button.
 *
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ComponentPropertyTableCellPanel extends javax.swing.JPanel {

    private JComponent cellComponent;

    public ComponentPropertyTableCellPanel() {
        this(createEmptyCellComponent());
    }

    public ComponentPropertyTableCellPanel(JComponent cellComponent) {
        this.cellComponent = cellComponent;
        initComponents();
        init();
    }

    private void init() {
        add(cellComponent, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        editorButton = new JButton() {
            @Override
            public void updateUI() {
                super.updateUI();

                // Make button more compact even for PLAFs with big borders
                Border border = getBorder();
                if (border instanceof CompoundBorder) {
                    // setBorder(new CompoundBorder(((CompoundBorder) border).getOutsideBorder(), new EmptyBorder(0, 0, 0, 0)));
                    Border insideBorder = ((CompoundBorder) border).getInsideBorder();
                    if (insideBorder instanceof BasicBorders.MarginBorder) {
                        // ((BasicBorders.MarginBorder) insideBorder).
                        // setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new EmptyBorder(0, 0, 0, 0)));
                    }
                    Border outsideBorder = ((CompoundBorder) border).getOutsideBorder();
                    if (outsideBorder instanceof Object) {

                    }
                } else {
                    try {
                        Class.forName("com.intellij.ide.ui.laf.darcula.ui.DarculaButtonPainter");
                        if (border instanceof DarculaButtonPainter) {
                            putClientProperty("JButton.buttonType", "square");
                        }
                    } catch(ClassNotFoundException ex) {
                        // ignore
                    }
                }
            }
        };

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        editorButton.setText("..."); // NOI18N
        editorButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        editorButton.setName("editorButton"); // NOI18N
        add(editorButton, java.awt.BorderLayout.EAST);
    }// </editor-fold>

    /**
     * Test method for this gui.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new ComponentPropertyTableCellPanel());
    }

    @Nonnull
    public static JLabel createEmptyCellComponent() {
        JLabel label = new JLabel() {
            @Override
            public void updateUI() {
                super.updateUI();
                setFont(UIManager.getDefaults().getFont("TextField.font"));
            }
        };
        return label;
    }

    // Variables declaration - do not modify
    private javax.swing.JButton editorButton;
    // End of variables declaration

    public void setEditorAction(ActionListener actionListener) {
        editorButton.addActionListener(actionListener);
    }

    @Nonnull
    public JComponent getCellComponent() {
        return cellComponent;
    }
}
