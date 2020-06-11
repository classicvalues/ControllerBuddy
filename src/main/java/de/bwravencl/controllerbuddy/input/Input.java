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

package de.bwravencl.controllerbuddy.input;

import static de.bwravencl.controllerbuddy.gui.Main.strings;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickGUID;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.lwjgl.glfw.GLFWGamepadState;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;
import de.bwravencl.controllerbuddy.gui.Main;
import de.bwravencl.controllerbuddy.gui.OnScreenKeyboard;
import de.bwravencl.controllerbuddy.input.action.ButtonToModeAction;
import de.bwravencl.controllerbuddy.input.action.IButtonToAction;
import de.bwravencl.controllerbuddy.input.action.IInitializationAction;
import de.bwravencl.controllerbuddy.input.action.IResetableAction;
import de.bwravencl.controllerbuddy.output.OutputThread;

public final class Input {

	public enum VirtualAxis {
		X, Y, Z, RX, RY, RZ, S0, S1
	}

	private static final Logger log = Logger.getLogger(Input.class.getName());

	private static final int LOW_BATTERY_WARNING = 20;
	private static final float AXIS_MOVEMENT_MIN_DELTA_FACTOR = 0.1f;
	private static final float AXIS_MOVEMENT_MAX_DELTA_FACTOR = 4f;
	private static final float ABORT_SUSPENSION_ACTION_DEADZONE = 0.25f;
	private static final long SUSPENSION_TIME = 500L;

	public static final int MAX_N_BUTTONS = 128;
	private static final byte[] DUAL_SHOCK_4_HID_REPORT = new byte[] { (byte) 0x05, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00,
			(byte) 0x0C, (byte) 0x18, (byte) 0x1C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	private static final int hidReportOffset = Main.windows ? 1 : 0;

	private static float clamp(final float v) {
		return Math.max(Math.min(v, 1f), -1f);
	}

	private static double correctNumericalImprecision(final double d) {
		if (d < 0.0000001)
			return 0d;
		else
			return d;
	}

	private static boolean isValidButton(final int button) {
		return button >= 0 && button <= GLFW_GAMEPAD_BUTTON_LAST;
	}

	private static void mapCircularAxesToSquareAxes(final GLFWGamepadState state, final int xAxisIndex,
			final int yAxisIndex) {
		final var u = clamp(state.axes(xAxisIndex));
		final var v = clamp(state.axes(yAxisIndex));

		final var u2 = u * u;
		final var v2 = v * v;

		final var subtermX = 2d + u2 - v2;
		final var subtermY = 2d - u2 + v2;

		final double twoSqrt2 = 2d * Math.sqrt(2d);

		var termX1 = subtermX + u * twoSqrt2;
		var termX2 = subtermX - u * twoSqrt2;
		var termY1 = subtermY + v * twoSqrt2;
		var termY2 = subtermY - v * twoSqrt2;

		termX1 = correctNumericalImprecision(termX1);
		termY1 = correctNumericalImprecision(termY1);
		termX2 = correctNumericalImprecision(termX2);
		termY2 = correctNumericalImprecision(termY2);

		final var x = 0.5 * Math.sqrt(termX1) - 0.5 * Math.sqrt(termX2);
		final var y = 0.5 * Math.sqrt(termY1) - 0.5 * Math.sqrt(termY2);

		state.axes(xAxisIndex, clamp((float) x));
		state.axes(yAxisIndex, clamp((float) y));
	}

	public static float normalize(final float value, final float inMin, final float inMax, final float outMin,
			final float outMax) {
		final float newValue;
		final var oldRange = inMax - inMin;

		if (oldRange == 0f)
			newValue = outMin;
		else {
			final var newRange = outMax - outMin;
			newValue = (value - inMin) * newRange / oldRange + outMin;
		}

		return newValue;
	}

	private final Main main;
	private final int jid;
	private final EnumMap<VirtualAxis, Integer> axes = new EnumMap<>(VirtualAxis.class);
	private Profile profile;
	private OutputThread outputThread;
	private boolean[] buttons;
	private volatile int cursorDeltaX = 5;
	private volatile int cursorDeltaY = 5;
	private volatile int scrollClicks = 0;
	private final Set<Integer> downMouseButtons = ConcurrentHashMap.newKeySet();
	private final Set<Integer> downUpMouseButtons = new HashSet<>();
	private final Set<KeyStroke> downKeyStrokes = new HashSet<>();
	private final Set<KeyStroke> downUpKeyStrokes = new HashSet<>();
	private final Set<Integer> onLockKeys = new HashSet<>();
	private final Set<Integer> offLockKeys = new HashSet<>();
	private boolean clearOnNextPoll = false;
	private boolean repeatModeActionWalk = false;
	private HidDevice hidDevice;
	private volatile boolean charging = true;
	private volatile int batteryState;
	private byte[] dualShock4HidReport;
	private final Map<VirtualAxis, Integer> virtualAxisToTargetValueMap = new HashMap<>();
	private long lastCallTime = 0L;
	private float rateMultiplier = 0f;
	private final Map<Integer, Long> axisToEndSuspensionTimestampMap = new HashMap<>();

	public Input(final Main main, final int jid) {
		this.main = main;
		this.jid = jid;

		for (final var virtualAxis : VirtualAxis.values())
			axes.put(virtualAxis, 0);

		profile = new Profile();

		final Short dualShock4ProductId = getDualShock4ProductId();
		if (dualShock4ProductId != null) {
			final var dualShock4Devices = PureJavaHidApi.enumerateDevices().stream()
					.filter(hidDeviceInfo -> hidDeviceInfo.getVendorId() == (short) 0x54C
							&& hidDeviceInfo.getProductId() == dualShock4ProductId)
					.collect(Collectors.toUnmodifiableList());
			final var count = dualShock4Devices.size();

			if (count > 0) {
				log.log(Level.INFO, "Found " + count + " DualShock 4 controller(s): "
						+ dualShock4Devices.stream().map(HidDeviceInfo::getDeviceId).collect(Collectors.joining(", ")));

				if (count > 1)
					JOptionPane.showMessageDialog(main.getFrame(),
							strings.getString("MULTIPLE_DUAL_SHOCK_4_CONTROLLERS_CONNECTED_DIALOG_TEXT"),
							strings.getString("WARNING_DIALOG_TITLE"), JOptionPane.WARNING_MESSAGE);

				final var hidDeviceInfo = dualShock4Devices.get(0);
				try {
					log.log(Level.INFO, "Using DualShock 4 controller " + hidDeviceInfo.getDeviceId());

					hidDevice = PureJavaHidApi.openDevice(hidDeviceInfo);
					resetDualShock4();

					hidDevice.setInputReportListener(new InputReportListener() {

						private static final int TOUCHPAD_MAX_DELTA = 150;
						private static final float TOUCHPAD_CURSOR_SENSITIVITY = 1.25f;
						private static final float TOUCHPAD_SCROLL_SENSITIVITY = 0.25f;

						private boolean prevTouchpadButtonDown;
						private boolean prevDown1;
						private boolean prevDown2;
						private int prevX1;
						private int prevY1;

						@Override
						public void onInputReport(final HidDevice source, final byte reportID, final byte[] reportData,
								final int reportLength) {
							if (reportID != 0x01 || reportData.length != 64) {
								log.log(Level.WARNING, "Received unknown HID input report with ID " + reportID
										+ " and length " + reportLength);
								return;
							}

							final var cableConnected = (reportData[30] >> 4 & 0x01) != 0;
							var battery = reportData[30] & 0x0F;

							setCharging(cableConnected);

							if (!cableConnected)
								battery++;

							battery = Math.min(battery, 10);
							battery *= 10;

							setBatteryState(battery);

							if (!main.isLocalThreadActive() && !main.isServerThreadActive())
								return;

							final var touchpadButtonDown = (reportData[7] & 1 << 2 - 1) != 0;
							final var down1 = reportData[35] >> 7 != 0 ? false : true;
							final var down2 = reportData[39] >> 7 != 0 ? false : true;
							final var x1 = reportData[36] + (reportData[37] & 0xF) * 255;
							final var y1 = ((reportData[37] & 0xF0) >> 4) + reportData[38] * 16;

							if (touchpadButtonDown)
								synchronized (downMouseButtons) {
									downMouseButtons.add(down2 ? 2 : 1);
								}
							else if (prevTouchpadButtonDown)
								synchronized (downMouseButtons) {
									downMouseButtons.clear();
								}

							if (down1 && prevDown1) {
								final var dX1 = x1 - prevX1;
								final var dY1 = y1 - prevY1;

								if (!prevDown2 || touchpadButtonDown) {
									if (prevX1 > 0 && Math.abs(dX1) < TOUCHPAD_MAX_DELTA)
										cursorDeltaX = (int) (dX1 * TOUCHPAD_CURSOR_SENSITIVITY);

									if (prevY1 > 0 && Math.abs(dY1) < TOUCHPAD_MAX_DELTA)
										cursorDeltaY = (int) (dY1 * TOUCHPAD_CURSOR_SENSITIVITY);
								} else if (prevY1 > 0 && Math.abs(dY1) < TOUCHPAD_MAX_DELTA)
									scrollClicks = (int) (-dY1 * TOUCHPAD_SCROLL_SENSITIVITY);
							}

							prevTouchpadButtonDown = touchpadButtonDown;
							prevDown1 = down1;
							prevDown2 = down2;
							prevX1 = x1;
							prevY1 = y1;
						}
					});
				} catch (final IOException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}

	public void deInit() {
		if (hidDevice != null) {
			resetDualShock4();
			try {
				hidDevice.close();
			} catch (final IllegalStateException e) {
			}
			hidDevice = null;
		}
	}

	public int floatToIntAxisValue(float value) {
		value = Math.max(value, -1f);
		value = Math.min(value, 1f);

		final var minAxisValue = outputThread.getMinAxisValue();
		final var maxAxisValue = outputThread.getMaxAxisValue();
		return (int) normalize(value, -1f, 1f, minAxisValue, maxAxisValue);
	}

	public EnumMap<VirtualAxis, Integer> getAxes() {
		return axes;
	}

	public int getBatteryState() {
		return batteryState;
	}

	public boolean[] getButtons() {
		return buttons;
	}

	public int getCursorDeltaX() {
		return cursorDeltaX;
	}

	public int getCursorDeltaY() {
		return cursorDeltaY;
	}

	public Set<KeyStroke> getDownKeyStrokes() {
		return downKeyStrokes;
	}

	public Set<Integer> getDownMouseButtons() {
		return downMouseButtons;
	}

	public Set<KeyStroke> getDownUpKeyStrokes() {
		return downUpKeyStrokes;
	}

	public Set<Integer> getDownUpMouseButtons() {
		return downUpMouseButtons;
	}

	public Short getDualShock4ProductId() {
		final var guid = glfwGetJoystickGUID(jid);
		if (guid != null)
			if (guid.startsWith("030000004c050000c405"))
				return 0x5C4;
			else if (guid.startsWith("030000004c050000cc09"))
				return 0x9CC;
			else if (guid.startsWith("030000004c050000a00b"))
				return 0xBA0;

		return null;
	}

	public int getJid() {
		return jid;
	}

	public Main getMain() {
		return main;
	}

	public Set<Integer> getOffLockKeys() {
		return offLockKeys;
	}

	public Set<Integer> getOnLockKeys() {
		return onLockKeys;
	}

	public OutputThread getOutputThread() {
		return outputThread;
	}

	public Profile getProfile() {
		return profile;
	}

	public float getRateMultiplier() {
		return rateMultiplier;
	}

	public int getScrollClicks() {
		return scrollClicks;
	}

	public void init() {
		for (final var mode : profile.getModes())
			for (final var action : mode.getAllActions())
				if (action instanceof IInitializationAction)
					((IInitializationAction) action).init(this);
	}

	public boolean isAxisSuspended(final int axis) {
		return axisToEndSuspensionTimestampMap.containsKey(axis);
	}

	public boolean isCharging() {
		return charging;
	}

	public void moveAxis(final VirtualAxis virtualAxis, final float targetValue) {
		virtualAxisToTargetValueMap.put(virtualAxis, floatToIntAxisValue(targetValue));
	}

	public boolean poll() {
		final var currentTime = System.currentTimeMillis();

		final var axisToEndSuspensionTimestampMapIterator = axisToEndSuspensionTimestampMap.values().iterator();
		while (axisToEndSuspensionTimestampMapIterator.hasNext()) {
			final var timestamp = axisToEndSuspensionTimestampMapIterator.next();

			if (timestamp < currentTime)
				axisToEndSuspensionTimestampMapIterator.remove();
		}

		var elapsedTime = outputThread.getPollInterval();
		if (lastCallTime > 0L)
			elapsedTime = currentTime - lastCallTime;
		lastCallTime = currentTime;
		rateMultiplier = (float) elapsedTime / (float) 1000L;

		try (var stack = stackPush()) {
			final var state = GLFWGamepadState.callocStack(stack);
			if (!glfwGetGamepadState(jid, state))
				return false;

			for (var i = 0; i < buttons.length; i++)
				buttons[i] = false;

			if (clearOnNextPoll) {
				downKeyStrokes.clear();
				downMouseButtons.clear();

				clearOnNextPoll = false;
			}

			final var onScreenKeyboard = main.getOnScreenKeyboard();
			if (onScreenKeyboard.isVisible())
				onScreenKeyboard.poll(this);

			final var axesToTargetValueMapIterator = virtualAxisToTargetValueMap.entrySet().iterator();
			while (axesToTargetValueMapIterator.hasNext()) {
				final var entry = axesToTargetValueMapIterator.next();
				final var virtualAxis = entry.getKey();
				final var targetValue = entry.getValue();

				final var currentValue = axes.get(virtualAxis);
				final var delta = targetValue - currentValue;
				final var axisRange = outputThread.getMaxAxisValue() - outputThread.getMinAxisValue();

				final var deltaFactor = normalize(Math.abs(delta), 0, axisRange, AXIS_MOVEMENT_MIN_DELTA_FACTOR,
						AXIS_MOVEMENT_MAX_DELTA_FACTOR);

				final var d = Integer.signum(delta) * (int) (axisRange * deltaFactor * rateMultiplier);

				var newValue = currentValue + d;
				if (delta > 0)
					newValue = Math.min(newValue, targetValue);
				else if (delta < 0)
					newValue = Math.max(newValue, targetValue);

				if (newValue == targetValue)
					axesToTargetValueMapIterator.remove();

				setAxis(virtualAxis, newValue, false, (Integer) null);
			}

			final var modes = profile.getModes();
			final var activeMode = profile.getActiveMode();
			final var axisToActionMap = activeMode.getAxisToActionsMap();
			final var buttonToActionMap = activeMode.getButtonToActionsMap();

			mapCircularAxesToSquareAxes(state, GLFW_GAMEPAD_AXIS_LEFT_X, GLFW_GAMEPAD_AXIS_LEFT_Y);
			mapCircularAxesToSquareAxes(state, GLFW_GAMEPAD_AXIS_RIGHT_X, GLFW_GAMEPAD_AXIS_RIGHT_Y);

			for (var axis = 0; axis <= GLFW_GAMEPAD_AXIS_LAST; axis++) {
				final var axisValue = state.axes(axis);

				if (Math.abs(axisValue) <= ABORT_SUSPENSION_ACTION_DEADZONE)
					axisToEndSuspensionTimestampMap.remove(axis);

				var actions = axisToActionMap.get(axis);
				if (actions == null) {
					final var buttonToModeActionStack = ButtonToModeAction.getButtonToModeActionStack();
					for (var i = 1; i < buttonToModeActionStack.size(); i++) {
						actions = buttonToModeActionStack.get(i).getMode(this).getAxisToActionsMap().get(axis);

						if (actions != null)
							break;
					}
				}

				if (actions == null)
					actions = modes.get(0).getAxisToActionsMap().get(axis);

				if (actions != null)
					for (final var action : actions)
						action.doAction(this, axis, axisValue);
			}

			for (var button = 0; button <= GLFW_GAMEPAD_BUTTON_LAST; button++) {
				var actions = buttonToActionMap.get(button);
				if (actions == null) {
					final var buttonToModeActionStack = ButtonToModeAction.getButtonToModeActionStack();
					for (var i = 1; i < buttonToModeActionStack.size(); i++) {
						actions = buttonToModeActionStack.get(i).getMode(this).getButtonToActionsMap().get(button);

						if (actions != null)
							break;
					}
				}

				if (actions == null)
					actions = modes.get(0).getButtonToActionsMap().get(button);

				if (actions != null)
					for (final var action : actions)
						action.doAction(this, button, state.buttons(button));
			}

			for (;;) {
				for (var button = 0; button <= GLFW_GAMEPAD_BUTTON_LAST; button++) {
					final var buttonToModeActions = profile.getButtonToModeActionsMap().get(button);
					if (buttonToModeActions != null)
						for (final var action : buttonToModeActions)
							action.doAction(this, button, state.buttons(button));
				}

				if (repeatModeActionWalk)
					repeatModeActionWalk = false;
				else
					break;
			}
		}

		SwingUtilities.invokeLater(() -> {
			main.updateOverlayAxisIndicators();
		});
		main.handleOnScreenKeyboardModeChange();

		return true;
	}

	public void repeatModeActionWalk() {
		repeatModeActionWalk = true;
	}

	public void reset() {
		clearOnNextPoll = false;
		repeatModeActionWalk = false;
		lastCallTime = 0;
		rateMultiplier = 0f;
		virtualAxisToTargetValueMap.clear();
		axisToEndSuspensionTimestampMap.clear();

		profile.setActiveMode(this, 0);

		ButtonToModeAction.reset();
		IButtonToAction.reset();

		for (final var mode : profile.getModes())
			for (final var action : mode.getAllActions())
				if (action instanceof IResetableAction)
					((IResetableAction) action).reset();
	}

	private void resetDualShock4() {
		dualShock4HidReport = Arrays.copyOf(DUAL_SHOCK_4_HID_REPORT, DUAL_SHOCK_4_HID_REPORT.length);
		sendDualShock4HidReport();
	}

	private void rumbleDualShock4(final long duration, final byte strength) {
		new Thread(() -> {
			synchronized (dualShock4HidReport) {
				dualShock4HidReport[5] = strength;
				if (sendDualShock4HidReport()) {
					try {
						Thread.sleep(duration);
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					dualShock4HidReport[5] = 0;
					sendDualShock4HidReport();
				}
			}
		}).start();
	}

	void scheduleClearOnNextPoll() {
		clearOnNextPoll = true;
	}

	private boolean sendDualShock4HidReport() {
		final var dataLength = dualShock4HidReport.length - hidReportOffset;

		try {
			for (var i = 0; i < 5; i++) {
				final var dataSent = hidDevice.setOutputReport(dualShock4HidReport[0],
						Arrays.copyOfRange(dualShock4HidReport, 0 + hidReportOffset, dualShock4HidReport.length),
						dataLength);
				if (dataSent == dataLength)
					return true;
			}
		} catch (final IllegalStateException e) {
		}

		return false;
	}

	public void setAxis(final VirtualAxis virtualAxis, final float value, final boolean hapticFeedback,
			final Float dententValue) {
		setAxis(virtualAxis, floatToIntAxisValue(value), hapticFeedback,
				dententValue != null ? floatToIntAxisValue(dententValue) : null);
	}

	private void setAxis(final VirtualAxis virtualAxis, int value, final boolean hapticFeedback,
			final Integer dententValue) {
		final var minAxisValue = outputThread.getMinAxisValue();
		final var maxAxisValue = outputThread.getMaxAxisValue();

		value = Math.max(value, minAxisValue);
		value = Math.min(value, maxAxisValue);

		final var prevValue = axes.put(virtualAxis, value);

		if (hapticFeedback && hidDevice != null && prevValue != value)
			if (value == minAxisValue || value == maxAxisValue)
				rumbleDualShock4(80L, Byte.MAX_VALUE);
			else if (dententValue != null && (prevValue > dententValue && value < dententValue
					|| prevValue < dententValue && value > dententValue))
				rumbleDualShock4(20L, (byte) 1);
	}

	private void setBatteryState(final int batteryState) {
		if (this.batteryState != batteryState) {
			this.batteryState = batteryState;

			updateDualShock4LightbarColor();

			if (main != null)
				SwingUtilities.invokeLater(() -> {
					main.updateTitleAndTooltip();

					if (batteryState == LOW_BATTERY_WARNING)
						main.displayLowBatteryWarning(batteryState / 100f);
				});
		}
	}

	public void setButton(final int id, final boolean value) {
		if (id < buttons.length)
			buttons[id] = value;
		else
			log.log(Level.WARNING, "Unable to set value for non-existent button " + id);
	}

	private void setCharging(final boolean charging) {
		if (this.charging != charging) {
			this.charging = charging;

			updateDualShock4LightbarColor();

			SwingUtilities.invokeLater(() -> {
				main.updateTitleAndTooltip();
				main.displayChargingStateInfo(charging);
			});

		}
	}

	public void setCursorDeltaX(final int cursorDeltaX) {
		this.cursorDeltaX = cursorDeltaX;
	}

	public void setCursorDeltaY(final int cursorDeltaY) {
		this.cursorDeltaY = cursorDeltaY;
	}

	public void setnButtons(final int nButtons) {
		buttons = new boolean[Math.min(outputThread.getnButtons(), MAX_N_BUTTONS)];
	}

	public void setOutputThread(final OutputThread outputThread) {
		this.outputThread = outputThread;
	}

	public boolean setProfile(final Profile profile, final int jid) {
		if (profile == null)
			throw new IllegalArgumentException();

		for (final var button : profile.getButtonToModeActionsMap().keySet())
			if (!isValidButton(button))
				return false;

		final var modes = profile.getModes();
		Collections.sort(modes, (o1, o2) -> {
			final var o1IsDefaultMode = Profile.defaultMode.equals(o1);
			final var o2IsDefaultMode = Profile.defaultMode.equals(o2);

			if (o1IsDefaultMode && o2IsDefaultMode)
				return 0;

			if (o1IsDefaultMode)
				return -1;

			if (o2IsDefaultMode)
				return 1;

			final var o1IsOnScreenKeyboardMode = OnScreenKeyboard.onScreenKeyboardMode.equals(o1);
			final var o2IsOnScreenKeyboardMode = OnScreenKeyboard.onScreenKeyboardMode.equals(o2);

			if (o1IsOnScreenKeyboardMode && o2IsOnScreenKeyboardMode)
				return 0;

			if (o1IsOnScreenKeyboardMode)
				return -1;

			if (o2IsOnScreenKeyboardMode)
				return 1;

			return o1.getDescription().compareTo(o2.getDescription());
		});

		for (final var mode : modes) {
			for (final var axis : mode.getAxisToActionsMap().keySet())
				if (axis < 0 || axis > GLFW_GAMEPAD_AXIS_LAST)
					return false;

			for (final var button : mode.getButtonToActionsMap().keySet())
				if (!isValidButton(button))
					return false;

			for (final var actions : mode.getButtonToActionsMap().values())
				Collections.sort(actions, (o1, o2) -> {
					if (o1 instanceof IButtonToAction && o2 instanceof IButtonToAction) {
						final var buttonToAction1 = (IButtonToAction) o1;
						final var buttonToAction2 = (IButtonToAction) o2;

						final var o1IsLongPress = buttonToAction1.isLongPress();
						final var o2IsLongPress = buttonToAction2.isLongPress();

						if (o1IsLongPress && !o2IsLongPress)
							return -1;
						if (!o1IsLongPress && o2IsLongPress)
							return 1;

						return 0;
					}

					return 0;
				});
		}

		this.profile = profile;
		return true;
	}

	public void setScrollClicks(final int scrollClicks) {
		this.scrollClicks = scrollClicks;
	}

	public void suspendAxis(final int axis) {
		axisToEndSuspensionTimestampMap.put(axis, System.currentTimeMillis() + SUSPENSION_TIME);
	}

	private void updateDualShock4LightbarColor() {
		synchronized (dualShock4HidReport) {
			if (charging) {
				dualShock4HidReport[6] = (byte) (batteryState >= 100 ? 0x00 : 0x1C);
				dualShock4HidReport[7] = (byte) 0x1C;
				dualShock4HidReport[8] = 0x00;
			} else {
				dualShock4HidReport[6] = (byte) (batteryState <= LOW_BATTERY_WARNING ? 0x1C : 0x00);
				dualShock4HidReport[7] = 0;
				dualShock4HidReport[8] = (byte) (batteryState <= LOW_BATTERY_WARNING ? 0x00 : 0x1C);
			}

			sendDualShock4HidReport();
		}
	}
}
