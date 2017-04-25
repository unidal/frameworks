package org.codehaus.plexus.component.discovery;

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
 * A listener which responds in some way to component discovery by a PlexusContainer.
 */
public interface ComponentDiscoveryListener
{
    /**
     * Signals to this listener that a component has been discovered.
     * @param event the event that signals what components have been discovered
     */
    void componentDiscovered( ComponentDiscoveryEvent event );
}
