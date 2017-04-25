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

import org.codehaus.plexus.component.repository.ComponentSetDescriptor;

/**
 * Signals to a ComponentDiscoveryListener that an event has taken place
 * involving a set of components. It is up to the Listener to decide what
 * to do with that information.
 */
public class ComponentDiscoveryEvent
{
    private ComponentSetDescriptor componentSetDescriptor;

    private Object data;

    /**
     * Constructs a <code>ComponentDiscoveryEvent</code> with a set of
     * ComponentDescriptors.
     * @param componentSetDescriptor a set of ComponentDescriptors
     */
    public ComponentDiscoveryEvent( ComponentSetDescriptor componentSetDescriptor )
    {
        this.componentSetDescriptor = componentSetDescriptor;
    }

    public ComponentDiscoveryEvent( ComponentSetDescriptor componentSetDescriptor, Object data )
    {
        this.componentSetDescriptor = componentSetDescriptor;
        this.data = data;
    }

    /**
     * Returns this event's set of ComponentDescriptors.
     * @return this event's set of ComponentDescriptors
     */
    public ComponentSetDescriptor getComponentSetDescriptor()
    {
        return componentSetDescriptor;
    }

    public Object getData()
    {
        return data;
    }
}
