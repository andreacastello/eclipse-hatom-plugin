/*
 * ---------------------------------------------------------------------------------
 * ValidatorEngine.java - History of changes
 * ---------------------------------------------------------------------------------
 * 15/09/2008 - 1.0: First implementation.
 * 16/09/2008 - 1.1: Delegated xml parse and rule object creation to RuleBuilder class 
 *                   where Document object has been moved.
 * 16/09/2008 - 1.2: Added methods getRule(String) and validate().
 * 23/09/2008 - 1.3: Logic rewritten.
 * 06/10/2008 - 1.4: Removed NodeMetadata references: used Node instead
 * 04/12/2008 - 1.5: checkInvalidHatomAttributes method has been splitted in two methods in order
 *                   to reduce its cyclomatic complexity.
 * 10/12/2008 - 1.6: Moved ALL_KEYWORDS from HentryChildAnalyzer, it replaces hAtomKeywords.
 * 					 Modified both analyzeUnmatchingNodes methods to receive an array of keywords to be chacked.
 */

package it.pronetics.madstore.hatom.eclipse.validator;

import static it.pronetics.madstore.hatom.eclipse.validator.BaseAnalyzer.HATOM_HENTRY_ATTRIBUTES;
import static it.pronetics.madstore.hatom.eclipse.validator.HentryChildAnalyzer.HENTRY_CHILDREN;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Handler for rule validation tasks.<br>
 * 
 * @author Andrea Castello
 * @version 1.6
 */
public class ValidatorEngine {
     
    // List of hAtom keywords: they're widely used in the whole validation process
    public final static String[] ALL_KEYWORDS;// = Arrays.copyOf( HENTRY_CHILDREN, HATOM_HENTRY_ATTRIBUTES.size() + 3);
    
    static {
        ALL_KEYWORDS = new String[HATOM_HENTRY_ATTRIBUTES.size() + 3];
        System.arraycopy(HENTRY_CHILDREN, 0, ALL_KEYWORDS, 0, HATOM_HENTRY_ATTRIBUTES.size());
        
        ALL_KEYWORDS[ALL_KEYWORDS.length - 3] = HfeedAnalyzer.KEYWORD_HFEED;
        ALL_KEYWORDS[ALL_KEYWORDS.length - 2] = HfeedAnalyzer.FEED_KEY;
        ALL_KEYWORDS[ALL_KEYWORDS.length - 1] = HfeedAnalyzer.KEYWORD_HENTRY;
    }
    
    
    // attribute names usually associated with hAtom keywords
    public static final String ATTR_CLASS = "class"; // many classes will use this
    public static final String ATTR_REL   = "rel";
    
    public static final String MATCHING_LIST = "matching";
    public static final String UNMATCHING_LIST = "unmatching";
    
    // List of error reports found during the validation process
    private List<Report> reports;
    
    // Name of the document open in the IDE, that will be validated by this engine
    private String documentName = "";
    
    // DOM object that represents the document to be validated
    private Document xhtmlDoc;

    /**
     * Creates a new engine instance and initialized its internal report list.<br>
     */
    public ValidatorEngine(){
        reports = new ArrayList<Report>();
    }
    
    /**
     * Checks and reports invalid hfeed attributes that are placed outside hfeed elements.
     * @param node Node to be checked
     * @param keyword hatom keyword to be checked
     */
    private static void checkInvalidHatomAttributes(Node node, String keyword, String docName) {
        
        if (node != null) {
           
            NamedNodeMap nnmap = node.getAttributes(); 
            
            if (nnmap != null) {
                
                for (int i = 0; i < nnmap.getLength(); i++) {
                    
                    Node child = nnmap.item(i);
                    
                    checkInvalidAttribute(node, child, keyword, docName);
                }
            }
        }
    }
    
    /**
     * Convenience method used inside <code>checkInvalidHatomAttributes</code> method.
     * It creates an error report if the hAtom <code>keyword</code> is found inside the
     * given <code>node</code>.
     * <br>
     * Note that the <code>docName</code> is just used for reporting purposes.<br>
     * 
     * @param parent Parent node of the node that we have to analyze
     * @param child Node to be analyzed
     * @param keyword hatom keyword that must be checked for the given node
     * @param docName the document's name
     */
    private static void checkInvalidAttribute(Node parent, Node child, String keyword, String docName ) {
    	
    	Report report;
    	String nodeValue;
    	
    	// We search just attribute values
        if (child.getNodeType() == Node.ATTRIBUTE_NODE) {
            
            nodeValue = child.getNodeValue();
            
            if (nodeValue != null && XMLUtils.attributeValueMatches(nodeValue, keyword)) {
                
                report = new Report();
                report.setNode(parent);
                
                StringBuffer message = new StringBuffer("Attribute ").append(child.getNodeName());
                        message.append(" with value ").append(nodeValue).append(" is in invalid position. \n");
                        message.append("Please check that node is inside his regular parent node \n");
                        
                report.setMessage(message.toString());
                ValidatorEngine engine = ValidatorCache.getInstance().getEngine(docName); 
                engine.addReport(report);
                
            }
        }
		
	}

	/**
     * Returns the list of report objects that contains all the validation error found during the process.<br>
     * @return
     */
    public List<Report> getReports(){
        return reports;
    }
    
    /**
     * Adds a new report object to the report list.<br>
     * @param report A report object.
     */
    public void addReport(Report report){
       reports.add(report); 
    }
    
    /**
     * Performs validation on a DOM object which is obtained from Netbeans' <code>StyleDocument</code>.
     * 
     * @param doc Netbeans StyedDocument that must be converted into a DOM object
     * @throws BadLocationException in case the styledDocument cannot return the document as string
     * @throws IOException in case Document object creation fails.
     */
    public void validate(String  stringDoc) throws BadLocationException, IOException {
        
        this.xhtmlDoc = XMLUtils.getDocument(new ByteArrayInputStream( stringDoc.getBytes() ));
        
        HfeedAnalyzer analyzer = new HfeedAnalyzer();
        analyzer.init(xhtmlDoc);
        analyzer.setDocumentName(this.documentName);
        analyzer.analyze();
        
    }
 
    /**
     * Check if there are hAtom microformats outside the validated html.<br>
     * 
     * @param unmatchingNodes nodes that should not contain hAtom microformat
     */
    public static void analyzeUnmatchingNodes(Document doc, List<Node> unmatchingNodes, String documentName, String[] targetKeywords){
        
        for (int i=0; i<targetKeywords.length; i++){
            
            for(Node node: unmatchingNodes){
                checkInvalidHatomAttributes(node, targetKeywords[i], documentName);
            }
            
        }
    }
    
    /**
     * Searches for hAtom keywords that can be found outside their valid position (ie: a hentry is found outside
     * a hfeed element).<br> 
     * 
     * @param doc the whole DOM object representing the XHTML document; it is used to create a node iterator
     * @param rootNode node that will be the root under which iterator will be built.
     * @param docName the name of the document opened in the IDE
     */
    public static void analyzeUnmatchingNodes(Document doc, Node rootNode, String docName, String[] targetKeywords){
        
        NodeIterator iterator = XMLUtils.getNodeIterator(doc, rootNode);
        
        for (int i=0; i<targetKeywords.length; i++){
            Node node;
            while((node = iterator.nextNode() ) != null){
                checkInvalidHatomAttributes(node, targetKeywords[i], docName);
            }
            
            // Bring iterator back to first node
            while (( node = iterator.previousNode())!=null){}
        }
        
        iterator.detach();
    }

    /**
     * Returns the name of the document to be validated.<br>
     * @return
     */
    public String getDocumentName() {
        return documentName;
    }

     /**
     * Sets the name of the document to be validated.<br>
     * @return
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}