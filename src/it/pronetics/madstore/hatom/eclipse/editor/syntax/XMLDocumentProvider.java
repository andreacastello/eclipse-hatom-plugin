/*
 * ---------------------------------------------------------------------------------
 * XMLDocumentProvider.java - History of changes
 * ---------------------------------------------------------------------------------
 * 31/10/2008 - 1.0: First commented implementation
 */
package it.pronetics.madstore.hatom.eclipse.editor.syntax;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * Document provider for XML content type, used a FastPartitioner object to split the document in its main
 * tokens (XML comments, tag and text).<br>
 * @author Andrea Castello
 * @version 1.0
 */
public class XMLDocumentProvider extends FileDocumentProvider {

    protected IDocument createDocument(Object element) throws CoreException {

        IDocument document = super.createDocument(element);

        if (document != null) {
            IDocumentPartitioner docPartitioner =
            // DumpPartitioner is used for debug purposes only
            // new DumpPartitioner (
            new FastPartitioner(new XMLPartitionScanner(), new String[] { XMLPartitionScanner.XML_TAG, XMLPartitionScanner.XML_COMMENT, XMLPartitionScanner.XML_CDATA, XMLPartitionScanner.XML_PI,
                    XMLPartitionScanner.HATOM_ELEMENT });

            docPartitioner.connect(document);
            document.setDocumentPartitioner(docPartitioner);
        }
        return document;
    }
}