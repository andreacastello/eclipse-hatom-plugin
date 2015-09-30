The hAtom validation task is started in the HatomEditor class (see HatomEditor configuration in plugin.xml here) by the run(IAction action) method, where a new ValidationEngine object is created, started and added to the ValidatorCache each time a new validation request for a document is made by the user.

Validation is started by pushing a button on Eclipse Workbench main toolbar. The button attributes and bound action are configured in the plugin.xml file

```
<extension point="org.eclipse.ui.editorActions">
    <editorContribution
        id="it.pronetics.madstore.hatom.editor.HatomEditor"
        targetID="it.pronetics.madstore.hatom.editor.HatomEditor">
        <action id="it.pronetics.madstore.hatom.editor.HatomEditor"
            label="hAtom Validator"
            toolbarPath="HatomEditor"
            icon="icons/hatomValidate.png"
            tooltip="Validate hAtom microformat"
            class="it.pronetics.madstore.hatom.HatomEditor"
        />
    </editorContribution>
</extension>
```

This code snippet basically tells the IDE that there will be a contribution to the editor and that the button will be bound an action, that is the one performed by the HatomEditor. The toolbarPath attribute of the action tag, sets the button position in the first available position of the main toolbar.

## Validation engine ##

All class in the it.pronetics.madstore.hatom.validator are part of the validation engine. The structure of the engine it's modeled as a Chain-of-responsibility in a tree form, where the engine access point, the ValidationEngine class, delegates the execution of the main validation task to the HfeedAnalyzer, which also delegates smaller parts of the task to other classes.

All the classes involved in the validation algorithm use the suffix "Analyzer" and are implementation of the Analyzer interface. Analyzer defines just one method, analyze(), while most of the common methods are accessible to the implementing classes via the abstract class BaseAnalyzer.

Current implementation of validator engine makes use of org.w3c.dom APIs.

Validation criteria for hAtom microformat are listed and explained here.

## Error report ##

Validation errors are stored as Report objects  that are organized as a List

&lt;Report&gt;

  in the ValidatorEngine. A new ValidatorEngine is created for every process, whose reference is stored inside a map of the singleton objectValidatorCache. ValidationEngines are mapped in the cache using the document names as keys.

The current Eclipse plugin implementation  does not allow multiple validation processes to be executed together. Each task must end before another one can be started. However, for each validation process a dedicated output tab (known as "Hatom Editor View") is opened.

The Hatom Editor View is also defined in the plugin.xml

```
<extension point="org.eclipse.ui.views">
    <category
        id="it.pronetics.madstore.hatom"
        name="Hatom Editor View">
    </category>
    <view
        allowMultiple="true"
        class="it.pronetics.madstore.hatom.editor.HatomEditorView"
        icon="icons/hatomValidate.png"
        id="it.pronetics.madstore.hatom.HatomEditorView"
        name="Hatom Editor View"
        restorable="true">
    </view>
</extension>
```

Note that the allowMultiple attribute must be true in order to have a dedicated view for each validation task. The HatomEditorView class in the editor package contributes the view to the workbench; it also prints the output report inside a scrollable ListViewer object, which puts one report in each row of the viewer. Scrolling between the ListViewer rows is also possible by using the Up and Down arrow buttons in the upper-right corner of the view. The following plugin.xml snippet contains the configuration of these two buttons.

```
<extension point="org.eclipse.ui.viewActions">
    <viewContribution
        id="it.pronetics.madstore.hatom.Up"
        targetID="it.pronetics.madstore.hatom.HatomEditorView">
        <action
            class="it.pronetics.madstore.hatom.editor.ReportScrollAction"
            id="it.pronetics.madstore.hatom.ReportScrollAction.Up"
            label="Scroll Up"
            icon="icons/arrow_up.gif"
            toolbarPath="Scroll"
            style="push">
        </action>
    </viewContribution>
    <viewContribution
        id="it.pronetics.madstore.hatom.Down"
        targetID="it.pronetics.madstore.hatom.HatomEditorView">
        <action
            class="it.pronetics.madstore.hatom.editor.ReportScrollAction"
            id="it.pronetics.madstore.hatom.ReportScrollAction.Down"
            label="Scroll Down"
            toolbarPath="Scroll"
            icon="icons/arrow_down.gif"
            style="push">
        </action>
    </viewContribution>
</extension>
```

A new viewContribution tag must be created for each button that must be added to the View's toolbar. The action performed avery time a button is pushed is defined in the class attribute of teh action tag. In our case, since up and down scrolling actions are pretty simple, class ReportScrollAction handles both of them.

The toolbarPath attribute defines a new toolbar called "Scroll" where the button will be shown.