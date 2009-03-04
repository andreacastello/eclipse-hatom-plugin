/*
 * ---------------------------------------------------------------------------------
 * ReportScrollAction.java - History of changes
 * ---------------------------------------------------------------------------------
 * 31/10/2008 - 1.0 - First implementation with comments and change history
 */
package it.pronetics.madstore.hatom.eclipse.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPartSite;

public class ReportScrollAction extends Action implements IViewActionDelegate {

	// Default scrolling types we would like to use: UP or DOWN
	private static final int TYPE_UP = 0;
	private static final int TYPE_DOWN = 1;
	
	/** Scrolling type */
	private int type = TYPE_DOWN; // default; 
	
	/** Viewer used to display validation records */
	private ListViewer viewer;
	
	/** Creates a new empty instance of ReportScrollAction */
	public ReportScrollAction(){}

	/**
	 * Sets the ListViewer object to be used to display the validation reports.<br>
	 * @param viewer the ListViewer
	 */
	public void setViewer(ListViewer viewer){
		this.viewer = viewer;
	}

	/**
	 * Sets the type of scrolling that must be used in this instance.<br>
	 * 
	 * @param actionId the type or scrolling that has to be used
	 */
	private void setType(String scrollType){
		// scrollType value comes from plugin.xml file
		if (scrollType.endsWith("Up")){
			type = TYPE_UP;
		}
		else if (scrollType.endsWith("Down")){
			type = TYPE_DOWN;
		}
	}
	
	/**
	 * Perform initialization task for this action.<br>
	 * Current implementation only retrieved from Eclipse WorkbenchPart object the 
	 * proper ListViewer object and sets it.<br>
	 * 
	 * @param view the View object associated with this action.
	 */
	public void init(IViewPart view) {
		
		IWorkbenchPartSite site = view.getSite();
		HatomEditorView hatomView = (HatomEditorView)site.getPart();
		
		if (viewer == null){
			this.setViewer(hatomView.getViewer());
		}
	
	}

	/**
	 * Executes the current action.<br>
	 */
	public void run(IAction action) {
		
		String actionId = action.getId();
		setType(actionId);
		
		if (type == TYPE_DOWN){
			scrollDown();
		}
		else if (type == TYPE_UP){
			scrollUp();
		}
		
	}

	/**
	 * Performs upwards scrolling.<br>
	 */
	private void scrollUp() {
		
		org.eclipse.swt.widgets.List list = viewer.getList(); 
		
		int selIndex = list.getSelectionIndex(); 
		
		if (selIndex>0){
			list.select(selIndex - 1);
		}
	}
	
	/**
	 * Performs downwards scrolling.<br>
	 */
	private void scrollDown() {
		
		org.eclipse.swt.widgets.List list = viewer.getList();
		
		int selIndex = list.getSelectionIndex();
		
		if ( selIndex < list.getItemCount() - 1){
			list.select(selIndex + 1);
		}
		
	}

	/**
	 * Not implemented.<br>
	 */
	public void selectionChanged(IAction action, ISelection selection) {}

}