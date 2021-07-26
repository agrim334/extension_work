package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface ExecuteStreamHandler
{
  public abstract void setProcessInputStream(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract void setProcessErrorStream(InputStream paramInputStream)
    throws IOException;
  
  public abstract void setProcessOutputStream(InputStream paramInputStream)
    throws IOException;
  
  public abstract void start()
    throws IOException;
  
  public abstract void stop();
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ExecuteStreamHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */