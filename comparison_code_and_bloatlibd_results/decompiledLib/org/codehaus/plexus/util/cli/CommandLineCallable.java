package org.codehaus.plexus.util.cli;

import java.util.concurrent.Callable;

public abstract interface CommandLineCallable
  extends Callable<Integer>
{
  public abstract Integer call()
    throws CommandLineException;
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.CommandLineCallable
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */