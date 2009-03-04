/*
 * ---------------------------------------------------------------------------------
 * Analyzer.java - History of changes
 * ---------------------------------------------------------------------------------
 * 24/09/2008 - 1.0: First implementation.
 * 03/10/2008 - 1.1: Moved members from subclass.
 * 06/10/2008 - 1.2: Removed NodeMetadata references: used Node
 * 07/10/2008 - 1.3: Moved attributeName and attributeValue to HentryChildAnalyzer
 *              since they're used only by HentryChildAnalyzer and its subclasses. 
 * 18/11/2008 - 1.4: added methods
                    - addMappedAttributeValue(String attrName, Node aNode)
 *                  - isDuplicateAttributeValue(String attrName, String value)
 *                  - resetAttributeValueList(String attrName)
 */

package it.pronetics.madstore.hatom.eclipse.validator;

import static it.pronetics.madstore.hatom.eclipse.validator.ValidatorEngine.ATTR_CLASS;
import static it.pronetics.madstore.hatom.eclipse.validator.ValidatorEngine.ATTR_REL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Base class for hfeed microformats analysis. Provides common methods to all the analyzers.<br>
 * 
 * It also stores data that will be widely used by most of its subclasses.<br>
 * In a few cases, <code>xhtmlDoc</code> and <code>node</code> variable may reference the same object: this happens
 * when a hfeed is found as a "whole page" entity. In all the other cases, they reference different objects.<br>
 * 
 *  
 * @author Andrea Castello
 * @version 1.4
 */
public abstract class BaseAnalyzer implements Analyzer {
    
    protected final static Map<String, String> HATOM_HENTRY_ATTRIBUTES = new HashMap<String, String>();
    
    static {
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_ENTRY_TITLE, ATTR_CLASS);
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_TAG, ATTR_REL);
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_ENTRY_CONTENT, ATTR_CLASS);
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_ENTRY_SUMMARY, ATTR_CLASS);
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_BOOKMARK, ATTR_REL);
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_UPDATED, ATTR_CLASS);
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_PUBLISHED, ATTR_CLASS);
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_AUTHOR, ATTR_CLASS);
        HATOM_HENTRY_ATTRIBUTES.put(KEYWORD_ENTRY_KEY, ATTR_CLASS);
    }
    
    // Name of the document to be validated
    private String documentName = "";
    
    // Instance of the engine used to validate the documents
    protected ValidatorEngine engine;
    
    // DOM document cointaining the whole XHTML document to be validated
    private Document xhtmlDoc;
    
    // Iterator on the DOM document object
    protected NodeIterator nodeIterator;
    
    // Node to be analyzed (can be the same of Document in case of "page hfeed"
    protected Node node;
    
    // Cache for generic attribute values lists
    protected Map<String, List<String>> attrValueCache;
    
    /**
     * Method that all the subclasses must implement in order to perform some kind of analysis on hAtom nodes.<br>
     * 
     * @throws java.io.IOException - Usually when a Document creation error occurs.
     */
    public abstract void analyze() throws IOException;

    protected BaseAnalyzer(){
        
        attrValueCache = new HashMap<String, List<String>>();
        
    }
    
    /**
     * Check if the elements of the given node list, which nodes contain the given attribute name and value,
     * have children containing the same attribute name and value.<br>
     * If so, an error report is added to the ValidatorEngine report list.<br><br>
     * 
     * @param nodes Nodes that have to be checked
     * @param attrClass name of the attribute to be checked
     * @param attrValue value of the attribute to be checked
     */
    protected void checkNestedNodes(List<Node> nodes, String attrName, String attrValue) {
        
        for (Node tnode : nodes) {
            checkNestedNode(tnode, attrName, attrValue);
        }
        
    }
    
    
    /**
     * Search for a node with the given name and attribute.<br>
     * @param node Node to be searched
     * @param attrName name of the attribute to be found
     * @param attrValue value of the attribute to be checked
     * @return
     */
    protected List<Node> searchNodes(Node node, String attrName, String attrValue) {
        
        setNodeIterator(XMLUtils.getNodeIterator(getXhtmlDoc(), node));
        
        
        List<Node> matchingList = new ArrayList<Node>();
        Node n;
        while((n = getNodeIterator().nextNode())!=null){
            
            if (XMLUtils.nodeAttributeMatches(n, attrName, attrValue)){
                matchingList.add(n);
                if( n.getParentNode() != null ){
                    n.getParentNode().removeChild(n); // we remove the matching child from the document
                }
            }
        }
        
        getNodeIterator().detach();
        
        return matchingList;
    }
    
    
    /** 
     * Check if the child elements of a given node, which nodes contain the given attribute name and value,
     * have children containing the same attribute name and value.<br>
     * The check is recursive, until all the DOM portion of the given node has been checked.<br>
     * @param node The node that has to be checked
     * @param attrName name of the attribute to be checked 
     * @param attrValue value of the attribute to be checked
     */
    protected void checkNestedNode(Node node, String attrName, String attrValue) {


        NodeList nodeList = node.getChildNodes();
        Report report;

        if (nodeList != null) {
            // If we catch a child node that matches the given values, it is a nested node
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if (XMLUtils.nodeAttributeMatches(child, attrName, attrValue)) {
                    report = new Report(attrValue + " keyword cannot be contained inside another hAtom node of the same level", child);
                    engine = ValidatorCache.getInstance().getEngine(documentName);
                    engine.addReport(report);
                    
                }
                // Recursively check for nested occurrences
                checkNestedNode(child, attrName, attrValue);
                
            }
        }
    }

    /**
     * Returns the name of the document.<br>
     * @return
     */
    public String getDocumentName() {
        return documentName;
    }

    
    /**
     * Sets the name of the document. Also retrieves and sets the reference to the engine oject associated
     * to this document's validation task.<br>
     *  
     * @param documentName Name of the document 
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
        this.engine = ValidatorCache.getInstance().getEngine(documentName);
    }

    /**
     * Returns the DOM document for the XHTML file that must be validated.<br>
     * @return the document
     */
    public Document getXhtmlDoc() {
        return xhtmlDoc;
    }

    /**
     * Sets the DOM document for the XHTML file that must be validated.<br>
     * @param the document
     */
    public void setXhtmlDoc(Document xhtmlDoc) {
        this.xhtmlDoc = xhtmlDoc;
    }
    
    /**
     * Returns the iterator associated to the XHTML document or to a portion of it.<br>
     * Note that there is no direct association between this class' Document object and
     * the Iterator. The iterator <b>may</b> iterate over the entire document or, if needed,
     * over one of its child nodes (see <code>node</code> variable).<br> 
     * @return the document's or one of its nodes' iterator
     */
    public NodeIterator getNodeIterator() {
        return nodeIterator;
    }

    /**
     * Sets the iterator associated to the XHTML document or to a portion of it.<br>
     * Note that there is no direct association between this class' Document object and
     * the Iterator. The iterator <b>may</b> iterate over the entire document or, if needed,
     * over one of its child nodes (see <code>node</code> variable).<br> 
     * @param the document's or one of its nodes' iterator
     */
    private void setNodeIterator(NodeIterator nodeIterator) {
        this.nodeIterator = nodeIterator;
    }
    
    /**
     * Returns the node on which the analyzer specific implementations do their tasks.<br>
     * Note that the node may be an Element or the whole Document.<br>
     * @return the node 
     */
    public Node getNode() {
        return node;
    }

    /**
     * Sets the node on which the analyzer specific implementations do their tasks.<br>
     * Note that the node may be an Element or the whole Document.<br>
     * @return the node 
     */
    public void setNode(Node node) {
        this.node = node;
    }
    
    /** 
     * Gets the node's feed key value (ie: title="key_001"), cheks if it is empty and - if present -
     * adds it to the cache of feed key values.<br>
     * 
     * @param aNode A node containing the feedKey hAtom keyword.
     */
    protected boolean addMappedAttributeValue(String attrName, Node aNode) {
        
        boolean added = false;
        List<String> list = attrValueCache.get(attrName);
        
        // Title attribute value
        if (aNode.getNodeType() == Node.ELEMENT_NODE) {
            
            String value = ((Element) aNode).getAttribute(attrName); // No possible ClassCast here

            if (!"".equals(value)) {
                
                if (list == null){
                    list = new ArrayList<String>();
                    attrValueCache.put(attrName, list);
                }
                
                list.add(value);
                added = true;
            } 
        }
        
        return added;
    }
    
    /**
     * Checks whether the given attribute value is already present in the list of
     * attribute values grouped by their attribute name.<br>
     * 
     * @param attrName the attribute name under which attribue values are grouped
     * @param value the value to be checked
     * @return <code>true</code> if the attribute value is altreay present in the list, 
     *         <code>false</code> otherwise.
     */
    protected boolean isDuplicateAttributeValue(String attrName, String value) {
        
        boolean duplicate = false;
        List<String> list = attrValueCache.get(attrName);
        
        if (list != null){
            duplicate = list.contains(value);
        }
        
        return duplicate;
    }
    
    /**
     * Clears the list of attribute values grouped under the given attribute name.<br>
     * @param attrName the name of the list under which attribute values are grouped.
     */
    protected void resetAttributeValueList(String attrName){
        List<String> list = attrValueCache.get(attrName);
        
        if(list != null){
            list.clear();
        }
    }
    
}
