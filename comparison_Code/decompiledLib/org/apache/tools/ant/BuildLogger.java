package org.apache.tools.ant;

import java.io.PrintStream;

public abstract interface BuildLogger
  extends BuildListener
{
  public abstract void setMessageOutputLevel(int paramInt);
  
  public abstract void setOutputPrintStream(PrintStream paramPrintStream);
  
  public abstract void setEmacsMode(boolean paramBoolean);
  
  public abstract void setErrorPrintStream(PrintStream paramPrintStream);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.BuildLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */