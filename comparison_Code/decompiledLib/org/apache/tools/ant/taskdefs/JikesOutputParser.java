package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.tools.ant.Task;

@Deprecated
public class JikesOutputParser
  implements ExecuteStreamHandler
{
  protected Task task;
  protected boolean errorFlag = false;
  protected int errors;
  protected int warnings;
  protected boolean error = false;
  protected boolean emacsMode;
  protected BufferedReader br;
  
  public void setProcessInputStream(OutputStream os) {}
  
  public void setProcessErrorStream(InputStream is) {}
  
  public void setProcessOutputStream(InputStream is)
    throws IOException
  {
    br = new BufferedReader(new InputStreamReader(is));
  }
  
  public void start()
    throws IOException
  {
    parseOutput(br);
  }
  
  public void stop() {}
  
  protected JikesOutputParser(Task task, boolean emacsMode)
  {
    System.err.println("As of Ant 1.2 released in October 2000, the JikesOutputParser class");
    
    System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
    
    System.err.println("Don't use it!");
    
    this.task = task;
    this.emacsMode = emacsMode;
  }
  
  protected void parseOutput(BufferedReader reader)
    throws IOException
  {
    if (emacsMode) {
      parseEmacsOutput(reader);
    } else {
      parseStandardOutput(reader);
    }
  }
  
  private void parseStandardOutput(BufferedReader reader)
    throws IOException
  {
    String line;
    while ((line = reader.readLine()) != null)
    {
      String lower = line.toLowerCase();
      if (!line.trim().isEmpty())
      {
        if (lower.contains("error")) {
          setError(true);
        } else if (lower.contains("warning")) {
          setError(false);
        } else if (emacsMode) {
          setError(true);
        }
        log(line);
      }
    }
  }
  
  private void parseEmacsOutput(BufferedReader reader)
    throws IOException
  {
    parseStandardOutput(reader);
  }
  
  private void setError(boolean err)
  {
    error = err;
    if (error) {
      errorFlag = true;
    }
  }
  
  private void log(String line)
  {
    if (!emacsMode) {
      task.log("", error ? 0 : 1);
    }
    task.log(line, error ? 0 : 1);
  }
  
  protected boolean getErrorFlag()
  {
    return errorFlag;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.JikesOutputParser
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */