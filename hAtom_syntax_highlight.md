Eclipse syntax features are based on the **document partition** idea. A partition is a portion of a text document that is identified by a well defined set of properties.The hAtom plugin defines a set of six partitions:

  * comment
  * doctype declaration
  * tag
  * processing instruction
  * hAtom element
  * CDATA

Many classes in the it.pronetics.madstore.hatom.editor.syntax have the XML prefix; they've been created by Eclipse's Plugin Development Enviroment and comply with the usual structure of XML-based syntax highlight structure.

When an XHTML document is opened into the editor, it is scanned using a set classes who define the scanning rules for the whole document and, if needed, sub-rules for the single document  partitions.

The **XMLConfiguration** class  specifies how these classes are used. Method

```
public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
```

contains some interesting objects. The first object created inside the mehod is a PresentationReconciler, an Eclipse class responsible for "repairing damages" occurred to the document after a change has been made by the user (ie: change the syntax highlight colors where is needed). PresentationReconciler delegates partition repairing to other classes, which are capable to handle different document partitions; these classes are bound to a document partition and stored inside the reconciler's registry using the setDamager and setRepairer methods.

There are two basic classes for document repairing: DefaultDamagerRepairer and NonRuleBasedDamageRepairer. The first one accepts an ITokenScanner implementation in the constructor, which is used to provide more fine-grained scanning for a document partition. Hatom plugin defines two DefaultDamageRepairer, a generic one that uses a basic XMLScanner and is bound to a nameless "default" partition, and one dedicated to the TAG document partition, that uses a specialized XMLTagScanner class.

A "scanner" class used by the DefaultDamageRepairer usually defines an array of IRule implementations that will be used in the process of partition scanning. It also defines the Token objects used to store attributes (ie: highlight color or editor font style) associated with a partition.

NonRuleBasedDamagedRepaired, on the other hand, is an Eclipse PDE generated class used to handle very simple document partitions, that don't require fine-grained scanning. Currently it is used for processing instruction and comments.

In addition to Eclipse native Rule classes, two more IRule implementations have been created: a generic TagRule and an HatomTagRule (name may change in the future, since hAtom elements are not really tags, they're just enclosed in tags). Each HatomTagRule instance is associated with a single hAtom keyword, so XMLTagScanner class creates a number of rule instances that is equal to the number of hAtom keywords to be highlighted.

XMLDocumentProvider class connects the main scanner, the  XMLPartitionScanner, to the currently used document and returns it through its method

```
protected IDocument createDocument(Object element)
```

which is usually invoked by the IDE.

## Color preferences for hAtom syntax highlight ##

Hatom plugin for Eclipse has a dedicated page for setting colors used for syntax highlight. This page contains several color field editors, which are set in the PreferencePage class of package prefs.