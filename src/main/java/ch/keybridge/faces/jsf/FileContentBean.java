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
package ch.keybridge.faces.jsf;

import ch.keybridge.faces.FacesUtil;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.macros.MacrosExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.KeepType;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;

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
 * @since v4.0.0 rewrite to use flexmark markdown parser
 * @since v5.1.0 rewrite 2021-01-09 to use faces context, content must be placed
 * in /resources instead of META-INF
 */
public class FileContentBean implements Serializable {

  private static final Logger LOGGER = Logger.getLogger(FileContentBean.class.getName());

  /**
   * ".locale" The locale cookie name.
   */
  private static final String LOCALE_COOKIE = ".locale";

  /**
   * "META-INF/content/". The content directory.
   */
  private static final String CONTENT = "/resources/content/";
  /**
   * ".md". The markdown file extension.
   */
  private static final String MD = ".md";
  /**
   * ".json". The JSON file extension.
   */
  private static final String JSON = ".json";
  /**
   * ".xml". The XML file extension.
   */
  private static final String XML = ".xml";
  /**
   * ".txt". Alternate markdown file extension.
   */
  private static final String TEXT = ".txt";
  /**
   * ".xhtml". The HTML file extension.
   */
  private static final String XHTML = ".xhtml";

  /**
   * "en". The default language.
   */
  private static final String ENGLISH = "en";

  /**
   * The preferred render language.
   */
  private String language = ENGLISH;

  /**
   * Initialize the Locale configuration. If there is no requested locale in the
   * FacesContext then the system default locale is used.
   */
  @PostConstruct
  protected void postConstruct() {
    Cookie localeCookie = FacesUtil.getCookie(LOCALE_COOKIE);
    if (localeCookie != null) {
      language = localeCookie.getValue();
    }
  }

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
      return readContentFile(label);
    } catch (IOException | URISyntaxException exception) {
      LOGGER.log(Level.INFO, "Error reading file \"{0}\".  {1}", new Object[]{label, exception.getMessage()});
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
  private String readContentFile(String label) throws URISyntaxException, IOException {
    /**
     * Search for a language specific file. If not found then search for a
     * default file with no language indicator.
     */
    for (String extension : new String[]{MD, JSON, XML, TEXT, XHTML}) {
      String path = CONTENT + label + extension;
      String pathLocale = CONTENT + label + "." + language + extension;
      String content = null;
      if (FacesUtil.getExternalContext().getResource(pathLocale) != null) {
        content = new BufferedReader(new InputStreamReader(FacesUtil.getExternalContext().getResourceAsStream(pathLocale))).lines().collect(Collectors.joining("\n"));
      } else if (FacesUtil.getExternalContext().getResource(path) != null) {
        content = new BufferedReader(new InputStreamReader(FacesUtil.getExternalContext().getResourceAsStream(path))).lines().collect(Collectors.joining("\n"));
      }
      if (content != null) {
        return MD.equals(extension) ? transformMarkdown(content) : content;
      }
    }
    /**
     * FAIL - NO file found.
     */
    throw new IOException(language.toUpperCase() + " file content not found for " + CONTENT + label);
  }

  /**
   * Read the content as Markdown, and convert it to XHTML
   *
   * @param filename the file base name. The {@code ".md"} extension is added.
   * @return the file content, converted to XHTML
   * @throws IOException if the file cannot be opened or read
   */
  private String readContentRaw(Path filename) throws IOException {
    return new String(Files.readAllBytes(filename));
  }

  /**
   * Transform the content as Markdown, converting it to XHTML
   *
   * @param filename the file base name. The {@code ".md"} extension is added.
   * @return the file content, converted to XHTML
   * @throws IOException if the file cannot be opened or read
   */
  private String transformMarkdown(String markdown) throws IOException {
    /**
     * Set extensions.
     * <p>
     * TablesExtension enables tables using pipes. <br>
     * GitLabExtension parses and renders GitLab Flavoured Markdown including
     * math (via Katex) and charts (via Mermaid). <br>
     * TaskListExtension renders check boxes in lists (cute).
     * <p>
     * Macro Definitions are block elements which can contain any markdown
     * element(s) but can be expanded in a block or inline context, allowing
     * block elements to be used where only inline elements are permitted by the
     * syntax. See https://github.com/vsch/flexmark-java/wiki/Macros-Extension
     */
    MutableDataSet options = new MutableDataSet();
    options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(),
                                                 GitLabExtension.create(),
                                                 TaskListExtension.create(),
                                                 MacrosExtension.create()));
    /**
     * Parser.REFERENCES_KEEP defines the behavior of references when duplicate
     * references are defined in the source. In this case it is configured to
     * keep the last value, whereas the default behavior is to keep the first
     * value.
     * <p>
     * TablesExtension added for full GFM table compatibility.
     */
    options.set(Parser.REFERENCES_KEEP, KeepType.LAST)
      .set(HtmlRenderer.INDENT_SIZE, 2)
      .set(HtmlRenderer.PERCENT_ENCODE_URLS, true)
      .set(TablesExtension.CLASS_NAME, "table")
      .set(TablesExtension.COLUMN_SPANS, false)
      .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
      .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
      .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
    /**
     * Optionally to convert soft-breaks to hard breaks. Disabled by default.
     */
    //      options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
    Parser parser = Parser.builder(options).build();
    HtmlRenderer renderer = HtmlRenderer.builder(options).build();
    Node document = parser.parse(markdown);
    return renderer.render(document);  // The file rendered to HTML
  }

  /**
   * @deprecated replaced with Java7 FileReader.
   *
   * Internal method to read a text file that is available as a resource stream
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
