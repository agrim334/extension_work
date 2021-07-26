package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.tools.ant.Task;

@Deprecated
public class TaskOutputStream
  extends OutputStream
{
  private Task task;
  private StringBuffer line;
  private int msgOutputLevel;
  
  TaskOutputStream(Task task, int msgOutputLevel)
  {
    System.err.println("As of Ant 1.2 released in October 2000, the TaskOutputStream class");
    
    System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
    
    System.err.println("Don't use it!");
    
    this.task = task;
    this.msgOutputLevel = msgOutputLevel;
    
    line = new StringBuffer();
  }
  
  public void write(int c)
    throws IOException
  {
    char cc = (char)c;
    if ((cc == '\r') || (cc == '\n'))
    {
      if (line.length() > 0) {
        processLine();
      }
    }
    else {
      line.append(cc);
    }
  }
  
  private void processLine()
  {
    String s = line.toString();
    task.log(s, msgOutputLevel);
    line = new StringBuffer();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.TaskOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */