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
package it.pronetics.madstore.hatom.eclipse.editor;

import java.util.List;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import it.pronetics.madstore.hatom.eclipse.validator.Report;
import it.pronetics.madstore.hatom.eclipse.validator.TimeMeasurer;

/**
 * Handler for the IDE's output "view" tab, where validation report list is displayed.<br>
 * @author Andrea Castello
 * @version 1.4
 */
public class HatomEditorView extends ViewPart {

    // Static View ID
    public static final String ID = "it.pronetics.madstore.hatom.eclipse.HatomEditorView";

    // Progressive ID used to create multiple instances of this view
    // private static int progressiveViewId = 0;

    // Widget used to display the list of reports.
    ListViewer viewer;

    /**
     * Creates a new instance of HatomEditorView.<br>
     */
    public HatomEditorView() {
    }

    /*
     * Gets the string value of the progressive id of the Hatom Editor View component. It is used to create
     * multiple instances of the component.<br> Every method call increments the id value.<br>
     * @return the string id
     */
    /*
     * public static synchronized String getProgressiveViewId(){ progressiveViewId++; return
     * String.valueOf(progressiveViewId); }
     */

    /**
     * Creates the internal ListViewer widget with a default vertical scroll.<br>
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {

        viewer = new ListViewer(parent, SWT.V_SCROLL);

    }

    /**
     * Gets a viewer that displays its elements as a list, where "rows" are highlighted in two different
     * colors.<br>
     * @return the ListViewer object
     */
    public ListViewer getViewer() {
        return viewer;
    }

    /**
     * Shows all the reports inside the ListViewer widget.<br>
     * @param reports The list of validation reports.
     * @param measure the time measurer (can be null)
     */
    public void printReports(List<Report> reports, TimeMeasurer measurer) {
        // Before populating the ListViewer, since the view is allocated only
        // once, we remove
        // all the possible occurrences of previous validation tasks.<br>
        viewer.getList().removeAll();

        viewer.refresh();

        if (measurer != null) {
            viewer.getList().add("Validation completed in " + measurer.getElapsedTime() + " milliseconds");
        }

        if (reports != null) {

            if (reports.size() > 0) {
                viewer.getList().add(reports.size() + " errors found");
                for (Report report : reports) {
                    viewer.getList().add(report.asString());
                }
                viewer.getList().select(0);
            } else {
                viewer.getList().add("File contains valid hAtom microformat");
            }
        }
    }

    /**
     * Not yet implemented.<br>
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }
}