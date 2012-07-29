/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.integration;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.Authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.user.domain.User;

/** Security related utilities.
 *
 * This class provides operations to obtain information about the currently
 * logged on user.
 *
 * There is a class named SecurityUtils in katari-core. This is a similar
 * implementation, but it is aware of the User entity. The katari-core class
 * knows about CoreUser only.
 *
 * This module needs an instance of User so it can call isAdministrator on it.
 */
@Deprecated
public final class SecurityUtils {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(SecurityUtils.class);

  /** A private constructor, so nobody can create instances.
   */
  private SecurityUtils() {
  }

  /** Obtains the currently logged in user.
   *
   * @return the currently logged in user. It is a detached User instance. It
   * returns null, if no user has logged in yet.
   */
  public static User getCurrentUser() {
    log.trace("Entering getCurrentUser");

    // Obtains the current user.
    Authentication authentication;
    authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      log.trace("Leaving getCurrentUser with null");
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal == null) {
      log.trace("Leaving getCurrentUser with null");
      return null;
    }

    User user = ((DomainUserDetails) principal).getUser();

    log.trace("Leaving getCurrentUser");
    return user;
  }
}

