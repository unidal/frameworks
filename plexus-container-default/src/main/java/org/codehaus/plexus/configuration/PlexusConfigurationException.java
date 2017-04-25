package org.codehaus.plexus.configuration;

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
 * Exception that is thrown when an expected configuration value encounters
 * problems loading correctly.
 */
public class PlexusConfigurationException
    extends Exception
{
    private static final long serialVersionUID = 7559886640184983689L;

    /**
     * Construct a new <code>PlexusConfigurationException</code> instance.
     * @param message exception message
     */
    public PlexusConfigurationException( String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>PlexusConfigurationException</code> instance.
     * @param message exception message
     * @param throwable causing exception to chain
     */
    public PlexusConfigurationException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
