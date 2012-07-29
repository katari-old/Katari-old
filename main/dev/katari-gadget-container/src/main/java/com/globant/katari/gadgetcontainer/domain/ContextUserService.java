/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.Authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.hibernate.coreuser.domain.CoreUserDetails;

/** This implementation should work together with the new katari User
 * implementation. Basically, should extract the user id assosiated with
 * the current thread.
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 */
public class ContextUserService {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(ContextUserService.class);

  /** Returns the currently logged in user.
   *
   * @return The currently logged in user, null if the user is anonymous.
   */
  public CoreUser getCurrentUser() {
    log.trace("Entering getCurrentUser");

    Authentication authentication = (Authentication)
      SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      log.trace("Leaving getCurrentUser with null");
      return null;
    }
    CoreUserDetails details = (CoreUserDetails) authentication.getPrincipal();
    if (details == null) {
      log.trace("Leaving getCurrentUser with null");
      return null;
    }
    CoreUser user = details.getCoreUser();
    log.trace("Leaving getCurrentUser");

    return user;
  }

  /** Retrieves from the context the user.
   *
   * @return {@link String} the userId, 0 if the user was anonymous.
   */
  public long getCurrentUserId() {
    log.trace("Entering getCurrentUserId");

    CoreUser user = getCurrentUser();
    if (user == null) {
      log.trace("Leaving getCurrentUserId with 0");
      return 0;
    }
    log.trace("Leaving getCurrentUserId");
    return user.getId();
  }
}

