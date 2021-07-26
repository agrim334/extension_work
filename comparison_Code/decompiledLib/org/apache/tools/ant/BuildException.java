package org.apache.tools.ant;

public class BuildException
  extends RuntimeException
{
  private static final long serialVersionUID = -5419014565354664240L;
  private Location location = Location.UNKNOWN_LOCATION;
  
  public BuildException() {}
  
  public BuildException(String message)
  {
    super(message);
  }
  
  public BuildException(String pattern, Object... formatArguments)
  {
    super(String.format(pattern, formatArguments));
  }
  
  public BuildException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public BuildException(String message, Throwable cause, Location location)
  {
    this(message, cause);
  }
  
  public BuildException(Throwable cause)
  {
    super(cause);
  }
  
  public BuildException(String message, Location location)
  {
    super(message);
    this.location = location;
  }
  
  public BuildException(Throwable cause, Location location)
  {
    this(cause);
  }
  
  @Deprecated
  public Throwable getException()
  {
    return getCause();
  }
  
  public String toString()
  {
    return location.toString() + getMessage();
  }
  
  public void setLocation(Location location)
  {
    this.location = location;
  }
  
  public Location getLocation()
  {
    return location;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.BuildException
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */