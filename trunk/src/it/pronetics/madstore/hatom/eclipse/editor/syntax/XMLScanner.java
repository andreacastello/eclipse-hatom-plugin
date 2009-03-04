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

import it.pronetics.madstore.hatom.eclipse.editor.ColorManager;
import it.pronetics.madstore.hatom.eclipse.prefs.PreferencesPage;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

/**
 * Infrastructure class created by Eclipse PDE.<br>
 * @version 1.1
 */
public class XMLScanner extends RuleBasedScanner {

    public XMLScanner(ColorManager manager) {
        IToken procInstr = new Token(new TextAttribute(manager.getPreferredColor(PreferencesPage.XML_PROC_INSTR_PREF_NAME)));

        IToken doctypeDecl = new Token(new TextAttribute(manager.getPreferredColor(PreferencesPage.XML_DOCTYPE_DECL_PREF_NAME)));

        IRule[] rules = new IRule[3];
        // Add rule for processing instructions
        rules[0] = new SingleLineRule("<?", "?>", procInstr);
        // Add generic whitespace rule.
        rules[1] = new WhitespaceRule(new XMLWhitespaceDetector());
        // Add doctype declaration
        rules[2] = new SingleLineRule("<!DOCTYPE", ">", doctypeDecl);

        setRules(rules);
    }
}