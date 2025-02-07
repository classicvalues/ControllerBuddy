/* Copyright (C) 2020  Matteo Hausner
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package de.bwravencl.controllerbuddy.input.action.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.bwravencl.controllerbuddy.gui.EditActionsDialog;
import de.bwravencl.controllerbuddy.gui.Main;
import de.bwravencl.controllerbuddy.input.KeyStroke;
import de.bwravencl.controllerbuddy.input.ScanCode;
import de.bwravencl.controllerbuddy.input.action.IAction;

public final class KeystrokeEditorBuilder extends EditorBuilder {

	private static final class CheckboxJList<E> extends JList<E> {

		private static final class CheckboxListCellRenderer<E> extends JCheckBox implements ListCellRenderer<E> {

			private static final long serialVersionUID = -7958791166718006570L;

			@Override
			public Component getListCellRendererComponent(final JList<? extends E> list, final E value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				setComponentOrientation(list.getComponentOrientation());

				setFont(list.getFont());
				setText(String.valueOf(value));

				setBackground(list.getBackground());
				setForeground(list.getForeground());

				setSelected(isSelected);
				setEnabled(list.isEnabled());

				return this;
			}
		}

		private static final long serialVersionUID = 5413881551745215922L;

		private CheckboxJList(final E[] listData) {
			super(listData);

			setCellRenderer(new CheckboxListCellRenderer<>());

			for (final var mouseMotionListener : getMouseMotionListeners())
				if (mouseMotionListener instanceof ListSelectionListener)
					removeMouseMotionListener(mouseMotionListener);

			setSelectionModel(new DefaultListSelectionModel() {

				private static final long serialVersionUID = 8997996268575032389L;

				@Override
				public void setSelectionInterval(final int index0, final int index1) {
					if (super.isSelectedIndex(index0))
						super.removeSelectionInterval(index0, index1);
					else
						super.addSelectionInterval(index0, index1);
				}
			});
		}
	}

	private final class JListSetPropertyListSelectionListener implements ListSelectionListener {

		private final Method setterMethod;
		private final KeyStroke keyStroke;
		private final boolean modifiers;

		private JListSetPropertyListSelectionListener(final Method setterMethod, final KeyStroke keyStroke,
				final boolean modifiers) {
			this.setterMethod = setterMethod;
			this.keyStroke = keyStroke;
			this.modifiers = modifiers;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			try {
				final Set<Integer> scanCodes = new HashSet<>();

				((JList<?>) e.getSource()).getSelectedValuesList()
						.forEach(object -> scanCodes.add(ScanCode.nameToKeyCodeMap.get(object)));

				final var scanCodesArray = scanCodes.toArray(new Integer[scanCodes.size()]);

				if (modifiers)
					keyStroke.setModifierCodes(scanCodesArray);
				else
					keyStroke.setKeyCodes(scanCodesArray);

				setterMethod.invoke(action, keyStroke);

				final Set<Object> keyStrokeSet = new LinkedHashSet<>();
				if (modifierList != null)
					keyStrokeSet.addAll(modifierList.getSelectedValuesList());
				if (keyList != null)
					keyStrokeSet.addAll(keyList.getSelectedValuesList());

				keyStrokeTextArea
						.setText(keyStrokeSet.stream().map(Object::toString).collect(Collectors.joining(" + ")));
			} catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				log.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
	}

	private static final Logger log = Logger.getLogger(KeystrokeEditorBuilder.class.getName());

	private static int getListModelIndex(final ListModel<?> model, final Object value) {
		if (value == null)
			return -1;

		if (model instanceof final DefaultListModel<?> defaultListModel)
			return defaultListModel.indexOf(value);

		for (var i = 0; i < model.getSize(); i++)
			if (value.equals(model.getElementAt(i)))
				return i;

		return -1;
	}

	private CheckboxJList<?> modifierList;
	private CheckboxJList<?> keyList;
	private final JTextArea keyStrokeTextArea = new JTextArea();

	public KeystrokeEditorBuilder(final EditActionsDialog editActionsDialog, final IAction<?> action,
			final String fieldName, final Class<?> fieldType) throws NoSuchFieldException, SecurityException,
			NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(editActionsDialog, action, fieldName, fieldType);
	}

	@Override
	public void buildEditor(final JPanel parentPanel) {
		final var keystrokePanel = new JPanel(new BorderLayout(5, 5));
		parentPanel.add(keystrokePanel);

		final var keyStroke = (KeyStroke) initialValue;

		final var availableScanCodes = ScanCode.nameToKeyCodeMap.keySet();

		final var modifiersPanel = new JPanel();
		modifiersPanel.setLayout(new BoxLayout(modifiersPanel, BoxLayout.PAGE_AXIS));
		final var modifiersLabel = new JLabel(Main.strings.getString("MODIFIERS_LABEL"));
		modifiersLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		modifiersPanel.add(modifiersLabel);
		modifiersPanel.add(Box.createVerticalStrut(5));
		modifierList = new CheckboxJList<>(availableScanCodes.toArray(new String[availableScanCodes.size()]));
		modifierList.addListSelectionListener(new JListSetPropertyListSelectionListener(setterMethod, keyStroke, true));

		final var addedModifiers = new ArrayList<String>();
		for (final int c1 : keyStroke.getModifierCodes())
			addedModifiers.add(ScanCode.keyCodeToNameMap.get(c1));
		addedModifiers.forEach(s1 -> {
			final var index1 = getListModelIndex(modifierList.getModel(), s1);
			if (index1 >= 0)
				modifierList.addSelectionInterval(index1, index1);
		});

		final var modifiersScrollPane = new JScrollPane(modifierList);
		modifiersScrollPane.setPreferredSize(new Dimension(130, 200));
		modifiersPanel.add(modifiersScrollPane);
		keystrokePanel.add(modifiersPanel, BorderLayout.WEST);

		final var keysPanel = new JPanel();
		keysPanel.setLayout(new BoxLayout(keysPanel, BoxLayout.PAGE_AXIS));
		final var keysLabel = new JLabel(Main.strings.getString("KEYS_LABEL"));
		keysLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		keysPanel.add(keysLabel);
		keysPanel.add(Box.createVerticalStrut(5));
		keyList = new CheckboxJList<>(availableScanCodes.toArray(new String[availableScanCodes.size()]));
		keyList.addListSelectionListener(new JListSetPropertyListSelectionListener(setterMethod, keyStroke, false));

		final var addedKeys = new ArrayList<String>();
		for (final int c2 : keyStroke.getKeyCodes())
			addedKeys.add(ScanCode.keyCodeToNameMap.get(c2));
		addedKeys.forEach(s2 -> {
			final var index2 = getListModelIndex(keyList.getModel(), s2);
			if (index2 >= 0)
				keyList.addSelectionInterval(index2, index2);
		});

		final var keysScrollPane = new JScrollPane(keyList);
		keysScrollPane.setPreferredSize(new Dimension(130, 200));
		keysPanel.add(keysScrollPane);
		keystrokePanel.add(keysPanel, BorderLayout.EAST);

		keyStrokeTextArea.setLineWrap(true);
		keyStrokeTextArea.setWrapStyleWord(true);
		keyStrokeTextArea.setEditable(false);
		keyStrokeTextArea.setFocusable(false);
		keystrokePanel.add(keyStrokeTextArea, BorderLayout.SOUTH);
	}
}
