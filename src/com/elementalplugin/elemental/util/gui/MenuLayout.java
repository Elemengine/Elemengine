package com.elementalplugin.elemental.util.gui;

public final class MenuLayout {

    private Button[] layout;

    MenuLayout(int size) {
        this.layout = new Button[size];
    }
    
    public int size() {
        return layout.length;
    }

    /**
     * Inserts the given button into the assigned slot
     * 
     * @param slot   where to insert the button
     * @param button {@link Button} to insert
     */
    public void insert(int slot, Button button) {
        if (slot < 0 || slot >= layout.length) {
            return;
        }
        
        layout[slot] = button;
    }

    /**
     * Inserts the given button into the slot calculated from the given row and
     * column
     * 
     * @param row    where to insert along y-axis
     * @param col    where to insert along x-axis
     * @param button {@link Button} to insert
     */
    public void insert(int row, int col, Button button) {
        int slot = row * 9 + col;
        
        if (slot < 0 || slot >= layout.length) {
            return;
        }
        
        layout[slot] = button;
    }

    /**
     * Inserts the button into the first available slot
     * 
     * @param button {@link Button} to insert
     */
    public boolean add(Button button) {
        for (int i = 0; i < layout.length; ++i) {
            if (layout[i] != null)
                continue;

            layout[i] = button;
            return true;
        }

        return false;
    }

    Button[] getLayout() {
        return layout;
    }
}
