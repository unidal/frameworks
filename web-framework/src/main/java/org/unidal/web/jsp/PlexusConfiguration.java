package org.unidal.web.jsp;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A configuration data hierarchy for configuring aspects of plexus. For
 * example, to populate a ComponentDescriptor. Implementation of
 * PlexusConfiguration may be populated by any means, for example, by XML file.
 */
public interface PlexusConfiguration
{
    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    /**
     * Returns the name of this configuration.
     * @return the name of this configuration
     */
    String getName();
    
    /**
     * Sets the name of this configuration.
     * @param name configuration name
     */
    void setName(String name);

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    /**
     * Returns the value of this configuration.
     * @return the value of this configuration
     * @throws PlexusConfigurationException when error
     */
    String getValue()
        throws PlexusConfigurationException;

    /**
     * Returns the value of this configuration, or default if one cannot be
     * found.
     * @param defaultValue value to return if none is found
     * @return the value of this configuration
     */
    String getValue( String defaultValue );

    /**
     * Set the value of a configuration element.
     * @param value configuration value
     */
    void setValue( String value );

    /**
     * Set the value of a configuration element and return the PlexusConfiguration object
     * so that further operations can be carried out.
     * @param value configuration value
     * @return configuration
     */
    PlexusConfiguration setValueAndGetSelf( String value );

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------
    
    /**
     * Sets an attribute on this configuration.
     * @param name attribute name
     * @param value attribute value
     */
    void setAttribute( String name, String value );

    /**
     * Returns an array of attribute names.
     * @return an array of attribute names
     */
    String[] getAttributeNames();

    /**
     * Returns the value of the named attribute.
     * @param paramName parameter name
     * @return the value of the named attribute
     * @throws PlexusConfigurationException when error
     */
    String getAttribute( String paramName )
        throws PlexusConfigurationException;

    /**
     * Returns the value of the named attribute, or default if one cannot be
     * found.
     * @param name attribute name
     * @param defaultValue value to return if none is found
     * @return the value of the named attribute
     */
    String getAttribute( String name, String defaultValue );

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    /**
     * Returns the child configuration of the given name.
     * @param child the name of the child to return
     * @return the child configuration of the given name
     */
    PlexusConfiguration getChild( String child );

    /**
     * Returns the child configuration at the given location.
     * @param i the position of the child under this configuration
     * @return the child configuration at the given location
     */
    PlexusConfiguration getChild( int i );

    /**
     * Returns the child configuration of the given name.
     * @param child the name of the child to return
     * @param createChild true if a new child should be create, if none found
     * @return the child configuration of the given name, or new child if
     *  created
     */
    PlexusConfiguration getChild( String child, boolean createChild );

    /**
     * Returns an array of all child configurations.
     * @return an array of all child configurations
     */
    PlexusConfiguration[] getChildren();

    /**
     * Returns an array of all child configurations with the given name.
     * @param name the name of the children configurations to return
     * @return an array of all child configurations with the given name
     */
    PlexusConfiguration[] getChildren( String name );

    /**
     * Adds a configuration under this configuration, which acts as
     * a parent.
     * @param configuration the child configuration to add
     */
    void addChild( PlexusConfiguration configuration );

    /**
     * Add a child element with a given name and return the newly created element.
     * @param name child name
     * @return child configuration
     */
    PlexusConfiguration addChild( String name );

    /**
     * Add a child element with a given name, and given value and return the
     * newly created element.
     * @param name child name
     * @param value child value
     * @return child configuration
     */
    PlexusConfiguration addChild( String name, String value );

    /**
     * Returns the number of directly children under this configuration.
     * @return the number of directly children under this configuration.
     */
    int getChildCount();
}
