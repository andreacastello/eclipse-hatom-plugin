/*
 * ---------------------------------------------------------------------------------
 * XMLConfiguration.java - History of changes
 * ---------------------------------------------------------------------------------
 * 31/10/2008 - 1.0: First commented implementation
 * 01/11/2008 - 1.1: Overridden getContentAssistant() method
 * 05/11/2008 - 1.2: Added a NonRuleBasedDamagerRepairer object for XML processing
 * 					 instructions in getPresentationReconciler()
 * 06/11/2008 - 1.3: Added a NonRuleBasedDamagerRepairer object for XML doctype
 * 					 declaration in getPresentationReconciler()
 */
package it.pronetics.madstore.hatom.eclipse.editor.syntax;

import it.pronetics.madstore.hatom.eclipse.editor.ColorManager;
import it.pronetics.madstore.hatom.eclipse.editor.completion.HatomKeywordAssistProcessor;
import it.pronetics.madstore.hatom.eclipse.prefs.PreferencesPage;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * Handles XML code configuration for syntax highlight and code completion.<br>
 * @version 1.3
 * @author Andrea Castello
 */
public class XMLConfiguration extends SourceViewerConfiguration {

    private XMLDoubleClickStrategy doubleClickStrategy;
    // Scanner for the whole XML document
    private XMLScanner scanner;
    // Sub-scanner for the XML tags
    private XMLTagScanner tagScanner;
    private ColorManager colorManager;

    /**
     * Creates a new instance of XMLConfiguration.<br>
     * @param colorManager the color manager.<br>
     */
    public XMLConfiguration(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    /**
     * Provides an XML content assistant for the hAtom editor.<br>
     */
    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

        // The code assistant to be provided
        ContentAssistant codeAssist = new ContentAssistant();

        IContentAssistProcessor hatomAssistProcessor = new HatomKeywordAssistProcessor(getXMLTagScanner());

        codeAssist.setContentAssistProcessor(hatomAssistProcessor, XMLPartitionScanner.XML_TAG);
        codeAssist.enableAutoActivation(true);
        // Delay before popup is shown
        codeAssist.setAutoActivationDelay(500);
        // Code assist popup disposition.
        codeAssist.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
        codeAssist.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);

        return codeAssist;

    }

    /**
     * Returns a list of default of XML and hAtom partitions.<br>
     */
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IDocument.DEFAULT_CONTENT_TYPE, XMLPartitionScanner.HATOM_ELEMENT, XMLPartitionScanner.XML_COMMENT, XMLPartitionScanner.XML_TAG, XMLPartitionScanner.XML_CDATA,
                XMLPartitionScanner.XML_PI, XMLPartitionScanner.XML_DOCTYPE };
    }

    /**
     * Returns the ITextDoubleClickStrategy instance for this objects.<br>
     */
    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {

        if (doubleClickStrategy == null)
            doubleClickStrategy = new XMLDoubleClickStrategy();
        return doubleClickStrategy;

    }

    /**
     * Returns the XMLScanner instance with its default return Token already set.<br>
     */
    protected XMLScanner getXMLScanner() {

        if (scanner == null) {
            scanner = new XMLScanner(colorManager);
            scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getDefaultColor(IXMLColorConstants.DEFAULT))));
        }
        return scanner;

    }

    /**
     * Returns the XMLTagScanner instance with its default return Token already set.<br>
     */
    protected XMLTagScanner getXMLTagScanner() {

        if (tagScanner == null) {
            tagScanner = new XMLTagScanner(colorManager);
            tagScanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getPreferredColor(PreferencesPage.XML_TAG_PREF_NAME))));
        }
        return tagScanner;

    }

    /**
     * Creates an instance of an IPresentationReconciler implementation and all the internal objects for
     * handling the different XML file partitions.
     */
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer xmlDr = null;

        xmlDr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(xmlDr, XMLPartitionScanner.XML_TAG);
        reconciler.setRepairer(xmlDr, XMLPartitionScanner.XML_TAG);

        xmlDr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(xmlDr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(xmlDr, IDocument.DEFAULT_CONTENT_TYPE);

        NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getDefaultColor(IXMLColorConstants.XML_COMMENT)));
        reconciler.setDamager(ndr, XMLPartitionScanner.XML_COMMENT);
        reconciler.setRepairer(ndr, XMLPartitionScanner.XML_COMMENT);

        ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getPreferredColor(PreferencesPage.XML_PROC_INSTR_PREF_NAME)));
        reconciler.setDamager(ndr, XMLPartitionScanner.XML_PI);
        reconciler.setRepairer(ndr, XMLPartitionScanner.XML_PI);

        ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getPreferredColor(PreferencesPage.XML_DOCTYPE_DECL_PREF_NAME)));
        reconciler.setDamager(ndr, XMLPartitionScanner.XML_DOCTYPE);
        reconciler.setRepairer(ndr, XMLPartitionScanner.XML_DOCTYPE);

        return reconciler;
    }

}