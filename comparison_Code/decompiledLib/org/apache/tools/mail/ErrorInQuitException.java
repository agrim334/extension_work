package org.apache.tools.mail;

import java.io.IOException;

public class ErrorInQuitException
  extends IOException
{
  private static final long serialVersionUID = 1L;
  
  public ErrorInQuitException(IOException e)
  {
    super(e.getMessage());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.mail.ErrorInQuitException
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */