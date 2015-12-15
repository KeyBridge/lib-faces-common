/*
 * Copyright 2012 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ch.keybridge.lib.faces.util;

import ch.keybridge.lib.faces.FacesUtil;
import java.beans.Introspector;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import static java.util.regex.Pattern.quote;
import javax.faces.component.*;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.json.Json;

/**
 * Collection of utility methods for working with {@link PartialViewContext}.
 * There are also shortcuts to the current {@link OmniPartialViewContext}
 * instance.
 * <p>
 * This utility class allows an easy way of programmaticaly (from inside a
 * managed bean method) specifying new client IDs which should be ajax-updated,
 * also {@link UIData} rows or columns on specific index, specifying callback
 * scripts which should be executed on complete of the ajax response and adding
 * arguments to the JavaScript scope. The added arguments are during the "on
 * complete" phase as a JSON object available by
 * <code>OmniFaces.Ajax.data</code> in JavaScript context.
 * <p>
 * The JSON object is been encoded by {@link Json#encode(Object)} which supports
 * standard Java types {@link Boolean},
 * {@link Number}, {@link CharSequence} and {@link Date} arrays,
 * {@link Collection}s and {@link Map}s of them and as last resort it will use
 * the {@link Introspector} to examine it as a Javabean and encode it like a
 * {@link Map}.
 * <p>
 * Note that {@link #updateRow(UIData, int)} and
 * {@link #updateColumn(UIData, int)} can only update cell content when it has
 * been wrapped in some container component with a fixed ID.
 *
 * @author Bauke Scholtz
 * @since forked from Omnifaces v2.2; added to faces-common v2.1.5 on 12/15/15
 */
public final class Ajax {

  private Ajax() {
    // Hide constructor.
  }

  /**
   * Update the entire view.
   *
   * @see PartialViewContext#setRenderAll(boolean)
   * @since 1.5
   */
  public static void updateAll() {
    FacesUtil.getContext().getPartialViewContext().setRenderAll(true);
  }

  /**
   * Update the given client IDs in the current ajax response. Note that those
   * client IDs should not start with the naming container separator character
   * like <code>:</code>. This method also supports the client ID keywords
   * <code>@all</code>, <code>@form</code> and <code>@this</code> which
   * respectively refers the entire view, the currently submitted form as
   * obtained by {@link Components#getCurrentForm()} and the currently processed
   * component as obtained by
   * {@link UIComponent#getCurrentComponent(FacesContext)}. Any other client ID
   * starting with <code>@</code> is by design ignored, including
   * <code>@none</code>.
   *
   * @param clientIds The client IDs to be updated in the current ajax response.
   * @see PartialViewContext#getRenderIds()
   */
  public static void update(String... clientIds) {
    /**
     * Get the current partial view context (the ajax context).
     */
    PartialViewContext context = FacesUtil.getContext().getPartialViewContext();
    Collection<String> renderIds = context.getRenderIds();

    for (String clientId : clientIds) {
      if (clientId.charAt(0) != '@') {
        renderIds.add(clientId);
      } else if (clientId.equals("@all")) {
        context.setRenderAll(true);
      } else if (clientId.equals("@form")) {
        UIComponent currentForm = getCurrentForm();

        if (currentForm != null) {
          renderIds.add(currentForm.getClientId());
        }
      } else if (clientId.equals("@this")) {
        UIComponent currentComponent = getCurrentComponent();

        if (currentComponent != null) {
          renderIds.add(currentComponent.getClientId());
        }
      }
    }
  }

  //<editor-fold defaultstate="collapsed" desc="Helper Methods">
  /**
   * Returns the current UI component from the EL context.
   *
   * @return The current UI component from the EL context.
   * @see UIComponent#getCurrentComponent(FacesContext)
   */
  private static UIComponent getCurrentComponent() {
    return UIComponent.getCurrentComponent(FacesUtil.getContext());
  }

  /**
   * Returns the currently submitted UI form component, or <code>null</code> if
   * there is none, which may happen when the current request is not a postback
   * request at all, or when the view has been changed by for example a
   * successful navigation. If the latter is the case, you'd better invoke this
   * method before navigation.
   *
   * @return The currently submitted UI form component.
   * @see UIForm#isSubmitted()
   */
  private static UIForm getCurrentForm() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.isPostback()) {
      return null;
    }
    UIViewRoot viewRoot = context.getViewRoot();
// The initial implementation has visited the tree for UIForm components which returns true on isSubmitted().
// But with testing it turns out to return false on ajax requests where the form is not included in execute!
// The current implementation just walks through the request parameter map instead.
    for (String name : context.getExternalContext().getRequestParameterMap().keySet()) {
      if (name.startsWith("javax.faces.")) {
        continue; // Quick skip.
      }
      UIComponent component = findComponentIgnoringIAE(viewRoot, stripIterationIndexFromClientId(name));
      if (component instanceof UIForm) {
        return (UIForm) component;
      } else if (component != null) {
        UIForm form = getClosestParent(component, UIForm.class);
        if (form != null) {
          return form;
        }
      }
    }
    return null;
  }

  /**
   * Strip UIData/UIRepeat iteration index in pattern <code>:[0-9+]:</code> from
   * given component client ID.
   */
  private static String stripIterationIndexFromClientId(String clientId) {
    String separatorChar = Character.toString(UINamingContainer.getSeparatorChar(FacesUtil.getContext()));
    return clientId.replaceAll(quote(separatorChar) + "[0-9]+" + quote(separatorChar), separatorChar);
  }

  /**
   * Use {@link UIViewRoot#findComponent(String)} and ignore the potential
   * {@link IllegalArgumentException} by returning null instead.
   */
  private static UIComponent findComponentIgnoringIAE(UIViewRoot viewRoot, String clientId) {
    try {
      return viewRoot.findComponent(clientId);
    } catch (IllegalArgumentException ignore) {
// May occur when view has changed by for example a successful navigation.
      return null;
    }
  }

  /**
   * Returns from the given component the closest parent of the given parent
   * type, or <code>null</code> if none is found.
   *
   * @param <C>        The generic component type.
   * @param component  The component to return the closest parent of the given
   *                   parent type for.
   * @param parentType The parent type.
   * @return From the given component the closest parent of the given parent
   *         type, or <code>null</code> if none is found.
   */
  private static <C extends UIComponent> C getClosestParent(UIComponent component, Class<C> parentType) {
    UIComponent parent = component.getParent();
    while (parent != null && !parentType.isInstance(parent)) {
      parent = parent.getParent();
    }
    return parentType.cast(parent);
  }//</editor-fold>

}
