package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

public class RecorderEntry
  implements BuildLogger, SubBuildListener
{
  private String filename = null;
  private boolean record = true;
  private int loglevel = 2;
  private PrintStream out = null;
  private long targetStartTime = 0L;
  private boolean emacsMode = false;
  private Project project;
  
  protected RecorderEntry(String name)
  {
    targetStartTime = System.currentTimeMillis();
    filename = name;
  }
  
  public String getFilename()
  {
    return filename;
  }
  
  public void setRecordState(Boolean state)
  {
    if (state != null)
    {
      flush();
      record = state.booleanValue();
    }
  }
  
  public void buildStarted(BuildEvent event)
  {
    log("> BUILD STARTED", 4);
  }
  
  public void buildFinished(BuildEvent event)
  {
    log("< BUILD FINISHED", 4);
    if ((record) && (out != null))
    {
      Throwable error = event.getException();
      if (error == null)
      {
        out.println(String.format("%nBUILD SUCCESSFUL", new Object[0]));
      }
      else
      {
        out.println(String.format("%nBUILD FAILED%n", new Object[0]));
        error.printStackTrace(out);
      }
    }
    cleanup();
  }
  
  public void subBuildFinished(BuildEvent event)
  {
    if (event.getProject() == project) {
      cleanup();
    }
  }
  
  public void subBuildStarted(BuildEvent event) {}
  
  public void targetStarted(BuildEvent event)
  {
    log(">> TARGET STARTED -- " + event.getTarget(), 4);
    log(String.format("%n%s:", new Object[] { event.getTarget().getName() }), 2);
    targetStartTime = System.currentTimeMillis();
  }
  
  public void targetFinished(BuildEvent event)
  {
    log("<< TARGET FINISHED -- " + event.getTarget(), 4);
    
    String time = formatTime(System.currentTimeMillis() - targetStartTime);
    
    log(event.getTarget() + ":  duration " + time, 3);
    flush();
  }
  
  public void taskStarted(BuildEvent event)
  {
    log(">>> TASK STARTED -- " + event.getTask(), 4);
  }
  
  public void taskFinished(BuildEvent event)
  {
    log("<<< TASK FINISHED -- " + event.getTask(), 4);
    flush();
  }
  
  public void messageLogged(BuildEvent event)
  {
    log("--- MESSAGE LOGGED", 4);
    
    StringBuffer buf = new StringBuffer();
    if (event.getTask() != null)
    {
      String name = event.getTask().getTaskName();
      if (!emacsMode)
      {
        String label = "[" + name + "] ";
        int size = 12 - label.length();
        for (int i = 0; i < size; i++) {
          buf.append(" ");
        }
        buf.append(label);
      }
    }
    buf.append(event.getMessage());
    
    log(buf.toString(), event.getPriority());
  }
  
  private void log(String mesg, int level)
  {
    if ((record) && (level <= loglevel) && (out != null)) {
      out.println(mesg);
    }
  }
  
  private void flush()
  {
    if ((record) && (out != null)) {
      out.flush();
    }
  }
  
  public void setMessageOutputLevel(int level)
  {
    if ((level >= 0) && (level <= 4)) {
      loglevel = level;
    }
  }
  
  public void setOutputPrintStream(PrintStream output)
  {
    closeFile();
    out = output;
  }
  
  public void setEmacsMode(boolean emacsMode)
  {
    this.emacsMode = emacsMode;
  }
  
  public void setErrorPrintStream(PrintStream err)
  {
    setOutputPrintStream(err);
  }
  
  private static String formatTime(long millis)
  {
    long seconds = millis / 1000L;
    long minutes = seconds / 60L;
    if (minutes > 0L) {
      return 
      
        Long.toString(minutes) + " minute" + (minutes == 1L ? " " : "s ") + Long.toString(seconds % 60L) + " second" + (seconds % 60L == 1L ? "" : "s");
    }
    return 
      Long.toString(seconds) + " second" + (seconds % 60L == 1L ? "" : "s");
  }
  
  public void setProject(Project project)
  {
    this.project = project;
    if (project != null) {
      project.addBuildListener(this);
    }
  }
  
  public Project getProject()
  {
    return project;
  }
  
  public void cleanup()
  {
    closeFile();
    if (project != null) {
      project.removeBuildListener(this);
    }
    project = null;
  }
  
  void openFile(boolean append)
    throws BuildException
  {
    openFileImpl(append);
  }
  
  void closeFile()
  {
    if (out != null)
    {
      out.close();
      out = null;
    }
  }
  
  void reopenFile()
    throws BuildException
  {
    openFileImpl(true);
  }
  
  private void openFileImpl(boolean append)
    throws BuildException
  {
    if (out == null) {
      try
      {
        out = new PrintStream(FileUtils.newOutputStream(Paths.get(filename, new String[0]), append));
      }
      catch (IOException ioe)
      {
        throw new BuildException("Problems opening file using a recorder entry", ioe);
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.RecorderEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */