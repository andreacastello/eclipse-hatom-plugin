/*
 * ---------------------------------------------------------------------------------
 * HatomActivator.java - History of changes
 * ---------------------------------------------------------------------------------
 * 22/10/2008 - First implementation
 * 31/10/2008 - Removed unused imports
 */
package it.pronetics.madstore.hatom.eclipse;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.<br>
 * @author Andrea Castello
 * @version 1.1
 */
public class HatomActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "HatomPlugin";

    // The shared instance
    private static HatomActivator plugin;

    /**
     * Creates a new instance of HatomActivator.<br>
     */
    public HatomActivator() {
    }

    /**
     * Starts or refreshed the plugin actions.<br>
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * Saves this plug-in's preference and dialog stores and shuts down its image registry (if they are in
     * use).<br>
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the singleton instance
     * @return the shared instance
     */
    public static HatomActivator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}