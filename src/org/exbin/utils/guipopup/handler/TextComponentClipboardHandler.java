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
package org.exbin.utils.guipopup.handler;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import org.exbin.utils.guipopup.ActionUtils;
import org.exbin.utils.guipopup.ClipboardActionsHandler;

/**
 * Clipboard handler for JTextComponent.
 *
 * @version 0.1.1 2020/04/25
 * @author ExBin Project (http://exbin.org)
 */
public class TextComponentClipboardHandler implements ClipboardActionsHandler {
    
    private final JTextComponent txtComp;

    public TextComponentClipboardHandler(JTextComponent txtComp) {
        this.txtComp = txtComp;
    }

    @Override
    public void performCut() {
        ActionUtils.invokeTextAction(txtComp, DefaultEditorKit.cutAction);
    }

    @Override
    public void performCopy() {
        ActionUtils.invokeTextAction(txtComp, DefaultEditorKit.copyAction);
    }

    @Override
    public void performPaste() {
        ActionUtils.invokeTextAction(txtComp, DefaultEditorKit.pasteAction);
    }

    @Override
    public void performDelete() {
        ActionUtils.invokeTextAction(txtComp, DefaultEditorKit.deleteNextCharAction);
    }

    @Override
    public void performSelectAll() {
        txtComp.requestFocus();
        ActionUtils.invokeTextAction(txtComp, DefaultEditorKit.selectAllAction);
    }

    @Override
    public boolean isSelection() {
        return txtComp.isEnabled() && txtComp.getSelectionStart() != txtComp.getSelectionEnd();
    }

    @Override
    public boolean isEditable() {
        return txtComp.isEnabled() && txtComp.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return txtComp.isEnabled() && !txtComp.getText().isEmpty();
    }

    @Override
    public boolean canPaste() {
        return true;
    }
    
}
