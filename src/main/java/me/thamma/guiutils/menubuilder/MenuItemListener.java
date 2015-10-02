package me.thamma.guiutils.menubuilder;

import javafx.scene.control.MenuItem;

@FunctionalInterface
public interface MenuItemListener {
	void handle(MenuItem item);
}
