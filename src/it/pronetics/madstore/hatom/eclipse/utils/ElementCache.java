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
package it.pronetics.madstore.hatom.eclipse.utils;

import it.pronetics.madstore.hatom.eclipse.HatomActivator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Status;

/**
 * Singleton class that loads the <b>hAtom</b> keyword list from a file into a cache that will be used in the
 * autocompletion task.<br>
 * The file that stores the keywords is the <code>tag.properties</code> contained in the module.<br>
 * @author Andrea Castello
 * @version 1.4
 */
public class ElementCache {

    // Path of the properties file where hAtom keywords are stored.
    private final static String PROPERTIES_CPATH = "it.pronetics.madstore.hatom.eclipse.utils.tags";
    // Used to access the module properties file.<br>
    private ResourceBundle bundle = null;
    // Hatom Keywords list.
    private List<String> hatomElementList = null;

    // XHTML tag list.
    private List<String> xhtmlElementList = null;

    // Single class instance.
    private static ElementCache instance = null;

    /**
     * When it is called by the static <code>getCache</code> method, it loads the list of the hAtom keywords
     * into its unique instance.<br>
     * <br>
     */
    private ElementCache() {
        loadTags();
    }

    /**
     * Gets the single instance for this object.<br>
     * @return the keyword cache.
     */
    public static synchronized ElementCache getCache() {
        if (instance == null) {
            instance = new ElementCache();
        }

        return instance;
    }

    /**
     * Loads the hAtom keyword list from the module's property file.<br>
     */
    private void loadTags() {
        try {
            bundle = ResourceBundle.getBundle(PROPERTIES_CPATH);
            Enumeration<String> en = bundle.getKeys();
            String key = null;

            setHatomElementList(new ArrayList<String>());
            setXhtmlElementList(new ArrayList<String>());

            for (; en.hasMoreElements();) {
                key = en.nextElement();
                if (key.startsWith("hatom.")) {
                    hatomElementList.add(bundle.getString(key).trim());
                } else if (key.startsWith("xhtml.")) {
                    xhtmlElementList.add(bundle.getString(key).trim());
                }
            }
        } catch (Exception ex) {
            Status status = new Status(Status.ERROR, HatomActivator.PLUGIN_ID, "Unable to load hAtom keyword list", ex);
            HatomActivator.getDefault().getLog().log(status);
        }
    }

    /**
     * Returns the complete, unmodifiable, hAtom keyword list.<br>
     * @return
     */
    public List<String> getHatomElementList() {
        return Collections.unmodifiableList(hatomElementList);
    }

    /**
     * Sets the hAtom keyword list.<br>
     * @param hatomElementList keyword list.
     */
    private void setHatomElementList(List<String> aList) {
        this.hatomElementList = aList;
    }

    public void setXhtmlElementList(List<String> xhtmlElementList) {
        this.xhtmlElementList = xhtmlElementList;
    }

    public List<String> getXhtmlElementList() {
        return xhtmlElementList;
    }
}