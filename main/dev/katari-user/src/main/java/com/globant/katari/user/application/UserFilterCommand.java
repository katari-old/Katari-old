package com.globant.katari.user.application;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.globant.katari.core.application.Command;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserFilter;
import com.globant.katari.user.domain.UserRepository;
import com.globant.katari.user.domain.filter.ContainsFilter;
import com.globant.katari.user.domain.filter.Paging;
import com.globant.katari.user.domain.filter.Sorting;

/** Command to obtain a list of users according to filtering and
 * pagination information.
 *
 * @author nicolas.frontini
 */
public class UserFilterCommand implements Command<List<User>> {

  /** The paging component.
   */
  private Paging paging = new Paging();

  /** The sorting component.
   */
  private Sorting sorting = new Sorting();

  /** The contains filter component.
   */
  private ContainsFilter containsFilter = new ContainsFilter();

  /** The user repository.
   */
  private UserRepository userRepository;

  /**
   * Default constructor so we can create proxies for this class.
   */
  protected UserFilterCommand() {
    // This allows proxying.
  }

  /** The constructor.
   *
   * @param theUserRepository The user repository. It cannot be null.
   */
  public UserFilterCommand(final UserRepository theUserRepository) {
    Validate.notNull(theUserRepository, "The user repository cannot be null");
    userRepository = theUserRepository;
  }

  /** Get the paging component.
   *
   * @return Returns the paging component.
   */
  public Paging getPaging() {
    return paging;
  }

  /** Sets the paging component.
   *
   * @param thePaging The paging component. It cannot be null.
   */
  public void setPaging(final Paging thePaging) {
    Validate.notNull(thePaging, "The paging component cannot be null");
    paging = thePaging;
  }

  /** Get the sorting component.
   *
   * @return Returns the sorting component.
   */
  public Sorting getSorting() {
    return sorting;
  }

  /** Sets the sorting component.
   *
   * @param theSorting The sorting component. It cannot be null.
   */
  public void setSorting(final Sorting theSorting) {
    Validate.notNull(theSorting, "The sorting component cannot be null");
    sorting = theSorting;
  }

  /** Get the contains filter component.
   *
   * @return Returns the contains filter component.
   */
  public ContainsFilter getContainsFilter() {
    return containsFilter;
  }

  /** Sets the contains filter component.
   *
   * @param theContainsFilter The contains filter component. It cannot be null.
   */
  public void setContainsFilter(final ContainsFilter theContainsFilter) {
    Validate.notNull(theContainsFilter,
        "The containsFilter component cannot be null");
    containsFilter = theContainsFilter;
  }

  /** Returns the url to obtain the user list with the current filter, sorting
   * and paging parameters.
   *
   * @return Returns the url, never null.
   */
  public String getUrl() {
    return getUrlPaging(paging.getPageNumber());
  }

  /** Returns the url with the corresponing parameters for paging.
   *
   * @param pageNumber The page number.
   *
   * @return Returns the url <code>String</code>.
   */
  public String getUrlPaging(final int pageNumber) {
    String encodedValue = "";
    try {
      encodedValue = URLEncoder.encode(getContainsFilter().getValue(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 Not supported.", e);
    }
    String url = "paging.pageNumber=" + pageNumber
        + "&amp;containsFilter.value="
        + encodedValue
        + "&amp;containsFilter.columnName="
        + getContainsFilter().getColumnName()
        + "&amp;sorting.columnName="
        + getSorting().getColumnName()
        + "&amp;sorting.ascendingOrder="
        + getSorting().isAscendingOrder();
    return url;
  }

  /** Gets the url for the next page.
   *
   * @return Returns the url for the next page.
   */
  public String getUrlNextPage() {
    return getUrlPaging(paging.getPageNumber() + 1);
  }

  /** Gets the url for the previous page.
   *
   * @return Returns the url for the previous page.
   */
  public String getUrlPrevPage() {
    return getUrlPaging(paging.getPageNumber() - 1);
  }

  /** Returns the url to toggle sort order.
   *
   * @return Returns the url <code>String</code>.
   */
  public String getUrlOrder() {
    String encodedValue = "";
    try {
      encodedValue = URLEncoder.encode(getContainsFilter().getValue(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 Not supported.", e);
    }
    String url = "sorting.ascendingOrder="
        + !getSorting().isAscendingOrder()
        + "&amp;containsFilter.value="
        + encodedValue
        + "&amp;containsFilter.columnName="
        + getContainsFilter().getColumnName();
    return url;
  }

  /** Execute the command and returns a list of users.
   *
   * @return Returns a list of users.
   */
  public List<User> execute() {
    UserFilter userFilter = new UserFilter();
    userFilter.setContainsFilter(getContainsFilter());
    userFilter.setPaging(getPaging());
    userFilter.setSorting(getSorting());
    return userRepository.getUsers(userFilter);
  }
}
