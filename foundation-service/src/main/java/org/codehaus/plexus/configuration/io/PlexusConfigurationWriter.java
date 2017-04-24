package org.codehaus.plexus.configuration.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.codehaus.plexus.configuration.PlexusConfiguration;

public interface PlexusConfigurationWriter
{
    void write( Writer writer, PlexusConfiguration configuration )
        throws IOException;

    void write( OutputStream outputStream, PlexusConfiguration configuration )
        throws IOException;
}
