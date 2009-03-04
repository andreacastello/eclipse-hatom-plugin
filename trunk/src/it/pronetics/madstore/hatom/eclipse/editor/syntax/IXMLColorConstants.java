/*
 * ---------------------------------------------------------------------------------
 * IXMLColorConstants.java - History of changes
 * ---------------------------------------------------------------------------------
 * 31/10/2008 - 1.0: First commented implementation
 * 05/11/2008 - 1.1: Modified processing instruction default color.
 * 06/11/2008 - 1.2: Added default color for XML declaration
 */
package it.pronetics.madstore.hatom.eclipse.editor.syntax;

import org.eclipse.swt.graphics.RGB;

/**
 * Convenience constants interface.<br>
 * @author Andrea Castello
 * @version 1.2
 */
public interface IXMLColorConstants {
    // Used for XML comments: brown
    RGB XML_COMMENT = new RGB(128, 0, 0);
    // Used for processing instructions
    RGB PROC_INSTR = new RGB(77, 77, 77); // grey
    // Used for XML String (ie: attribute values): deep green
    RGB STRING = new RGB(59, 174, 59);
    // Default color: Black
    RGB DEFAULT = new RGB(0, 0, 0);
    // Generic XML tag color: light blue
    RGB TAG = new RGB(65, 139, 212);
    // Hatom keyword color: crimson
    RGB DEFAULT_HATOM_TAG = new RGB(220, 20, 60);
    // XML file declaration
    RGB XML_DOCTYPE_DECLARATION = new RGB(185, 6, 201);
}