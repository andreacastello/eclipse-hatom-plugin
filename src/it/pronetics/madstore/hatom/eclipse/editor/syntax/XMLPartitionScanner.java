/*
 * ---------------------------------------------------------------------------------
 * XMLPartitionScanner.java - History of changes
 * ---------------------------------------------------------------------------------
 * 23/10/2008 - 1.0: First implementation
 * 31/10/2008 - 1.1: Added constants and a new Token for processing instruction. 
 * 06/11/2008 - 1.2: Added support for XML doctype declaration.
 */
package it.pronetics.madstore.hatom.eclipse.editor.syntax;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Scanner for XML file body. Set the main rules to be used to XML file partitioning.<br>
 * Other rules can be definied for XML sub elements (see XMLTagScanner).<br>
 * @author Andrea Castello
 * @version 1.2
 */
public class XMLPartitionScanner extends RuleBasedPartitionScanner {

    public final static String XML_COMMENT = "__xml_comment";
    public final static String XML_DOCTYPE = "__xml_doctype";
    public final static String XML_TAG = "__xml_tag";
    public final static String XML_PI = "__xml_pi"; // processing instruction
    public final static String HATOM_ELEMENT = "__hatom_element"; // hatom keywords
    public final static String XML_CDATA = "__xml_cdata";

    public XMLPartitionScanner() {

        IToken xmlComment = new Token(XML_COMMENT);
        IToken tag = new Token(XML_TAG);
        IToken xmlPI = new Token(XML_PI);
        IToken doctypeDeclaration = new Token(XML_DOCTYPE);

        IPredicateRule[] rules = new IPredicateRule[4];

        rules[0] = new MultiLineRule("<!--", "-->", xmlComment);
        rules[1] = new TagRule(tag);
        rules[2] = new MultiLineRule("<?", "?>", xmlPI);
        rules[3] = new MultiLineRule("<!DOCTYPE", ">", doctypeDeclaration);

        setPredicateRules(rules);
    }
}
