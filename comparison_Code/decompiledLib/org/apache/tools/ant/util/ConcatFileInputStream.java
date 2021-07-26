package org.apache.tools.ant.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;

public class ConcatFileInputStream
  extends InputStream
{
  private static final int EOF = -1;
  private int currentIndex = -1;
  private boolean eof = false;
  private File[] file;
  private InputStream currentStream;
  private ProjectComponent managingPc;
  
  public ConcatFileInputStream(File[] file)
    throws IOException
  {
    this.file = file;
  }
  
  public void close()
    throws IOException
  {
    closeCurrent();
    eof = true;
  }
  
  public int read()
    throws IOException
  {
    int result = readCurrent();
    if ((result == -1) && (!eof))
    {
      openFile(++currentIndex);
      result = readCurrent();
    }
    return result;
  }
  
  public void setManagingTask(Task task)
  {
    setManagingComponent(task);
  }
  
  public void setManagingComponent(ProjectComponent pc)
  {
    managingPc = pc;
  }
  
  public void log(String message, int loglevel)
  {
    if (managingPc != null) {
      managingPc.log(message, loglevel);
    } else if (loglevel > 1) {
      System.out.println(message);
    } else {
      System.err.println(message);
    }
  }
  
  private int readCurrent()
    throws IOException
  {
    return (eof) || (currentStream == null) ? -1 : currentStream.read();
  }
  
  private void openFile(int index)
    throws IOException
  {
    closeCurrent();
    if ((file != null) && (index < file.length))
    {
      log("Opening " + file[index], 3);
      try
      {
        currentStream = new BufferedInputStream(Files.newInputStream(file[index].toPath(), new OpenOption[0]));
      }
      catch (IOException eyeOhEx)
      {
        log("Failed to open " + file[index], 0);
        throw eyeOhEx;
      }
    }
    else
    {
      eof = true;
    }
  }
  
  private void closeCurrent()
  {
    FileUtils.close(currentStream);
    currentStream = null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ConcatFileInputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */