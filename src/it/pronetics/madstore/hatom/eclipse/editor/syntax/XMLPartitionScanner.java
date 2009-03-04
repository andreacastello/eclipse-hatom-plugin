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
