The Eclipse module works on Eclipse version 3.4 "Gamymede".

## Source code structure ##

Source code for Eclipse module is organized as follows:

the **plugin.xml** file is placed under the project root and lists all the Eclipse platform extension points that are used by the plugin and what are the plugin classes that handle interactions with the main platform.

**it.pronetics.madstore.hatom** is the main package for source code; it contains two classes:

  * HatomActivator is the plugin's entry point
  * HatomEditor is a central class for the plugin editor configurations and actions

Under the main package, six other packages - test class packages excluded - are found.

  * the editor package provides classes for handling the plugin's UI contribution to the IDE editor.
  * the editor.completion package provides classes for code completion tasks.
  * the editor.syntax package classes for XML-based syntax highlight task.
  * the prefs package contains just one class that interacts with eclipse preference window.
  * the utils package provides generic utility classes.
  * the validator package provides classes for performing validation of all hAtom elements.

For further informations about hAtom plugin features, see the following documents.

Hatom code completion in Eclipse

Hatom syntax highlight in Eclipse

Hatom microformat validation in Eclipse

Plugin installation