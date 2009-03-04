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
package it.pronetics.madstore.hatom.eclipse.editor.syntax;

import org.eclipse.swt.graphics.RGB;

/**
 * Convenience constants interface.<br>
 * @author Andrea Castello
 * @version 1.2
 */
public interface IXMLColorConstants {
    // Used for XML comments: brown
    RGB XML_COMMENT = new RGB(128, 0, 0);
    // Used for processing instructions
    RGB PROC_INSTR = new RGB(77, 77, 77); // grey
    // Used for XML String (ie: attribute values): deep green
    RGB STRING = new RGB(59, 174, 59);
    // Default color: Black
    RGB DEFAULT = new RGB(0, 0, 0);
    // Generic XML tag color: light blue
    RGB TAG = new RGB(65, 139, 212);
    // Hatom keyword color: crimson
    RGB DEFAULT_HATOM_TAG = new RGB(220, 20, 60);
    // XML file declaration
    RGB XML_DOCTYPE_DECLARATION = new RGB(185, 6, 201);
}