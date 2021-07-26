package org.apache.tools.ant.taskdefs;

import java.io.PrintWriter;
import java.util.Hashtable;
import org.apache.tools.ant.Project;

public abstract interface AntStructure$StructurePrinter
{
  public abstract void printHead(PrintWriter paramPrintWriter, Project paramProject, Hashtable<String, Class<?>> paramHashtable1, Hashtable<String, Class<?>> paramHashtable2);
  
  public abstract void printTargetDecl(PrintWriter paramPrintWriter);
  
  public abstract void printElementDecl(PrintWriter paramPrintWriter, Project paramProject, String paramString, Class<?> paramClass);
  
  public abstract void printTail(PrintWriter paramPrintWriter);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.AntStructure.StructurePrinter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */