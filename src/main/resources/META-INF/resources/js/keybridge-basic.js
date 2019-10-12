/* 
 * Copyright 2019 Key Bridge. All rights reserved. Use is subject to license
 * terms.
 * 
 * This software code is protected by Copyrights and remains the property of
 * Key Bridge and its suppliers, if any. Key Bridge reserves all rights in and to
 * Copyrights and no license is granted under Copyrights in this Software
 * License Agreement.
 * 
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request by sending an email to info@keybridgewireless.com.
 *  
 * All information contained herein is the property of Key Bridge and its
 * suppliers, if any. The intellectual and technical concepts contained herein
 * are proprietary.
 * 
 * @since v4.1.1 copy active items from sas-portal
 */


// set the active tab on document load
$(function () {
  setActiveTab();
  selectListItem();
});
// set the active tab
function setActiveTab() {
  var path = window.location.pathname;
  $('.nav-undertabs a').each(function () {
    var href = $(this).attr('href');
    var hrefdir = href.substring(0, href.length - 11); // strip index.xhtml
    if (path.substring(0, hrefdir.length) === hrefdir) {
      $(this).addClass('nav-tabs');
      $(this).addClass('active');
    }
  });
}
// set the active list item
function selectListItem() {
  var path = window.location.pathname;
  $('.nav-list-group a').each(function () {
    var href = $(this).attr('href');
    var hrefdir = href.substring(0, href.length - 11); // strip index.xhtml
    if (path.substring(0, hrefdir.length) === hrefdir) {
      $(this).addClass('selected');
    }
  });
}