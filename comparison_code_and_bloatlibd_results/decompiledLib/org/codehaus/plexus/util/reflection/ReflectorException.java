package org.codehaus.plexus.util.reflection;

public class ReflectorException
  extends Exception
{
  public ReflectorException() {}
  
  public ReflectorException(String msg)
  {
    super(msg);
  }
  
  public ReflectorException(Throwable root)
  {
    super(root);
  }
  
  public ReflectorException(String msg, Throwable root)
  {
    super(msg, root);
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.reflection.ReflectorException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */