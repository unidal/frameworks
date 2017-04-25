package org.codehaus.plexus.context;

import java.util.Map;

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
 * Context is a Map of arbitrary data associated with the container.
 */
public interface Context
{
    /**
     * Returns true if this context contains a value for the specified key.
     *
     * @param key the key to search
     * @return true if the key was found; false otherwise
     */
    boolean contains( Object key );

    /**
     * Returns the value of the key. If the key can't be found it will throw a exception.
     *
     * @param key the key of the value to look up.
     * @return returns the value associated with the key
     * @throws ContextException if the key doesn't exist
     */
    Object get( Object key )
        throws ContextException;

    /**
     * Utility method to retrieve containerContext data.
     * The returned Map is an unmodifiable view.
     * @return the containerContext data
     * @since 1.0-alpha-18
     */
    Map<Object, Object> getContextData();

    /**
     * Adds the item to the containerContext.
     *
     * @param key the key of the item
     * @param value the item
     * @throws IllegalStateException if this context is read-only
     */
    public void put( Object key, Object value )
        throws IllegalStateException;

    // todo [dain] this isn't needed anymore since containers are no longer nestable
    /**
     * Hides the item in the containerContext.
     * After remove(key) has been called, a get(key)
     * will always fail, even if the parent containerContext
     * has such a mapping.
     *
     * @param key the items key
     * @throws IllegalStateException if this context is read-only
     */
    void hide( Object key )
        throws IllegalStateException;

    /**
     * Make the containerContext read-only.
     * Any attempt to write to the containerContext via put()
     * will result in an IllegalStateException.
     */
    void makeReadOnly();
}
