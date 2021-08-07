package org.codehaus.plexus.util.cli.shell;

public class CommandShell
  extends Shell
{
  public CommandShell()
  {
    setShellCommand("command.com");
    setShellArgs(new String[] { "/C" });
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.shell.CommandShell
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */