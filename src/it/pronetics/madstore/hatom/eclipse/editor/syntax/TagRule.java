/*
 * ---------------------------------------------------------------------------------
 * TagRule.java - History of changes
 * ---------------------------------------------------------------------------------
 * 31/10/2008 - 1.0: First commented implementation
 */
package it.pronetics.madstore.hatom.eclipse.editor.syntax;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

/**
 * Infrastructure class created by Eclipse PDE.<br>
 * @version 1.0
 */
public class TagRule extends MultiLineRule {

    public TagRule(IToken token) {
        super("<", ">", token);
    }

    protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {

        int c = scanner.read();
        if (sequence[0] == '<') {
            if (c == '?') {
                // processing instruction - abort
                scanner.unread();
                return false;
            }
            if (c == '!') {
                scanner.unread();
                // comment - abort
                return false;
            }
        } else if (sequence[0] == '>') {
            scanner.unread();
        }

        return super.sequenceDetected(scanner, sequence, eofAllowed);
    }
}
