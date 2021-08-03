package org.apache.maven.it;

class LauncherException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  
  LauncherException(String message)
  {
    super(message);
  }
  
  LauncherException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.it.LauncherException
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */