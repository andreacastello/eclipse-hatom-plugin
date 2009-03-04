/*
 * ---------------------------------------------------------------------------------
 * DocumentationCache.java - History of changes
 * ---------------------------------------------------------------------------------
 * 21/10/2008 - 1.0: First implementation.
 */
package it.pronetics.madstore.hatom.eclipse.utils;

import java.util.ResourceBundle;

/**
 * Singleton class that loads the <b>hAtom</b> documentation from a properties file into a cache that will be
 * used in the autocompletion documentation panel.<br>
 * The file that stores the keywords is the <code>documentation.properties</code> contained in the completion
 * package.<br>
 * @author Andrea Castello
 * @version 1.0
 */
public class DocumentationCache {

    // Path of the properties file where hAtom documentation is stored.
    private final static String DOCFILE_PATH = "it.pronetics.madstore.hatom.utils.documentation";
    // Used to access the module properties file.<br>
    private ResourceBundle bundle = null;
    // Single class instance.
    private static DocumentationCache instance = null;

    /**
     * When it is called by the static <code>getCache</code> method, this constructor gets the ResourceBundle
     * instance associated with the hAtom documentation file.<br>
     * <br>
     */
    private DocumentationCache() {
        try {
            bundle = ResourceBundle.getBundle(DOCFILE_PATH);
        } catch (Exception ex) {
            // "Unable to load tag list for code completion"
            ex.printStackTrace(); // writes on the IDE's output tab.
        }
    }

    /**
     * Gets the single instance for this object.<br>
     * @return the documentation cache.
     */
    public static synchronized DocumentationCache getCache() {
        if (instance == null) {
            instance = new DocumentationCache();
        }

        return instance;
    }

    /**
     * Gets the proper docs for the given hAtom keyword.<br>
     * @param elementName the hAtom keyword for which we want to get the documents.
     * @return the docs associated with the given hAtom keyword
     */
    public String getElementDocumentation(String elementName) {
        return bundle.getString(elementName);
    }
}