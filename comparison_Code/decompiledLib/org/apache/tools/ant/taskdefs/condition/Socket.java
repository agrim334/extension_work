package org.apache.tools.ant.taskdefs.condition;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

public class Socket
  extends ProjectComponent
  implements Condition
{
  private String server = null;
  private int port = 0;
  
  public void setServer(String server)
  {
    this.server = server;
  }
  
  public void setPort(int port)
  {
    this.port = port;
  }
  
  public boolean eval()
    throws BuildException
  {
    if (server == null) {
      throw new BuildException("No server specified in socket condition");
    }
    if (port == 0) {
      throw new BuildException("No port specified in socket condition");
    }
    log("Checking for listener at " + server + ":" + port, 3);
    try
    {
      java.net.Socket s = new java.net.Socket(server, port);
      try
      {
        boolean bool = true;
        s.close();return bool;
      }
      catch (Throwable localThrowable2)
      {
        try
        {
          s.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable2.addSuppressed(localThrowable1);
        }
        throw localThrowable2;
      }
      return false;
    }
    catch (IOException e) {}
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.Socket
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */