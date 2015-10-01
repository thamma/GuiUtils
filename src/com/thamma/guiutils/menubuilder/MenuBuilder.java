package com.thamma.guiutils.menubuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;

public class MenuBuilder {

	private Map<String, Tuple> map;

	/**
	 * Main MenuBuilder constructor
	 */
	public MenuBuilder() {
		this.map = new HashMap<String, Tuple>();
	}

	/**
	 * Appends a new entry to the MenuBuilder
	 * 
	 * @param key
	 *            The by dots split path to the menu entry
	 * @param type
	 *            The MenuItemType to determine functionality
	 * @param listener
	 *            The onClick event
	 * @return The MenuBuilder itself for further .append(...) calls
	 * 
	 *         see https://github.com/thamma/GuiUtils for an example and further
	 *         information
	 */
	public MenuBuilder append(String key, MenuItemType type, MenuItemListener listener) {
		if (this.map.containsKey(key))
			System.out.println("[MenuBuilder] Overwrote key \"" + key + "\"");
		this.map.put(key, new Tuple(type, listener));
		return this;
	}

	/**
	 * Appends a new entry to the MenuBuilder
	 * 
	 * @param key
	 *            The by dots split path to the menu entry
	 * @param listener
	 *            The onClick event
	 * @return The MenuBuilder itself for further .append(...) calls
	 * 
	 *         see https://github.com/thamma/GuiUtils for an example and further
	 *         information
	 */
	public MenuBuilder append(String key, MenuItemListener listener) {
		return this.append(key, MenuItemType.NORMAL, listener);
	}

	/**
	 * Builds the MenuBar to be used in a JavaFX gui
	 * 
	 * @return The built MenuBar after resolving each .append(...)
	 */
	public MenuBar getMenu() {
		@SuppressWarnings("serial")
		List<String> keys = new ArrayList<String>() {
			{
				addAll(map.keySet());
			}
		};
		// The menuBar to first be modyfied then returned
		MenuBar menuBar = new MenuBar();
		// work through each key entry
		for (String s : keys)
			recurseMenu(s, menuBar, map.get(s));
		return menuBar;
	}

	/**
	 * Part of the messy recursive internal call of getMenu()
	 * 
	 * @param input
	 *            The key String to be split by dots
	 * @param menuBar
	 *            The MenuBar to add Menu elements to
	 * @param tuple
	 *            The Tuple object containing MenuItemListener and MenuItemType
	 */
	private void recurseMenu(String input, MenuBar menuBar, Tuple tuple) {
		// assert there is still work to be done
		String[] args = input.split("\\.");
		if (args.length == 0)
			return;
		// current outermost key
		String key = args[0];
		// test for existing sub menus with matching name
		for (Menu temp : menuBar.getMenus()) {
			// Exists already, merge into existing menu
			if (temp.getText().equalsIgnoreCase(key)) {
				// call recursively with cut arguments
				recurseMenu(cutDot(input), temp, tuple);
				return;
			}
		}
		// code only called if no existing was found, create one!

		if (args.length == 1) {
			// call on toplevel menu entry
			MenuItem out = new Menu(key);
			/*
			 * Note: As you should not have any functions on toplevel menu
			 * items, this won't work. Javafx does not support actions on
			 * toplevel Menu entries
			 */
			out.setOnAction((event) -> tuple.abstraction.handle(out));
			menuBar.getMenus().add((Menu) out);
		} else {
			Menu out = new Menu(key);
			menuBar.getMenus().add(out);
			// call recursively with cut arguments
			recurseMenu(cutDot(input), out, tuple);
		}
	}

	/**
	 * Part of the messy recursive internal call of getMenu()
	 * 
	 * @param input
	 *            The key String to be split by dots
	 * @param menu
	 *            The Menu object to recursively add further Menu objects
	 * @param tuple
	 *            The Tuple object containing MenuItemListener and MenuItemType
	 */
	@SuppressWarnings("deprecation")
	private void recurseMenu(String input, Menu menu, Tuple tuple) {
		// assert there is still work to be done
		String[] args = input.split("\\.");
		if (input.equals(""))
			return;
		// current outermost key
		String key = args[0];
		// test for existing sub menus with matching name
		for (MenuItem temp : menu.getItems()) {
			if (temp.getText().equalsIgnoreCase(key)) {
				// Exists already, merge into existing menu
				Menu out = (Menu) temp;
				// call recursively with cut arguments
				recurseMenu(cutDot(input), out, tuple);
				return;
			}
		}
		// very last entry hit, add MenuItem and set OnAction event
		if (!input.contains(".")) {
			// check for specific type
			switch (tuple.type) {
			case RADIO: {
				// add onAction event and add to parental menu
				MenuItem out = new RadioMenuItem(key);
				out.setOnAction((event) -> tuple.abstraction.handle(out));
				menu.getItems().add(out);
				break;
			}
			case CHECKBOX: {
				// add onAction event and add to parental menu
				CheckMenuItem out = new CheckMenuItem(key);
				out.setOnAction((event) -> tuple.abstraction.handle(out));
				menu.getItems().add(out);
				break;
			}
			default: {
				// add onAction event and add to parental menu
				MenuItem out = new MenuItem(key);
				out.setOnAction((event) -> tuple.abstraction.handle(out));
				menu.getItems().add(out);
				break;
			}
			}
		} else {
			// input is not very last entry
			// therefore code only called if no existing was found, create one!
			MenuItem out = new Menu(key);
			menu.getItems().add(out);
			// call recursively with cut arguments
			recurseMenu(cutDot(input), (Menu) out, tuple);
		}
	}

	/**
	 * Cuts the first pattern to match "*." off a String
	 * 
	 * @param input
	 *            The String to cut
	 * @return The new, modyfied String
	 */
	private String cutDot(String input) {
		if (input.contains("."))
			return input.substring(input.indexOf(".") + 1);
		return "";
	}
}

/**
 * 
 * Assistive helper class storing information
 *
 */
class Tuple {
	public MenuItemListener abstraction;
	public MenuItemType type;

	/**
	 * Default constructor
	 * 
	 * @param type
	 *            The MenuItemType
	 * @param abstraction
	 *            The MenuItemListener
	 */
	public Tuple(MenuItemType type, MenuItemListener abstraction) {
		this.abstraction = abstraction;
		this.type = type;
	}
}