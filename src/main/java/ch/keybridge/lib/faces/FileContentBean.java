/*
 * Copyright 2017 Key Bridge. All rights reserved.
 * Use is subject to license terms.
 *
 * Software Code is protected by Copyrights. Author hereby reserves all rights
 * in and to Copyrights and no license is granted under Copyrights in this
 * Software License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request from: info@keybridgewireless.com
 */
package ch.keybridge.lib.faces;

import ch.keybridge.lib.markdown.Markdown;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Request scoped bean to read FIXED content from a project resource file. This
 * bean supports both Markdown and XHTML formatted source content. Markdown
 * content is automatically translated to XHTML output. This bean depends upon
 * and uses the {@code LocalBean} to identify which language translation of the
 * requested content should be read and returned.
 * <p>
 * Content must be located in the {@code META-INF/content} directory.
 *
 * @author Key Bridge
 * @since v0.3.0 created 03/26/17 to support translated page content.
 * @since v3.0.0 moved 01/15/18 to faces-common
 */
@Named(value = "fileContent")
@RequestScoped
public class FileContentBean {

  private static final Logger LOGGER = Logger.getLogger(FileContentBean.class.getName());

  /**
   * "META-INF/content/". The content directory.
   */
  private static final String CONTENT = "META-INF/content/";
  /**
   * ".md". The markdown file extension.
   */
  private static final String MD = ".md";
  /**
   * ".xhtml". The HTML file extension.
   */
  private static final String XHTML = ".xhtml";
  /**
   * "en". The default language.
   */
  private static final String ENGLISH = "en";

  /**
   * The Locale bean. This is used to identify the locale and which language
   * translation should be loaded.
   */
  @Inject
  private LocaleBean localeBean;

  /**
   * Read and return the FILE based content.
   * <p>
   * If a non-English language is selected in the LocaleBean then then a
   * corresponding language-translated file will be searched. If one is not
   * available then ENGLISH will be provided.
   *
   * @param label the file based name without language or extension
   * @return the file content
   */
  public String readContent(String label) {
    /**
     * Content type is identified by file extension. We can support file content
     * in either Markdown (".md") or XHTML (".xhtml") format. The output is
     * always XHTML, and Markdown content is converted.
     */
    try {
      String filename = findContentFile(label);
      return filename.endsWith(XHTML)
             ? readContentXHTML(filename)
             : readContentMD(filename);
    } catch (Exception exception) {
      LOGGER.log(Level.SEVERE, "{0} FILE content not found for \"{1}\"", new Object[]{localeBean.getLanguage().toUpperCase(), label});
      return null;
    }
  }

  /**
   * Find the content file name. This method looks for the language translated
   * file, in both MD or XHTML. If not found then the default file with no
   * language indicator is returned.
   *
   * @param label the file based name without language or extension
   * @return the language translated file name, if it exists.
   */
  private String findContentFile(String label) throws FileNotFoundException {
    String language = localeBean.getLanguage();
    /**
     * Search for the language specific file.
     */
    for (String extension : new String[]{XHTML, MD}) {
      String path = CONTENT + label + "." + language + extension;
      if (getClass().getClassLoader().getResource(path) != null) {
        return path;
      }
    }
    /**
     * Search for a default file with no language indicator.
     */
    for (String extension : new String[]{XHTML, MD}) {
      String path = CONTENT + label + extension;
      if (getClass().getClassLoader().getResource(path) != null) {
        return path;
      }
    }
    /**
     * FAIL - NO file found.
     */
    throw new FileNotFoundException(label + " not found.");
  }

  /**
   * Read the content as Markdown, and convert it to XHTML
   *
   * @param filename the file base name. The {@code ".md"} extension is added.
   * @return the file content, converted to XHTML
   * @throws IOException if the file cannot be opened or read
   */
  private String readContentXHTML(String filename) throws IOException {
    return readFile(filename);
  }

  /**
   * Read the content as Markdown, and convert it to XHTML
   *
   * @param filename the file base name. The {@code ".md"} extension is added.
   * @return the file content, converted to XHTML
   * @throws IOException if the file cannot be opened or read
   */
  private String readContentMD(String filename) throws IOException {
    String md = readFile(filename);
    Markdown processor = new Markdown();
    return processor.toHtml(md);

//    return readFile(filename);
//    See https://github.com/vsch/flexmark-java
//    MutableDataSet options = new MutableDataSet();
    // uncomment to set optional extensions
    //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
    // uncomment to convert soft-breaks to hard breaks
    //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
//    Parser parser = Parser.builder(options).build();
//    HtmlRenderer renderer = HtmlRenderer.builder(options).build();
//    String md = readFile(filename);
//    Node document = parser.parse(md);
//    String html = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
//    return html;
  }

  /**
   * Simple method to read a text file that is available as a resource stream
   * from this class.
   *
   * @param filename the fully qualified file name
   * @return the unmodified file content
   * @throws IOException if the file cannot be opened or read
   */
  private String readFile(String filename) throws IOException {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
    char[] buffer = new char[2048];
    Reader reader = new InputStreamReader(inputStream, "UTF-8");
    StringBuilder sb = new StringBuilder();
    while (true) {
      int n = reader.read(buffer);
      if (n < 0) {
        break;
      }
      sb.append(buffer, 0, n);
    }
    return sb.toString();
  }

}
