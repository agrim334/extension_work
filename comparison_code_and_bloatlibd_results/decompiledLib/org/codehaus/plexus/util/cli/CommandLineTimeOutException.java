package org.codehaus.plexus.util.cli;

public class CommandLineTimeOutException
  extends CommandLineException
{
  public CommandLineTimeOutException(String message)
  {
    super(message);
  }
  
  public CommandLineTimeOutException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.CommandLineTimeOutException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */