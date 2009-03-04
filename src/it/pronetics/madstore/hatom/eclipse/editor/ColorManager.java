/*
 * ---------------------------------------------------------------------------------
 * ColorManager.java - History of changes
 * ---------------------------------------------------------------------------------
 * 31/10/2008 - 1.0 - First implementation
 * 05/11/2008 - 1.1 - Added a map for preference-saved colors.
 */
package it.pronetics.madstore.hatom.eclipse.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Simple color manager whose skeleton has been created by Eclipse infrastructure.<br>
 * @author Andrea Castello
 * @version 1.1
 */
public class ColorManager {

    /** Map of DEFAULT colors used by the code highlight submodule */
    protected Map<RGB, Color> defaultColorTable = new HashMap<RGB, Color>();

    /** Map of the colors saved by the preferences page */
    protected Map<String, Color> prefColorTable = new HashMap<String, Color>();

    /**
     * Disposes all the colors instances in the color maps.<br>
     */
    public void dispose() {
        Iterator<Color> e = defaultColorTable.values().iterator();
        while (e.hasNext()) {
            ((Color) e.next()).dispose();
        }

        e = prefColorTable.values().iterator();
        while (e.hasNext()) {
            ((Color) e.next()).dispose();
        }
    }

    /**
     * Gets the default Color object associated with the given RGB.<br>
     * @param rgb The RGB for which we want the color.
     * @return Color the color for the given RGB
     */
    public Color getDefaultColor(RGB rgb) {

        Color color = (Color) defaultColorTable.get(rgb);
        if (color == null) {
            color = new Color(Display.getCurrent(), rgb);
            defaultColorTable.put(rgb, color);
        }
        return color;
    }

    /**
     * Adds a color to the table of preferred color. The given key is the name of the token associated with
     * the color.<br>
     * @param name name of the token associated with the color
     * @param rgbColor An RGB color object
     */
    public void addPreferredColor(String key, RGB rgbColor) {

        Color color = new Color(Display.getCurrent(), rgbColor);
        prefColorTable.put(key, color);

    }

    /**
     * Gets the preference-saved Color object associated with the given string label.<br>
     * @param The label associated with the color.
     * @return Color the color for the given label, or null
     */
    public Color getPreferredColor(String label) {

        return (Color) prefColorTable.get(label);

    }

}