/*
 * ---------------------------------------------------------------------------------
 * Analyzer.java - History of changes
 * ---------------------------------------------------------------------------------
 * 24/09/2008 - 1.0: First implementation.
 * 03/10/2008 - 1.1: Added contants for various keywords
 * 21/11/2008 - 1.2: Added constants for feed-key and entry-key handling
 */

package it.pronetics.madstore.hatom.eclipse.validator;

import java.io.IOException;

/**
 * Interface for hAtom nodes analyzers
 * @author Andrea Castello
 * @version 1.2
 */
public interface Analyzer {

    // hAtom keywords
    public static final String KEYWORD_HFEED = "hfeed";
    public static final String KEYWORD_HENTRY = "hentry";
    public static final String KEYWORD_ENTRY_KEY = "entry-key";
    public static final String KEYWORD_ENTRY_TITLE = "entry-title";
    public static final String KEYWORD_ENTRY_CONTENT = "entry-content";
    public static final String KEYWORD_ENTRY_SUMMARY = "entry-summary";
    public static final String KEYWORD_BOOKMARK = "bookmark";
    public static final String KEYWORD_UPDATED = "updated";
    public static final String KEYWORD_PUBLISHED = "published";
    public static final String KEYWORD_AUTHOR = "author";
    // Class names of commonly used analyzers
    public static final String HENTRY_CHILD_ANALYZER = "HentryChildAnalyzer";
    public static final String DATE_TIME_ANALYZER = "DateTimeAnalyzer";
    public static final String AUTHOR_ANALYZER = "AuthorVcardAnalyzer";

    // hCard keywords
    public static final String KEYWORD_VCARD = "vcard";

    // rel-tag keyword
    public static final String KEYWORD_TAG = "tag";

    // title attribute name
    public static final String ATTR_NAME_TITLE = "title";

    public void analyze() throws IOException;

}
