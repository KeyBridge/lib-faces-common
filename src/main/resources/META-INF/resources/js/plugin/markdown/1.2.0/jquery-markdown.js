/**
 * jQuery Markdown provides necessary wrapper functions for a markdown editor so
 * anybody can create his/her own markdown editor using this plugin.
 *
 * jquery-markdown 1.1.0
 * @author Can Geliş
 * @author Key Bridge since 2018
 * @license License file must be attached with the source code (MIT License)
 * @see https://github.com/cangelis/jquery-markdown
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
  if (selectionStart === selectionEnd) {
    value = $(textarea).val() + settings.no_selection();
  } else {
    value = $(textarea).val().substring(0, selectionStart) +
            settings.selected(text_to_replace) +
            $(textarea).val().substring(selectionEnd);
  }
  $(textarea).val(value);
};
String.prototype.repeat = function (num)
{
  return new Array(num + 1).join(this);
};
$.fn.mdBold = function (options) {
  var settings = $.extend({
    default: "Text Here"
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
$.fn.mdItalic = function (options) {
  var settings = $.extend({
    default: "Text Here"
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return "*" + text + "*";
    },
    no_selection: function () {
      return "*" + settings.default + "*";
    }
  });
};
$.fn.mdHeader = function (options) {
  var settings = $.extend({
    default: "Heading Here",
    number: 1
  }, options);
  $(this).textReplace({
    selected: function (text) {
      return '\n\n' + "#".repeat(settings.number) + " " + text + '\n\n';
    },
    no_selection: function () {
      return '\n' + "#".repeat(settings.number) + " " + settings.default + '\n\n';
    }
  });
};
$.fn.mdLink = function (options) {
  var settings = $.extend({
    default_text: "Link Text Here",
    default_url: "URL Here"
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
$.fn.mdImage = function (options) {
  var settings = $.extend({
    default_alt_text: "",
    default_image_url: "Image URL Here",
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
$.fn.mdCodeInline = function (options) {
  var settings = $.extend({
    default: "Code here"
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
$.fn.mdCode = function (options) {
  var settings = $.extend({
    default: "Code here"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '\n\n```\n' + code + '\n```\n\n';
      } else {
        final_code = "\n\n```\n";
        for (i = 0; i < lines.length; i++) {
          final_code += lines[i] + '\n';
        }
        final_code += "\n```\n\n";
        return final_code;
      }
    },
    no_selection: function () {
      return '\n```\n' + settings.default + '\n```\n';
    }
  });
};
$.fn.mdQuote = function (options) {
  var settings = $.extend({
    default: "Quote here"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '\n> ' + code + '\n';
      } else {
        final_code = "\n\n";
        for (i = 0; i < lines.length; i++) {
          final_code += "> " + lines[i] + '\n';
        }
        final_code += "\n";
        return final_code;
      }
    },
    no_selection: function () {
      return '\n> ' + settings.default + '\n';
    }
  });
}
$.fn.mdNumberedList = function (options) {
  var settings = $.extend({
    default: "List item"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '1. ' + code + '\n';
      } else {
        final_code = "\n\n";
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
}
$.fn.mdBulletList = function (options) {
  var settings = $.extend({
    default: "List item"
  }, options);
  $(this).textReplace({
    selected: function (code) {
      lines = code.split('\n');
      if (lines.length === 1) {
        return '- ' + code + '\n';
      } else {
        final_code = "\n\n";
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
}

