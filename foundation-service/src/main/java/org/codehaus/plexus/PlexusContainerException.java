package org.codehaus.plexus;

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
 * Container execution exception.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class PlexusContainerException extends Exception
{
    private static final long serialVersionUID = 2213861902264275451L;

    /**
     * Construct a new <code>PlexusContainerException</code> instance.
     * @param message exception message
     * @param throwable causing exception to chain
     */
    public PlexusContainerException( String message, Throwable throwable )
    {
        super( message, throwable );
    }

    /**
     * Construct a new <code>PlexusContainerException</code> instance.
     * @param message exception message
     */
    public PlexusContainerException( String message )
    {
        super( message );
    }
}
