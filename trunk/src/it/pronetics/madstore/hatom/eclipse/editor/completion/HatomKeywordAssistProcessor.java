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
package it.pronetics.madstore.hatom.eclipse.editor.completion;

import it.pronetics.madstore.hatom.eclipse.HatomActivator;
import it.pronetics.madstore.hatom.eclipse.editor.syntax.XMLPartitionScanner;
import it.pronetics.madstore.hatom.eclipse.utils.ElementCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.rules.RuleBasedScanner;

/**
 * The content assist processor for hAtom keywords code completion.<br>
 * 
 * @author Andrea Castello
 * @version 1.4
 */
public class HatomKeywordAssistProcessor implements IContentAssistProcessor {

	private static char ATTR_DELIMITER = '\"'; 
	
	/** Scanner used for single XML tags.<br> */
	RuleBasedScanner scanner;
	
	/**
	 * Creates a new instance of HatomKeywordAssistProcessor.<br>
	 */
	public HatomKeywordAssistProcessor(RuleBasedScanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * Creates the array of code completion containing the sorted hAtom keywords.<br>
	 * Returns null if there is some error.<br>
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		
		ICompletionProposal[] proposals = null;
		
		KeywordTextFilter keywordFilter = KeywordTextFilter.getFilter();
		
		try {
			// Prepares the filter text
			keywordFilter.computeText(viewer.getDocument(), offset); 
			
			// The hatom keyword cache
			ElementCache cache = ElementCache.getCache();
			// Get the proper completion list according to the current partition.
			List<String> elementList = getElementList(cache, viewer.getDocument(), offset);
			
			elementList = keywordFilter.apply( elementList );
			
			// Parameters for correct keyword subistitution
			int begin = getReplacementBegin(viewer.getDocument(), offset);
			int replacementLen = getReplacementLenght(keywordFilter.getText());
			
			// Sort the keyword list
			Collections.sort(elementList);
			
			proposals = new ICompletionProposal[elementList.size()];
			
			for (int i = 0; i < elementList.size(); i++) {				
				// The proposal inserts the keyword right in the current caret position, right shifting
				// the other characters.
				proposals[i] = new CompletionProposal(elementList.get(i), begin , replacementLen, elementList.get(i).length()  );
			}
		} catch (Exception ex) {
			Status status = new Status(Status.ERROR, HatomActivator.PLUGIN_ID, 
            		"Unable to create content assist proposals", ex);
            HatomActivator.getDefault().getLog().log(status);
		}
		
		return proposals;
	}

	/**
	 * Evaluates the point at which autocomplete insertion must begin.<br>
	 * @param document
	 * @param offset caret offset
	 * @return The point at which autocomplete insertion must begin
	 */
	private int getReplacementBegin(IDocument document, int offset) {
		
		int refPoint = offset - 1;
		
		try {
			char c = document.getChar(refPoint);
			while ( c != ATTR_DELIMITER ){
				c = document.getChar(--refPoint);
			}
			
			
        } catch (BadLocationException ex) {
        	return refPoint;
        }
		
        if (refPoint <= offset - 1){
        	refPoint++;
        }
        
		return refPoint;
	}

	/**
	 * Evaluates the number of characters in the document editor that must be replaced when
	 * an autocomplete task occurs.<br>
	 * @param document
	 * @param offset caret offset
	 * @return Number of characters to be substituted
	 */
	private int getReplacementLenght(String text) {
		
		int len = 0;
		if (text!=null){
			len = text.length();
		}
		
		return len;
	}

	/**
	 * Gets the proper element list (XHTML tags or hAtom keywords), depending on the current
	 * document offset. If the offset is enclosed inside an attribute, it returns the list of
	 * hAtom keywords, else it returns the XHTML 1.0 tag list.<br>
	 * Returns an empty list in case the offset is placed outside an XML_TAG document partition.<br>
	 *   
	 * @param cache the completion element cache
	 * @param document the current document
	 * @param offset the current document offset
	 * @return list of completion elements
	 */
	private List<String> getElementList(ElementCache cache, IDocument document, int offset) {
	
		List<String> result = null;
		
		// Get the document partition corresponding to the actual cursor position
		String partitionType;
		try {
			partitionType = document.getContentType(offset);
			// No completion outside XHTML tag partitions
			if (partitionType.equals(XMLPartitionScanner.XML_TAG)){
				
				if (isInsideAttribute(document, offset)){
			
					result = cache.getHatomElementList();
					
				}
				else {
					
					result = cache.getXhtmlElementList();
					
				}
			}
			
			
		}
		catch (Exception e) {
			// We return an emptylist in case of bad location or null pointer
			result = new ArrayList<String>();
			// Then log the status
			Status status = new Status(Status.ERROR, HatomActivator.PLUGIN_ID, 
            		"Unable to create content assist proposals", e);
            HatomActivator.getDefault().getLog().log(status);
			
		}
		
		return result;
	}

	/**
	 * Not yet implemented
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	/**
	 * Auto activation character set. Right now it contains just the double quote character (").<br>
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { ATTR_DELIMITER };
	}

	/**
	 * Not yet implemented.<br>
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/**
	 * Not yet implemented.<br>
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	/**
	 * Not yet implemented.<br>
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return "";
	}

	/**
	 * Returns <code>true</code> is the current offset is inside an attribute element, which means
	 * enclosed between two double quote characters.<br>
	 * 
	 * @param document the current document
	 * @param offeset the current document offset
	 */
	public boolean isInsideAttribute(IDocument document, int offset){
		
		boolean isInsideAttr = false;
		
		try {
			int lineStart = document.getLineOffset( document.getLineOfOffset(offset) );
			String linePart = document.get(lineStart, offset - lineStart);
			
			int doubleQuoteCount = KeywordTextFilter.countCharOccurrences(linePart, '\"');

			// if count is 0 or an even number, offset is outside an attribute value
			if (! (doubleQuoteCount%2 == 0) ){
				isInsideAttr = true;
			}
			
		} catch (BadLocationException e) {
			e.printStackTrace(); // we just return false in case of BadLocation
		}
		
		return isInsideAttr;
	}

}
