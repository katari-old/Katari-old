/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.tools.DatabaseTestSupport;

/** Utility class to drop objects from a development data base.
 *
 * This class contains methods to drop objects from the database. The
 * implementation is tied to MySql.
 *
 * @author nicolas.frontini
 */
public class MySqlDropAllObjects extends DatabaseTestSupport {

  /** A logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      MySqlDropAllObjects.class);

  /** string to see al the tables.
   */
  private static String listTables = "show tables";

  /** {@inheritDoc}
   */
  @Override
  protected void doDropAll(final Connection connection, final String
      markerTable) throws Exception {
    dropTables(connection, markerTable);
  }

  /** Drops all tables from the database.
   *
   * @param connection The connection to execute sql sentences. It cannot be
   * null.
   *
   * @param markerTable The marker table name. Is the marker of the db of
   * permision.
   *
   * @throws Exception in case of error.
   */
  protected void dropTables(final Connection connection,
      final String markerTable) throws Exception {
    Validate.notNull(connection, "The connection cannot be null.");

    // Drops all tables.
    log.debug("Dropping all tables");
    Statement st = connection.createStatement();
    ResultSet rs = null;
    rs = st.executeQuery(listTables);
    List<String> tableNames = new ArrayList<String>();
    while (rs.next()) {
      tableNames.add(rs.getString(1));
    }
    rs.close();
    st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
    for (String table : tableNames) {
      if (!table.equalsIgnoreCase(markerTable)) {
        log.debug("Dropping table " + table);
        st.executeUpdate("DROP TABLE " + table + " cascade");
      }
    }
  }

  /** {@inheritDoc}
   */
  @Override
  protected void doDeleteAll(final Connection connection, final String
      markerTable) throws Exception {
    Validate.notNull(connection, "The connection cannot be null.");

    // Drops all tables.
    log.debug("Deleting all rows");
    Statement st = connection.createStatement();
    ResultSet rs = null;
    rs = st.executeQuery(listTables);
    List<String> tableNames = new ArrayList<String>();
    while (rs.next()) {
      tableNames.add(rs.getString(1));
    }
    rs.close();
    st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
    for (String table : tableNames) {
      if (!table.equalsIgnoreCase(markerTable)) {
        log.debug("Deleting table " + table);
        st.executeUpdate("delete from " + table);
      }
    }
  }

  /** Template method to initialize the auto increment columns to a predefined
   * value.
   *
   * @param connection The database connectino to use to drop all the objects.
   * It cannot be null.
   *
   * @param initialValue the initial value to use for autoincrement.
   *
   * @throws Exception in case of error.
   */
  protected void doInitializeAutoincrement(final Connection connection,
      final int initialValue) throws Exception {
    Validate.notNull(connection, "The connection cannot be null.");

    log.trace("Entering initializeAutoincrement");
    Statement st = connection.createStatement();
    ResultSet rs = null;
    rs = st.executeQuery(listTables);
    List<String> tableNames = new ArrayList<String>();
    while (rs.next()) {
      tableNames.add(rs.getString(1));
    }
    rs.close();
    for (String table : tableNames) {
      log.debug("Initializing autoincrement for table " + table);
      st.executeUpdate("ALTER TABLE " + table + "  AUTO_INCREMENT = "
            + initialValue);
    }
    log.trace("Leaving initializeAutoincrement");
  }
}

