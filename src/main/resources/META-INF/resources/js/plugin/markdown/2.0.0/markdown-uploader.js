/**
 * Markdown editor upload scripts. Adds button plus drag and drop support
 * to a markdown editor widget.
 *
 * The script is wrapped in CDATA tags to ensure compatibility with JSF.
 * CDATA means, Character Data. CDATA is defined as blocks of text that
 * are not parsed by the parser, but are otherwise recognized as markup.
 *
 * @author Key Bridge
 * @since v1.0.0 created 03/02/29
 */

//  <![CDATA[

/**
 * The total bytes to be uploaded to the server. This is used to evaluate 100%
 * in the upload progress.
 * @type Number
 */
var totalBytesToUpload = 0;
/**
 * The current count of uploaded bytes. This value is periodically updated
 * by the browser. It is then used to update the NANOBAR ajax upload status
 * indicator. See the function "uploadProgressCallback"
 * @type Number
 */
var totalBytesUploaded = 0;

/**
 * Bind an element listener for the file opload button.
 * This causes the selected file(s) to be automatically uploaded upon selection.
 * @see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/file
 */
document.getElementById('#{cc.id}-file-input').onchange = function (e) {
  /**
   * Do not need to call e.preventDefault() because by default an input
   * element does nothing on its own.
   */
  console.debug('event=change', e, this.files);
  uploadFiles(document.getElementById('#{cc.id}-edit'), this.files);
};

/**
 * Bind an element listener to handle files dropped onto a text area.
 * The ondrop event occurs when a draggable element or text selection
 * is dropped on a valid drop target.
 */
document.getElementById('#{cc.id}-edit').ondrop = function (e) {
  /**
   * Prevent the browser default handling of the data (default is open as link on drop).
   */
  e.preventDefault(); // IMPORTANT
  uploadFiles(document.getElementById('#{cc.id}-edit'), e.dataTransfer.files);
  /**
   * Revert to normal CSS.
   */
  e.target.classList.add('undotted');
  e.target.classList.remove('dotted');
};

/**
 * IMPORTANT. Required for drag and drop functionality.
 *
 * Glue code to make drag and drop work. Prevent the default DRAG action.
 * We only care about when a file is DROPPED.
 *
 * The ondragover event occurs when a draggable element or text selection
 * is being dragged over a valid drop target. By default, data/elements
 * cannot be dropped in other elements. To allow a drop, we must prevent
 * the default handling of the element. This is done by calling the
 * event.preventDefault() method for the ondragover event.
 * @see https://www.w3schools.com/jsref/event_ondragover.asp
 */
document.getElementById('#{cc.id}-edit').ondragover = function (e) {
  e.preventDefault(); // IMPORTANT
  /**
   * Toggle the CSS to show a dotted border, indicating an action will occur.
   */
  e.target.classList.remove('undotted');
  e.target.classList.add('dotted');
};

/**
 * Revert to normal CSS.
 *
 * Binds an element listener to andle the dragLeave event. This method
 * only toggles the element CSS back to normal, indicating a drop event
 * will not trigger a file upload.
 */
document.getElementById('#{cc.id}-edit').ondragleave = function (e) {
  e.target.classList.add('undotted');
  e.target.classList.remove('dotted');
};

/**
 * Upload the selected files to to the specified upload URL. This
 * method iterated through the selected file(s) and uploads each. Depending
 * upon the browser, files may be uploaded one-by-one or in parallel.
 * Default behavior is parallel upload.
 *
 * @param {type} textArea target textArea to which to insert link to uploaded file
 * @param {type} fileList fileList of files
 * @returns {undefined}
 */
function uploadFiles(textArea, fileList) {
  /**
   * Initialize the upload status counters, then calculate total byte count to be uploaded.
   */
  totalBytesToUpload = 0;
  totalBytesUploaded = 0;
  for (var i = 0; i < fileList.length; i++) {
    totalBytesToUpload += fileList[i].size;
  }
  /**
   * Iterate over the files and upload each one individually. The totalBytesUploaded
   * variable is updated inside the uploadOneFile method (by making periodic
   * calls to "uploadProgressCallback").
   */
  for (var i = 0; i < fileList.length; i++) {
    console.debug('uploading file', fileList[i]);
    uploadOneFile(fileList[i], textArea);
  }
}

/**
 * Insert an HREF link into a markdown text area. Markdown HREF link format
 * is "[label](url)". If the link is to an image then an IMG tag presented
 * by preceeding the HREF link format with an exclamation: "![label](url)".
 *
 * @param {type} textArea the input text area editor
 * @param {type} name the file name
 * @param {type} url the HREF link url
 * @returns {undefined}
 */
function insertUrlIntoText(textArea, name, url) {
  const idx = textArea.selectionStart;
  const textBefore = textArea.value.substring(0, idx);
  const textAfter = textArea.value.substring(idx);
  /**
   * Text is inserted at the current cursor position, with the new HREF inserted
   * on a new line. Add a "!" character if the file is an image.
   */
  var newText = '\n' + (isImageFile(name) ? '![' : '[') + name + '](' + url + ')\n';
  textArea.value = textBefore + newText + textAfter;
  /**
   * Highlight and select the inserted file link reference.
   */
//  textArea.selectionStart = idx + newText.length; // move the cursor to just after the added link
  textArea.focus();
  textArea.selectionStart = idx;
  textArea.selectionEnd = idx + newText.length;

}
/**
 * Inspect a file name and determine if it is an image file.
 * This matches common image filename extensions.
 *
 * @param {type} name the file name
 * @returns {unresolved}
 */
function isImageFile(name) {
  name = name.toLowerCase();
  return name.endsWith('jpg') || name.endsWith('jpeg') || name.endsWith('png') || name.endsWith('gif') || name.endsWith('svg');
}

/**
 * A stub callback function for upload progress. Will be called around 20 times
 * per second for a single file. Updates NANOBAR, the default AJAX notification
 * widget on Key Bridge web pages (and which must be already loaded!)
 * <pre>
 * While the request entity body is being uploaded and the upload complete flag
 * is false, queue a task to fire a progress event named progress at the
 * XMLHttpRequestUpload object about every 50ms or for every byte transmitted,
 * whichever is least frequent.
 * </pre>
 * @see https://dvcs.w3.org/hg/xhr/raw-file/tip/Overview.html
 * @param {File} file the File object for which this progress event happened
 * @param {Event} event an object with two fields: 'loaded' and 'total', containing the uploaded and total bytes of the file, respectively.
 * @returns {void}
 */
function uploadProgressCallback(file, event) {
  console.debug("Upload progress for " + file.name, event.loaded / event.total * 100, '(%)');
  totalBytesUploaded += event.loaded;
  /**
   * Developer note: Total bytes uploaded can exceed the size of the original
   * file (due to form field data) so we cap the ratio at 1.
   */
  nanobar.go(Math.ceil(Math.min(totalBytesUploaded / totalBytesToUpload, 1) * 100));
}

/**
 * A stub handler of the IOIO server response.
 *
 * @param {type} response the string response from the server
 * @param {type} textArea  the input text area editor
 * @param {type} fileName the uploading file name
 * @returns {undefined}
 */
function handleIoioServerResponse(response, textArea, fileName) {
  /**
   * Developer note: request.response is a JSON string so we have to manually parse it as an object.
   */
  const responseObj = JSON.parse(response);
  insertUrlIntoText(textArea, fileName, responseObj.url);
}

/**
 * Upload a single file to the server
 *
 * @param {type} file the uploading file
 * @param {type} textArea  the input text area editor
 * @returns {undefined}
 * @see https://developer.mozilla.org/en-US/docs/Web/API/File
 */
function uploadOneFile(file, textArea) {
  var formData = new FormData();

  // The user selected file to upload.
  formData.append("file", file);
  formData.append("contentType", file.type);
  formData.append("size", file.size);
  /**
   * fileUploadToken is a JWT identifying the sending application and possibly the user.
   * fileUploadToken ispresented by the sending web service on behalf of the user
   */
  formData.append("token", '#{cc.attrs.fileUploadToken}');
  /**
   * Prepare an AJAX request to upload the file.
   */
  var request = new XMLHttpRequest();
  request.open('POST', '#{cc.attrs.fileUploadUrl}');
  /**
   * Bind a listener to the upload progress reporter. Use it to update the AJAX status.
   */
  request.upload.onprogress = function (event) {
    uploadProgressCallback(file, event);
  };
  /**
   * Bind a listener to handle the Server response, which identifies the uploaded
   * file URL.
   */
  request.onload = function (event) {
    handleIoioServerResponse(request.response, textArea, file.name);
  };
  /**
   * Send the file.
   */
  request.send(formData);
}
//      ]]>
