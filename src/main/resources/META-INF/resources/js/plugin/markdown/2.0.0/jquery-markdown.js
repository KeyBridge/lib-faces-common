/**
 * jQuery Markdown is a jQuery Plugin that provides necessary functions for a markdown editor
 *
 * jQuery Markdown provides necessary wrapper functions for a markdown editor so
 * anybody can create his/her own markdown editor using this plugin.
 *
 * jquery-markdown 1.1.0
 *
 * @author Can Geliş (original) ca 2013
 * @author Key Bridge since 2018
 * @license License file must be attached with the source code (MIT License)
 * @see https://github.com/cangelis/jquery-markdown  (original)
 *
 * @since v2.0.0 created 03/02/19 add text formatting functions, tables
 */

/**
 * Prototype text substitution function. Used for text formatting.
 * @param {type} options the options, determines default text
 * @returns {undefined}
 */
$.fn.textReplace = function (options) {
  var settings = $.extend({
    selected: function () {
      return "";
    },
    no_selection: function () {
      return "";
    }
  }, options);
  var textarea = this, actual = this.get(0);
  var selectionStart = actual.selectionStart, selectionEnd = actual.selectionEnd;
  var text_to_replace = $(textarea).val().substring(selectionStart, selectionEnd);
  var scrollTop = this.scrollTop;

  var newText;
  if (selectionStart === selectionEnd) {
    newText = settings.no_selection();
  } else {
    newText = settings.selected(text_to_replace);
  }
  /**
   * Update the textarea content with the new text.
   */
  $(textarea).val($(textarea).val().substring(0, selectionStart) + newText + $(textarea).val().substring(selectionEnd));
  /**
   * Highlight and select the inserted text.
   */
  $(textarea).focus();
  $(textarea).prop('selectionStart', selectionStart);
  $(textarea).prop('selectionEnd', selectionStart + newText.length);
};

String.prototype.repeat = function (num) {
  return new Array(num + 1).join(this);
};

/**
 * Bold text.
 */
$.fn.mdBold = function (options) {
  var settings = $.extend({
    default: "bold"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "**" + text + "**";
    },
    no_selection: function () {
      return "**" + settings.default + "**";
    }
  });
};

/**
 * Italic text.
 */
$.fn.mdItalic = function (options) {
  var settings = $.extend({
    default: "italic"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "_" + text + "_";
    },
    no_selection: function () {
      return "_" + settings.default + "_";
    }
  });
};

/**
 * Underline text.
 */
$.fn.mdUnderline = function (options) {
  var settings = $.extend({
    default: "underline"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "++" + text + "++";
    },
    no_selection: function () {
      return "++" + settings.default + "++";
    }
  });
};


/**
 * Superscript text.
 */
$.fn.mdSuperscript = function (options) {
  var settings = $.extend({
    default: "super"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "^" + text + "^";
    },
    no_selection: function () {
      return "^" + settings.default + "^";
    }
  });
};

/**
 * Subscript text.
 */
$.fn.mdSubscript = function (options) {
  var settings = $.extend({
    default: "sub"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "~" + text + "~";
    },
    no_selection: function () {
      return "~" + settings.default + "~";
    }
  });
};

/**
 * Highlighted text.
 */
$.fn.mdHighlight = function (options) {
  var settings = $.extend({
    default: "highlighted"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "==" + text + "==";
    },
    no_selection: function () {
      return "==" + settings.default + "==";
    }
  });
};

/**
 * Strikethrough text.
 */
$.fn.mdStrikethrough = function (options) {
  var settings = $.extend({
    default: "strikethrough"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "~~" + text + "~~";
    },
    no_selection: function () {
      return "~~" + settings.default + "~~";
    }
  });
};

/**
 * Header. Options should specify the number.
 */
$.fn.mdHeader = function (options) {
  var settings = $.extend({
    default: "Heading",
    number: 1
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return '\n' + "#".repeat(settings.number) + " " + text + '\n';
    },
    no_selection: function () {
      return '\n' + "#".repeat(settings.number) + " " + settings.default + '\n';
    }
  });
};

/**
 * An HREF link.
 */
$.fn.mdLink = function (options) {
  var settings = $.extend({
    default_text: "Link",
    default_url: "URL"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return '[' + text + '](' + settings.default_url + ')';
    },
    no_selection: function () {
      return '[' + settings.default_text + '](' + settings.default_url + ')';
    }
  });
};

/**
 * An HREF link to an image.
 */
$.fn.mdImage = function (options) {
  var settings = $.extend({
    default_alt_text: "",
    default_image_url: "Image URL",
    default_image_title: ""
  }, options);
  $(this).textReplace({
    selected: function (image_link) {
      return '![' + settings.default_alt_text + '](' + image_link + ' "' + settings.default_image_title + '")';
    },
    no_selection: function () {
      return '![' + settings.default_alt_text + '](' + settings.default_image_url + ' "' + settings.default_image_title + '")';
    }
  });
};

/**
 * Inline code.
 */
$.fn.mdCodeInline = function (options) {
  var settings = $.extend({
    default: "inline code"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '`' + code + '`';
      } else {
        final_code = "";
        for (i = 0; i < lines.length; i++) {
          final_code += "    " + lines[i] + '\n';
        }
        return final_code;
      }
    },
    no_selection: function () {
      return '`' + settings.default + '`';
    }
  });
};

/**
 * A code block
 */
$.fn.mdCode = function (options) {
  var settings = $.extend({
    default: "Code block",
    language: "language"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '\n```' + settings.language + '\n' + code + '\n```\n';
      } else {
        final_code = "\n```\n";
        for (i = 0; i < lines.length; i++) {
          final_code += lines[i] + '\n';
        }
        final_code += "\n```\n";
        return final_code;
      }
    },
    no_selection: function () {
      return '\n```' + settings.language + '\n' + settings.default + '\n```\n';
    }
  });
};
/**
 * Block quote.
 */
$.fn.mdQuote = function (options) {
  var settings = $.extend({
    default: "Block quote"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '\n> ' + code + '\n';
      } else {
        final_code = "\n";
        for (i = 0; i < lines.length; i++) {
          final_code += "> " + lines[i] + '\n';
        }
//        final_code += "\n";
        return final_code;
      }
    },
    no_selection: function () {
      return '\n> ' + settings.default + '\n';
    }
  });
};

/**
 * Numbered list.
 */
$.fn.mdNumberedList = function (options) {
  var settings = $.extend({
    default: "Numbered list item"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '1. ' + code + '\n';
      } else {
        final_code = "\n";
        for (i = 0; i < lines.length; i++) {
          final_code += (i + 1) + ". " + lines[i] + '\n';
        }
        final_code += "\n";
        return final_code;
      }
    },
    no_selection: function () {
      return '1. ' + settings.default + '\n';
    }
  });
};

/**
 * Bullet list.
 */
$.fn.mdBulletList = function (options) {
  var settings = $.extend({
    default: "Bullet list item"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '- ' + code + '\n';
      } else {
        final_code = "\n";
        for (i = 0; i < lines.length; i++) {
          final_code += "- " + lines[i] + '\n';
        }
        final_code += "\n";
        return final_code;
      }
    },
    no_selection: function () {
      return '- ' + settings.default + '\n';
    }
  });
};


/**
 * Footnote reference.
 */
$.fn.mdFootnote = function (options) {
  var settings = $.extend({
    default: "footnote"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return  text + "[^1]\n\n[^1] Footnote text\n\n";
    },
    no_selection: function () {
      return  "[^1]\n\n[^1] Footnote text\n\n";
    }
  });
};

/**
 * Horizontal line.
 */
$.fn.mdHorizontalLine = function (options) {
  $(this).textReplace({
    selected: function (text) {
      return "\n---\n" + text;
    },
    no_selection: function () {
      return "\n---\n";
    }
  });
};


/**
 * Convert CSV text to a table. If not text is selected create an empty three
 * column table.
 */
$.fn.mdTable = function (options) {
  $(this).textReplace({
    selected: function (csvText) {
      lines = csvText.split('\n');
      tbody = '';
      cellcount = 0;
      for (i = 0; i < lines.length; i++) {
        line = lines[i]; // select the current line
        cells = line.split(',');
        cellcount = cells.length; // count the number of cells;
        tbody += '|'; // initialize the row
        for (var j = 0; j < cells.length; j++) {
          tbody += ' ' + cells[j] + ' |'; // add the current cell to the row
        }
        tbody += '\n'; // finalize the row
      }
      // create the header
      thead = "|";
      for (var i = 0; i < cellcount; i++) {
        thead += "---|";
      }
      return thead + "\n" + tbody;
    },
    no_selection: function () {
      return "|---|---|---|\n| a | b | c |\n";
    }
  });
};

/**
 * Insert a flexmark macro extension.
 *
 * Macro Definitions are block elements which can contain any
 * markdown element(s) but can be expanded in a block or inline context,
 * allowing block elements to be used where only inline elements are permitted
 * by the syntax.
 * @see https://github.com/vsch/flexmark-java/wiki/Macros-Extension
 */
$.fn.mdMacro = function (options) {
  var textarea = this, actual = this.get(0);
  var selectionStart = actual.selectionStart, selectionEnd = actual.selectionEnd;
  var text_to_replace = $(textarea).val().substring(selectionStart, selectionEnd);
  var scrollTop = this.scrollTop;
  $(textarea).focus();

  var newText;
  var macroText;
  if (selectionStart === selectionEnd) {
    newText = "<<<macroname>>>\n\n>>>macroname\nmacro text\n<<<\n";
    /**
     * Update the textarea content with the new text.
     */
    $(textarea).val($(textarea).val().substring(0, selectionStart) + newText + $(textarea).val().substring(selectionEnd));
    /**
     * Highlight and select the inserted text.
     */  $(textarea).prop('selectionStart', selectionStart);
    $(textarea).prop('selectionEnd', selectionStart + newText.length);
  } else {
    newText = "<<<" + text_to_replace + "Macro>>>";
    macroText = "\n\n>>>" + text_to_replace + "Macro\nmacro content\n<<<\n";
    $(textarea).val($(textarea).val().substring(0, selectionStart) + newText + $(textarea).val().substring(selectionEnd) + macroText);
    /**
     * Highlight and select the inserted text.
     */ $(textarea).prop('selectionStart', $(textarea).val().length - macroText.length);
    $(textarea).prop('selectionEnd', $(textarea).val().length);
  }
};


/**
 * Inserted text. This marks text as added (when showing diffs).
 */
$.fn.mdInsert = function (options) {
  var settings = $.extend({
    default: "inserted"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "{+" + text + "+}";
    },
    no_selection: function () {
      return "{+" + settings.default + "+}";
    }
  });
};
/**
 * Deleted text. This marks text as deleted (when showing diffs).
 */
$.fn.mdDelete = function (options) {
  var settings = $.extend({
    default: "deleted"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "{-" + text + "-}";
    },
    no_selection: function () {
      return "{-" + settings.default + "-}";
    }
  });
};


/**
 * KaTEX Math.
 * It is possible to have math written with the LaTeX syntax rendered using KaTeX.
 * Math written inside $``$ will be rendered inline with the text.
 * Math written inside triple back quotes, with the language declared as math,
 * will be rendered on a separate line. Math, inline via $``$ or as fenced code
 * with math info string requiring inclusion of Katex in the rendered HTML page.
 *
 * Requires https://cdn.jsdelivr.net/npm/katex@0.10.0/dist/katex.min.css
 * Requires https://cdn.jsdelivr.net/npm/katex@0.10.0/dist/katex.min.js
 * Requires https://cdn.jsdelivr.net/npm/katex@0.10.0/dist/contrib/auto-render.min.js
 * and renderMathInElement(document.body);
 * @see https://katex.org
 */
$.fn.mdEquationInline = function (options) {
  var settings = $.extend({
    default: "a^2 + b^2 = c^2"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "$`" + text + "`$";
    },
    no_selection: function () {
      return "$`" + settings.default + "`$";
    }
  });
};

/**
 * KaTEX Math.
 * It is possible to have math written with the LaTeX syntax rendered using KaTeX.
 * Math written inside $``$ will be rendered inline with the text.
 * Math written inside triple back quotes, with the language declared as math,
 * will be rendered on a separate line. Math, inline via $``$ or as fenced code
 * with math info string requiring inclusion of Katex in the rendered HTML page.
 *
 * Requires https://cdn.jsdelivr.net/npm/katex@0.10.0/dist/katex.min.css
 * Requires https://cdn.jsdelivr.net/npm/katex@0.10.0/dist/katex.min.js
 * Requires https://cdn.jsdelivr.net/npm/katex@0.10.0/dist/contrib/auto-render.min.js
 * and renderMathInElement(document.body);
 * @see https://katex.org
 */
$.fn.mdEquation = function (options) {
  var settings = $.extend({
    default: 'c = \pm\sqrt{a^2 + b^2}'
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "\n```math\n" + text + "```\n";
    },
    no_selection: function () {
      return "\n```math\n" + settings.default + "\n```\n";
    }
  });
};


/**
 * Mermaid chart.
 * It is possible to generate diagrams and flowcharts from text using Mermaid.
 * In order to generate a diagram or flowchart, you should write your text
 * inside the mermaid block. Graphing via Mermaid as fenced code with mermaid
 * info string, via Mermaid inclusion similar to Math solution above.

 * Requires https://unpkg.com/mermaid@8.0.0/dist/mermaid.core.js
 * Requires https://unpkg.com/mermaid@8.0.0/dist/mermaid.js
 * @see https://mermaidjs.github.io
 */
$.fn.mdChart = function (options) {
  var settings = $.extend({
    default: 'graph LR\n  A[Square Rect] -- Link text --> B((Circle))\n  A --> C(Round Rect)\n  B --> D{Rhombus}\n  C --> D'
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "\n```mermaid\n" + text + "\n```\n";
    },
    no_selection: function () {
      return "\n```mermaid\n" + settings.default + "\n```\n";
    }
  });
};
