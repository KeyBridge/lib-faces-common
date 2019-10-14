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
   * fileUploadToken ispresented by the sending web service on behalf of the user. 
   * In the JWT: the user IP address is the shared secret.
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
