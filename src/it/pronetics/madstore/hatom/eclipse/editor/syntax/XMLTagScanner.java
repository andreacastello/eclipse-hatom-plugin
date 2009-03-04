/*
 * ---------------------------------------------------------------------------------
 * XMLTagScanner.java - History of changes
 * ---------------------------------------------------------------------------------
 * 23/10/2008 - 0.9:  First implementation
 * 05/11/2008 - 0.91: getPreferredColor replaces the use of default colors.
 * 21/11/2008 - 1.0:  Stable version with the creation of a separate rule for each keyword. 
 */
package it.pronetics.madstore.hatom.eclipse.editor.syntax;

/**
 * Specific scanner for XML tags. It also defines basical rules for hAtom keywords syntax
 * highlight.<br>
 * 
 * @author Andrea Castello
 * @version 1.0
 */
import it.pronetics.madstore.hatom.eclipse.editor.ColorManager;
import it.pronetics.madstore.hatom.eclipse.prefs.PreferencesPage;
import it.pronetics.madstore.hatom.eclipse.utils.ElementCache;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class XMLTagScanner extends RuleBasedScanner {

    // Constant for double quotes
    private final static String DOUBLE_QUOTE = "\"";

    // Cache containing all cod ecompletion elements (both XHTML and hAtom)
    private ElementCache cache = ElementCache.getCache();

    /**
     * Creates the scanner for the XML and derivate formats (in our case XHTML docs).<br>
     * @param manager
     */
    public XMLTagScanner(ColorManager manager) {

        // Token for XML strings such as attribute values.
        IToken string = new Token(new TextAttribute(manager.getPreferredColor(PreferencesPage.XML_STRING_PREF_NAME)));
        // Token for Hatom keywords
        IToken hatomElement = new Token(new TextAttribute(manager.getPreferredColor(PreferencesPage.HATOM_KEYWORD_PREF_NAME)));

        IRule[] keywordRules = createKeywordRules(hatomElement);

        IRule[] rules = new IRule[keywordRules.length + 3];

        // Add a rule for single quotes
        rules[0] = new SingleLineRule("'", "'", string, '\\');
        // Add generic whitespace rule.
        rules[1] = new WhitespaceRule(new XMLWhitespaceDetector());

        for (int i = 0; i < keywordRules.length; i++) {
            rules[i + 2] = keywordRules[i];
        }

        // Add rule for double quotes
        rules[rules.length - 1] = new SingleLineRule("\"", "\"", string, '\\');

        setRules(rules);
    }

    /**
     * Creates the rules for handling hAtom keywords.<br>
     * @param the token used for the newly created rules.
     */
    private IRule[] createKeywordRules(IToken hatomElement) {

        List<String> HatomElement = cache.getHatomElementList();
        List<IRule> rules = new ArrayList<IRule>();

        for (String element : HatomElement) {

            if (!element.equalsIgnoreCase("vcard") && !element.equalsIgnoreCase("author")) {
                rules.add(new HatomTagRule(hatomElement, DOUBLE_QUOTE + element + DOUBLE_QUOTE));
            }

        }

        rules.add(new HatomTagRule(hatomElement, DOUBLE_QUOTE + "vcard author" + DOUBLE_QUOTE));

        return rules.toArray(new IRule[rules.size()]);
    }
}