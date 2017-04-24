package org.codehaus.plexus.context;


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
 * Exception signalling a badly formed Context.
 *
 * This can be thrown by Context object when a entry is not
 * found. It can also be thrown manually in contextualize()
 * when Component detects a malformed containerContext value.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision$ $Date$
 */
public class ContextException
    extends Exception
{
    private static final long serialVersionUID = 2030206863811644180L;

    /**
     * Construct a new <code>ContextException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public ContextException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>ContextException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public ContextException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
