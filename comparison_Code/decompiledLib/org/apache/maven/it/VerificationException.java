package org.apache.maven.it;

public class VerificationException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  
  public VerificationException() {}
  
  public VerificationException(String message)
  {
    super(message);
  }
  
  public VerificationException(Throwable cause)
  {
    super(cause);
  }
  
  public VerificationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.it.VerificationException
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */