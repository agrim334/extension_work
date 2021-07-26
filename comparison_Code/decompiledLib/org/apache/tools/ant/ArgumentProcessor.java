package org.apache.tools.ant;

import java.io.PrintStream;
import java.util.List;

public abstract interface ArgumentProcessor
{
  public abstract int readArguments(String[] paramArrayOfString, int paramInt);
  
  public abstract boolean handleArg(List<String> paramList);
  
  public abstract void prepareConfigure(Project paramProject, List<String> paramList);
  
  public abstract boolean handleArg(Project paramProject, List<String> paramList);
  
  public abstract void printUsage(PrintStream paramPrintStream);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.ArgumentProcessor
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */