package org.apache.tools.ant;

public class ExitStatusException
  extends BuildException
{
  private static final long serialVersionUID = 7760846806886585968L;
  private int status;
  
  public ExitStatusException(int status)
  {
    this.status = status;
  }
  
  public ExitStatusException(String msg, int status)
  {
    super(msg);
    this.status = status;
  }
  
  public ExitStatusException(String message, int status, Location location)
  {
    super(message, location);
    this.status = status;
  }
  
  public int getStatus()
  {
    return status;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.ExitStatusException
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */