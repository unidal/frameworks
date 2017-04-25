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
public interface Logger
{
    /** Typecode for debugging messages. */
    int LEVEL_DEBUG = 0;

    /** Typecode for informational messages. */
    int LEVEL_INFO = 1;

    /** Typecode for warning messages. */
    int LEVEL_WARN = 2;

    /** Typecode for error messages. */
    int LEVEL_ERROR = 3;

    /** Typecode for fatal error messages. */
    int LEVEL_FATAL = 4;

    /** Typecode for disabled log levels. */
    int LEVEL_DISABLED = 5;

    void debug( String message );

    void debug( String message, Throwable throwable );

    boolean isDebugEnabled();

    void info( String message );

    void info( String message, Throwable throwable );

    boolean isInfoEnabled();

    void warn( String message );

    void warn( String message, Throwable throwable );

    boolean isWarnEnabled();

    void error( String message );

    void error( String message, Throwable throwable );

    boolean isErrorEnabled();

    void fatalError( String message );

    void fatalError( String message, Throwable throwable );

    boolean isFatalErrorEnabled();

    Logger getChildLogger( String name );

    int getThreshold();

    void setThreshold( int threshold );

    String getName();
}
