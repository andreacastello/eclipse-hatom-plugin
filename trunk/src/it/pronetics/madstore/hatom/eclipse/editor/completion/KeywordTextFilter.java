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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * Defines a search filter for the hAtom keywords that must be set in the code completion popup<br>
 * 
 * The criteria we use to define the filter is this: "The filter starts from the nearest character that
 * precedes the current caret position and follows one of the characters defined in the
 * <code>FILTER_START_CHARS</code> array.<br>
 * <br><br>
 * We assume that filtering operation are executed one by one, by a single user, therefore the class is 
 * defined as <i>singleton</i>.<br>
 * 
 * @author Andrea Castello
 * @version 1.4
 */
public class KeywordTextFilter {

    /** Useful constant for empty strings. */
    public final static String EMPRTY_STRING = "";
    
    /** Set of characters that can define the filter's "left border".*/
    private static final char[] FILTER_START_CHARS = { '"', ' ', '<', '>', '=' };
    
    /** The textual body of the filter.<br> */
    private String filterText = EMPRTY_STRING;
    
    /** Singleton instance of the filter */
    private static KeywordTextFilter filter = null;
   
    /** Index of the filter's starting character.<br> */
    private int filterOffset = -1;
    
    /** Private constructor (<i>singleton</i> pattern) for the filter object.<br> */
    private KeywordTextFilter(){}
    
    /**
     * Access method for the filter' singleton instance.<br>
     * @return the filter
     */
    public static synchronized KeywordTextFilter getFilter(){
        if (filter == null){
            filter = new KeywordTextFilter();
        }
        
        return filter;
    }
    
    /**
     * Returns the text of the filter.<br><br>
     * @return the filter's text
     */
    public String getText(){
    	return this.filterText;
    }
    
    
    /** 
     * Evaluates the filters text and returns it.<br><br>
     * @param doc Document on which the completion task must be applied.<br>
     * @param offset current caret position.<br>
     * @return filter's text.<br>
     */
    public String computeText(IDocument doc, int offset){
        try{
            this.filterOffset = getInitialOffset(doc, offset);
            setText( doc.get(filterOffset +1, offset - filterOffset -1) );
        }
        catch(Exception ex){
            Status status = new Status(Status.ERROR, HatomActivator.PLUGIN_ID, 
            		"Unable to create filter text", ex);
            HatomActivator.getDefault().getLog().log(status);
        }
        return filterText;
    }
    
    /** 
     * Returns a list of hatom keyword that match the current text filter.<br>
     * The method uses two different lists because the input filterable list is unmodifiable.<br>
     * @param filterableList the list to be filtered
     * @return the list of filtered hAtom keywords.
     */
    public List<String> apply(List<String> tagList){
    	
    	List<String> filteredList = null;
    	
    	if (tagList!=null && tagList.size()>0){
    		filteredList = new ArrayList<String>(tagList.size());
            // Gets the elements that match the given filter value.
    		boolean startWithFilter = false;
            for (String tag : tagList) {
                startWithFilter = tag.startsWith(this.filterText); 
                if (!tag.equals(KeywordTextFilter.EMPRTY_STRING) &&  startWithFilter) {
                    filteredList.add(tag);
                }
            }
    	}
    	
    	return filteredList;
    }
    
    /**
     * sets the filter's text.<br>
     * @param text the filter's text.<br>
     */
    public void setText(String text){
        this.filterText = text;
    }
    
    /**
     * Returns the starting index character of the filter.<br>
     * @return filter's initial char index
     */
    public int getFilterOffset(){
        return filterOffset;
    }
    
    /** 
     * Calculates the filter's <b>left border</b>, according to the given definition (see class description).
     * The filter's real starting character is <code>getInitialOffset() + 1</code>.<br><br>
     * 
     * @param doc Document on which the completion task must be applied.<br>
     * @param offset current cursor position.<br>
     * @return the index of the filter's left border.<br>
     * @throws javax.swing.text.BadLocationException Invalid caret position in the document.<br>
     */
    private static int getInitialOffset(IDocument document, int offset) throws BadLocationException {
        int initialOffset = -1;
        // We get the line where is the current caret position
        //Element lineElement = document.getParagraphElement(offset);
        // line starts here
        int start = document.getLineOffset( document.getLineOfOffset(offset) );
        int refPoint = offset - 1;
        
        char c;
        
        while (offset - 1 > start){
            try {
            	c = document.getChar(refPoint);
                
                if (isFilterStartChar(c)) {
                    initialOffset = refPoint;
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start + 1) +
                        ") on a document of length: " + document.getLength() ).initCause(ex);
            }
            refPoint--;
        }
        
        return initialOffset;
    }
    
    /**
     * Verifies whether the parameter character is found in the array of possible "left borders" or not.<br><br>
     * @param c the character to be found in the array
     * @return <code>true</code> id the character is found in the array, <code>false</code> otherwise.
     */
    private static boolean isFilterStartChar(char c){
        
        for(int i=0; i < FILTER_START_CHARS.length; i++){
            if (c == FILTER_START_CHARS[i]){
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Counts the numer of occurrences of the given char inside a string.<br>
     * @param s the string where occurences must be counted
     * @param c the character to be searched
     * @return the number of occurrences
     */
    public static int countCharOccurrences(String s, char c){
    	
    	int result = 0;
		  
	    if (s!=null && !"".equals(s)) {  
	        int start = s.indexOf(c);
	        while (start != -1) {
	            result++;
	            start = s.indexOf(c, start+1);
	        }
	    }
	    
	    return result;
    }
    
}