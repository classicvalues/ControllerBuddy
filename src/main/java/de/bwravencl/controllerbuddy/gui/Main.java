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

package de.bwravencl.controllerbuddy.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultFormatter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.constants.XMLConstants;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Configuration;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGDocument;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.WinDef.UINT;

import de.bwravencl.controllerbuddy.gui.GuiUtils.FrameDragListener;
import de.bwravencl.controllerbuddy.input.Input;
import de.bwravencl.controllerbuddy.input.Input.VirtualAxis;
import de.bwravencl.controllerbuddy.input.Mode;
import de.bwravencl.controllerbuddy.input.OverlayAxis;
import de.bwravencl.controllerbuddy.input.Profile;
import de.bwravencl.controllerbuddy.input.action.AxisToRelativeAxisAction;
import de.bwravencl.controllerbuddy.input.action.IAction;
import de.bwravencl.controllerbuddy.json.ActionTypeAdapter;
import de.bwravencl.controllerbuddy.json.ColorTypeAdapter;
import de.bwravencl.controllerbuddy.json.ModeAwareTypeAdapterFactory;
import de.bwravencl.controllerbuddy.output.ClientOutput;
import de.bwravencl.controllerbuddy.output.LocalOutput;
import de.bwravencl.controllerbuddy.output.Output;
import de.bwravencl.controllerbuddy.output.ServerOutput;
import de.bwravencl.controllerbuddy.output.VJoyOutput;
import de.bwravencl.controllerbuddy.version.Version;
import de.bwravencl.controllerbuddy.version.VersionUtils;
import in.pratanumandal.unique4j.Unique4j;
import in.pratanumandal.unique4j.Unique4jList;
import in.pratanumandal.unique4j.exception.Unique4jException;

public final class Main {

	private final class ChangeVJoyDirectoryAction extends AbstractAction {

		private static final long serialVersionUID = -7672382299595684105L;

		private ChangeVJoyDirectoryAction() {
			putValue(NAME, "...");
			putValue(SHORT_DESCRIPTION, strings.getString("CHANGE_VJOY_DIRECTORY_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final var vJoyDirectoryFileChooser = new JFileChooser(
					preferences.get(PREFERENCES_VJOY_DIRECTORY, VJoyOutput.getDefaultInstallationPath()));
			vJoyDirectoryFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (vJoyDirectoryFileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
				return;

			final var vjoyDirectory = vJoyDirectoryFileChooser.getSelectedFile();
			final var dllFile = new File(vjoyDirectory,
					VJoyOutput.getArchFolderName() + File.separator + VJoyOutput.LIBRARY_FILENAME);
			if (dllFile.exists()) {
				final var vjoyPath = vjoyDirectory.getAbsolutePath();
				preferences.put(PREFERENCES_VJOY_DIRECTORY, vjoyPath);
				vJoyDirectoryLabel.setText(vjoyPath);
			} else
				GuiUtils.showMessageDialog(frame,
						MessageFormat.format(strings.getString("INVALID_VJOY_DIRECTORY_DIALOG_TEXT"),
								VJoyOutput.getDefaultInstallationPath()),
						strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
		}
	}

	public static record ControllerInfo(int jid, String name, String guid) {

		private ControllerInfo(final int jid) {
			this(jid, GLFW.glfwGetGamepadName(jid), GLFW.glfwGetJoystickGUID(jid));
		}
	}

	private final class DisplayIndicatorAction extends AbstractAction {

		private static final long serialVersionUID = 3316770144012465987L;

		private final VirtualAxis virtualAxis;

		private DisplayIndicatorAction(final VirtualAxis virtualAxis) {
			this.virtualAxis = virtualAxis;

			putValue(NAME, strings.getString("DISPLAY_INDICATOR_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("DISPLAY_INDICATOR_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (((JCheckBox) e.getSource()).isSelected())
				input.getProfile().getVirtualAxisToOverlayAxisMap().put(virtualAxis, new OverlayAxis());
			else
				input.getProfile().getVirtualAxisToOverlayAxisMap().remove(virtualAxis);

			setUnsavedChanges(true);
			updateOverlayPanel();
		}
	}

	private final class ExportAction extends AbstractAction {

		private static final long serialVersionUID = -6582801831348704984L;

		private ExportAction() {
			putValue(NAME, strings.getString("EXPORT_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("EXPORT_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final var htmlFileChooser = new HtmlFileChooser(currentFile);

			if (htmlFileChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION)
				return;

			exportVisualization(htmlFileChooser.getSelectedFile());
		}
	}

	public enum HotSwappingButton {

		None(-1, "NONE"), A(GLFW.GLFW_GAMEPAD_BUTTON_A, "A_BUTTON"), B(GLFW.GLFW_GAMEPAD_BUTTON_B, "B_BUTTON"),
		X(GLFW.GLFW_GAMEPAD_BUTTON_X, "X_BUTTON"), Y(GLFW.GLFW_GAMEPAD_BUTTON_Y, "Y_BUTTON"),
		LEFT_BUMPER(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, "LEFT_BUMPER"),
		RIGHT_BUMPER(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, "RIGHT_BUMPER"),
		BACK(GLFW.GLFW_GAMEPAD_BUTTON_BACK, "BACK_BUTTON"), START(GLFW.GLFW_GAMEPAD_BUTTON_START, "START_BUTTON"),
		GUIDE(GLFW.GLFW_GAMEPAD_BUTTON_GUIDE, "GUIDE_BUTTON"),
		LEFT_THUMB(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB, "LEFT_THUMB"),
		RIGHT_THUMB(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB, "RIGHT_THUMB"),
		DPAD_UP(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, "DPAD_UP"),
		DPAD_RIGHT(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, "DPAD_RIGHT"),
		DPAD_DOWN(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, "DPAD_DOWN"),
		DPAD_LEFT(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, "DPAD_LEFT");

		private static HotSwappingButton getById(final int id) {
			for (final var hotSwappingButton : EnumSet.allOf(HotSwappingButton.class))
				if (hotSwappingButton.id == id)
					return hotSwappingButton;

			return None;
		}

		public final int id;

		private final String label;

		HotSwappingButton(final int id, final String labelKey) {
			this.id = id;
			label = Main.strings.getString(labelKey);
		}

		@Override
		public String toString() {
			return label;
		}
	}

	private static final class HtmlFileChooser extends JFileChooser {

		private static final long serialVersionUID = -1707951153902772391L;

		private HtmlFileChooser(final File profileFile) {
			setFileFilter(new FileNameExtensionFilter(strings.getString("HTML_FILE_DESCRIPTION"), "htm", "html"));

			String filename;
			if (profileFile != null) {
				filename = profileFile.getName();
				filename = filename.substring(0, filename.lastIndexOf('.'));
			} else
				filename = "*";

			setSelectedFile(new File(filename + ".html"));
		}

		@Override
		public void approveSelection() {
			final var file = getSelectedFile();
			if (file.exists() && getDialogType() == SAVE_DIALOG) {
				final var result = JOptionPane.showConfirmDialog(this,
						MessageFormat.format(file.getName(), strings.getString("FILE_EXISTS_DIALOG_TEXT")),
						strings.getString("FILE_EXISTS_DIALOG_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION);
				switch (result) {
				case JOptionPane.CANCEL_OPTION:
					cancelSelection();
				case JOptionPane.NO_OPTION, JOptionPane.CLOSED_OPTION:
					return;
				default:
					break;
				}
			}
			super.approveSelection();
		}
	}

	private static final class IndicatorProgressBar extends JProgressBar {

		private static final long serialVersionUID = 8167193907929992395L;

		private final HashSet<Float> detentValues;
		private final OverlayAxis overlayAxis;

		private IndicatorProgressBar(final int orient, final HashSet<Float> dententValues,
				final OverlayAxis overlayAxis) {
			super(orient);
			detentValues = dententValues;
			this.overlayAxis = overlayAxis;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);

			final var width = getWidth();
			final var height = getHeight();

			final var subdivisions = 3;
			for (var i = 1; i <= subdivisions; i++) {
				g.setColor(Color.WHITE);
				final var y = i * (height / (subdivisions + 1));
				g.drawLine(0, y, width, y);
			}

			detentValues.forEach(detentValue -> {
				g.setColor(Color.RED);
				final var y = (int) Input.normalize(detentValue, -1f, 1f, 0, height);
				g.drawLine(0, y, width, y);
			});
		}

		@Override
		public void setMaximum(final int n) {
			if (overlayAxis.inverted)
				super.setMinimum(-n);
			else
				super.setMaximum(n);
		}

		@Override
		public void setMinimum(final int n) {
			if (overlayAxis.inverted)
				super.setMaximum(-n);
			else
				super.setMinimum(n);
		}

		@Override
		public void setValue(final int n) {
			super.setValue(overlayAxis.inverted ? -n : n);
		}
	}

	private final class InvertIndicatorAction extends AbstractAction {

		private static final long serialVersionUID = 3316770144012465987L;

		private final VirtualAxis virtualAxis;

		private InvertIndicatorAction(final VirtualAxis virtualAxis) {
			this.virtualAxis = virtualAxis;

			putValue(NAME, strings.getString("INVERT_INDICATOR_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("INVERT_INDICATOR_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			input.getProfile().getVirtualAxisToOverlayAxisMap().get(virtualAxis).inverted = ((JCheckBox) e.getSource())
					.isSelected();

			setUnsavedChanges(true);
			updateOverlayPanel();
		}
	}

	private static record JsonContext(Gson gson, ActionTypeAdapter actionTypeAdapter) {

		private static JsonContext create() {
			final var actionAdapter = new ActionTypeAdapter();
			final var gson = new GsonBuilder().registerTypeAdapterFactory(new ModeAwareTypeAdapterFactory())
					.registerTypeAdapter(IAction.class, actionAdapter)
					.registerTypeAdapter(Color.class, new ColorTypeAdapter()).setPrettyPrinting().create();

			return new JsonContext(gson, actionAdapter);
		}
	}

	private final class NewAction extends UnsavedChangesAwareAction {

		private static final long serialVersionUID = 5703987691203427504L;

		private NewAction() {
			putValue(NAME, strings.getString("NEW_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("NEW_ACTION_DESCRIPTION"));
		}

		@Override
		protected void doAction(final ActionEvent e) {
			newProfile();
		}
	}

	private final class NewModeAction extends AbstractAction {

		private static final long serialVersionUID = -4881923833724315489L;

		private NewModeAction() {
			putValue(NAME, strings.getString("NEW_MODE_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("NEW_MODE_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final var mode = new Mode();
			input.getProfile().getModes().add(mode);

			setUnsavedChanges(true);
			updateModesPanel();

			final var verticalScrollBar = modesScrollPane.getVerticalScrollBar();
			verticalScrollBar.setValue(verticalScrollBar.getMaximum());
		}
	}

	private final class OpenAction extends UnsavedChangesAwareAction {

		private static final long serialVersionUID = -8932510785275935297L;

		private OpenAction() {
			putValue(NAME, strings.getString("OPEN_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("OPEN_ACTION_DESCRIPTION"));
		}

		@Override
		protected void doAction(final ActionEvent e) {
			if (profileFileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
				loadProfile(profileFileChooser.getSelectedFile(), false);
		}
	}

	private enum OutputType {
		NONE, LOCAL, CLIENT, SERVER
	}

	private static final class ProfileFileChooser extends JFileChooser {

		private static final long serialVersionUID = -4669170626378955605L;

		private ProfileFileChooser() {
			setFileFilter(
					new FileNameExtensionFilter(strings.getString("PROFILE_FILE_DESCRIPTION"), PROFILE_FILE_EXTENSION));
			setSelectedFile(new File(PROFILE_FILE_SUFFIX));
		}

		@Override
		public void approveSelection() {
			final var file = getSelectedFile();
			if (file.exists() && getDialogType() == SAVE_DIALOG) {
				final var result = JOptionPane.showConfirmDialog(this,
						MessageFormat.format(file.getName(), strings.getString("FILE_EXISTS_DIALOG_TEXT")),
						strings.getString("FILE_EXISTS_DIALOG_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION);
				switch (result) {
				case JOptionPane.CANCEL_OPTION:
					cancelSelection();
				case JOptionPane.NO_OPTION, JOptionPane.CLOSED_OPTION:
					return;
				default:
					break;
				}
			}
			super.approveSelection();
		}
	}

	private final class QuitAction extends UnsavedChangesAwareAction {

		private static final long serialVersionUID = 8952460723177800923L;

		private QuitAction() {
			putValue(NAME, strings.getString("QUIT_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("QUIT_ACTION_DESCRIPTION"));
		}

		@Override
		protected void doAction(final ActionEvent e) {
			quit();
		}
	}

	private final class RemoveModeAction extends AbstractAction {

		private static final long serialVersionUID = -1056071724769862582L;

		private final Mode mode;

		private RemoveModeAction(final Mode mode) {
			this.mode = mode;

			putValue(NAME, strings.getString("REMOVE_MODE_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION,
					MessageFormat.format(strings.getString("REMOVE_MODE_ACTION_DESCRIPTION"), mode.getDescription()));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			input.getProfile().removeMode(input, mode);
			setUnsavedChanges(true);
			updateModesPanel();
			updateVisualizationPanel();
		}
	}

	private final class SaveAction extends AbstractAction {

		private static final long serialVersionUID = -8469921697479550983L;

		private SaveAction() {
			putValue(NAME, strings.getString("SAVE_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("SAVE_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			saveProfile();
		}
	}

	private final class SaveAsAction extends AbstractAction {

		private static final long serialVersionUID = -8469921697479550983L;

		private SaveAsAction() {
			putValue(NAME, strings.getString("SAVE_AS_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("SAVE_AS_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			saveProfileAs();
		}
	}

	private final class SelectControllerAction extends AbstractAction {

		private static final long serialVersionUID = -2043467156713598592L;

		private final ControllerInfo controller;

		private SelectControllerAction(final ControllerInfo controller) {
			this.controller = controller;

			putValue(NAME, controller.name);
			putValue(SHORT_DESCRIPTION,
					MessageFormat.format(strings.getString("SELECT_CONTROLLER_ACTION_DESCRIPTION"), controller.name));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (selectedController != null && selectedController.jid == controller.jid)
				return;

			setSelectedControllerAndUpdateInput(controller, input.isInitialized() ? input.getAxes() : null);
			restartLast();
		}
	}

	private final class SelectIndicatorColorAction extends AbstractAction {

		private static final long serialVersionUID = 3316770144012465987L;

		private final VirtualAxis virtualAxis;

		private SelectIndicatorColorAction(final VirtualAxis virtualAxis) {
			this.virtualAxis = virtualAxis;

			putValue(NAME, strings.getString("CHANGE_INDICATOR_COLOR_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("CHANGE_INDICATOR_COLOR_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final var overlayAxis = input.getProfile().getVirtualAxisToOverlayAxisMap().get(virtualAxis);

			final var newColor = JColorChooser.showDialog(frame, "Choose Background Color", overlayAxis.color);
			if (newColor != null)
				overlayAxis.color = newColor;

			setUnsavedChanges(true);
			updateOverlayPanel();
		}
	}

	private final class SetHostAction extends AbstractAction implements FocusListener {

		private static final long serialVersionUID = -7674562782751876814L;

		private final JTextField hostTextField;

		private SetHostAction(final JTextField hostTextField) {
			this.hostTextField = hostTextField;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			setHost();
		}

		@Override
		public void focusGained(final FocusEvent e) {
		}

		@Override
		public void focusLost(final FocusEvent e) {
			setHost();
		}

		private void setHost() {
			final var host = hostTextField.getText();

			if (host != null && host.length() > 0)
				preferences.put(PREFERENCES_HOST, host);
			else
				hostTextField.setText(preferences.get(PREFERENCES_HOST, ClientOutput.DEFAULT_HOST));
		}
	}

	private final class SetHotSwapButtonAction extends AbstractAction {

		private static final long serialVersionUID = 6854936097922617928L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			final var hotSwappingButton = (HotSwappingButton) ((JComboBox<?>) e.getSource()).getSelectedItem();
			preferences.putInt(PREFERENCES_HOT_SWAPPING_BUTTON, hotSwappingButton.id);
		}
	}

	private final class SetModeDescriptionAction extends AbstractAction implements DocumentListener {

		private static final long serialVersionUID = -6706537047137827688L;

		private final Mode mode;
		private final JTextField modeDescriptionTextField;

		private SetModeDescriptionAction(final Mode mode, final JTextField modeDescriptionTextField) {
			this.mode = mode;
			this.modeDescriptionTextField = modeDescriptionTextField;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			setModeDescription();
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			setModeDescription();
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			setModeDescription();
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			setModeDescription();
		}

		private void setModeDescription() {
			final var description = modeDescriptionTextField.getText();

			if (description != null && description.length() > 0) {
				mode.setDescription(description);
				setUnsavedChanges(true);
			}
		}
	}

	private final class ShowAboutDialogAction extends AbstractAction {

		private static final long serialVersionUID = -2578971543384483382L;

		private ShowAboutDialogAction() {
			putValue(NAME, strings.getString("SHOW_ABOUT_DIALOG_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("SHOW_ABOUT_DIALOG_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final var icon = new ImageIcon(Main.class.getResource(Main.ICON_RESOURCE_PATHS[2]));
			GuiUtils.showMessageDialog(frame,
					MessageFormat.format(strings.getString("ABOUT_DIALOG_TEXT"), Version.VERSION),
					(String) getValue(NAME), JOptionPane.INFORMATION_MESSAGE, icon);
		}
	}

	private final class ShowAction extends AbstractAction {

		private static final long serialVersionUID = 8578159622754054457L;

		private ShowAction() {
			putValue(NAME, strings.getString("SHOW_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("SHOW_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final var openEvent = new WindowEvent(frame, WindowEvent.WINDOW_OPENED);
			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(openEvent);
			frame.setVisible(true);
			frame.setExtendedState(Frame.NORMAL);
		}
	}

	private final class ShowLicensesAction extends AbstractAction {

		private static final long serialVersionUID = 2471952794110895043L;

		private ShowLicensesAction() {
			putValue(NAME, strings.getString("SHOW_LICENSES_DIALOG_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("SHOW_LICENSES_DIALOG_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			try (final var bufferedReader = new BufferedReader(new InputStreamReader(
					ClassLoader.getSystemResourceAsStream(Main.LICENSES_FILENAME), StandardCharsets.UTF_8))) {
				final var text = bufferedReader.lines().collect(Collectors.joining("\n"));
				final var textArea = new JTextArea(text);
				textArea.setLineWrap(true);
				textArea.setEditable(false);
				final var scrollPane = new JScrollPane(textArea);
				scrollPane.setPreferredSize(new Dimension(600, 400));
				GuiUtils.showMessageDialog(frame, scrollPane, (String) getValue(NAME), JOptionPane.DEFAULT_OPTION);
			} catch (final IOException e1) {
				log.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
	}

	private static final class ShowWebsiteAction extends AbstractAction {

		private static final long serialVersionUID = -9029607010261185834L;

		private ShowWebsiteAction() {
			putValue(NAME, strings.getString("SHOW_WEBSITE_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("SHOW_WEBSITE_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				Desktop.getDesktop().browse(new URI(WEBSITE_URL));
			} catch (IOException | URISyntaxException e1) {
				log.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
	}

	private final class StartClientAction extends AbstractAction {

		private static final long serialVersionUID = 3975574941559749481L;

		private StartClientAction() {
			putValue(NAME, strings.getString("START_CLIENT_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("START_CLIENT_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			startClient();
		}
	}

	private final class StartLocalAction extends AbstractAction {

		private static final long serialVersionUID = -2003502124995392039L;

		private StartLocalAction() {
			putValue(NAME, strings.getString("START_LOCAL_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("START_LOCAL_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			startLocal();
		}
	}

	private final class StartServerAction extends AbstractAction {

		private static final long serialVersionUID = 1758447420975631146L;

		private StartServerAction() {
			putValue(NAME, strings.getString("START_SERVER_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("START_SERVER_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			startServer();
		}
	}

	private final class StopAction extends AbstractAction {

		private static final long serialVersionUID = -2863419586328503426L;

		private StopAction() {
			putValue(NAME, strings.getString("STOP_ACTION_NAME"));
			putValue(SHORT_DESCRIPTION, strings.getString("STOP_ACTION_DESCRIPTION"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			stopAll(true, true, false);
		}
	}

	private static final class TaskRunner {

		private Thread thread = isMac ? null : Thread.currentThread();

		private volatile boolean pollGLFWEvents = false;

		private volatile Object result;

		private volatile Object task;

		private void enterLoop() {
			if (isMac)
				return;

			log.log(Level.INFO, "Entering main loop");

			for (;;)
				if (task != null) {
					result = null;
					var notify = false;

					try {
						if (task instanceof final Callable<?> callable) {
							notify = true;
							result = callable.call();
						} else if (task instanceof final Runnable runnable)
							runnable.run();
					} catch (final Throwable t) {
						result = t;
					} finally {
						if (notify)
							synchronized (this) {
								notifyAll();
							}

						task = null;
					}
				} else {
					if (pollGLFWEvents)
						GLFW.glfwPollEvents();

					try {
						Thread.sleep(10L);
					} catch (final InterruptedException e) {
						log.log(Level.INFO, "Exiting main loop");

						return;
					}
				}
		}

		private boolean isTaskOfTypeRunning(final Class<?> clazz) {
			if (task == null || isMac && (thread == null || !thread.isAlive()))
				return false;

			return clazz.isAssignableFrom(task.getClass());
		}

		@SuppressWarnings("unchecked")
		private <V> V run(final Callable<V> callable) {
			if (isMac)
				try {
					return callable.call();
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}

			waitForTask();

			task = callable;

			try {
				synchronized (this) {
					this.wait();
				}

				if (result instanceof final Throwable throwable)
					throw new RuntimeException(throwable);

				return (V) result;
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			return null;

		}

		private void run(final Runnable runnable) {
			waitForTask();
			task = runnable;

			if (isMac)
				if (runnable instanceof Output) {
					thread = new Thread(runnable);
					thread.start();
				} else
					runnable.run();
		}

		private void shutdown() {
			pollGLFWEvents = false;

			if (thread != null) {
				thread.interrupt();

				while (thread.isAlive())
					try {
						Thread.sleep(10L);
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
					}
			}
		}

		private void stopTask() {
			if (thread == null)
				return;

			thread.interrupt();

			waitForTask();
		}

		private void waitForTask() {
			while (isMac ? thread != null && thread.isAlive() : task != null)
				try {
					Thread.sleep(10L);
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
				}
		}
	}

	private abstract class UnsavedChangesAwareAction extends AbstractAction {

		private static final long serialVersionUID = 1387266903295357716L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (unsavedChanges) {
				final var path = currentFile != null ? currentFile.getAbsolutePath() : strings.getString("UNTITLED");

				final var selectedOption = JOptionPane.showConfirmDialog(frame,
						MessageFormat.format(strings.getString("SAVE_CHANGES_DIALOG_TEXT"), path),
						strings.getString("WARNING_DIALOG_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION);

				switch (selectedOption) {
				case JOptionPane.YES_OPTION:
					saveProfile();
					if (unsavedChanges)
						return;
				case JOptionPane.NO_OPTION:
					break;
				default:
					return;
				}
			}

			doAction(e);
		}

		protected abstract void doAction(final ActionEvent e);
	}

	static volatile Main main;

	private static final Options options = new Options();

	private static final String APP_ID;

	private static final Logger log = Logger.getLogger(Main.class.getName());

	public static final boolean isWindows = Platform.getOSType() == Platform.WINDOWS;

	public static final boolean isMac = Platform.isMac();

	static boolean skipMessageDialogs;

	public static final ResourceBundle strings = ResourceBundle.getBundle("strings");

	private static final String PROFILE_FILE_EXTENSION = "json";

	private static final String PROFILE_FILE_SUFFIX = "." + PROFILE_FILE_EXTENSION;

	private static final int DIALOG_BOUNDS_X = 100;

	private static final int DIALOG_BOUNDS_Y = 100;

	private static final int DIALOG_BOUNDS_WIDTH = 930;

	private static final int DIALOG_BOUNDS_HEIGHT = 655;

	private static final int SVG_VIEWBOX_MARGIN = 20;

	private static final String SVG_DARK_THEME_TEXT_COLOR = "#FFFFFF";

	private static final String SVG_DARK_THEME_PATH_COLOR = "#AAA";

	static final int DEFAULT_HGAP = 10;

	static final int DEFAULT_VGAP = 10;

	public static final Dimension BUTTON_DIMENSION = new Dimension(110, 25);

	private static final Dimension SETTINGS_LABEL_DIMENSION = new Dimension(160, 15);

	private static final FlowLayout DEFAULT_FLOW_LAYOUT = new FlowLayout(FlowLayout.LEADING, DEFAULT_HGAP,
			DEFAULT_VGAP);

	private static final FlowLayout LOWER_BUTTONS_FLOW_LAYOUT = new FlowLayout(FlowLayout.RIGHT, DEFAULT_HGAP + 2, 5);

	private static final Border LIST_ITEM_BORDER = BorderFactory.createEtchedBorder();

	private static final Insets LIST_ITEM_INSETS = new Insets(8, DEFAULT_HGAP, 8, DEFAULT_HGAP);

	private static final Insets LIST_ITEM_INNER_INSETS = new Insets(4, 4, 4, 4);

	private static final String OPTION_AUTOSTART = "autostart";
	private static final String OPTION_PROFILE = "profile";

	private static final String OPTION_GAME_CONTROLLER_DB = "gamecontrollerdb";

	private static final String OPTION_TRAY = "tray";

	private static final String OPTION_SAVE = "save";

	private static final String OPTION_EXPORT = "export";

	private static final String OPTION_SKIP_MESSAGE_DIALOGS = "skipMessageDialogs";

	private static final String OPTION_QUIT = "quit";

	private static final String OPTION_VERSION = "version";

	private static final String OPTION_HELP = "help";

	private static final String OPTION_AUTOSTART_VALUE_LOCAL = "local";

	private static final String OPTION_AUTOSTART_VALUE_CLIENT = "client";

	private static final String OPTION_AUTOSTART_VALUE_SERVER = "server";

	private static final String PREFERENCES_POLL_INTERVAL = "poll_interval";

	private static final String PREFERENCES_LAST_CONTROLLER = "last_controller";

	private static final String PREFERENCES_LAST_PROFILE = "last_profile";

	public static final String PREFERENCES_VJOY_DIRECTORY = "vjoy_directory";

	private static final String PREFERENCES_VJOY_DEVICE = "vjoy_device";

	private static final String PREFERENCES_HOST = "host";

	private static final String PREFERENCES_PORT = "port";

	private static final String PREFERENCES_TIMEOUT = "timeout";

	private static final String PREFERENCES_DARK_THEME = "dark_theme";

	private static final String PREFERENCES_PREVENT_POWER_SAVE_MODE = "prevent_power_save_mode";

	private static final String PREFERENCES_HOT_SWAPPING_BUTTON = "hot_swapping_button";

	private static final long OVERLAY_POSITION_UPDATE_INTERVAL = 10000L;

	private static final String[] ICON_RESOURCE_PATHS = { "/icon_16.png", "/icon_32.png", "/icon_64.png",
			"/icon_128.png" };

	private static final String LICENSES_FILENAME = "licenses.txt";

	private static final String CONTROLLER_SVG_FILENAME = "controller.svg";

	private static final String GAME_CONTROLLER_DATABASE_FILENAME = "gamecontrollerdb.txt";

	static final Color TRANSPARENT = new Color(255, 255, 255, 0);

	private static final String VJOY_GUID = "0300000034120000adbe000000000000";

	private static final String WEBSITE_URL = "https://controllerbuddy.org";

	static {
		options.addOption(OPTION_AUTOSTART, true, MessageFormat.format(
				strings.getString("AUTOSTART_OPTION_DESCRIPTION"),
				isWindows ? strings.getString("LOCAL_FEEDER_OR_CLIENT_OR_SERVER") : strings.getString("SERVER")));
		options.addOption(OPTION_PROFILE, true, strings.getString("PROFILE_OPTION_DESCRIPTION"));
		options.addOption(OPTION_GAME_CONTROLLER_DB, true, strings.getString("GAME_CONTROLLER_DB_OPTION_DESCRIPTION"));
		options.addOption(OPTION_TRAY, false, strings.getString("TRAY_OPTION_DESCRIPTION"));
		options.addOption(OPTION_SAVE, true, strings.getString("SAVE_OPTION_DESCRIPTION"));
		options.addOption(OPTION_EXPORT, true, strings.getString("EXPORT_OPTION_DESCRIPTION"));
		options.addOption(OPTION_SKIP_MESSAGE_DIALOGS, false,
				strings.getString("SKIP_MESSAGE_DIALOGS_OPTION_DESCRIPTION"));
		options.addOption(OPTION_QUIT, false, strings.getString("QUIT_OPTION_DESCRIPTION"));
		options.addOption(OPTION_VERSION, false, strings.getString("VERSION_OPTION_DESCRIPTION"));
		options.addOption(OPTION_HELP, false, strings.getString("HELP_OPTION_DESCRIPTION"));

		final var mainClassPackageName = Main.class.getPackageName();
		APP_ID = mainClassPackageName.substring(0, mainClassPackageName.lastIndexOf('.'));

		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (final UnsupportedLookAndFeelException e) {
			throw new RuntimeException(e);
		}
	}

	public static String assembleControllerLoggingMessage(final String prefix, final ControllerInfo controller) {
		final var sb = new StringBuilder();
		sb.append(prefix + " controller ");

		final var appendGamepadName = controller.name != null;

		if (appendGamepadName)
			sb.append(controller.name + " (");

		sb.append(String.valueOf(controller.jid));

		if (appendGamepadName)
			sb.append(")");

		if (controller.guid != null)
			sb.append(" [" + controller.guid + "]");

		return sb.toString();
	}

	private static int getExtendedKeyCodeForMenu(final AbstractButton button,
			final Set<Integer> alreadyAssignedKeyCodes) {
		var keyCode = KeyEvent.VK_UNDEFINED;

		final var text = button.getText();
		if (text != null && text.length() > 0) {
			var index = 0;
			do {
				keyCode = KeyEvent.getExtendedKeyCodeForChar(text.charAt(index));
				index++;
			} while (keyCode != KeyEvent.VK_UNDEFINED && !alreadyAssignedKeyCodes.add(keyCode));
		}

		return keyCode;
	}

	private static int getExtendedKeyCodeForMenuItem(final AbstractButton button) {
		final var action = button.getAction();
		if (action != null) {
			if (action instanceof NewAction)
				return KeyEvent.VK_N;
			if (action instanceof OpenAction)
				return KeyEvent.VK_O;
			if (action instanceof SaveAction)
				return KeyEvent.VK_S;
			if (action instanceof StartLocalAction)
				return KeyEvent.VK_L;
			if (action instanceof StartClientAction)
				return KeyEvent.VK_C;
			if (action instanceof StartServerAction)
				return KeyEvent.VK_E;
			if (action instanceof StopAction)
				return KeyEvent.VK_T;
		}

		return KeyEvent.VK_UNDEFINED;
	}

	public static List<ControllerInfo> getPresentControllers() {
		final var presentControllers = new ArrayList<ControllerInfo>();
		for (var jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++)
			if (GLFW.glfwJoystickPresent(jid) && GLFW.glfwJoystickIsGamepad(jid)
					&& !VJOY_GUID.equals(GLFW.glfwGetJoystickGUID(jid)))
				presentControllers.add(new ControllerInfo(jid));

		return presentControllers;
	}

	private static boolean isModalDialogShowing() {
		final var windows = Window.getWindows();
		if (windows != null)
			for (final Window w : windows)
				if (w.isShowing() && w instanceof final Dialog dialog && dialog.isModal())
					return true;

		return false;
	}

	public static void main(final String[] args) {
		final var unique = new Unique4jList(APP_ID) {

			@Override
			protected void receiveMessageList(final List<String> messageList) {
				final var args = messageList.toArray(new String[0]);

				log.log(Level.INFO, "New activation with arguments: " + Arrays.toString(args));

				if (args.length > 0)
					try {
						final var commandLine = new DefaultParser().parse(options, args);
						final var cmdProfilePath = commandLine.getOptionValue(OPTION_PROFILE);
						final var gameControllerDbPath = commandLine.getOptionValue(OPTION_GAME_CONTROLLER_DB);

						EventQueue.invokeLater(() -> {
							if (cmdProfilePath != null)
								main.loadProfile(new File(cmdProfilePath), false);

							if (gameControllerDbPath != null)
								main.updateGameControllerMappingsFromFile(gameControllerDbPath);

							main.handleRemainingCommandLine(commandLine);
						});
					} catch (final ParseException e) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				else
					EventQueue.invokeLater(() -> GuiUtils.showMessageDialog(main.frame,
							strings.getString("ALREADY_RUNNING_DIALOG_TEXT"), strings.getString("ERROR_DIALOG_TITLE"),
							JOptionPane.ERROR_MESSAGE));
			}

			@Override
			protected List<String> sendMessageList() {
				return Arrays.asList(args);
			}
		};

		try {
			if (unique.acquireLock()) {
				log.log(Level.INFO, "Launching " + strings.getString("APPLICATION_NAME") + " " + Version.VERSION);

				Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
					log.log(Level.SEVERE, e.getMessage(), e);

					if (main != null && main.frame != null)
						GuiUtils.invokeOnEventDispatchThreadIfRequired(() -> {
							final var sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw));

							GuiUtils.showMessageDialog(main.frame,
									MessageFormat.format(strings.getString("UNCAUGHT_EXCEPTION_DIALOG_TEXT"),
											sw.toString()),
									strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);

							terminate(1);
						});
					else
						terminate(1);
				});

				try {
					final var commandLine = new DefaultParser().parse(options, args);
					if (commandLine.hasOption(OPTION_VERSION)) {
						System.out.println(strings.getString("APPLICATION_NAME") + " " + Version.VERSION);
						unique.releaseLock();

						return;
					}
					if (!commandLine.hasOption(OPTION_HELP)) {
						final var taskRunner = new TaskRunner();

						EventQueue.invokeLater(() -> {
							skipMessageDialogs = commandLine.hasOption(OPTION_SKIP_MESSAGE_DIALOGS);

							final var cmdProfilePath = commandLine.getOptionValue(OPTION_PROFILE);
							final var gameControllerDbPath = commandLine.getOptionValue(OPTION_GAME_CONTROLLER_DB);
							main = new Main(unique, taskRunner, cmdProfilePath, gameControllerDbPath);

							main.handleRemainingCommandLine(commandLine);

							taskRunner.pollGLFWEvents = true;
						});

						taskRunner.enterLoop();

						return;
					}
				} catch (final ParseException e) {
				}

				new HelpFormatter().printHelp(strings.getString("APPLICATION_NAME"), options, true);
				unique.releaseLock();
			}
		} catch (final Unique4jException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private static void terminate(final int status) {
		if (main != null && main.shutdownHookTriggered)
			return;

		log.log(Level.INFO, "Terminated (" + status + ")");
		System.exit(status);
	}

	private volatile boolean shutdownHookTriggered;

	private final Unique4j unique;

	private final TaskRunner taskRunner;

	private final Preferences preferences = Preferences.userRoot().node("/" + APP_ID.replace('.', '/'));

	private final Map<VirtualAxis, JProgressBar> virtualAxisToProgressBarMap = new HashMap<>();

	private volatile Output output;

	private ControllerInfo selectedController;

	private Input input;

	private OutputType lastOutputType = OutputType.NONE;

	private final JFrame frame;

	private final OpenAction openAction = new OpenAction();

	private final QuitAction quitAction = new QuitAction();

	private final StartLocalAction startLocalAction = new StartLocalAction();

	private final StartClientAction startClientAction = new StartClientAction();

	private final StartServerAction startServerAction = new StartServerAction();

	private final StopAction stopAction = new StopAction();

	private final JMenuBar menuBar = new JMenuBar();

	private final JMenu fileJMenu = new JMenu(strings.getString("FILE_MENU"));

	private final JMenu deviceJMenu = new JMenu(strings.getString("DEVICE_MENU"));

	private final JMenu runJMenu = new JMenu(strings.getString("RUN_MENU"));

	private final JMenuItem newJMenuItem = fileJMenu.add(new NewAction());

	private final JMenuItem openJMenuItem = fileJMenu.add(openAction);

	private final JMenuItem saveJMenuItem = fileJMenu.add(new SaveAction());

	private final JMenuItem saveAsJMenuItem = fileJMenu.add(new SaveAsAction());

	private JMenuItem startLocalJMenuItem;

	private JMenuItem startClientJMenuItem;

	private final JMenuItem startServerJMenuItem;

	private final JMenuItem stopJMenuItem;

	private PopupMenu runPopupMenu;

	private MenuItem showMenuItem;

	private MenuItem startLocalMenuItem;

	private MenuItem startClientMenuItem;

	private MenuItem startServerMenuItem;

	private MenuItem stopMenuItem;

	private final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);

	private JPanel modesPanel;

	private JScrollPane modesScrollPane;

	private JPanel modesListPanel;

	private JPanel newModePanel;

	private JPanel overlayPanel;

	private JPanel visualizationPanel;

	private AssignmentsComponent assignmentsComponent;

	private JScrollPane profileSettingsScrollPane;

	private JPanel profileSettingsPanel;

	private JCheckBox showVrOverlayCheckBox;

	private final JScrollPane globalSettingsScrollPane = new JScrollPane();

	private final JPanel globalSettingsPanel;

	private JScrollPane indicatorsScrollPane;

	private JPanel indicatorsListPanel;

	private TimerTask overlayTimerTask;

	private JLabel vJoyDirectoryLabel;

	private JTextField hostTextField;

	private final JLabel statusLabel = new JLabel(strings.getString("STATUS_READY"));

	private TrayIcon trayIcon;

	private boolean unsavedChanges = false;

	private String loadedProfile = null;

	private File currentFile;

	private ServerSocket serverSocket;

	private volatile boolean scheduleOnScreenKeyboardModeSwitch;

	private JLabel currentModeLabel;

	private final JFileChooser profileFileChooser = new ProfileFileChooser();

	private final Timer timer = new Timer();

	private volatile OpenVrOverlay openVrOverlay;

	private FrameDragListener overlayFrameDragListener;

	private FlowLayout indicatorPanelFlowLayout;

	private JPanel indicatorPanel;

	private Rectangle prevTotalDisplayBounds;

	private final GridBagConstraints settingsPanelGridBagConstraints = new GridBagConstraints(0,
			GridBagConstraints.RELATIVE, 1, 1, 0d, 0d, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE,
			new Insets(0, 0, 0, 0), 0, 5);

	private volatile JFrame overlayFrame;

	private final OnScreenKeyboard onScreenKeyboard = new OnScreenKeyboard(this);

	private JComboBox<Mode> modeComboBox;

	private JSVGCanvas svgCanvas;

	private SVGDocument templateSvgDocument;

	private FlatLaf lookAndFeel;

	private final Thread shutdownHook = new Thread(() -> {
		shutdownHookTriggered = true;
		quit();
	});

	private Main(final Unique4j unique, final TaskRunner taskRunner, final String cmdProfilePath,
			final String gameControllerDbPath) {
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		this.unique = unique;
		this.taskRunner = taskRunner;

		frame = new JFrame();

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				super.windowClosing(e);

				if (showMenuItem != null)
					showMenuItem.setEnabled(true);
			}

			@Override
			public void windowDeiconified(final WindowEvent e) {
				super.windowDeiconified(e);

				if (showMenuItem != null)
					showMenuItem.setEnabled(false);
			}

			@Override
			public void windowIconified(final WindowEvent e) {
				super.windowIconified(e);

				if (showMenuItem != null)
					showMenuItem.setEnabled(true);
			}

			@Override
			public void windowOpened(final WindowEvent e) {
				super.windowOpened(e);

				if (showMenuItem != null)
					showMenuItem.setEnabled(false);

				updateVisualizationPanel();
			}
		});

		frame.setBounds(DIALOG_BOUNDS_X, DIALOG_BOUNDS_Y, DIALOG_BOUNDS_WIDTH, DIALOG_BOUNDS_HEIGHT);
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		final var icons = new ArrayList<Image>();
		for (final var path : ICON_RESOURCE_PATHS) {
			final var icon = new ImageIcon(Main.class.getResource(path));
			icons.add(icon.getImage());
		}
		frame.setIconImages(icons);

		frame.setJMenuBar(menuBar);

		menuBar.add(fileJMenu);

		fileJMenu.add(quitAction);
		menuBar.add(deviceJMenu);

		if (isWindows) {
			startLocalJMenuItem = runJMenu.add(startLocalAction);
			startClientJMenuItem = runJMenu.add(startClientAction);
		}

		startServerJMenuItem = runJMenu.add(startServerAction);
		runJMenu.addSeparator();
		stopJMenuItem = runJMenu.add(stopAction);

		menuBar.add(runJMenu);

		final var helpMenu = new JMenu(strings.getString("HELP_MENU"));
		menuBar.add(helpMenu);
		helpMenu.add(new ShowLicensesAction());
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
			helpMenu.add(new ShowWebsiteAction());
		helpMenu.add(new ShowAboutDialogAction());

		frame.getContentPane().add(tabbedPane);

		globalSettingsPanel = new JPanel();
		globalSettingsPanel.setLayout(new GridBagLayout());

		globalSettingsScrollPane.setViewportView(globalSettingsPanel);
		tabbedPane.addTab(strings.getString("GLOBAL_SETTINGS_TAB"), null, globalSettingsScrollPane);

		final var pollIntervalPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
		globalSettingsPanel.add(pollIntervalPanel, settingsPanelGridBagConstraints);

		final var pollIntervalLabel = new JLabel(strings.getString("POLL_INTERVAL_LABEL"));
		pollIntervalLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
		pollIntervalPanel.add(pollIntervalLabel);

		final var pollIntervalSpinner = new JSpinner(new SpinnerNumberModel(
				preferences.getInt(PREFERENCES_POLL_INTERVAL, Output.DEFAULT_POLL_INTERVAL), 1, 100, 1));
		final var pollIntervalSpinnerEditor = new JSpinner.NumberEditor(pollIntervalSpinner,
				"# " + strings.getString("MILLISECOND_SYMBOL"));
		((DefaultFormatter) pollIntervalSpinnerEditor.getTextField().getFormatter()).setCommitsOnValidEdit(true);
		pollIntervalSpinner.setEditor(pollIntervalSpinnerEditor);
		pollIntervalSpinner.addChangeListener(event -> preferences.putInt(PREFERENCES_POLL_INTERVAL,
				(int) ((JSpinner) event.getSource()).getValue()));
		pollIntervalPanel.add(pollIntervalSpinner);

		if (isWindows) {
			final var vJoyDirectoryPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
			globalSettingsPanel.add(vJoyDirectoryPanel, settingsPanelGridBagConstraints);

			final var vJoyDirectoryLabel = new JLabel(strings.getString("VJOY_DIRECTORY_LABEL"));
			vJoyDirectoryLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
			vJoyDirectoryPanel.add(vJoyDirectoryLabel);

			this.vJoyDirectoryLabel = new JLabel(
					preferences.get(PREFERENCES_VJOY_DIRECTORY, VJoyOutput.getDefaultInstallationPath()));
			vJoyDirectoryPanel.add(this.vJoyDirectoryLabel);

			final var vJoyDirectoryButton = new JButton(new ChangeVJoyDirectoryAction());
			vJoyDirectoryPanel.add(vJoyDirectoryButton);

			final var vJoyDevicePanel = new JPanel(DEFAULT_FLOW_LAYOUT);
			globalSettingsPanel.add(vJoyDevicePanel, settingsPanelGridBagConstraints);

			final var vJoyDeviceLabel = new JLabel(strings.getString("VJOY_DEVICE_LABEL"));
			vJoyDeviceLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
			vJoyDevicePanel.add(vJoyDeviceLabel);

			final var vJoyDeviceSpinner = new JSpinner(new SpinnerNumberModel(
					preferences.getInt(PREFERENCES_VJOY_DEVICE, VJoyOutput.DEFAULT_VJOY_DEVICE), 1, 16, 1));
			final var vJoyDeviceSpinnerEditor = new JSpinner.NumberEditor(vJoyDeviceSpinner, "#");
			((DefaultFormatter) vJoyDeviceSpinnerEditor.getTextField().getFormatter()).setCommitsOnValidEdit(true);
			vJoyDeviceSpinner.setEditor(vJoyDeviceSpinnerEditor);
			vJoyDeviceSpinner.addChangeListener(event -> preferences.putInt(PREFERENCES_VJOY_DEVICE,
					(int) ((JSpinner) event.getSource()).getValue()));
			vJoyDevicePanel.add(vJoyDeviceSpinner);

			final var hostPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
			globalSettingsPanel.add(hostPanel, settingsPanelGridBagConstraints);

			final var hostLabel = new JLabel(strings.getString("HOST_LABEL"));
			hostLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
			hostPanel.add(hostLabel);

			hostTextField = new JTextField(preferences.get(PREFERENCES_HOST, ClientOutput.DEFAULT_HOST), 15);
			hostTextField.setCaretPosition(0);
			final var setHostAction = new SetHostAction(hostTextField);
			hostTextField.addActionListener(setHostAction);
			hostTextField.addFocusListener(setHostAction);
			hostPanel.add(hostTextField);
		}

		final var portPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
		globalSettingsPanel.add(portPanel, settingsPanelGridBagConstraints);

		final var portLabel = new JLabel(strings.getString("PORT_LABEL"));
		portLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
		portPanel.add(portLabel);

		final var portSpinner = new JSpinner(new SpinnerNumberModel(
				preferences.getInt(PREFERENCES_PORT, ServerOutput.DEFAULT_PORT), 1024, 65535, 1));
		final var portSpinnerEditor = new JSpinner.NumberEditor(portSpinner, "#");
		((DefaultFormatter) portSpinnerEditor.getTextField().getFormatter()).setCommitsOnValidEdit(true);
		portSpinner.setEditor(portSpinnerEditor);
		portSpinner.addChangeListener(
				event -> preferences.putInt(PREFERENCES_PORT, (int) ((JSpinner) event.getSource()).getValue()));
		portPanel.add(portSpinner);

		final var timeoutPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
		globalSettingsPanel.add(timeoutPanel, settingsPanelGridBagConstraints);

		final var timeoutLabel = new JLabel(strings.getString("TIMEOUT_LABEL"));
		timeoutLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
		timeoutPanel.add(timeoutLabel);

		final var timeoutSpinner = new JSpinner(new SpinnerNumberModel(
				preferences.getInt(PREFERENCES_TIMEOUT, ServerOutput.DEFAULT_TIMEOUT), 10, 60000, 1));
		final var timeoutSpinnerEditor = new JSpinner.NumberEditor(timeoutSpinner,
				"# " + strings.getString("MILLISECOND_SYMBOL"));
		((DefaultFormatter) timeoutSpinnerEditor.getTextField().getFormatter()).setCommitsOnValidEdit(true);
		timeoutSpinner.setEditor(timeoutSpinnerEditor);
		timeoutSpinner.addChangeListener(
				event -> preferences.putInt(PREFERENCES_TIMEOUT, (int) ((JSpinner) event.getSource()).getValue()));
		timeoutPanel.add(timeoutSpinner);

		final var hotSwapPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
		globalSettingsPanel.add(hotSwapPanel, settingsPanelGridBagConstraints);

		final var hotSwappingLabel = new JLabel(strings.getString("HOT_SWAPPING_LABEL"));
		hotSwappingLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
		hotSwapPanel.add(hotSwappingLabel);

		final var hotSwappingButtonLabel = new JLabel(strings.getString("HOT_SWAPPING_BUTTON_LABEL"));
		hotSwapPanel.add(hotSwappingButtonLabel);

		final var hotSwapButtonComboBox = new JComboBox<>(HotSwappingButton.values());
		final var selectedHotSwappingButton = HotSwappingButton.getById(getSelectedHotSwappingButtonId());
		hotSwapButtonComboBox.setSelectedItem(selectedHotSwappingButton);
		hotSwapPanel.add(hotSwapButtonComboBox);
		hotSwapButtonComboBox.setAction(new SetHotSwapButtonAction());

		final var darkThemePanel = new JPanel(DEFAULT_FLOW_LAYOUT);
		globalSettingsPanel.add(darkThemePanel, settingsPanelGridBagConstraints);

		final var darkThemeLabel = new JLabel(strings.getString("DARK_THEME_LABEL"));
		darkThemeLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
		darkThemePanel.add(darkThemeLabel);

		final var darkThemeCheckBox = new JCheckBox(strings.getString("DARK_THEME_CHECK_BOX"));
		darkThemeCheckBox.setSelected(preferences.getBoolean(PREFERENCES_DARK_THEME, false));
		darkThemeCheckBox.addActionListener(event -> {
			final var darkTheme = ((JCheckBox) event.getSource()).isSelected();
			preferences.putBoolean(PREFERENCES_DARK_THEME, darkTheme);
			updateTheme();
		});
		darkThemePanel.add(darkThemeCheckBox);

		if (isWindows) {
			final var preventPowerSaveModeSettingsPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
			globalSettingsPanel.add(preventPowerSaveModeSettingsPanel, settingsPanelGridBagConstraints);

			final var preventPowerSaveModeLabel = new JLabel(strings.getString("POWER_SAVE_MODE_LABEL"));
			preventPowerSaveModeLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
			preventPowerSaveModeSettingsPanel.add(preventPowerSaveModeLabel);

			final var preventPowerSaveModeCheckBox = new JCheckBox(
					strings.getString("PREVENT_POWER_SAVE_MODE_CHECK_BOX"));
			preventPowerSaveModeCheckBox.setSelected(preferences.getBoolean(PREFERENCES_PREVENT_POWER_SAVE_MODE, true));
			preventPowerSaveModeCheckBox.addActionListener(event -> {
				final var preventPowerSaveMode = ((JCheckBox) event.getSource()).isSelected();
				preferences.putBoolean(PREFERENCES_PREVENT_POWER_SAVE_MODE, preventPowerSaveMode);
			});
			preventPowerSaveModeSettingsPanel.add(preventPowerSaveModeCheckBox);
		}

		updateTitleAndTooltip();

		globalSettingsPanel.add(Box.createGlue(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1d, 1d,
				GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		final var outsideBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		final var insideBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
		statusLabel.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));
		frame.add(statusLabel, BorderLayout.SOUTH);

		updateTheme();

		if (Platform.getOSType() == Platform.MAC)
			Configuration.GLFW_CHECK_THREAD0.set(false);

		final var glfwInitialized = taskRunner.run(GLFW::glfwInit);
		if (!glfwInitialized) {
			log.log(Level.SEVERE, "Could not initialize GLFW");

			if (isWindows)
				GuiUtils.showMessageDialog(frame,
						strings.getString("COULD_NOT_INITIALIZE_GLFW.GLFW_DIALOG_TEXT_WINDOWS"),
						strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
			else {
				GuiUtils.showMessageDialog(frame, strings.getString("COULD_NOT_INITIALIZE_GLFW.GLFW_DIALOG_TEXT"),
						strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
				quit();
			}
		}

		var mappingsUpdated = updateGameControllerMappings(
				ClassLoader.getSystemResourceAsStream(Main.GAME_CONTROLLER_DATABASE_FILENAME));
		log.log(mappingsUpdated ? Level.INFO : Level.WARNING,
				(mappingsUpdated ? "Successfully updated" : "Failed to update")
						+ " game controller mappings from internal file " + Main.GAME_CONTROLLER_DATABASE_FILENAME);

		if (gameControllerDbPath != null)
			mappingsUpdated &= updateGameControllerMappingsFromFile(gameControllerDbPath);

		if (!mappingsUpdated) {
			log.log(Level.WARNING, "An error occurred while updating the SDL game controller mappings");

			GuiUtils.showMessageDialog(frame, strings.getString("ERROR_UPDATING_GAME_CONTROLLER_DB_DIALOG_TEXT"),
					strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
		}

		final var presentControllers = taskRunner.run(Main::getPresentControllers);
		if (!presentControllers.isEmpty()) {
			final var lastControllerGuid = preferences.get(PREFERENCES_LAST_CONTROLLER, null);
			if (lastControllerGuid != null)
				presentControllers.stream().filter(controller -> lastControllerGuid.equals(controller.guid)).findFirst()
						.ifPresentOrElse(controller -> {
							log.log(Level.INFO, assembleControllerLoggingMessage("Found previously used", controller));
							setSelectedController(controller);
						}, () -> {
							log.log(Level.INFO, "Previously used controller is not present");
							setSelectedController(presentControllers.get(0));
						});
		}

		newProfile();

		onControllersChanged(presentControllers, true);

		taskRunner.run(() -> GLFW.glfwSetJoystickCallback((jid, event) -> {
			final var disconnected = event == GLFW.GLFW_DISCONNECTED;
			if (disconnected || GLFW.glfwJoystickIsGamepad(jid)) {
				if (disconnected) {
					log.log(Level.INFO, assembleControllerLoggingMessage("Disconnected", new ControllerInfo(jid)));

					if (selectedController != null && selectedController.jid == jid) {
						selectedController = null;
						input.deInit(true);
					}
				} else if (event == GLFW.GLFW_CONNECTED)
					log.log(Level.INFO, assembleControllerLoggingMessage("Connected", new ControllerInfo(jid)));

				final var presentControllers1 = getPresentControllers();

				EventQueue.invokeLater(() -> onControllersChanged(presentControllers1, false));
			}
		}));

		final var noControllerConnected = glfwInitialized && presentControllers.isEmpty();

		if (noControllerConnected)
			if (isWindows)
				GuiUtils.showMessageDialog(frame, strings.getString("NO_CONTROLLER_CONNECTED_DIALOG_TEXT_WINDOWS"),
						strings.getString("INFORMATION_DIALOG_TITLE"), JOptionPane.INFORMATION_MESSAGE);
			else
				GuiUtils.showMessageDialog(frame, strings.getString("NO_CONTROLLER_CONNECTED_DIALOG_TEXT"),
						strings.getString("INFORMATION_DIALOG_TITLE"), JOptionPane.INFORMATION_MESSAGE);

		final var profilePath = cmdProfilePath != null ? cmdProfilePath
				: preferences.get(PREFERENCES_LAST_PROFILE, null);
		if (profilePath != null) {
			loadProfile(new File(profilePath), noControllerConnected);
			if (loadedProfile == null && cmdProfilePath == null) {
				log.log(Level.INFO, "Removing " + PREFERENCES_LAST_PROFILE + " from preferences");
				preferences.remove(PREFERENCES_LAST_PROFILE);
			}
		}
	}

	private void deInitOverlay() {
		if (openVrOverlay != null) {
			openVrOverlay.stop();
			openVrOverlay = null;
		}

		if (overlayFrame != null) {
			for (var i = 0; i < 10; i++) {
				overlayFrame.dispose();

				if (!overlayFrame.isDisplayable())
					break;

				try {
					Thread.sleep(100L);
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			overlayFrame = null;
		}

		currentModeLabel = null;
		virtualAxisToProgressBarMap.clear();
	}

	private void deInitOverlayAndHideOnScreenKeyboard() {
		deInitOverlay();
		onScreenKeyboard.setVisible(false);
	}

	public void displayChargingStateInfo(final boolean charging) {
		if (trayIcon != null && input != null)
			trayIcon.displayMessage(strings.getString("CHARGING_STATE_CAPTION"),
					MessageFormat.format(
							strings.getString(charging ? "CHARGING_STATE_CHARGING" : "CHARGING_STATE_DISCHARGING"),
							input.getSonyExtension().getBatteryState() / 100f),
					MessageType.INFO);
	}

	public void displayLowBatteryWarning(final float batteryCharge) {
		EventQueue.invokeLater(() -> {
			if (trayIcon != null)
				trayIcon.displayMessage(strings.getString("LOW_BATTERY_CAPTION"),
						MessageFormat.format("{0,number,percent}", batteryCharge), MessageType.WARNING);
		});
	}

	public void exportVisualization(final File file) {
		if (templateSvgDocument == null)
			return;

		try {
			final var domImplementation = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder()
					.getDOMImplementation();
			final var htmlDocumentType = domImplementation.createDocumentType("html", "-//W3C//DTD XHTML 1.1//EN",
					"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
			final var htmlDocument = domImplementation.createDocument(XMLConstants.XLINK_NAMESPACE_URI, "html",
					htmlDocumentType);

			final var headElement = htmlDocument.createElementNS(XMLConstants.XLINK_NAMESPACE_URI, "head");
			htmlDocument.getDocumentElement().appendChild(headElement);

			final var titleElement = htmlDocument.createElementNS(XMLConstants.XLINK_NAMESPACE_URI, "title");
			final var title = currentFile != null ? currentFile.getName() : strings.getString("UNTITLED");
			titleElement.setTextContent(title);
			headElement.appendChild(titleElement);

			final var bodyElement = htmlDocument.createElementNS(XMLConstants.XLINK_NAMESPACE_URI, "body");
			htmlDocument.getDocumentElement().appendChild(bodyElement);

			final var profileHeaderElement = htmlDocument.createElementNS(XMLConstants.XLINK_NAMESPACE_URI, "h1");
			profileHeaderElement.setTextContent(title);
			bodyElement.appendChild(profileHeaderElement);

			final var labelElement = htmlDocument.createElementNS(XMLConstants.XLINK_NAMESPACE_URI, "label");
			labelElement.setTextContent("Mode: ");
			labelElement.setAttribute("style", "font-size:1.17em;font-weight:bold");
			bodyElement.appendChild(labelElement);

			final var selectElement = htmlDocument.createElementNS(XMLConstants.XLINK_NAMESPACE_URI, "select");
			selectElement.setAttribute("onchange",
					"Array.from(document.getElementsByClassName('svg-div')).forEach(e=>e.style.display=(e.id===this.value?'block':'none'))");
			selectElement.setAttribute("style", "vertical-align:text-bottom");
			labelElement.appendChild(selectElement);

			input.getProfile().getModes().forEach(mode -> {
				final var svgDivElement = htmlDocument.createElementNS(XMLConstants.XLINK_NAMESPACE_URI, "div");
				final var svgDivElementId = mode.getUuid().toString();

				svgDivElement.setAttribute("id", svgDivElementId);
				svgDivElement.setAttribute("class", "svg-div");
				svgDivElement.setAttribute("style", "margin-top:50px;margin-bottom:75px;height:450px;display:"
						+ (Profile.defaultMode.equals(mode) ? "block" : "none"));
				bodyElement.appendChild(svgDivElement);

				final var svgDocument = generateSvgDocument(mode, false);
				final var importedSvgNode = htmlDocument.importNode(svgDocument.getRootElement(), true);
				svgDivElement.appendChild(importedSvgNode);

				final var optionElement = htmlDocument.createElementNS(XMLConstants.XLINK_NAMESPACE_URI, "option");
				optionElement.setAttribute("value", svgDivElementId);
				optionElement.setTextContent(mode.getDescription());
				selectElement.appendChild(optionElement);
			});

			final var transformerFactory = TransformerFactory.newInstance();
			final var transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, htmlDocumentType.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, htmlDocumentType.getSystemId());

			try (final var fileOutputStream = new FileOutputStream(file)) {
				transformer.transform(new DOMSource(htmlDocument), new StreamResult(fileOutputStream));
				log.log(Level.INFO, "Exported visualization of profile " + title + " to: " + file.getAbsolutePath());
			}
		} catch (final DOMException | ParserConfigurationException | TransformerException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			GuiUtils.showMessageDialog(frame, strings.getString("COULD_NOT_EXPORT_VISUALIZATION_DIALOG_TEXT"),
					strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private SVGDocument generateSvgDocument(final Mode mode, final boolean darkTheme) {
		if (templateSvgDocument == null)
			return null;

		final var workingCopySvgDocument = (SVGDocument) DOMUtilities.deepCloneDocument(templateSvgDocument,
				templateSvgDocument.getImplementation());

		final var userAgentAdapter = new UserAgentAdapter();
		final var documentLoader = new DocumentLoader(userAgentAdapter);
		final var bridgeContext = new BridgeContext(userAgentAdapter, documentLoader);
		bridgeContext.setDynamicState(BridgeContext.DYNAMIC);
		new GVTBuilder().build(bridgeContext, workingCopySvgDocument);

		for (var axis = 0; axis <= GLFW.GLFW_GAMEPAD_AXIS_LAST; axis++) {
			final var idPrefix = switch (axis) {
			case GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER -> "lefttrigger";
			case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X -> "leftx";
			case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y -> "lefty";
			case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER -> "righttrigger";
			case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X -> "rightx";
			case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y -> "righty";
			default -> null;
			};

			final var actions = mode.getAxisToActionsMap().get(axis);
			updateSvgElements(workingCopySvgDocument, idPrefix, actions, darkTheme);
		}

		for (var button = 0; button <= GLFW.GLFW_GAMEPAD_BUTTON_LAST; button++) {
			final var idPrefix = switch (button) {
			case GLFW.GLFW_GAMEPAD_BUTTON_A -> "a";
			case GLFW.GLFW_GAMEPAD_BUTTON_B -> "b";
			case GLFW.GLFW_GAMEPAD_BUTTON_BACK -> "back";
			case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN -> "dpdown";
			case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT -> "dpleft";
			case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT -> "dpright";
			case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP -> "dpup";
			case GLFW.GLFW_GAMEPAD_BUTTON_GUIDE -> "guide";
			case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER -> "leftshoulder";
			case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB -> "leftstick";
			case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER -> "rightshoulder";
			case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB -> "rightstick";
			case GLFW.GLFW_GAMEPAD_BUTTON_START -> "start";
			case GLFW.GLFW_GAMEPAD_BUTTON_X -> "x";
			case GLFW.GLFW_GAMEPAD_BUTTON_Y -> "y";
			default -> null;
			};

			final var combinedActions = new ArrayList<IAction<Byte>>();

			final var normalActions = mode.getButtonToActionsMap().get(button);
			if (normalActions != null)
				combinedActions.addAll(normalActions);

			if (Profile.defaultMode.equals(mode)) {
				final var modeActions = input.getProfile().getButtonToModeActionsMap().get(button);
				if (modeActions != null)
					combinedActions.addAll(modeActions);
			}

			updateSvgElements(workingCopySvgDocument, idPrefix, combinedActions, darkTheme);
		}

		return workingCopySvgDocument;
	}

	public JFrame getFrame() {
		return frame;
	}

	Input getInput() {
		return input;
	}

	public OnScreenKeyboard getOnScreenKeyboard() {
		return onScreenKeyboard;
	}

	JFrame getOverlayFrame() {
		return overlayFrame;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public int getSelectedHotSwappingButtonId() {
		return Math.min(Math.max(preferences.getInt(PREFERENCES_HOT_SWAPPING_BUTTON, HotSwappingButton.None.id),
				HotSwappingButton.None.id), GLFW.GLFW_GAMEPAD_BUTTON_LAST);
	}

	public void handleOnScreenKeyboardModeChange() {
		if (scheduleOnScreenKeyboardModeSwitch) {
			for (final var buttonToModeActions : input.getProfile().getButtonToModeActionsMap().values())
				for (final var buttonToModeAction : buttonToModeActions)
					if (OnScreenKeyboard.onScreenKeyboardMode.equals(buttonToModeAction.getMode(input))) {
						buttonToModeAction.doAction(input, -1, Byte.MAX_VALUE);
						break;
					}

			scheduleOnScreenKeyboardModeSwitch = false;
		}
	}

	private void handleRemainingCommandLine(final CommandLine commandLine) {
		if (frame != null)
			frame.setVisible(!commandLine.hasOption(OPTION_TRAY));

		final var autostartOptionValue = commandLine.getOptionValue(OPTION_AUTOSTART);
		if (autostartOptionValue != null)
			if (isWindows && OPTION_AUTOSTART_VALUE_LOCAL.equals(autostartOptionValue))
				startLocal();
			else if (isWindows && OPTION_AUTOSTART_VALUE_CLIENT.equals(autostartOptionValue))
				startClient();
			else if (OPTION_AUTOSTART_VALUE_SERVER.equals(autostartOptionValue))
				startServer();
			else
				GuiUtils.showMessageDialog(frame,
						MessageFormat.format(
								strings.getString("INVALID_VALUE_FOR_COMMAND_LINE_OPTION_AUTOSTART_DIALOG_TEXT"),
								OPTION_AUTOSTART, autostartOptionValue,
								MessageFormat.format(
										isWindows ? strings.getString("LOCAL_FEEDER_OR_CLIENT_OR_SERVER")
												: strings.getString("SERVER"),
										strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE)));

		final var saveOptionValue = commandLine.getOptionValue(OPTION_SAVE);
		if (saveOptionValue != null)
			saveProfile(new File(saveOptionValue), false);

		final var exportOptionValue = commandLine.getOptionValue(OPTION_EXPORT);
		if (exportOptionValue != null)
			exportVisualization(new File(exportOptionValue));

		if (commandLine.hasOption(OPTION_QUIT))
			quit();
	}

	private void initOverlay() {
		if (!Toolkit.getDefaultToolkit().isAlwaysOnTopSupported() || !input.getProfile().isShowOverlay())
			return;

		final var modes = input.getProfile().getModes();
		final var multipleModes = modes.size() > 1;
		final var virtualAxisToOverlayAxisMap = input.getProfile().getVirtualAxisToOverlayAxisMap();
		if (!multipleModes && virtualAxisToOverlayAxisMap.isEmpty())
			return;

		var longestDescription = "";
		for (final var mode : modes) {
			final var description = mode.getDescription();
			if (description.length() > longestDescription.length())
				longestDescription = description;
		}

		overlayFrame = new JFrame("Overlay");
		overlayFrame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		overlayFrame.setUndecorated(true);
		overlayFrame.setType(JFrame.Type.UTILITY);
		overlayFrame.setLayout(new BorderLayout());
		overlayFrame.setFocusableWindowState(false);
		overlayFrame.setBackground(TRANSPARENT);
		overlayFrame.setAlwaysOnTop(true);
		overlayFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		if (multipleModes) {
			currentModeLabel = new JLabel(input.getProfile().getActiveMode().getDescription());
			final var font = currentModeLabel.getFont().deriveFont(Font.BOLD);
			currentModeLabel.setFont(font);
			final var fontMetrics = currentModeLabel.getFontMetrics(font);
			currentModeLabel.setPreferredSize(
					new Dimension(fontMetrics.stringWidth(longestDescription), fontMetrics.getHeight()));
			currentModeLabel.setForeground(Color.RED);
			overlayFrame.add(currentModeLabel, BorderLayout.PAGE_END);
		}

		indicatorPanelFlowLayout = new FlowLayout();
		indicatorPanel = new JPanel(indicatorPanelFlowLayout);
		indicatorPanel.setBackground(TRANSPARENT);

		EnumSet.allOf(Input.VirtualAxis.class).forEach(virtualAxis -> {
			final var overlayAxis = virtualAxisToOverlayAxisMap.get(virtualAxis);
			if (overlayAxis != null) {
				final var detentValues = new HashSet<Float>();

				input.getProfile().getModes().forEach(
						mode -> mode.getAxisToActionsMap().values().forEach(actions -> actions.forEach(action -> {
							if (action instanceof final AxisToRelativeAxisAction axisToRelativeAxisAction
									&& axisToRelativeAxisAction.getVirtualAxis() == virtualAxis) {
								final var detentValue = axisToRelativeAxisAction.getDetentValue();

								if (detentValue != null)
									detentValues.add(detentValue);
							}
						})));

				final var indicatorProgressBar = new IndicatorProgressBar(SwingConstants.VERTICAL, detentValues,
						overlayAxis);
				indicatorProgressBar.setPreferredSize(new Dimension(20, 150));
				indicatorProgressBar.setForeground(overlayAxis.color);
				indicatorProgressBar.setValue(1);

				indicatorPanel.add(indicatorProgressBar);
				virtualAxisToProgressBarMap.put(virtualAxis, indicatorProgressBar);
			}
		});

		overlayFrame.add(indicatorPanel);

		overlayFrameDragListener = new FrameDragListener(this, overlayFrame) {

			@Override
			public void mouseDragged(final MouseEvent e) {
				super.mouseDragged(e);
				updateOverlayAlignment(GuiUtils.getTotalDisplayBounds());
			}
		};
		overlayFrame.addMouseListener(overlayFrameDragListener);
		overlayFrame.addMouseMotionListener(overlayFrameDragListener);
		overlayFrame.pack();

		final var maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		final var defaultLocation = new Point();
		defaultLocation.x = maximumWindowBounds.width - overlayFrame.getWidth();
		defaultLocation.y = maximumWindowBounds.height - overlayFrame.getHeight();

		prevTotalDisplayBounds = GuiUtils.getTotalDisplayBounds();

		GuiUtils.loadFrameLocation(preferences, overlayFrame, defaultLocation, prevTotalDisplayBounds);

		updateOverlayAlignment(prevTotalDisplayBounds);

		overlayFrame.setVisible(true);
	}

	private void initVrOverlay() {
		final var profile = input.getProfile();

		if (!isWindows || !Toolkit.getDefaultToolkit().isAlwaysOnTopSupported() || !profile.isShowOverlay()
				|| !profile.isShowVrOverlay())
			return;

		try {
			openVrOverlay = new OpenVrOverlay(this);
		} catch (final Exception e) {
			openVrOverlay = null;
		}
	}

	private boolean isClientRunning() {
		return taskRunner.isTaskOfTypeRunning(ClientOutput.class);
	}

	public boolean isLocalRunning() {
		return taskRunner.isTaskOfTypeRunning(LocalOutput.class);
	}

	private boolean isRunning() {
		return isLocalRunning() || isClientRunning() || isServerRunning();
	}

	public boolean isServerRunning() {
		return taskRunner.isTaskOfTypeRunning(ServerOutput.class);
	}

	boolean isShutdownHookTriggered() {
		return shutdownHookTriggered;
	}

	private void loadProfile(final File file, final boolean skipMessageDialogs) {
		stopAll(true, false, true);

		log.log(Level.INFO, "Loading profile: " + file.getAbsolutePath());

		var profileLoaded = false;

		try {
			final var jsonString = Files.readString(file.toPath());
			final var jsonContext = JsonContext.create();

			try {
				final var profile = jsonContext.gson.fromJson(jsonString, Profile.class);
				final var versionsComparisonResult = VersionUtils.compareVersions(profile.getVersion());
				if (versionsComparisonResult.isEmpty()) {
					log.log(Level.WARNING, "Trying to load a profile without version information");

					if (!skipMessageDialogs)
						GuiUtils.showMessageDialog(frame,
								MessageFormat.format(strings.getString("PROFILE_VERSION_MISMATCH_DIALOG_TEXT"),
										strings.getString("AN_UNKNOWN")),
								strings.getString("WARNING_DIALOG_TITLE"), JOptionPane.WARNING_MESSAGE);
				} else {
					final var v = versionsComparisonResult.get();
					if (v < 0) {
						log.log(Level.WARNING, "Trying to load a profile for an older release");

						if (!skipMessageDialogs)
							GuiUtils.showMessageDialog(frame,
									MessageFormat.format(strings.getString("PROFILE_VERSION_MISMATCH_DIALOG_TEXT"),
											strings.getString("AN_OLDER")),
									strings.getString("WARNING_DIALOG_TITLE"), JOptionPane.WARNING_MESSAGE);
					} else if (v > 0) {
						log.log(Level.WARNING, "Trying to load a profile for a newer release");

						if (!skipMessageDialogs)
							GuiUtils.showMessageDialog(frame,
									MessageFormat.format(strings.getString("PROFILE_VERSION_MISMATCH_DIALOG_TEXT"),
											strings.getString("A_NEWER")),
									strings.getString("WARNING_DIALOG_TITLE"), JOptionPane.WARNING_MESSAGE);
					}
				}

				final var unknownActionClasses = jsonContext.actionTypeAdapter.getUnknownActionClasses();
				if (!unknownActionClasses.isEmpty()) {
					log.log(Level.WARNING, "Encountered the unknown actions while loading profile:"
							+ String.join(", ", unknownActionClasses));

					if (!skipMessageDialogs)
						GuiUtils.showMessageDialog(frame,
								MessageFormat.format(strings.getString("UNKNOWN_ACTION_TYPES_DIALOG_TEXT"),
										String.join("\n", unknownActionClasses)),
								strings.getString("WARNING_DIALOG_TITLE"), JOptionPane.WARNING_MESSAGE);
				}

				profileLoaded = input.setProfile(profile);
				if (profileLoaded) {
					saveLastProfile(file);
					updateModesPanel();
					updateVisualizationPanel();
					updateOverlayPanel();
					updateProfileSettingsPanel();
					loadedProfile = file.getName();
					setUnsavedChanges(false);
					setStatusBarText(
							MessageFormat.format(strings.getString("STATUS_PROFILE_LOADED"), file.getAbsolutePath()));
					scheduleStatusBarText(strings.getString("STATUS_READY"));
					profileFileChooser.setSelectedFile(file);

					restartLast();
				}
			} catch (final JsonParseException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch (final NoSuchFileException | InvalidPathException e) {
			log.log(Level.FINE, e.getMessage(), e);
		} catch (final IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		if (!profileLoaded) {
			log.log(Level.SEVERE, "Could load profile");

			if (!skipMessageDialogs)
				GuiUtils.showMessageDialog(frame, strings.getString("COULD_NOT_LOAD_PROFILE_DIALOG_TEXT"),
						strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void newProfile() {
		stopAll(true, false, true);

		currentFile = null;

		if (input != null)
			input.deInit(false);

		input = new Input(this, selectedController, null);

		loadedProfile = null;
		unsavedChanges = false;
		updateTitleAndTooltip();
		updateModesPanel();
		updateVisualizationPanel();
		updateOverlayPanel();
		updateProfileSettingsPanel();
		setStatusBarText(strings.getString("STATUS_READY"));
		profileFileChooser.setSelectedFile(new File(PROFILE_FILE_SUFFIX));
	}

	private void onControllersChanged(final List<ControllerInfo> presentControllers, final boolean selectFirstTab) {
		final var controllerConnected = !presentControllers.isEmpty();

		final var previousSelectedTabIndex = tabbedPane.getSelectedIndex();
		fileJMenu.remove(newJMenuItem);
		fileJMenu.remove(openJMenuItem);
		fileJMenu.remove(saveJMenuItem);
		fileJMenu.remove(saveAsJMenuItem);
		if (fileJMenu.getItemCount() > 1)
			fileJMenu.remove(0);
		deviceJMenu.removeAll();
		menuBar.remove(deviceJMenu);

		final var runMenuVisible = startClientJMenuItem != null || controllerConnected;

		runJMenu.setVisible(runMenuVisible);
		if (startLocalJMenuItem != null)
			startLocalJMenuItem.setVisible(controllerConnected);
		if (startServerJMenuItem != null)
			startServerJMenuItem.setVisible(controllerConnected);

		if (startLocalMenuItem != null)
			runPopupMenu.remove(startLocalMenuItem);
		if (startServerMenuItem != null)
			runPopupMenu.remove(startServerMenuItem);

		tabbedPane.remove(modesPanel);
		tabbedPane.remove(assignmentsComponent);
		tabbedPane.remove(overlayPanel);
		tabbedPane.remove(visualizationPanel);
		tabbedPane.remove(profileSettingsScrollPane);

		if (SystemTray.isSupported()) {
			final var systemTray = SystemTray.getSystemTray();

			if (trayIcon != null)
				systemTray.remove(trayIcon);

			final var popupMenu = new PopupMenu();

			final var showAction = new ShowAction();
			showMenuItem = new MenuItem((String) showAction.getValue(Action.NAME));
			showMenuItem.setEnabled(!frame.isVisible());
			showMenuItem.addActionListener(showAction);
			popupMenu.add(showMenuItem);

			popupMenu.addSeparator();

			final var openMenuItem = new MenuItem((String) openAction.getValue(Action.NAME));
			openMenuItem.addActionListener(openAction);
			popupMenu.add(openMenuItem);

			if (runMenuVisible) {
				runPopupMenu = new PopupMenu(strings.getString("RUN_MENU"));

				if (isWindows) {
					if (controllerConnected) {
						startLocalMenuItem = new MenuItem((String) startLocalAction.getValue(Action.NAME));
						startLocalMenuItem.addActionListener(startLocalAction);
						runPopupMenu.add(startLocalMenuItem);
					}

					startClientMenuItem = new MenuItem((String) startClientAction.getValue(Action.NAME));
					startClientMenuItem.addActionListener(startClientAction);
					runPopupMenu.add(startClientMenuItem);
				}

				if (controllerConnected) {
					startServerMenuItem = new MenuItem((String) startServerAction.getValue(Action.NAME));
					startServerMenuItem.addActionListener(startServerAction);
					runPopupMenu.add(startServerMenuItem);
				}

				runPopupMenu.addSeparator();

				stopMenuItem = new MenuItem((String) stopAction.getValue(Action.NAME));
				stopMenuItem.addActionListener(stopAction);
				runPopupMenu.add(stopMenuItem);

				popupMenu.add(runPopupMenu);
			}

			popupMenu.addSeparator();

			final var quitMenuItem = new MenuItem((String) quitAction.getValue(Action.NAME));
			quitMenuItem.addActionListener(quitAction);
			popupMenu.add(quitMenuItem);

			trayIcon = new TrayIcon(frame.getIconImage());
			trayIcon.addActionListener(showAction);
			trayIcon.setPopupMenu(popupMenu);
			try {
				systemTray.add(trayIcon);
			} catch (final AWTException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		if (!controllerConnected)
			selectedController = null;
		else if (selectedController == null) {
			setSelectedControllerAndUpdateInput(presentControllers.get(0), null);
			updateTitleAndTooltip();
		}

		try (final var bufferedReader = new BufferedReader(new InputStreamReader(
				ClassLoader.getSystemResourceAsStream(Main.CONTROLLER_SVG_FILENAME), StandardCharsets.UTF_8))) {
			final var svgDocumentFactory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
			templateSvgDocument = (SVGDocument) svgDocumentFactory.createDocument(null, bufferedReader);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		if (controllerConnected) {
			fileJMenu.insert(newJMenuItem, 0);
			fileJMenu.insert(openJMenuItem, 1);
			fileJMenu.insert(saveJMenuItem, 2);
			fileJMenu.insert(saveAsJMenuItem, 3);
			fileJMenu.insertSeparator(4);

			final var devicesButtonGroup = new ButtonGroup();
			presentControllers.forEach(controller -> {
				final var deviceRadioButtonMenuItem = new JRadioButtonMenuItem(new SelectControllerAction(controller));
				devicesButtonGroup.add(deviceRadioButtonMenuItem);
				deviceJMenu.add(deviceRadioButtonMenuItem);
			});
			menuBar.add(deviceJMenu, 1);

			if (runPopupMenu != null) {
				if (startLocalMenuItem != null)
					runPopupMenu.insert(startLocalMenuItem, 0);
				if (startServerMenuItem != null)
					runPopupMenu.insert(startServerMenuItem, isWindows ? 2 : 0);
			}

			modesPanel = new JPanel(new BorderLayout());
			tabbedPane.insertTab(strings.getString("MODES_TAB"), null, modesPanel, null,
					tabbedPane.indexOfComponent(globalSettingsScrollPane));

			modesListPanel = new JPanel();
			modesListPanel.setLayout(new GridBagLayout());

			modesScrollPane = new JScrollPane();
			modesPanel.add(modesScrollPane, BorderLayout.CENTER);

			newModePanel = new JPanel(LOWER_BUTTONS_FLOW_LAYOUT);
			final var newModeButton = new JButton(new NewModeAction());
			newModeButton.setPreferredSize(BUTTON_DIMENSION);
			newModePanel.add(newModeButton);
			modesPanel.add(newModePanel, BorderLayout.SOUTH);

			assignmentsComponent = new AssignmentsComponent(this);
			tabbedPane.insertTab(strings.getString("ASSIGNMENTS_TAB"), null, assignmentsComponent, null,
					tabbedPane.indexOfComponent(globalSettingsScrollPane));

			overlayPanel = new JPanel(new BorderLayout());
			tabbedPane.insertTab(strings.getString("OVERLAY_TAB"), null, overlayPanel, null,
					tabbedPane.indexOfComponent(globalSettingsScrollPane));

			indicatorsListPanel = new JPanel();
			indicatorsListPanel.setLayout(new GridBagLayout());

			indicatorsScrollPane = new JScrollPane();
			overlayPanel.add(indicatorsScrollPane, BorderLayout.CENTER);

			if (templateSvgDocument != null) {
				visualizationPanel = new JPanel(new BorderLayout());
				tabbedPane.insertTab(strings.getString("VISUALIZATION_TAB"), null, visualizationPanel, null,
						tabbedPane.indexOfComponent(globalSettingsScrollPane));

				final var modes = input.getProfile().getModes();
				modeComboBox = GuiUtils.addModePanel(visualizationPanel, modes, new AbstractAction() {

					private static final long serialVersionUID = -9107064465015662054L;

					@SuppressWarnings("unchecked")
					@Override
					public void actionPerformed(final ActionEvent e) {
						final var selectedMode = (Mode) ((JComboBox<Mode>) e.getSource()).getSelectedItem();
						final var darkTheme = lookAndFeel instanceof FlatDarkLaf;

						final var workingCopySvgDocument = generateSvgDocument(selectedMode, darkTheme);

						svgCanvas.setSVGDocument(workingCopySvgDocument);
					}
				});

				svgCanvas = new JSVGCanvas(null, false, false);
				svgCanvas.setBackground(TRANSPARENT);
				visualizationPanel.add(new JScrollPane(svgCanvas), BorderLayout.CENTER);

				final var exportPanel = new JPanel(LOWER_BUTTONS_FLOW_LAYOUT);
				final var exportButton = new JButton(new ExportAction());
				exportButton.setPreferredSize(BUTTON_DIMENSION);
				exportPanel.add(exportButton);
				visualizationPanel.add(exportPanel, BorderLayout.SOUTH);
			}

			profileSettingsPanel = new JPanel();
			profileSettingsPanel.setLayout(new GridBagLayout());

			profileSettingsScrollPane = new JScrollPane(profileSettingsPanel);
			tabbedPane.insertTab(strings.getString("PROFILE_SETTINGS_TAB"), null, profileSettingsScrollPane, null,
					tabbedPane.indexOfComponent(globalSettingsScrollPane));
		} else
			log.log(Level.INFO, "No controllers connected");

		updateDeviceMenuSelection();

		if (selectFirstTab || !controllerConnected)
			tabbedPane.setSelectedIndex(0);
		else if (previousSelectedTabIndex < tabbedPane.getTabCount())
			tabbedPane.setSelectedIndex(previousSelectedTabIndex);

		updateMenuShortcuts();
		updateModesPanel();
		updateVisualizationPanel();
		updateOverlayPanel();
		updateProfileSettingsPanel();
		updatePanelAccess();

		frame.getContentPane().invalidate();
		frame.getContentPane().repaint();
	}

	private void onOutputChanged() {
		final var running = isRunning();

		if (startLocalJMenuItem != null)
			startLocalJMenuItem.setEnabled(!running);

		if (startClientJMenuItem != null)
			startClientJMenuItem.setEnabled(!running);

		if (startServerJMenuItem != null)
			startServerJMenuItem.setEnabled(!running);

		if (stopJMenuItem != null)
			stopJMenuItem.setEnabled(running);

		if (startLocalMenuItem != null)
			startLocalMenuItem.setEnabled(!running);

		if (startClientMenuItem != null)
			startClientMenuItem.setEnabled(!running);

		if (startServerMenuItem != null)
			startServerMenuItem.setEnabled(!running);

		if (stopMenuItem != null)
			stopMenuItem.setEnabled(running);

		updateMenuShortcuts();
		updatePanelAccess();
	}

	public boolean preventPowerSaveMode() {
		return preferences.getBoolean(PREFERENCES_PREVENT_POWER_SAVE_MODE, true);
	}

	private void quit() {
		if (serverSocket != null)
			try {
				serverSocket.close();
			} catch (final IOException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}

		if (input != null)
			input.deInit(false);

		stopAll(true, false, false);

		taskRunner.shutdown();

		taskRunner.run(GLFW::glfwTerminate);

		try {
			unique.releaseLock();
		} catch (final Unique4jException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		if (!shutdownHookTriggered) {
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
			terminate(0);
		}
	}

	private void repaintOnScreenKeyboard() {
		if (!onScreenKeyboard.isVisible())
			return;

		onScreenKeyboard.getContentPane().validate();
		onScreenKeyboard.getContentPane().repaint();
	}

	private void repaintOverlay() {
		if (overlayFrame == null)
			return;

		overlayFrame.getContentPane().validate();
		overlayFrame.getContentPane().repaint();
	}

	public void restartLast() {
		switch (lastOutputType) {
		case LOCAL -> startLocal();
		case CLIENT -> startClient();
		case SERVER -> startServer();
		case NONE -> {
		}
		}
	}

	private void saveLastProfile(final File file) {
		currentFile = file;
		preferences.put(PREFERENCES_LAST_PROFILE, file.getAbsolutePath());
	}

	private void saveProfile() {
		if (currentFile != null)
			saveProfile(currentFile, true);
		else
			saveProfileAs();
	}

	private void saveProfile(File file, final boolean saveAsLastProfile) {
		input.reset();

		if (!file.getName().toLowerCase(Locale.ROOT).endsWith(PROFILE_FILE_SUFFIX))
			file = new File(file.getAbsoluteFile() + PROFILE_FILE_SUFFIX);

		log.log(Level.INFO, "Saving profile: " + file.getAbsolutePath());

		final var profile = input.getProfile();
		profile.setVersion(VersionUtils.getMajorAndMinorVersion());

		final var jsonString = JsonContext.create().gson.toJson(profile);
		try {
			Files.writeString(file.toPath(), jsonString);

			if (saveAsLastProfile)
				saveLastProfile(file);

			loadedProfile = file.getName();
			setUnsavedChanges(false);
			setStatusBarText(MessageFormat.format(strings.getString("STATUS_PROFILE_SAVED"), file.getAbsolutePath()));
			scheduleStatusBarText(strings.getString("STATUS_READY"));
		} catch (final IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			GuiUtils.showMessageDialog(frame, strings.getString("COULD_NOT_SAVE_PROFILE_DIALOG_TEXT"),
					strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveProfileAs() {
		profileFileChooser.setSelectedFile(currentFile != null ? currentFile : new File("*." + PROFILE_FILE_EXTENSION));
		if (profileFileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
			saveProfile(profileFileChooser.getSelectedFile(), true);
	}

	public void scheduleStatusBarText(final String text) {
		class StatusBarTextTimerTask extends TimerTask {

			private final String newText;
			private final String originalText;

			private StatusBarTextTimerTask(final String newText) {
				this.newText = newText;
				originalText = statusLabel.getText();
			}

			@Override
			public void run() {
				EventQueue.invokeLater(() -> {
					if (statusLabel.getText().equals(originalText))
						setStatusBarText(newText);
				});
			}
		}

		timer.schedule(new StatusBarTextTimerTask(text), 5000L);
	}

	public void setOnScreenKeyboardVisible(final boolean visible) {
		if (isLocalRunning() || isServerRunning())
			EventQueue.invokeLater(() -> {
				onScreenKeyboard.setVisible(visible);
				repaintOnScreenKeyboard();
				repaintOverlay();
			});
	}

	public void setOverlayText(final String text) {
		if (currentModeLabel != null)
			GuiUtils.invokeOnEventDispatchThreadIfRequired(() -> {
				if (currentModeLabel != null)
					currentModeLabel.setText(text);
			});
	}

	private void setSelectedController(final ControllerInfo controller) {
		selectedController = controller;

		log.log(Level.INFO, assembleControllerLoggingMessage("Selected controller", controller));

		if (controller.guid != null)
			preferences.put(PREFERENCES_LAST_CONTROLLER, controller.guid);
	}

	public void setSelectedControllerAndUpdateInput(final ControllerInfo controller,
			final EnumMap<VirtualAxis, Integer> axes) {
		stopAll(true, false, true);

		setSelectedController(controller);

		Profile previousProfile = null;
		if (input != null) {
			input.deInit(false);
			previousProfile = input.getProfile();
		}

		input = new Input(Main.this, selectedController, axes);

		if (previousProfile != null)
			input.setProfile(previousProfile);
	}

	public void setStatusBarText(final String text) {
		if (statusLabel != null) {
			statusLabel.setText(text);
			repaintOverlay();
		}
	}

	void setUnsavedChanges(final boolean unsavedChanges) {
		this.unsavedChanges = unsavedChanges;
		updateTitleAndTooltip();
	}

	private void startClient() {
		if (isRunning())
			return;

		lastOutputType = OutputType.CLIENT;
		final var clientThread = new ClientOutput(Main.this, input);
		clientThread
				.setvJoyDevice(new UINT(preferences.getInt(PREFERENCES_VJOY_DEVICE, VJoyOutput.DEFAULT_VJOY_DEVICE)));
		clientThread.setHost(hostTextField.getText());
		clientThread.setPort(preferences.getInt(PREFERENCES_PORT, ServerOutput.DEFAULT_PORT));
		clientThread.setTimeout(preferences.getInt(PREFERENCES_TIMEOUT, ServerOutput.DEFAULT_TIMEOUT));

		output = clientThread;
		taskRunner.run(clientThread);

		onOutputChanged();
	}

	private void startLocal() {
		if (selectedController == null || isRunning())
			return;

		lastOutputType = OutputType.LOCAL;
		final var localThread = new LocalOutput(Main.this, input);
		localThread
				.setvJoyDevice(new UINT(preferences.getInt(PREFERENCES_VJOY_DEVICE, VJoyOutput.DEFAULT_VJOY_DEVICE)));
		localThread.setPollInterval(preferences.getInt(PREFERENCES_POLL_INTERVAL, Output.DEFAULT_POLL_INTERVAL));

		output = localThread;
		taskRunner.run(localThread);

		onOutputChanged();

		initOverlay();
		initVrOverlay();
		startOverlayTimerTask();
	}

	private void startOverlayTimerTask() {
		stopOverlayTimerTask();

		overlayTimerTask = new TimerTask() {

			@Override
			public void run() {
				EventQueue.invokeLater(() -> {
					if (!isModalDialogShowing()) {
						if (overlayFrame != null)
							GuiUtils.makeWindowTopmost(overlayFrame);

						if (onScreenKeyboard.isVisible())
							GuiUtils.makeWindowTopmost(onScreenKeyboard);
					}

					final var totalDisplayBounds = GuiUtils.getTotalDisplayBounds();
					if (!totalDisplayBounds.equals(prevTotalDisplayBounds)) {
						prevTotalDisplayBounds = totalDisplayBounds;

						if (overlayFrame != null && overlayFrameDragListener != null
								&& !overlayFrameDragListener.isDragging()) {
							deInitOverlay();
							initOverlay();
						}

						onScreenKeyboard.updateLocation();
					}

					repaintOverlay();
					repaintOnScreenKeyboard();
				});
			}
		};

		timer.schedule(overlayTimerTask, OVERLAY_POSITION_UPDATE_INTERVAL, OVERLAY_POSITION_UPDATE_INTERVAL);
	}

	private void startServer() {
		if (selectedController == null || isRunning())
			return;

		lastOutputType = OutputType.SERVER;
		final var serverThread = new ServerOutput(Main.this, input);
		serverThread.setPort(preferences.getInt(PREFERENCES_PORT, ServerOutput.DEFAULT_PORT));
		serverThread.setTimeout(preferences.getInt(PREFERENCES_TIMEOUT, ServerOutput.DEFAULT_TIMEOUT));
		serverThread.setPollInterval(preferences.getInt(PREFERENCES_POLL_INTERVAL, Output.DEFAULT_POLL_INTERVAL));

		output = serverThread;
		taskRunner.run(serverThread);

		onOutputChanged();

		initOverlay();
		startOverlayTimerTask();
	}

	public void stopAll(final boolean initiateStop, final boolean resetLastOutputType,
			final boolean performGarbageCollection) {
		if (isWindows) {
			stopLocal(initiateStop, resetLastOutputType);
			stopClient(initiateStop, resetLastOutputType);
		}
		stopServer(initiateStop, resetLastOutputType);

		if (performGarbageCollection)
			System.gc();
	}

	private void stopClient(final boolean initiateStop, final boolean resetLastOutputType) {
		final var running = isClientRunning();

		if (initiateStop && running)
			taskRunner.stopTask();

		if (running)
			taskRunner.waitForTask();

		if (resetLastOutputType)
			lastOutputType = OutputType.NONE;

		GuiUtils.invokeOnEventDispatchThreadIfRequired(() -> {
			onOutputChanged();
		});
	}

	private void stopLocal(final boolean initiateStop, final boolean resetLastOutputType) {
		final var running = isLocalRunning();

		if (initiateStop && running)
			taskRunner.stopTask();

		if (running)
			taskRunner.waitForTask();

		if (resetLastOutputType)
			lastOutputType = OutputType.NONE;

		GuiUtils.invokeOnEventDispatchThreadIfRequired(() -> {
			stopOverlayTimerTask();
			deInitOverlayAndHideOnScreenKeyboard();
			onOutputChanged();
		});
	}

	private void stopOverlayTimerTask() {
		if (overlayTimerTask != null)
			overlayTimerTask.cancel();
	}

	private void stopServer(final boolean initiateStop, final boolean resetLastOutputType) {
		final var running = output instanceof ServerOutput;

		if (initiateStop && running)
			((ServerOutput) output).close();

		if (running)
			taskRunner.waitForTask();

		if (resetLastOutputType)
			lastOutputType = OutputType.NONE;

		GuiUtils.invokeOnEventDispatchThreadIfRequired(() -> {
			stopOverlayTimerTask();
			deInitOverlayAndHideOnScreenKeyboard();
			onOutputChanged();
		});
	}

	public void updateDeviceMenuSelection() {
		for (var i = 0; i < deviceJMenu.getItemCount(); i++) {
			final var menuItem = deviceJMenu.getItem(i);
			final var action = (SelectControllerAction) menuItem.getAction();
			if (selectedController.jid == action.controller.jid) {
				menuItem.setSelected(true);
				break;
			}
		}
	}

	private boolean updateGameControllerMappings(final InputStream is) {
		if (is == null)
			return false;

		var mappingsUpdated = false;

		final var defaultCharset = Charset.defaultCharset();
		try (var bufferedReader = new BufferedReader(new InputStreamReader(is, defaultCharset))) {
			final var sb = new StringBuilder();

			while (bufferedReader.ready()) {
				final var line = bufferedReader.readLine();
				if (line != null) {
					sb.append(line);
					sb.append("\n");
				}
			}

			if (sb.charAt(sb.length() - 1) != 0)
				sb.append((char) 0);

			final var content = sb.toString().getBytes(defaultCharset);
			final var byteBuffer = ByteBuffer.allocateDirect(content.length).put(content).flip();
			mappingsUpdated = taskRunner.run(() -> GLFW.glfwUpdateGamepadMappings(byteBuffer));
		} catch (final IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		return mappingsUpdated;
	}

	private boolean updateGameControllerMappingsFromFile(final String path) {
		if (isLocalRunning())
			stopLocal(true, false);
		else if (isServerRunning())
			stopServer(true, false);

		var mappingsUpdated = false;

		try (var fileInputStream = new FileInputStream(path)) {
			mappingsUpdated = updateGameControllerMappings(fileInputStream);

			log.log(mappingsUpdated ? Level.INFO : Level.WARNING,
					(mappingsUpdated ? "Successfully updated" : "Failed to update")
							+ " game controller mappings from external file: " + path);
		} catch (final FileNotFoundException e) {
			log.log(Level.WARNING, "Could not read external game controller mappings file: " + path);

			GuiUtils.showMessageDialog(frame, MessageFormat
					.format(strings.getString("COULD_NOT_READ_GAME_CONTROLLER_MAPPINGS_FILE_DIALOG_TEXT"), path),
					strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
		} catch (final IOException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}

		if (!mappingsUpdated) {
			log.log(Level.WARNING, "An error occurred while updating the SDL game controller mappings");

			GuiUtils.showMessageDialog(frame, strings.getString("ERROR_UPDATING_GAME_CONTROLLER_DB_DIALOG_TEXT"),
					strings.getString("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
		}

		return mappingsUpdated;
	}

	private void updateMenuShortcuts() {
		final var menuCount = menuBar.getMenuCount();
		final var alreadyAssignedMenuKeyCodes = new HashSet<Integer>(menuCount);

		for (var i = 0; i < menuCount; i++) {
			final var menu = menuBar.getMenu(i);
			menu.setMnemonic(getExtendedKeyCodeForMenu(menu, alreadyAssignedMenuKeyCodes));

			final var menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
			for (var j = 0; j < menu.getItemCount(); j++) {
				final var menuItem = menu.getItem(j);
				if (menuItem != null) {
					KeyStroke keyStroke = null;

					if (menuItem.isEnabled()) {
						final var keyCode = getExtendedKeyCodeForMenuItem(menuItem);

						if (keyCode != KeyEvent.VK_UNDEFINED)
							keyStroke = KeyStroke.getKeyStroke(keyCode, menuShortcutKeyMask);
					}

					menuItem.setAccelerator(keyStroke);
				}
			}
		}
	}

	void updateModesPanel() {
		if (modesListPanel == null)
			return;

		modesListPanel.removeAll();

		final var buttonGridBagConstraints = new GridBagConstraints(4, GridBagConstraints.RELATIVE, 1, 1, 0d, 0d,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0);

		final var modes = input.getProfile().getModes();
		for (var i = 0; i < modes.size(); i++) {
			final var mode = modes.get(i);

			final var modePanel = new JPanel(new GridBagLayout());
			modePanel.setBorder(LIST_ITEM_BORDER);
			modesListPanel.add(modePanel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0d, 0d,
					GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, LIST_ITEM_INSETS, 0, 0));

			final var modeNoLabel = new JLabel(MessageFormat.format(strings.getString("MODE_LABEL_NO"), i + 1));
			modeNoLabel.setPreferredSize(new Dimension(100, 15));
			modePanel.add(modeNoLabel, new GridBagConstraints(0, 0, 1, 1, 0d, 0d, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));

			modePanel.add(Box.createGlue(), new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 1d, 1d,
					GridBagConstraints.CENTER, GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));

			final var descriptionTextField = new JTextField(mode.getDescription(), 20);
			descriptionTextField.setCaretPosition(0);
			modePanel.add(descriptionTextField, new GridBagConstraints(2, 0, 1, 1, 1d, 1d, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));

			final var setModeDescriptionAction = new SetModeDescriptionAction(mode, descriptionTextField);
			descriptionTextField.addActionListener(setModeDescriptionAction);
			descriptionTextField.getDocument().addDocumentListener(setModeDescriptionAction);

			modePanel.add(Box.createGlue(), new GridBagConstraints(3, GridBagConstraints.RELATIVE, 1, 1, 1d, 1d,
					GridBagConstraints.CENTER, GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));

			if (Profile.defaultMode.equals(mode) || OnScreenKeyboard.onScreenKeyboardMode.equals(mode)) {
				descriptionTextField.setEditable(false);
				descriptionTextField.setFocusable(false);
				modePanel.add(Box.createHorizontalStrut(BUTTON_DIMENSION.width), buttonGridBagConstraints);
			} else {
				final var deleteButton = new JButton(new RemoveModeAction(mode));
				deleteButton.setPreferredSize(BUTTON_DIMENSION);
				modePanel.add(deleteButton, buttonGridBagConstraints);
			}
		}

		modesListPanel.add(Box.createGlue(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1d, 1d,
				GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		modesScrollPane.setViewportView(modesListPanel);
	}

	private void updateOverlayAlignment(final Rectangle totalDisplayBounds) {
		final var inLowerHalf = overlayFrame.getY() + overlayFrame.getHeight() / 2 < totalDisplayBounds.height / 2;

		if (currentModeLabel != null) {
			overlayFrame.remove(currentModeLabel);
			overlayFrame.add(currentModeLabel, inLowerHalf ? BorderLayout.PAGE_START : BorderLayout.PAGE_END);
		}

		var alignment = SwingConstants.RIGHT;
		var flowLayoutAlignment = FlowLayout.RIGHT;
		if (overlayFrame.getX() + overlayFrame.getWidth() / 2 < totalDisplayBounds.width / 2) {
			alignment = SwingConstants.LEFT;
			flowLayoutAlignment = FlowLayout.LEFT;
		}

		if (currentModeLabel != null)
			currentModeLabel.setHorizontalAlignment(alignment);

		indicatorPanelFlowLayout.setAlignment(flowLayoutAlignment);
		indicatorPanel.invalidate();

		overlayFrame.setBackground(TRANSPARENT);
		overlayFrame.pack();
	}

	public void updateOverlayAxisIndicators() {
		if (output == null || !isLocalRunning() && !isServerRunning())
			return;

		EnumSet.allOf(Input.VirtualAxis.class).stream()
				.filter(virtualAxis -> virtualAxisToProgressBarMap.containsKey(virtualAxis)).forEach(virtualAxis -> {
					final var progressBar = virtualAxisToProgressBarMap.get(virtualAxis);
					var changed = false;

					final var newMinimum = -output.getMaxAxisValue();
					if (progressBar.getMinimum() != newMinimum) {
						progressBar.setMinimum(newMinimum);
						changed = true;
					}

					final var newMaximum = output.getMinAxisValue();
					if (progressBar.getMaximum() != newMaximum) {
						progressBar.setMaximum(newMaximum);
						changed = true;
					}

					final var newValue = -input.getAxes().get(virtualAxis);
					if (progressBar.getValue() != newValue) {
						progressBar.setValue(newValue);
						changed = true;
					}

					if (changed)
						repaintOverlay();
				});
	}

	private void updateOverlayPanel() {
		if (indicatorsListPanel == null)
			return;

		indicatorsListPanel.removeAll();

		final var borderColor = UIManager.getColor("Component.borderColor");

		EnumSet.allOf(Input.VirtualAxis.class).forEach(virtualAxis -> {
			final var indicatorPanel = new JPanel(new GridBagLayout());
			indicatorPanel.setBorder(LIST_ITEM_BORDER);
			indicatorsListPanel.add(indicatorPanel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0d, 0d,
					GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, LIST_ITEM_INSETS, 0, 0));

			final var virtualAxisLabel = new JLabel(
					MessageFormat.format(strings.getString("AXIS_LABEL"), virtualAxis.toString()));
			virtualAxisLabel.setPreferredSize(new Dimension(100, 15));
			indicatorPanel.add(virtualAxisLabel, new GridBagConstraints(0, 0, 1, 1, 0d, 0d, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));

			final var virtualAxisToOverlayAxisMap = input.getProfile().getVirtualAxisToOverlayAxisMap();
			final var overlayAxis = virtualAxisToOverlayAxisMap.get(virtualAxis);
			final var enabled = overlayAxis != null;

			final var colorLabel = new JLabel();
			if (enabled) {
				colorLabel.setOpaque(true);
				colorLabel.setBackground(overlayAxis.color);
			} else
				colorLabel.setText(strings.getString("INDICATOR_DISABLED_LABEL"));
			colorLabel.setHorizontalAlignment(SwingConstants.CENTER);

			colorLabel.setPreferredSize(new Dimension(100, 15));
			colorLabel.setBorder(BorderFactory.createLineBorder(borderColor));
			indicatorPanel.add(colorLabel, new GridBagConstraints(1, 0, 1, 1, 1d, 0d, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));

			final var colorButton = new JButton(new SelectIndicatorColorAction(virtualAxis));
			colorButton.setPreferredSize(BUTTON_DIMENSION);
			colorButton.setEnabled(enabled);
			indicatorPanel.add(colorButton, new GridBagConstraints(2, 0, 1, 1, 1d, 0d, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));

			final var invertedCheckBox = new JCheckBox(new InvertIndicatorAction(virtualAxis));
			invertedCheckBox.setSelected(enabled && overlayAxis.inverted);
			invertedCheckBox.setEnabled(enabled);
			indicatorPanel.add(invertedCheckBox, new GridBagConstraints(3, 0, 1, 1, 1d, 0d, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));

			final var displayCheckBox = new JCheckBox(new DisplayIndicatorAction(virtualAxis));
			displayCheckBox.setSelected(enabled);
			indicatorPanel.add(displayCheckBox, new GridBagConstraints(4, GridBagConstraints.RELATIVE, 1, 1, 0d, 0d,
					GridBagConstraints.CENTER, GridBagConstraints.NONE, LIST_ITEM_INNER_INSETS, 0, 0));
		});

		indicatorsListPanel.add(Box.createGlue(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1d, 1d,
				GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		indicatorsScrollPane.setViewportView(indicatorsListPanel);
	}

	private void updatePanelAccess() {
		final var panelsEnabled = !isRunning();

		GuiUtils.setEnabledRecursive(modesListPanel, panelsEnabled);
		GuiUtils.setEnabledRecursive(newModePanel, panelsEnabled);

		if (assignmentsComponent != null)
			assignmentsComponent.setEnabled(panelsEnabled);

		if (!panelsEnabled || input != null && !input.getProfile().isShowOverlay())
			GuiUtils.setEnabledRecursive(indicatorsListPanel, false);
		else
			updateOverlayPanel();

		GuiUtils.setEnabledRecursive(profileSettingsPanel, panelsEnabled);
		GuiUtils.setEnabledRecursive(globalSettingsPanel, panelsEnabled);
	}

	private void updateProfileSettingsPanel() {
		if (profileSettingsPanel == null)
			return;

		profileSettingsPanel.removeAll();
		showVrOverlayCheckBox = null;

		final var keyRepeatIntervalPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
		profileSettingsPanel.add(keyRepeatIntervalPanel, settingsPanelGridBagConstraints);

		final var keyRepeatIntervalLabel = new JLabel(strings.getString("KEY_REPEAT_INTERVAL_LABEL"));
		keyRepeatIntervalLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
		keyRepeatIntervalPanel.add(keyRepeatIntervalLabel);

		final var profile = input.getProfile();

		final var keyRepeatIntervalSpinner = new JSpinner(
				new SpinnerNumberModel((int) profile.getKeyRepeatInterval(), 0, 1000, 1));
		final var keyRepeatIntervalEditor = new JSpinner.NumberEditor(keyRepeatIntervalSpinner,
				"# " + strings.getString("MILLISECOND_SYMBOL"));
		((DefaultFormatter) keyRepeatIntervalEditor.getTextField().getFormatter()).setCommitsOnValidEdit(true);
		keyRepeatIntervalSpinner.setEditor(keyRepeatIntervalEditor);
		keyRepeatIntervalSpinner.addChangeListener(event -> {
			final var keyRepeatInterval = (int) ((JSpinner) event.getSource()).getValue();
			input.getProfile().setKeyRepeatInterval(keyRepeatInterval);
			setUnsavedChanges(true);
		});
		keyRepeatIntervalPanel.add(keyRepeatIntervalSpinner);

		if (Toolkit.getDefaultToolkit().isAlwaysOnTopSupported()) {
			final var overlaySettingsPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
			profileSettingsPanel.add(overlaySettingsPanel, settingsPanelGridBagConstraints);

			final var overlayLabel = new JLabel(strings.getString("OVERLAY_LABEL"));
			overlayLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
			overlaySettingsPanel.add(overlayLabel);

			final var showOverlayCheckBox = new JCheckBox(strings.getString("SHOW_OVERLAY_CHECK_BOX"));
			showOverlayCheckBox.setSelected(profile.isShowOverlay());
			showOverlayCheckBox.addActionListener(event -> {
				final var showOverlay = ((JCheckBox) event.getSource()).isSelected();
				profile.setShowOverlay(showOverlay);
				if (!showOverlay)
					profile.setShowVrOverlay(false);
				if (showVrOverlayCheckBox != null) {
					showVrOverlayCheckBox.setEnabled(showOverlay);
					if (!showOverlay)
						showVrOverlayCheckBox.setSelected(false);
				}
				updatePanelAccess();
				setUnsavedChanges(true);
			});
			overlaySettingsPanel.add(showOverlayCheckBox);

			if (isWindows) {
				final var vrOverlaySettingsPanel = new JPanel(DEFAULT_FLOW_LAYOUT);
				profileSettingsPanel.add(vrOverlaySettingsPanel, settingsPanelGridBagConstraints);

				final var vrOverlayLabel = new JLabel(strings.getString("VR_OVERLAY_LABEL"));
				vrOverlayLabel.setPreferredSize(SETTINGS_LABEL_DIMENSION);
				vrOverlaySettingsPanel.add(vrOverlayLabel);

				showVrOverlayCheckBox = new JCheckBox(strings.getString("SHOW_VR_OVERLAY_CHECK_BOX"));
				showVrOverlayCheckBox.setSelected(profile.isShowVrOverlay());
				showVrOverlayCheckBox.addActionListener(event -> {
					final var showVrOverlay = ((JCheckBox) event.getSource()).isSelected();
					profile.setShowVrOverlay(showVrOverlay);
					setUnsavedChanges(true);
				});
				vrOverlaySettingsPanel.add(showVrOverlayCheckBox);
			}
		}

		profileSettingsPanel.add(Box.createGlue(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1d, 1d,
				GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	private void updateSvgElements(final SVGDocument svgDocument, final String idPrefix,
			final List<? extends IAction<?>> actions, final boolean darkTheme) {
		final var groupElement = (SVGStylableElement) svgDocument.getElementById(idPrefix + "Group");

		final var hide = actions == null || actions.isEmpty();
		groupElement.getStyle().setProperty(CSSConstants.CSS_DISPLAY_PROPERTY,
				hide ? CSSConstants.CSS_NONE_VALUE : CSSConstants.CSS_INLINE_VALUE, "");

		if (hide)
			return;

		final var textContent = actions.stream().map(action -> action.getDescription(input)).distinct()
				.collect(Collectors.joining(", "));
		final var textElement = (SVGStylableElement) svgDocument.getElementById(idPrefix + "Text");
		final var tSpanElement = textElement.getFirstChild();
		tSpanElement.setTextContent(textContent);

		if (darkTheme) {
			textElement.getStyle().setProperty(CSSConstants.CSS_FILL_PROPERTY, SVG_DARK_THEME_TEXT_COLOR, "");

			final var pathElement = (SVGStylableElement) svgDocument.getElementById(idPrefix + "Path");
			pathElement.getStyle().setProperty(CSSConstants.CSS_STROKE_PROPERTY, SVG_DARK_THEME_PATH_COLOR, "");
		}

		final var rootElement = svgDocument.getRootElement();
		final var bBox = rootElement.getBBox();

		final var halfMargin = SVG_VIEWBOX_MARGIN / 2;

		final var viewBoxX = bBox.getX() - halfMargin;
		final var viewBoxY = bBox.getY() - halfMargin;
		final var viewBoxWidth = bBox.getWidth() + SVG_VIEWBOX_MARGIN;
		final var viewBoxHeight = bBox.getHeight() + SVG_VIEWBOX_MARGIN;

		rootElement.setAttributeNS(null, "viewBox",
				viewBoxX + " " + viewBoxY + " " + viewBoxWidth + " " + viewBoxHeight);
	}

	private void updateTheme() {
		lookAndFeel = preferences.getBoolean(PREFERENCES_DARK_THEME, false) ? new FlatDarkLaf() : new FlatLightLaf();

		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (final UnsupportedLookAndFeelException e) {
			throw new RuntimeException(e);
		}

		FlatLaf.updateUI();

		updateVisualizationPanel();
	}

	public void updateTitleAndTooltip() {
		final String title;

		if (selectedController == null)
			title = strings.getString("APPLICATION_NAME");
		else {
			final var profileTitle = (unsavedChanges ? "*" : "")
					+ (loadedProfile != null ? loadedProfile : strings.getString("UNTITLED"));
			title = MessageFormat.format(strings.getString("MAIN_FRAME_TITLE"), profileTitle);
		}

		frame.setTitle(title);

		if (trayIcon != null && input != null) {
			final String toolTip;

			final var sonyExtension = input.getSonyExtension();
			if (sonyExtension != null)
				toolTip = MessageFormat.format(strings.getString(
						sonyExtension.isCharging() ? "BATTERY_TOOLTIP_CHARGING" : "BATTERY_TOOLTIP_DISCHARGING"), title,
						sonyExtension.getBatteryState() / 100f);
			else
				toolTip = title;

			trayIcon.setToolTip(toolTip);
		}
	}

	void updateVisualizationPanel() {
		if (visualizationPanel == null)
			return;

		final var modes = input.getProfile().getModes();
		final var model = new DefaultComboBoxModel<>(modes.toArray(new Mode[modes.size()]));
		modeComboBox.setModel(model);
		modeComboBox.setSelectedIndex(model.getSize() > 0 ? 0 : -1);
	}
}
