package org.codehaus.plexus.util.cli;

final class CommandLineUtils$1
  extends Thread
{
  CommandLineUtils$1(Process paramProcess)
  {
    setName("CommandLineUtils process shutdown hook");
    setContextClassLoader(null);
  }
  
  public void run()
  {
    val$p.destroy();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.CommandLineUtils.1
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */