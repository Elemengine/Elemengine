package com.elementalplugin.elemental.util.gui;

import org.bukkit.event.inventory.ClickType;

import com.elementalplugin.elemental.user.PlayerUser;

public class MenuEvent {

    private PlayerUser viewer;
    private ClickType click;
    private Menu clicked;

    public MenuEvent(PlayerUser viewer, ClickType click, Menu clicked) {
        this.viewer = viewer;
        this.click = click;
        this.clicked = clicked;
    }

    public PlayerUser getViewer() {
        return viewer;
    }

    public ClickType getClickType() {
        return click;
    }

    public Menu getClicked() {
        return clicked;
    }

    public void refreshClicked() {
        clicked.refresh(viewer);
    }

    public void closeClicked() {
        clicked.close(viewer);
    }
}
