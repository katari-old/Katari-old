/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.menu.classic.application;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.security.MenuAccessFilterer;
import com.globant.katari.core.web.MenuBar;
import com.globant.katari.core.web.MenuNode;

/** Utility class to assist in the selection of menu entries to display for a
 * user.
 */
public class MenuDisplayHelper {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(MenuDisplayHelper.class);

  /** The menu bar to obtain the list of menu nodes.
   *
   * It is never null.
   */
  private MenuBar menu;

  /** The current menu entry, expressed as a path of menu names, like
   * /root/Administration/Users, never null.
   */
  private String currentMenuEntry;

  /** The Menu Access Filterer.
   *
   * It's used to filter the menu nodes according to the user permissions.
   * This is never null.
   */
  private MenuAccessFilterer filterer;

  /** Builds a menu display helper.
   *
   * @param menuBar The menu bar to obtain the list of menu nodes. It cannot be
   * null.
   *
   * @param current The current menu entry, expressed as a path of menu names,
   * like /root/Administration/Users. It cannot be null.
   *
   * @param theFilterer The Menu Access Filterer used to filter the menu nodes
   * according to the user permissions.  It cannot be null.
   */
  public MenuDisplayHelper(final MenuBar menuBar, final String current,
      final MenuAccessFilterer theFilterer) {
    Validate.notNull(menuBar, "The menu bar cannot be null");
    Validate.notNull(current, "The current menu entry cannot be null");
    Validate.notNull(menuBar, "The menu access filterer cannot be null");
    menu = menuBar;
    currentMenuEntry = current;
    filterer = theFilterer;
  }

  /** Obtains a list of MenuNodes with its display attributes according to the
   * currently logged on user permissions, the menu level and the current
   * selection.
   *
   * @param level The level required. The list of menu nodes is obtained from
   * this level. It must be greater than 1. A level 0 represent the root level,
   * never returned by this method.
   *
   * @return a list of menu nodes with the display attributes according to the
   * currently logged on user permissions.
   */
  public List<MenuNodeDisplay> getMenuNodesForLevel(final int level) {
    Validate.isTrue(level > 0, "The level must be greater than 0");
    return getMenuNodesForLevel(menu, level - 1);
  }

  /** Obtains a list of MenuNodes with its display attributes according to the
   * currently logged on user permissions and the current selection.
   *
   * It returns all the menus that are children of the specified path.
   *
   * @return a list of menu nodes with the display attributes.
   */
  public List<MenuNodeDisplay> getMenuNodesForCurrentPath() {

    int level = StringUtils.countMatches(currentMenuEntry, "/");

    return getMenuNodesForLevel(level);
  }

  /** Obtains a list of MenuNodes with its display attributes according to the
   * currently logged on user permissions, the menu level and the current
   * selection.
   *
   * It starts looking from the provided node.
   *
   * @param node The node to start looking. It cannot be null.
   *
   * @param level The level required. The list of menu nodes is obtained from
   * this level.
   *
   * @return a list of menu nodes with the display attributes according to the
   * currently logged on user permissions. If node if a leaf, it returns the
   * empty list. Never returns null.
   */
  private List<MenuNodeDisplay> getMenuNodesForLevel(final MenuNode node,
      final int level) {

    Validate.notNull(node, "The menu node cannot be null");

    if (log.isTraceEnabled()) {
      log.trace("Entering getMenuNodesForLevel('" + node.getPath() + "', '"
          + currentMenuEntry + "', " + level + ")");
    }

    List<MenuNodeDisplay> nodes = new LinkedList<MenuNodeDisplay>();
    if (node.isLeaf()) {
      return nodes;
    }

    List<MenuNode> filteredMenuNodes;
    filteredMenuNodes = filterer.filterMenuNodes(node.getChildNodes());

    for (MenuNode child : filteredMenuNodes) {
      log.debug("Checking if '" + child.getPath() + "' is selected");
      boolean selected;
      selected = (currentMenuEntry + "/").startsWith(child.getPath() + "/");
      if (level == 0) {
        // We reached the desired level, add the child node to the list.
        log.debug("Adding '" + child.getPath() + "' to the list");

        String linkPath;
        String path;
        if (child.isLeaf()) {
          linkPath = child.getLinkPath();
          path = child.getPath();
        } else {
          // It is not a leaf, we must find the first non-leaf descendant node
          // that the user has access to and use its link for the current node
          // link.
          if (child.getHome() == null) {
            // The children of the node come from merging two (or more) menu
            // entries.
            linkPath = "/module/classic-menu/menu.do";
            path = child.getPath();
          } else {
            MenuNode descendant = findFirstDescendentLeaf(child);
            /// TODO Is this null check necessary? Aren't we masking an error?
            if (descendant != null) {
              linkPath = descendant.getLinkPath();
              path = descendant.getPath();
            } else {
              linkPath = "/module/classic-menu/menu.do";
              path = child.getPath();
            }
          }
        }
        nodes.add(new MenuNodeDisplay(child, path, linkPath, true, selected));
      } else if (selected) {
        // Not in the desired level yet, but the current node is the selected
        // one, go one level deeper.
        nodes = getMenuNodesForLevel(child, level - 1);
        break;
      }
    }

    log.trace("Leaving getMenuNodesForLevel");
    return nodes;
  }

  /** Returns the current menu entry.
   *
   * @return the current menu entry, never null.
   */
  public String getCurrentMenuEntry() {
    return currentMenuEntry;
  }

  /** Finds the first descendant leaf of the provided node that the user has
   * access to.
   *
   * @param node The node to start looking. It must be a non-leaf node. It
   * cannot be null.
   *
   * @return Returns the first accesible descendant, or null if the node has no
   * accessible descendants.
   */
  MenuNode findFirstDescendentLeaf(final MenuNode node) {
    Validate.notNull(node, "The menu node cannot be null");
    Validate.isTrue(!node.isLeaf(), "The menu node cannot be a leaf");
    List<MenuNode> filteredMenuNodes;
    filteredMenuNodes = filterer.filterMenuNodes(node.getChildNodes());
    if (filteredMenuNodes.size() > 0) {
      MenuNode child = filteredMenuNodes.get(0);
      if (child.isLeaf()) {
        return child;
      } else {
        return findFirstDescendentLeaf(child);
      }
    }
    return null;
  }
}

