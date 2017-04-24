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
 * This represents a project which this component depends upon to function
 * properly, for example, a required jar file. See Apache Maven for an
 * example of a dependency in action.
 * 
 * @author Jason van Zyl
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentDependency
{
    private static final String DEAULT_DEPENDENCY_TYPE = "jar";
    
    private String groupId;

    private String artifactId;

    private String type = DEAULT_DEPENDENCY_TYPE;

    private String version;

    /**
     * Gets a key for an artifact, which is an alias for a specific
     * project timeline in a group.
     * @return a key for an artifact
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /**
     * Sets the dependency's artifact ID.
     * @param artifactId the artifact ID
     */
    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    /**
     * Gets a key for a group, which represents a set of artifacts timelines.
     * @return a key for a group
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * Sets the dependency's group ID.
     * @param groupId the group ID
     */
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    /**
     * Gets the type of dependency, for example a "jar".
     * @return the type of dependency
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the dependency project's type.
     * @param type the dependency's type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns a specific point in a project's timeline.
     * i.e. version 1, or 2.1.4
     * @return a specific point in a project's timeline
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the point in a project's development timeline
     * @param version the project's version
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "groupId = " ).append( groupId ).
            append( ", artifactId = " ).append( artifactId ).
            append( ", version = " ).append( version ).
            append( ", type = " ).append( type );

        return sb.toString();
    }
}
