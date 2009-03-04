/**
 * Copyright 2008 - 2009 Pro-Netics S.P.A.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.pronetics.madstore.hatom.eclipse.prefs;

import it.pronetics.madstore.hatom.eclipse.HatomActivator;
import it.pronetics.madstore.hatom.eclipse.editor.syntax.IXMLColorConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Handles the preferences page's components and lifecycle.<br>
 * @author Andrea Castello
 * @version 1.1
 */
public class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    /** Key for hatom keyword preference field */
    public static String HATOM_KEYWORD_PREF_NAME = "HatomKeyword";
    /** Key for xml string preference field */
    public static String XML_STRING_PREF_NAME = "XmlString";
    /** Key for xml tag preference field */
    public static String XML_TAG_PREF_NAME = "XmlTag";
    /** Key for xml processing instruction preference field */
    public static String XML_PROC_INSTR_PREF_NAME = "XmlPI";
    /** Key for xml declaration */
    public static String XML_DOCTYPE_DECL_PREF_NAME = "XmlDoctypeDecl";

    // convenience arrays for ColorFieldEditor objects handling
    private String[] fieldEditorKeys = { HATOM_KEYWORD_PREF_NAME, XML_STRING_PREF_NAME, XML_TAG_PREF_NAME, XML_PROC_INSTR_PREF_NAME, XML_DOCTYPE_DECL_PREF_NAME };
    private String[] fieldEditorLabel = { "Keyword color", "XHTML attribute", "XHTML tag", "Processing instruction", "DOCTYPE declaration" };
    // Convenience map for storing the ColorFieldEditor
    private Map<String, ColorFieldEditor> colorEditorMap = new HashMap<String, ColorFieldEditor>();

    /** Creates a new instance of PreferencesPage */
    public PreferencesPage() {
        super(GRID);
        setPreferenceStore(HatomActivator.getDefault().getPreferenceStore());
        setDescription("Hatom editor preferences");
    }

    /** Not yet implemented */
    public void init(IWorkbench workbench) {
    }

    /**
     * Always returns true.<br>
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Always returns true.<br>
     */
    @Override
    public boolean okToLeave() {
        return true;
    }

    /**
     * Store all the data in the field editors.
     */
    @Override
    public boolean performOk() {

        Iterator<ColorFieldEditor> iterator = colorEditorMap.values().iterator();
        for (; iterator.hasNext();) {
            iterator.next().store();
        }

        return true;

    }

    /**
     * Restores all the default value for the preferences color fields.<br>
     */
    @Override
    public void performDefaults() {

        colorEditorMap.get(HATOM_KEYWORD_PREF_NAME).getColorSelector().setColorValue(IXMLColorConstants.DEFAULT_HATOM_TAG);
        colorEditorMap.get(XML_TAG_PREF_NAME).getColorSelector().setColorValue(IXMLColorConstants.TAG);
        colorEditorMap.get(XML_STRING_PREF_NAME).getColorSelector().setColorValue(IXMLColorConstants.STRING);
        colorEditorMap.get(XML_PROC_INSTR_PREF_NAME).getColorSelector().setColorValue(IXMLColorConstants.PROC_INSTR);
        colorEditorMap.get(XML_DOCTYPE_DECL_PREF_NAME).getColorSelector().setColorValue(IXMLColorConstants.XML_DOCTYPE_DECLARATION);

    }

    /**
     * Always return true.<br>
     */
    @Override
    public boolean performCancel() {
        return true;
    }

    @Override
    protected void createFieldEditors() {

        ColorFieldEditor colorFieldEditor = null;

        for (int i = 0; i < fieldEditorKeys.length; i++) {
            colorFieldEditor = newColorFieldEditor(fieldEditorKeys[i], fieldEditorLabel[i]);
            addField(colorFieldEditor);
            colorEditorMap.put(fieldEditorKeys[i], colorFieldEditor);
        }

    }

    /**
     * Creates a new instance of ColorFieldEditor with the given name and label.<br>
     * If some data exist in the preference store under the "name" key, they are loaded into the
     * ColorFieldEditor instance.<br>
     * @param name Unique name of the ColorFieldeditor instance.
     * @param label Editor label that will be shown in the preferences page
     * @return A new instance of ColorFieldEditor
     */
    private ColorFieldEditor newColorFieldEditor(String name, String label) {

        ColorFieldEditor cfe = new ColorFieldEditor(name, label, getFieldEditorParent());
        cfe.setPreferenceStore(this.getPreferenceStore());
        cfe.load();

        return cfe;
    }

}
