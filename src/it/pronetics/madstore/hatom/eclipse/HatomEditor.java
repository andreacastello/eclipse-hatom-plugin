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

package it.pronetics.madstore.hatom.eclipse;

import it.pronetics.madstore.hatom.eclipse.editor.ColorManager;
import it.pronetics.madstore.hatom.eclipse.editor.HatomEditorView;
import it.pronetics.madstore.hatom.eclipse.editor.syntax.IXMLColorConstants;
import it.pronetics.madstore.hatom.eclipse.editor.syntax.XMLConfiguration;
import it.pronetics.madstore.hatom.eclipse.editor.syntax.XMLDocumentProvider;
import it.pronetics.madstore.hatom.eclipse.prefs.PreferencesPage;
import it.pronetics.madstore.hatom.eclipse.validator.TimeMeasurer;
import it.pronetics.madstore.hatom.eclipse.validator.ValidatorCache;
import it.pronetics.madstore.hatom.eclipse.validator.ValidatorEngine;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * Editor handler for files that have HTML/XHTML extension.<br>
 * @author Andrea Castello
 * @version 1.7
 */
public class HatomEditor extends TextEditor implements IEditorActionDelegate {

    private ColorManager colorManager;
    // Editor document handler
    private IEditorPart editorPart;

    /**
     * Creates a new instance of HatomEditor.<br>
     */
    public HatomEditor() {
        super();
        colorManager = getColorManager();
        setSourceViewerConfiguration(new XMLConfiguration(colorManager));
        // Since we handle an XHTML content, we assume it is at least a well-formed XML,
        // so we create an XML document provider.<br>
        setDocumentProvider(new XMLDocumentProvider());
    }

    /**
     * Provides a unique instance of the instance variable colorManager<br>
     * . If it's been already created, it is just returned.
     * @return colorManager
     */
    private ColorManager getColorManager() {

        if (colorManager == null) {
            colorManager = new ColorManager();
        }

        return colorManager;
    }

    /**
     * Permforms initialization tasks for this editor.<br>
     * As of version 1.3 it just load preferences values.<br>
     */
    @Override
    protected void initializeEditor() {
        IPreferenceStore store = HatomActivator.getDefault().getPreferenceStore();

        setupPreferredValues(store);

    }

    /**
     * Performs setup operations for preferences values.<br>
     * @param store the preferences store
     */
    private void setupPreferredValues(IPreferenceStore store) {

        // Hatom Keyword color
        addPreferredToManager(store, PreferencesPage.HATOM_KEYWORD_PREF_NAME, IXMLColorConstants.DEFAULT_HATOM_TAG);
        // XML text color
        addPreferredToManager(store, PreferencesPage.XML_STRING_PREF_NAME, IXMLColorConstants.STRING);
        // XML TAG color
        addPreferredToManager(store, PreferencesPage.XML_TAG_PREF_NAME, IXMLColorConstants.TAG);
        // XML processing instruction
        addPreferredToManager(store, PreferencesPage.XML_PROC_INSTR_PREF_NAME, IXMLColorConstants.PROC_INSTR);
        // XML declaration
        addPreferredToManager(store, PreferencesPage.XML_DOCTYPE_DECL_PREF_NAME, IXMLColorConstants.XML_DOCTYPE_DECLARATION);

    }

    /**
     * Adds a stored preferred color to the color manager object that stores the preferences and default color
     * configurations.<br>
     * @param store The preference store
     * @param prefName Key name for the preferred color. The key is used by both store and color manager.
     * @param defaultColor default color.
     */
    private void addPreferredToManager(IPreferenceStore store, String prefName, RGB defaultColor) {

        String savedValue = store.getString(prefName);
        String[] rgbCoords = null;
        RGB hkColor = defaultColor;
        if (savedValue != null && savedValue.length() > 0) {
            // Color coords are restured as comma separated values string (ie: "65,139,212")
            rgbCoords = savedValue.split(",");
            hkColor = new RGB(Integer.valueOf(rgbCoords[0]), Integer.valueOf(rgbCoords[1]), Integer.valueOf(rgbCoords[2]));
        }

        getColorManager().addPreferredColor(prefName, hkColor);

    }

    /**
     * Performs operation that have to be done before this editor disposal (ie: release resources)
     */
    public void dispose() {
        colorManager.dispose();
        super.dispose();
    }

    /**
     * Sets the active editor.<br>
     * @param targetEditor - The target editor
     */
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        editorPart = targetEditor;

    }

    /**
     * Create actions associated with this editor.<br>
     * As of version 1.2, the only created action is the ContentAssistAction.<br>
     */
    @Override
    protected void createActions() {

        ResourceBundle bundle = ResourceBundle.getBundle("it.pronetics.madstore.hatom.eclipse.utils.assist");
        IAction action = new ContentAssistAction(bundle, "ContentAssistProposal.", this);
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", action);
        markAsStateDependentAction("ContentAssistProposal", true);

    }

    /**
     * Executes the action associated with the current editor.<br>
     * @param action the current action associated with the editor.
     */
    public void run(IAction action) {
        // Retrieve the editor input
        IEditorInput editorInput = this.editorPart.getEditorInput();
        // We create the engine associated to the file name retrieved by editor input.
        ValidatorEngine engine = ValidatorCache.getInstance().createEngine(editorInput.getName());

        IDocumentProvider documentProvider = this.getDocumentProvider();

        try {
            // Connects the document provider (here XMLDocumentProvider) to the up to date input.
            documentProvider.connect(editorInput);

            // Retrieves the document data (name, content, etc.)
            IDocument document = documentProvider.getDocument(editorInput);
            engine.setDocumentName(editorInput.getName());

            TimeMeasurer measurer = new TimeMeasurer();

            // Performs document validation
            engine.validate(document.get());

            // Static access to workbench UI components
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
            // We retrieve the view associated to this editor (see file plugin.xml)
            HatomEditorView hatomView = (HatomEditorView) page.showView(HatomEditorView.ID);
            hatomView.printReports(engine.getReports(), measurer);

        } catch (Exception ex) { // Can be Core or NullPointerException

            handleUnrecoverableException(ex);

        }
    }

    /**
     * Not yet implemented.<br>
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Handles exceptions that we want to redirect to an Eclipse popup window and we don't want to be handled
     * by the IDE's infrastructure.<br>
     * @param exception The exception to be handled.
     */
    private void handleUnrecoverableException(Exception exception) {

        String message = "Unable to perform hatom validation action";

        try {

            IStatus status = new Status(IStatus.ERROR, HatomActivator.PLUGIN_ID, message, exception);
            int windowCount = PlatformUI.getWorkbench().getWorkbenchWindowCount();
            if (windowCount != 0) {
                ErrorDialog.openError(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell(), "Hatom validation error", null, status);
            } else {
                System.out.println(message);
                exception.printStackTrace();
            }

        } catch (Exception e) { // Just in case of some unchecked exception
            System.out.println(message);
            e.printStackTrace();
        }
    }
}