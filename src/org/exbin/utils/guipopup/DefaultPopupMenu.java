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
package org.exbin.utils.guipopup;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.IdeGlassPaneImpl;
import org.exbin.utils.guipopup.handler.TextComponentClipboardHandler;
import org.exbin.utils.guipopup.handler.ListClipboardHandler;
import org.exbin.utils.guipopup.handler.TableClipboardHandler;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import org.exbin.utils.guipopup.gui.InspectComponentPanel;

/**
 * Utilities for default menu generation.
 *
 * @version 0.1.2 2022/07/22
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultPopupMenu {

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("org.exbin.utils.guipopup.GuiPopupMenu");

    public static final String EDIT_CUT_ACTION_ID = "editCutAction";
    public static final String EDIT_COPY_ACTION_ID = "editCopyAction";
    public static final String EDIT_PASTE_ACTION_ID = "editPasteAction";
    public static final String EDIT_DELETE_ACTION_ID = "editDeleteAction";
    public static final String EDIT_SELECT_ALL_ACTION_ID = "editSelectAllAction";

    private ActionMap defaultTextActionMap;
    private JPopupMenu defaultPopupMenu;
    private JPopupMenu defaultEditPopupMenu;
    private DefaultPopupClipboardAction defaultCutAction;
    private DefaultPopupClipboardAction defaultCopyAction;
    private DefaultPopupClipboardAction defaultPasteAction;
    private DefaultPopupClipboardAction defaultDeleteAction;
    private DefaultPopupClipboardAction defaultSelectAllAction;
    private DefaultPopupClipboardAction[] defaultTextActions;

    private final List<ComponentPopupEventDispatcher> clipboardEventDispatchers = new ArrayList<>();

    private static DefaultPopupMenu instance = null;

    private boolean registered = false;
    private boolean inspectMode = false;
//    private EventQueue systemEventQueue;
    private IdeEventQueue.EventDispatcher overriddenQueue;
    private IdeEventQueue.EventDispatcher overriddenPostQueue;

    private DefaultPopupMenu() {
    }

    @Nonnull
    public static synchronized DefaultPopupMenu getInstance() {
        if (instance == null) {
            instance = new DefaultPopupMenu();
            UIManager.addPropertyChangeListener(evt -> {
                updateUI();
            });
        }

        return instance;
    }

    /**
     * Registers default popup menu to AWT.
     */
    public static void register() {
        DefaultPopupMenu defaultPopupMenu = getInstance();
        if (!defaultPopupMenu.registered) {
            defaultPopupMenu.initDefaultPopupMenu();
            defaultPopupMenu.registerToEventQueue();
        }
    }

    /**
     * Registers default popup menu to AWT.
     *
     * @param resourceBundle resource bundle
     * @param resourceClass resource class
     */
    public static void register(ResourceBundle resourceBundle, Class resourceClass) {
        DefaultPopupMenu defaultPopupMenu = getInstance();
        defaultPopupMenu.initDefaultPopupMenu(resourceBundle, resourceClass);
        defaultPopupMenu.registerToEventQueue();
    }

    public static void updateUI() {
        if (instance != null) {
            if (instance.defaultPopupMenu != null) {
                SwingUtilities.updateComponentTreeUI(instance.defaultPopupMenu);

            }
            if (instance.defaultEditPopupMenu != null) {
                SwingUtilities.updateComponentTreeUI(instance.defaultEditPopupMenu);
            }
        }
    }

    private void registerToEventQueue() {
        overriddenQueue = new PopupEventQueue();
        overriddenPostQueue = new PopupEventPostQueue();
//        systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
//        systemEventQueue.push(overriddenQueue);
        IdeEventQueue instance = IdeEventQueue.getInstance();
        instance.addDispatcher(overriddenQueue, null);
        instance.addPostprocessor(overriddenPostQueue, null);
        registered = true;
    }

    public static void unregister() {
        DefaultPopupMenu defaultPopupMenu = getInstance();
        if (defaultPopupMenu.registered) {
            defaultPopupMenu.unregisterQueue();
        }
    }

    private void unregisterQueue() {
        IdeEventQueue instance = IdeEventQueue.getInstance();
        instance.removeDispatcher(overriddenQueue);
        instance.removePostprocessor(overriddenPostQueue);
//        overriddenQueue.push(systemEventQueue);
        registered = false;
    }

    private void initDefaultPopupMenu() {
        initDefaultPopupMenu(resourceBundle, this.getClass());
    }

    private void initDefaultPopupMenu(ResourceBundle resourceBundle, Class resourceClass) {
        defaultTextActionMap = new ActionMap();
        defaultCutAction = new DefaultPopupClipboardAction(DefaultEditorKit.cutAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performCut();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isEditable() && clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultCutAction, resourceBundle, resourceClass, EDIT_CUT_ACTION_ID);
        defaultCutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        defaultCutAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), defaultCutAction);

        defaultCopyAction = new DefaultPopupClipboardAction(DefaultEditorKit.copyAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performCopy();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultCopyAction, resourceBundle, resourceClass, EDIT_COPY_ACTION_ID);
        defaultCopyAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        defaultCopyAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME), defaultCopyAction);

        defaultPasteAction = new DefaultPopupClipboardAction(DefaultEditorKit.pasteAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performPaste();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isEditable());
            }
        };
        ActionUtils.setupAction(defaultPasteAction, resourceBundle, resourceClass, EDIT_PASTE_ACTION_ID);
        defaultPasteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
        defaultPasteAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME), defaultPasteAction);

        defaultDeleteAction = new DefaultPopupClipboardAction(DefaultEditorKit.deleteNextCharAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performDelete();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isEditable() && clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultDeleteAction, resourceBundle, resourceClass, EDIT_DELETE_ACTION_ID);
        defaultDeleteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        defaultDeleteAction.setEnabled(false);
        defaultTextActionMap.put("delete", defaultDeleteAction);

        defaultSelectAllAction = new DefaultPopupClipboardAction(DefaultEditorKit.selectAllAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performSelectAll();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.canSelectAll());
            }
        };
        ActionUtils.setupAction(defaultSelectAllAction, resourceBundle, resourceClass, EDIT_SELECT_ALL_ACTION_ID);
        defaultSelectAllAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        defaultTextActionMap.put("selectAll", defaultSelectAllAction);

        DefaultPopupClipboardAction[] actions = {defaultCutAction, defaultCopyAction, defaultPasteAction, defaultDeleteAction, defaultSelectAllAction};
        defaultTextActions = actions;

        buildDefaultPopupMenu();
        buildDefaultEditPopupMenu();
    }

    private void buildDefaultPopupMenu() {
        defaultPopupMenu = new JPopupMenu();

        defaultPopupMenu.setName("defaultPopupMenu"); // NOI18N
        fillDefaultPopupMenu(defaultPopupMenu, -1);
    }

    private void buildDefaultEditPopupMenu() {
        defaultEditPopupMenu = new JPopupMenu();

        defaultEditPopupMenu.setName("defaultEditPopupMenu"); // NOI18N
        fillDefaultEditPopupMenu(defaultEditPopupMenu, -1);
    }

    public void fillDefaultPopupMenu(JPopupMenu popupMenu, int position) {
        JMenuItem basicPopupCopyMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupSelectAllMenuItem = new javax.swing.JMenuItem();

        basicPopupCopyMenuItem.setAction(defaultCopyAction);
        basicPopupCopyMenuItem.setName("basicEditPopupCopyMenuItem"); // NOI18N
        basicPopupSelectAllMenuItem.setAction(defaultSelectAllAction);
        basicPopupSelectAllMenuItem.setName("basicEditPopupSelectAllMenuItem"); // NOI18N

        if (position >= 0) {
            popupMenu.insert(basicPopupCopyMenuItem, position);
            popupMenu.insert(new JPopupMenu.Separator(), position + 1);
            popupMenu.insert(basicPopupSelectAllMenuItem, position + 2);
        } else {
            popupMenu.add(basicPopupCopyMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(basicPopupSelectAllMenuItem);
        }
    }

    public void fillDefaultEditPopupMenu(JPopupMenu popupMenu, int position) {
        JMenuItem basicPopupCutMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupCopyMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupPasteMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupDeleteMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupSelectAllMenuItem = new javax.swing.JMenuItem();

        basicPopupCutMenuItem.setAction(defaultCutAction);
        basicPopupCutMenuItem.setName("basicPopupCutMenuItem");
        basicPopupCopyMenuItem.setAction(defaultCopyAction);
        basicPopupCopyMenuItem.setName("basicPopupCopyMenuItem");
        basicPopupPasteMenuItem.setAction(defaultPasteAction);
        basicPopupPasteMenuItem.setName("basicPopupPasteMenuItem");
        basicPopupDeleteMenuItem.setAction(defaultDeleteAction);
        basicPopupDeleteMenuItem.setName("basicPopupDeleteMenuItem");
        basicPopupSelectAllMenuItem.setAction(defaultSelectAllAction);
        basicPopupSelectAllMenuItem.setName("basicPopupSelectAllMenuItem");

        if (position >= 0) {
            popupMenu.insert(basicPopupCutMenuItem, position);
            popupMenu.insert(basicPopupCopyMenuItem, position + 1);
            popupMenu.insert(basicPopupPasteMenuItem, position + 2);
            popupMenu.insert(basicPopupDeleteMenuItem, position + 3);
            popupMenu.insert(new JPopupMenu.Separator(), position + 4);
            popupMenu.insert(basicPopupSelectAllMenuItem, position + 5);
        } else {
            popupMenu.add(basicPopupCutMenuItem);
            popupMenu.add(basicPopupCopyMenuItem);
            popupMenu.add(basicPopupPasteMenuItem);
            popupMenu.add(basicPopupDeleteMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(basicPopupSelectAllMenuItem);
        }
    }

    public void addClipboardEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        clipboardEventDispatchers.add(dispatcher);
    }

    public void removeClipboardEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        clipboardEventDispatchers.remove(dispatcher);
    }

    @ParametersAreNonnullByDefault
    public class PopupEventQueue implements IdeEventQueue.EventDispatcher {

        @Override
        public boolean dispatch(AWTEvent event) {
            if (event.getID() == MouseEvent.MOUSE_MOVED && inspectMode) {
                inspectMode = false;
                MouseEvent mouseEvent = (MouseEvent) event;
                Component component = getSource(mouseEvent);
                InspectComponentPanel inspectComponentPanel = new InspectComponentPanel();
                inspectComponentPanel.setComponent(component, null);
                Frame mainWindow = WindowManager.getInstance().getFrame(ProjectManager.getInstance().getDefaultProject());
                final WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(inspectComponentPanel, mainWindow, "Inspect Component", Dialog.ModalityType.MODELESS);
                inspectComponentPanel.setCloseActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.close();
                    }
                });
                dialog.show();
                return true;
            }

            return false;
        }
    }

    @ParametersAreNonnullByDefault
    public class PopupEventPostQueue implements IdeEventQueue.EventDispatcher {

        @Override
        public boolean dispatch(AWTEvent event) {
        //            super.dispatchEvent(event);

            if (event.getID() == MouseEvent.MOUSE_RELEASED || event.getID() == MouseEvent.MOUSE_PRESSED) {
                MouseEvent mouseEvent = (MouseEvent) event;

                if (mouseEvent.isPopupTrigger()) {
                    if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                        // Menu was already created
                        return false;
                    }

                    Component component = getSource(mouseEvent);
                    if (component instanceof JViewport) {
                        component = ((JViewport) component).getView();
                    }

                    if (component instanceof JTextComponent) {
                        activateMousePopup(mouseEvent, component, new TextComponentClipboardHandler((JTextComponent) component));
                    } else if (component instanceof JList) {
                        activateMousePopup(mouseEvent, component, new ListClipboardHandler((JList) component));
                    } else if (component instanceof JTable) {
                        activateMousePopup(mouseEvent, component, new TableClipboardHandler((JTable) component));
                    }
                }
            } else if (event.getID() == KeyEvent.KEY_PRESSED) {
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.getKeyCode() == KeyEvent.VK_F12 && keyEvent.isShiftDown() && keyEvent.isAltDown() && (keyEvent.isControlDown() || keyEvent.isMetaDown())) {
                    // Unable to infer component from mouse position, so simulate click instead
                    inspectMode = true;
                    try {
                        Robot robot = new Robot();
                        Point location = MouseInfo.getPointerInfo().getLocation();
                        robot.mouseMove(location.x, location.y);
                    } catch (AWTException ex) {
                        Logger.getLogger(DefaultPopupMenu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_CONTEXT_MENU || (keyEvent.getKeyCode() == KeyEvent.VK_F10 && keyEvent.isShiftDown())) {
                    if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                        // Menu was already created
                        return false;
                    }

                    Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                    if (component instanceof JTextComponent) {
                        Point point;
                        try {
                            Rectangle relativeRect = ((JTextComponent) component).modelToView(((JTextComponent) component).getCaretPosition());
                            point = relativeRect == null ? null : new Point(relativeRect.x + relativeRect.width, relativeRect.y + relativeRect.height);
                        } catch (BadLocationException ex) {
                            point = null;
                        }
                        activateKeyPopup(component, point, new TextComponentClipboardHandler((JTextComponent) component));
                    } else if (component instanceof JList) {
                        Point point = null;
                        int selectedIndex = ((JList) component).getSelectedIndex();
                        if (selectedIndex >= 0) {
                            Rectangle cellBounds = ((JList) component).getCellBounds(selectedIndex, selectedIndex);
                            point = new Point(component.getWidth() / 2, cellBounds.y);
                        }
                        activateKeyPopup(component, point, new ListClipboardHandler((JList) component));
                    } else if (component instanceof JTable) {
                        Point point = null;
                        int selectedRow = ((JTable) component).getSelectedRow();
                        if (selectedRow >= 0) {
                            int selectedColumn = ((JTable) component).getSelectedColumn();
                            if (selectedColumn < -1) {
                                selectedColumn = 0;
                            }
                            Rectangle cellBounds = ((JTable) component).getCellRect(selectedRow, selectedColumn, false);
                            point = new Point(cellBounds.x, cellBounds.y);
                        }
                        activateKeyPopup(component, point, new TableClipboardHandler((JTable) component));
                    }
                }
            }

            return false;
        }

        private void activateMousePopup(MouseEvent mouseEvent, Component component, ClipboardActionsHandler clipboardHandler) {
            for (DefaultPopupClipboardAction action : defaultTextActions) {
                action.setClipboardHandler(clipboardHandler);
            }

            Point point = mouseEvent.getLocationOnScreen();
            Point locationOnScreen = component.getLocationOnScreen();
            point.translate(-locationOnScreen.x, -locationOnScreen.y);

            showPopupMenu(component, point, clipboardHandler.isEditable());
        }

        private void activateKeyPopup(Component component, @Nullable Point point, ClipboardActionsHandler clipboardHandler) {
            for (DefaultPopupClipboardAction action : defaultTextActions) {
                action.setClipboardHandler(clipboardHandler);
            }

            if (point == null) {
                if (component.getParent() instanceof ScrollPane) {
                    // TODO
                    point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                } else {
                    point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                }
            }

            showPopupMenu(component, point, clipboardHandler.isEditable());
        }

        private void showPopupMenu(Component component, Point point, boolean editable) {
            if (editable) {
                defaultEditPopupMenu.show(component, (int) point.getX(), (int) point.getY());
            } else {
                defaultPopupMenu.show(component, (int) point.getX(), (int) point.getY());
            }
        }
    }

    private static Component getSource(MouseEvent e) {
        return getDeepestComponent(e.getComponent(), e.getX(), e.getY());
    }

    private static Component getDeepestComponent(Component parentComponent, int x, int y) {
        Component component = SwingUtilities.getDeepestComponentAt(parentComponent, x, y);

        // Workaround for buggy gui
        if (component instanceof IdeGlassPaneImpl) {
            try {
                Field myRootPane = component.getClass().getDeclaredField("myRootPane");
                myRootPane.setAccessible(true);
                JRootPane rootPane = (JRootPane) myRootPane.get(component);
                final Point lpPoint = SwingUtilities.convertPoint(parentComponent, new Point(x, y), component);
                return getDeepestComponent(rootPane.getContentPane(), lpPoint.x, lpPoint.y);
            } catch (NoSuchFieldException | IllegalAccessException e1) {
                // Cannot serve
            }
//                final Point lpPoint = SwingUtilities.convertPoint(parentComponent, e.getPoint(), component);

//                int componentCount = ((IdeGlassPaneEx) component).getComponentCount();
//                for (int i = 0; i < componentCount; i++) {
//                    Component subComponent = ((IdeGlassPaneEx) component).getComponent(i);
//                    if (subComponent.contains(e.getX(), e.getY())) {
//                        return getDeepestComponent(subComponent, e);
//                    }
//                }
//                component = SwingUtilities.getDeepestComponentAt(component, lpPoint.x, lpPoint.y);
        }
        return component;
    }

    /**
     * Clipboard action for default popup menu.
     */
    @ParametersAreNullableByDefault
    private static abstract class DefaultPopupClipboardAction extends AbstractAction {

        protected ClipboardActionsHandler clipboardHandler;

        public DefaultPopupClipboardAction(String name) {
            super(name);
        }

        public void setClipboardHandler(ClipboardActionsHandler clipboardHandler) {
            this.clipboardHandler = clipboardHandler;
            postTextComponentInitialize();
        }

        protected abstract void postTextComponentInitialize();
    }
}
