/**
 * Documentation generation stuff. Alot.
 * <p>
 *     This is a top-level panel, hence it's UI lives within {@link com.j3d.ui.docs}
 * </p>
 * <h1>Documentation Frame</h1>
 * <p>
 *     The way J3Engine documentation works is that all UI is generated from markdown files containing the actual content.
 *     Allowing for easy content updates and separation of content from presentation. This also means that these
 *     same documentation files can be viewed in other Markdown compatible viewers. With the nuance that these Markdown
 *     files have a special Markdown format that may render a bit oddly in others. See
 *     <a href="https://github.com/yetnt/j3engine/blob/main/src/main/resources/docs/standrdj3.md">J3Engine Markdown Standard (Github)</a>
 * </p>
 * <p>
 *     This naturally means we have to split this into 3 operations
 *     <ol>
 *         <li>Reading and parsing the Markdown files into tokens</li>
 *         <li>Interpreting and Enforcing Markdown rules</li>
 *         <li>Formatting tokens into Swing GUI code for the user to read without opening the Markdown file</li>
 *     </ol>
 * </p>
 * <p>
 *     The main documentations frame is always available to be shown as it needs to show the list to the user.
 *     The other "files" are themselves top-level frames which are only parsed when the user actually requests it
 *     and hence fourth are cached as to not parse the content again.
 * </p>
 * <h2>Classes</h2>
 * <ul>
 *     <li>
 *         {@link com.j3d.gen.docs.DocsGenException}, the base exception for anything that went wrong during
 *         generation of documentation.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.docs.ImgGenException}, an extender of {@link com.j3d.gen.docs.DocsGenException}
 *          which is thrown when there is an issue with image generation.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.docs.Documentation}, an enum representing the different documentation files available in the engine.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.docs.DocsProvider}, the class which handles showing, fetching and caching of each documentation
 *         file. This is the orchestrator for documentation frames and hence only one instance lives within {@link com.j3d.StaticRefs}
 *         such that other code cna reference documentation.
 *     </li>
 * </ul>
 * <h2>Sub-packages</h2>
 * <ul>
 *     <li>
 *         {@link com.j3d.gen.docs.reader}, the package which handles reading and parsing of Markdown files into tokens.
 *         </li>
 *     <li>
 *         {@link com.j3d.gen.docs.api}, the package which has extra API for any content that is already parsed from tokens
 *         and needs a higher level abstraction before being rendered into Swing GUI components.
 *     </li>
 * </ul>
 * @author Lehlogonolo Poole
 */
package com.j3d.gen.docs;