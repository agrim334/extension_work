package org.apache.tools.ant.input;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.util.FileUtils;

public class GreedyInputHandler
  extends DefaultInputHandler
{
  public void handleInput(InputRequest request)
    throws BuildException
  {
    String prompt = getPrompt(request);
    InputStream in = null;
    try
    {
      in = getInputStream();
      System.err.println(prompt);
      System.err.flush();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      StreamPumper p = new StreamPumper(in, baos);
      Thread t = new Thread(p);
      t.start();
      try
      {
        t.join();
      }
      catch (InterruptedException e)
      {
        try
        {
          t.join();
        }
        catch (InterruptedException localInterruptedException1) {}
      }
      request.setInput(new String(baos.toByteArray()));
      if (!request.isInputValid()) {
        throw new BuildException("Received invalid console input");
      }
      if (p.getException() != null) {
        throw new BuildException("Failed to read input from console", p.getException());
      }
    }
    finally
    {
      FileUtils.close(in);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.input.GreedyInputHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */