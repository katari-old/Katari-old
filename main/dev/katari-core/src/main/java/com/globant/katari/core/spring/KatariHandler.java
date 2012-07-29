/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/** This is the handler that Spring uses to create a bean from a schema
 * definition.
 *
 * It parses the menuBar element. delegating it to the
 * MenuBarBeanDefinitionParser.
 *
 * @author nicolas.santini
 */
public class KatariHandler extends NamespaceHandlerSupport {

  /** Registers the parser for each kind of element defined in the schema.
   */
  public final void init() {
    PropertiesParser propertiesParser = new PropertiesParser();
    ConditionalImportParser importParser;
    importParser = new ConditionalImportParser(propertiesParser);
    NullPropertyParser nullParser;
    nullParser = new NullPropertyParser();
    registerBeanDefinitionParser("menuBar", new MenuBarBeanDefinitionParser());
    registerBeanDefinitionParser("import", importParser);
    registerBeanDefinitionParser("properties", propertiesParser);
    registerBeanDefinitionParser("null", nullParser);
  }
}

