package org.codehaus.plexus.component.composition;

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
 * Thrown when component composition goes awry.
 * 
 * @author Jason van Zyl 
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class CycleDetectedInComponentGraphException
    extends Exception
{
    private static final long serialVersionUID = -5587124702588800322L;

    /**
     * Construct a new <code>CompositionException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public CycleDetectedInComponentGraphException( String message )
    {
        super( message );
    }

    /**
     * Construct a new <code>CompositionException</code> instance.
     *
     * @param message   The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public CycleDetectedInComponentGraphException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
