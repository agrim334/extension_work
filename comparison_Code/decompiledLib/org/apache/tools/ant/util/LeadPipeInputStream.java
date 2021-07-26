package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;

public class LeadPipeInputStream
  extends PipedInputStream
{
  private static final int BYTE_MASK = 255;
  private ProjectComponent managingPc;
  
  public LeadPipeInputStream() {}
  
  public LeadPipeInputStream(int size)
  {
    setBufferSize(size);
  }
  
  public LeadPipeInputStream(PipedOutputStream src)
    throws IOException
  {
    super(src);
  }
  
  public LeadPipeInputStream(PipedOutputStream src, int size)
    throws IOException
  {
    super(src);
    setBufferSize(size);
  }
  
  public synchronized int read()
    throws IOException
  {
    int result = -1;
    try
    {
      result = super.read();
    }
    catch (IOException eyeOhEx)
    {
      String msg = eyeOhEx.getMessage();
      if (("write end dead".equalsIgnoreCase(msg)) || 
        ("pipe broken".equalsIgnoreCase(msg)))
      {
        if ((in > 0) && (out < buffer.length) && (out > in)) {
          result = buffer[(out++)] & 0xFF;
        }
      }
      else {
        log("error at LeadPipeInputStream.read():  " + msg, 2);
      }
    }
    return result;
  }
  
  public synchronized void setBufferSize(int size)
  {
    if (size > buffer.length)
    {
      byte[] newBuffer = new byte[size];
      if (in >= 0) {
        if (in > out)
        {
          System.arraycopy(buffer, out, newBuffer, out, in - out);
        }
        else
        {
          int outlen = buffer.length - out;
          System.arraycopy(buffer, out, newBuffer, 0, outlen);
          System.arraycopy(buffer, 0, newBuffer, outlen, in);
          in += outlen;
          out = 0;
        }
      }
      buffer = newBuffer;
    }
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
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LeadPipeInputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */