/* Copyright (C) 2019  Matteo Hausner
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.bwravencl.controllerbuddy.gui.Main;
import de.bwravencl.controllerbuddy.input.Mode.Component.ComponentType;
import de.bwravencl.controllerbuddy.input.action.IAction;

public final class Mode implements Cloneable {

	public static final class Component {

		public enum ComponentType {
			AXIS, BUTTON
		}

		public final ComponentType type;

		public final int index;

		public Component(final ComponentType type, final int index) {
			this.type = type;
			this.index = index;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			final var other = (Component) obj;
			if (index != other.index || type != other.type)
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			return Objects.hash(index, type);
		}
	}

	@SuppressWarnings("unchecked")
	private static <V extends Number> Map<Integer, List<IAction<V>>> cloneActionMap(
			final Map<Integer, List<IAction<V>>> actionMap) throws CloneNotSupportedException {
		final var clonedActionMap = new HashMap<Integer, List<IAction<V>>>();
		for (final var entry : actionMap.entrySet())
			for (final var action : entry.getValue()) {
				final var key = entry.getKey();

				var clonedActions = clonedActionMap.get(key);
				if (clonedActions == null) {
					clonedActions = new ArrayList<>();
					clonedActionMap.put(key, clonedActions);
				}

				clonedActions.add((IAction<V>) action.clone());
			}

		return clonedActionMap;
	}

	private UUID uuid;
	private String description;

	private Map<Integer, List<IAction<Float>>> axisToActionsMap = new HashMap<>();
	private Map<Integer, List<IAction<Byte>>> buttonToActionsMap = new HashMap<>();

	public Mode() {
		uuid = UUID.randomUUID();
		description = Main.strings.getString("NEW_MODE_DESCRIPTION");
	}

	public Mode(final UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		final var mode = (Mode) super.clone();
		mode.uuid = uuid;
		mode.setDescription(description);

		mode.axisToActionsMap = cloneActionMap(axisToActionsMap);
		mode.buttonToActionsMap = cloneActionMap(buttonToActionsMap);

		return mode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		final var other = (Mode) obj;
		if (!Objects.equals(uuid, other.uuid))
			return false;
		return true;
	}

	public Set<IAction<?>> getAllActions() {
		return Stream.concat(axisToActionsMap.values().stream(), buttonToActionsMap.values().stream())
				.flatMap(List::stream).collect(Collectors.toUnmodifiableSet());
	}

	public Map<Integer, List<IAction<Float>>> getAxisToActionsMap() {
		return axisToActionsMap;
	}

	public Map<Integer, List<IAction<Byte>>> getButtonToActionsMap() {
		return buttonToActionsMap;
	}

	public Map<Integer, ?> getComponentToActionsMap(final ComponentType type) {
		if (type == ComponentType.AXIS)
			return axisToActionsMap;
		return buttonToActionsMap;
	}

	public String getDescription() {
		return description;
	}

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
