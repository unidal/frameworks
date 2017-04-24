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
public interface LoggerManager
{
    String ROLE = LoggerManager.class.getName();

    /**
     * Sets the threshold for all new loggers. It will NOT affect the existing loggers.
     *
     * This is usually only set once while the logger manager is configured.
     * 
     * @param threshold The new threshold.
     */
    void setThreshold( int threshold );

    /**
     * Returns the current threshold for all new loggers.
     *
     * @return Returns the current threshold for all new loggers.
     */
    int getThreshold();

    /**
     * Sets the threshold for all loggers. It affects all the existing loggers
     * as well as future loggers.
     *
     * @param threshold The new threshold.
     */
    void setThresholds( int threshold );

    // The new stuff
    void setThreshold( String role, int threshold );

    void setThreshold( String role, String roleHint, int threshold );

    int getThreshold( String role );

    int getThreshold( String role, String roleHint );

    Logger getLoggerForComponent( String role );

    Logger getLoggerForComponent( String role, String roleHint );

    void returnComponentLogger( String role );

    void returnComponentLogger( String role, String hint );

    int getActiveLoggerCount();
}
