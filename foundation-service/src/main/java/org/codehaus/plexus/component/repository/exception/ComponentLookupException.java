package org.codehaus.plexus.component.repository.exception;

import org.codehaus.plexus.classworlds.realm.ClassRealm;

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
 * The exception which is thrown by a component repository when
 * the requested component cannot be found.
 *
 * @author Jason van Zyl
 * @version $Id$
 */
public class ComponentLookupException
    extends Exception
{
   private static final long serialVersionUID = 1L;

   private String LS = System.getProperty( "line.separator" );

    private String role;

    private String roleHint;

    private ClassRealm realm;

    public ComponentLookupException( String message, String role, String roleHint )
    {
        super( message );

        this.role = role;

        this.roleHint = roleHint;
    }

    public ComponentLookupException( String message, String role, String roleHint, Throwable cause )
    {
        super( message, cause );

        this.role = role;

        this.roleHint = roleHint;
    }

    public ComponentLookupException( String message, String role, String roleHint, ClassRealm realm )
    {
        super( message );

        this.role = role;

        this.roleHint = roleHint;

        this.realm = realm;
    }

    public ComponentLookupException( String message, String role, String roleHint, ClassRealm realm, Throwable cause )
    {
        super( message, cause );

        this.role = role;

        this.roleHint = roleHint;

        this.realm = realm;
    }

    public String getMessage()
    {
        StringBuilder sb = new StringBuilder()
            .append( super.getMessage() ).append( LS )
            .append( "      role: ").append( role ).append( LS )
            .append( "  roleHint: ").append( roleHint ).append( LS )
            .append("classRealm: ");

        if ( realm == null )
        {
            sb.append( "none specified" );
        } else {
           sb.append(realm);
        }

        return sb.toString();
    }
}
