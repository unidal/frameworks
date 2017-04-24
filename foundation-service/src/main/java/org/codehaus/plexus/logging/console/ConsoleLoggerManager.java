package org.codehaus.plexus.logging.console;

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

import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This is a simple logger manager that will only write the logging statements to the console.
 *
 * Sample configuration:
 * <pre>
 * <logging>
 *   <implementation>org.codehaus.plexus.logging.ConsoleLoggerManager</implementation>
 *   <logger>
 *     <threshold>DEBUG</threshold>
 *   </logger>
 * </logging>
 * </pre>
 * 
 * @author Jason van Zyl
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class ConsoleLoggerManager
    extends AbstractLoggerManager
    implements LoggerManager, Initializable
{
    /**
     * Message of this level or higher will be logged. 
     * 
     * This field is set by the plexus container thus the name is 'threshold'. The field
     * currentThreshold contains the current setting of the threshold.
     */
    private String threshold = "info";

    private int currentThreshold;

   private Map loggers;

    /** The number of active loggers in use. */
    private int loggerCount;

   private boolean bootTimeLogger = false;

    public ConsoleLoggerManager()
    {
    }

    /**
     * This special constructor is called directly when the container is bootstrapping itself.
     */
    public ConsoleLoggerManager( String threshold )
    {
        this.threshold = threshold;

        bootTimeLogger = true;

        initialize();
    }

    public void initialize()
    {
        debug( "Initializing ConsoleLoggerManager: " + this.hashCode() + "." );
//        if ( !bootTimeLogger )
//            new Throwable().printStackTrace(System.err);
        currentThreshold = parseThreshold( threshold );

        if ( currentThreshold == -1 )
        {
            debug( "Could not parse the threshold level: '" + threshold + "', setting to debug." );
            currentThreshold = Logger.LEVEL_DEBUG;
        }

        loggers = new HashMap();
    }

    public void setThreshold( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;
    }

    public void setThresholds( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;

        for (Object o : loggers.values()) {
            Logger logger = (Logger) o;
            logger.setThreshold(currentThreshold);
        }
    }

    /**
     * @return Returns the threshold.
     */
    public int getThreshold()
    {
        return currentThreshold;
    }

    // new stuff

    public void setThreshold( String role, String roleHint, int threshold ) {
        ConsoleLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (ConsoleLogger)loggers.get( name );

        if(logger == null) {
            debug( "Trying to set the threshold of a unknown logger '" + name + "'." );
            return; // nothing to do
        }

        logger.setThreshold( threshold );
    }

    public int getThreshold( String role, String roleHint ) {
        ConsoleLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (ConsoleLogger)loggers.get( name );

        if(logger == null) {
            debug( "Trying to get the threshold of a unknown logger '" + name + "'." );
            return Logger.LEVEL_DEBUG; // does not return null because that could create a NPE
        }

        return logger.getThreshold();
    }

    public Logger createLogger(int threshold, String name)
    {
        return new ConsoleLogger( threshold, name );
    }

    public Logger getLoggerForComponent( String role, String roleHint )
    {
        Logger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (Logger)loggers.get( name );

        if ( logger != null )
            return logger;

        debug( "Creating logger '" + name + "' " + this.hashCode() + "." );
        logger = createLogger( getThreshold(), name );
        loggers.put( name, logger );

        return logger;
    }

    public void returnComponentLogger( String role, String roleHint )
    {
        Object obj;
        String name;

        name = toMapKey( role, roleHint );
        obj = loggers.remove( name );

        if ( obj == null )
        {
            debug( "There was no such logger '" + name + "' " + this.hashCode() + ".");
        }
        else
        {
            debug( "Removed logger '" + name + "' " + this.hashCode() + ".");
        }
    }

    public int getActiveLoggerCount()
    {
        return loggers.size();
    }

    private int parseThreshold( String text )
    {
        text = text.trim().toLowerCase( Locale.ENGLISH );

        if ( text.equals( "debug" ) )
        {
            return ConsoleLogger.LEVEL_DEBUG;
        }
        else if ( text.equals( "info" ) )
        {
            return ConsoleLogger.LEVEL_INFO;
        }
        else if ( text.equals( "warn" ) )
        {
            return ConsoleLogger.LEVEL_WARN;
        }
        else if ( text.equals( "error" ) )
        {
            return ConsoleLogger.LEVEL_ERROR;
        }
        else if ( text.equals( "fatal" ) )
        {
            return ConsoleLogger.LEVEL_FATAL;
        }

        return -1;
    }

    private String decodeLogLevel( int logLevel )
    {
        switch(logLevel) {
        case ConsoleLogger.LEVEL_DEBUG: return "debug";
        case ConsoleLogger.LEVEL_INFO: return "info";
        case ConsoleLogger.LEVEL_WARN: return "warn";
        case ConsoleLogger.LEVEL_ERROR: return "error";
        case ConsoleLogger.LEVEL_FATAL: return "fatal";
        case ConsoleLogger.LEVEL_DISABLED: return "disabled";
        default: return "unknown";
        }
    }

    /**
     * Remove this method and all references when this code is verified.
     *
     * @param msg
     */
    private void debug( String msg )
    {
//        if ( !bootTimeLogger )
//            System.out.println( "[Console] " + msg );
    }
}
