package org.unidal.web.jsp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface PlexusConfigurationWriter
{
    void write( Writer writer, PlexusConfiguration configuration )
        throws IOException;

    void write( OutputStream outputStream, PlexusConfiguration configuration )
        throws IOException;
}
