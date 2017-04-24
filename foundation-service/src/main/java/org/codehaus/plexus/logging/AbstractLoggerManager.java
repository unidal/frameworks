package org.codehaus.plexus.logging;

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
 * @author Jason van Zyl
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLoggerManager
    implements LoggerManager
{
    /** */
    public AbstractLoggerManager()
    {        
    }

    public void setThreshold( String role, int threshold )
    {
        setThreshold( role, null, threshold );
    }

    public int getThreshold( String role )
    {
        return getThreshold( role, null );
    }

    public Logger getLoggerForComponent( String role )
    {
        return getLoggerForComponent( role, null );
    }

    public void returnComponentLogger( String role )
    {
        returnComponentLogger( role, null );
    }

    /**
     * Creates a string key useful as keys in <code>Map</code>'s.
     * 
     * @param role The component role.
     * @param roleHint The component role hint.
     * @return Returns a string thats useful as a key for components.
     */
    protected String toMapKey( String role, String roleHint )
    {
         if ( roleHint == null )
         {
             return role;
         }
         else
         {
             return role + ":" + roleHint;
         }
    }
}
