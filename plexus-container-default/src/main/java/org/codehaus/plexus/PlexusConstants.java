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

public abstract class PlexusConstants
{
    /** Key used to retrieve the plexus container from the containerContext. */
    public static final String PLEXUS_KEY = "plexus";

    /** The role-hint to use for components or lookups that do not specify a role.*/
    public static final String PLEXUS_DEFAULT_HINT = "default";
}
