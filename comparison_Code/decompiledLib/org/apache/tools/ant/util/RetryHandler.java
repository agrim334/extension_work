package org.apache.tools.ant.util;

import java.io.IOException;
import org.apache.tools.ant.Task;

public class RetryHandler
{
  private int retriesAllowed = 0;
  private Task task;
  
  public RetryHandler(int retriesAllowed, Task task)
  {
    this.retriesAllowed = retriesAllowed;
    this.task = task;
  }
  
  public void execute(Retryable exe, String desc)
    throws IOException
  {
    int retries = 0;
    for (;;)
    {
      try
      {
        exe.execute();
      }
      catch (IOException e)
      {
        retries++;
        if ((retries > retriesAllowed) && (retriesAllowed > -1))
        {
          task.log("try #" + retries + ": IO error (" + desc + "), number of maximum retries reached (" + retriesAllowed + "), giving up", 1);
          
          throw e;
        }
        task.log("try #" + retries + ": IO error (" + desc + "), retrying", 1);
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.RetryHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */