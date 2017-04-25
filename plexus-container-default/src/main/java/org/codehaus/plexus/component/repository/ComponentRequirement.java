package org.codehaus.plexus.component.repository;

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
 * This represents a component this is required by another component.
 *
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class ComponentRequirement
{
    private String role;

    private String roleHint = "";

    private String fieldName;

    private String fieldMappingType;

    private boolean optional;

    /**
     * Returns the field name that this component requirement will inject.
     * @return the field name that this component requirement will inject
     */
    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * Sets the name of the field that will be populated by the required
     * component.
     * @param fieldName the name of the field to be populated
     */
    public void setFieldName( String fieldName )
    {
        this.fieldName = fieldName;
    }

    /**
     * Returns the role of the required component.
     * @return the role of the required component
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Sets the role of the require component.
     * @param role the required component's role
     */
    public void setRole( String role )
    {
        this.role = role;
    }

    /**
     * Returns the role-hint of the required component.
     * @return the role-hint of the required component
     */
    public String getRoleHint()
    {
        return roleHint;
    }

    /**
     * Sets the role-hint of the require component.
     * Passing null or an empty string will match any available implementation.
     * @param roleHint the required component's role-hint
     */
    public void setRoleHint( String roleHint )
    {
        this.roleHint = ( roleHint != null ) ? roleHint : "";
    }

    /**
     * Returns the type of the field this component requirement will inject.
     * @return the type of the field this component requirement will inject
     */
    public String getFieldMappingType()
    {
        return fieldMappingType;
    }

    /**
     * Sets the type of the field that will be populated by the required
     * component.
     * @param fieldType the type of the field to be populated
     */
    public void setFieldMappingType( String fieldType )
    {
        this.fieldMappingType = fieldType;
    }

    /**
     * Whether this component requirement is optional and needs not be satisfied
     *
     * @return {@code true} if the requested component may be missing, {@code false} if the component is mandatory.
     * @since 1.3.0
     */
    public boolean isOptional()
    {
        return optional;
    }

    /**
     * Controls whether a failure to satisfy this requirement can be tolerated by host component or whether construction
     * of the host component should also fail.
     *
     * @param optional {@code true} if the requested component may be missing, {@code false} if the component is
     *            mandatory.
     * @since 1.3.0
     */
    public void setOptional( boolean optional )
    {
        this.optional = optional;
    }

    public String toString()
    {
        return "ComponentRequirement{" +
            "role='" + getRole() + "'" + ", " +
            "roleHint='" + getRoleHint() + "', " +
            "fieldName='" + getFieldName() + "'" +
            "}";
    }

    /**
     * Returns a human-friendly key, suitable for display.
     * @return a human-friendly key
     */
    public String getHumanReadableKey()
    {
        StringBuilder key = new StringBuilder();

        key.append( "role: '").append( getRole() ).append( "'" );

        if ( getRoleHint() != null )
        {
            key.append( ", role-hint: '" ).append( getRoleHint() ).append( "'. " );
        }

        if ( getFieldName() != null )
        {
            key.append( ", field name: '" ).append( getFieldName() ).append( "' " );
        }

        return key.toString();
    }

    public boolean equals( Object other )
    {
        if ( other instanceof ComponentRequirement )
        {
            String myId = role + ":" + roleHint;

            ComponentRequirement req = (ComponentRequirement) other;
            String otherId = req.role + ":" + req.roleHint;

            return myId.equals( otherId );
        }

        return false;
    }

    public int hashCode()
    {
        return ( role + ":" + roleHint ).hashCode();
    }
}
