Writing an Eclipse plugin that interacts with some kind of generic text editor (in our case XHTML), requires that the plugin contributes an editor extension point in the plugin.xml file.

The hAtom plugin defines a HatomEditor extension point as follows:

```
<extension point="org.eclipse.ui.editors">
    <editor
        name="Hatom Editor"
        extensions="xhtml,html"
        icon="icons/hatomValidate.png"
        contributorClass="org.eclipse.ui.texteditor.BasicTextEditorActionContributor"
        class="it.pronetics.madstore.hatom.HatomEditor"
        id="it.pronetics.madstore.hatom.editor.HatomEditor">
    </editor>
</extension>
```

The extension attribute defines the file extension(s) which the plugin is associated with. We define XHTML and HTML because a well-formed HTML can be handled.

The contributorClass add new actions to the workbench menus and/or toolbars, and should be defined only if class attribute has been defined. In our case, the class attribute is defined as our HatomEditor class. HatomEditor provides methods to initialize the editor and execute its associated action(s) through its run method. More information about the HatomEditor run(IAction action) method can be found in the validation section.

Code completion (or code assist, as defined by Eclipse docs) is provided by implementing interface IContentAssistProcessor, which hAtom plugins does with HatomKeywordAssistProcessor class. The code completion items are made available to the editor with the method

```
public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
```

which takes care of retrival, selection and filtering of the completion items, which are return as an array of ICompletionProposal.

Eclipse infrastructure divides a text document into partitions; each is associated with a sort of "content-type", which describes its identity and properties. Ie: an XML document can be divided into  "XML comment" partitions, "Processing instruction" partitions, and so on. A list of partitions defined for the hAtom compliant XHTML documents is available in the [syntax highlight document](http://code.google.com/p/eclipse-hatom-plugin/wiki/hAtom_syntax_highlight).