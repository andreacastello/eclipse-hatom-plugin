/*
 * ---------------------------------------------------------------------------------
 * HatomTagRule.java - History of changes
 * ---------------------------------------------------------------------------------
 * 11/11/2008 - 1.0: First implementation.
 */
package it.pronetics.madstore.hatom.eclipse.editor.syntax;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Rule used for highlighting hAtom keywords.<br>
 * @author Andrea Castello
 * @version 1.0
 */
public class HatomTagRule implements IPredicateRule {

    // Token to be returned
    private IToken token;

    // Charcters that define the possible right limits of the hAtom keyword
    private char[] END_CHARS = { '\"', ' ' };

    // Counter for read characters
    private int charCounter = 0;

    // Keyword that must be matched
    private String matcherKeyword;

    public HatomTagRule(IToken token, String keyword) {
        this.matcherKeyword = keyword;
        this.token = token;
    }

    /**
     * Returns the token associated with the current rule.<br>
     */
    public IToken getSuccessToken() {
        return token;
    }

    /**
     * Verifies if the scanned text contains occurences of this rule.<br>
     * Basically, it can return three kinds of token: <li>EOF if end of file has been reached <li>UNDEFINED if
     * no occurence of this rule has not been found <li>getSuccessToken() in case this rule has been found in
     * the scanned text <br>
     * This method just calls evaluate(ICharacterScanner).<br>
     * @param scanner the character scanner
     */
    public IToken evaluate(ICharacterScanner scanner, boolean resume) {

        return evaluate(scanner);
    }

    /**
     * Verifies if the scanned text contains occurences of this rule.<br>
     * Basically, it can return three kinds of token: <li>EOF if end of file has been reached <li>UNDEFINED if
     * no occurence of this rule has not been found <li>getSuccessToken() in case this rule has been found in
     * the scanned text <br>
     * @param scanner the character scanner
     */
    public IToken evaluate(ICharacterScanner scanner) {

        charCounter = 0;

        char c = (char) read(scanner);

        if (c == matcherKeyword.charAt(0)) {

            // Keyword start has been found, now we have to verify if the
            // rest of the keyword matches.

            do {
                c = (char) read(scanner);
            } while (isKeywordPart((char) c));

            /*
             * We have to verify if the whole keyword has been read and if it is // followed by the expected
             * characters (this is used to avoid highlighting of words that match part of the keyword.
             */
            if (charCounter == matcherKeyword.length() && isAllowedEndChar(c)) {
                return token;
            } else {
                // No matching: we rollback the scanner offset until we reach the first
                // character again
                rollback(scanner);
                return Token.UNDEFINED;
            }

        }

        // Keyword start has not been found: we go back in the scanner and return UNDEFINED token.
        scanner.unread();
        return Token.UNDEFINED;
    }

    /**
     * Verifies if a character is a valid keyword right limit.<br>
     * @param c Character to be checked
     * @return <code>true</code>, if c is one of the possible keyword right limits characters,
     *         <code>false</code> otherwise.
     */
    private boolean isAllowedEndChar(char c) {

        boolean allowedEnd = false;

        for (int i = 0; i < END_CHARS.length; i++) {
            if (c == END_CHARS[i]) {
                allowedEnd = true;
                break;
            }
        }

        return allowedEnd;
    }

    /**
     * Verifies if a character is a part of a keyword.<br>
     * Character MUST ALSO be at the position specified by the <code>charCounter</code> variable.<br>
     * @param c Character to be checked
     * @return <code>true</code>, if c is a part of the expected keyword, <code>false</code> otherwise.
     */
    private boolean isKeywordPart(char c) {

        if (charCounter >= matcherKeyword.length()) {
            return false;
        }

        return (matcherKeyword.charAt(charCounter - 1) == c);

    }

    /**
     * Reads a character from the scanner and increments the <code>charCounter</code><br>
     * @param scanner The character scanner
     * @return the read character
     */
    private int read(ICharacterScanner scanner) {

        int c = scanner.read();
        charCounter++;
        return c;

    }

    /**
     * Bring the scanner offset back of a <code>charCounter</code> number of characters.<br>
     * @param scanner The character scanner.
     */
    private void rollback(ICharacterScanner scanner) {

        int rewindLength = charCounter;

        while (rewindLength > 0) {
            scanner.unread();
            rewindLength--;
        }

    }

}
