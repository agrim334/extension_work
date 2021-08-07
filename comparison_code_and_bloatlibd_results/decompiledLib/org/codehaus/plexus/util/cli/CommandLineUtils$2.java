package org.codehaus.plexus.util.cli;

import java.io.InputStream;

final class CommandLineUtils$2
  implements CommandLineCallable
{
  CommandLineUtils$2(InputStream paramInputStream, Process paramProcess, StreamConsumer paramStreamConsumer1, StreamConsumer paramStreamConsumer2, int paramInt, Thread paramThread) {}
  
  public Integer call()
    throws CommandLineException
  {
    StreamFeeder inputFeeder = null;
    StreamPumper outputPumper = null;
    StreamPumper errorPumper = null;
    boolean success = false;
    try
    {
      if (val$systemIn != null)
      {
        inputFeeder = new StreamFeeder(val$systemIn, val$p.getOutputStream());
        inputFeeder.start();
      }
      outputPumper = new StreamPumper(val$p.getInputStream(), val$systemOut);
      outputPumper.start();
      
      errorPumper = new StreamPumper(val$p.getErrorStream(), val$systemErr);
      errorPumper.start();
      int returnValue;
      long now;
      int returnValue;
      if (val$timeoutInSeconds <= 0)
      {
        returnValue = val$p.waitFor();
      }
      else
      {
        now = System.nanoTime();
        long timeout = now + 1000000000L * val$timeoutInSeconds;
        while ((CommandLineUtils.isAlive(val$p)) && (System.nanoTime() < timeout)) {
          Thread.sleep(999L);
        }
        if (CommandLineUtils.isAlive(val$p)) {
          throw new InterruptedException(String.format("Process timed out after %d seconds.", new Object[] { Integer.valueOf(val$timeoutInSeconds) }));
        }
        returnValue = val$p.exitValue();
      }
      if (inputFeeder != null) {
        inputFeeder.waitUntilDone();
      }
      outputPumper.waitUntilDone();
      errorPumper.waitUntilDone();
      if (inputFeeder != null)
      {
        inputFeeder.close();
        CommandLineUtils.access$000(inputFeeder, "stdin");
      }
      outputPumper.close();
      CommandLineUtils.access$100(outputPumper, "stdout");
      
      errorPumper.close();
      CommandLineUtils.access$100(errorPumper, "stderr");
      
      success = true;
      return Integer.valueOf(returnValue);
    }
    catch (InterruptedException ex)
    {
      throw new CommandLineTimeOutException("Error while executing external command, process killed.", ex);
    }
    finally
    {
      if (inputFeeder != null) {
        inputFeeder.disable();
      }
      if (outputPumper != null) {
        outputPumper.disable();
      }
      if (errorPumper != null) {
        errorPumper.disable();
      }
      try
      {
        ShutdownHookUtils.removeShutdownHook(val$processHook);
        val$processHook.run();
      }
      finally
      {
        try
        {
          if (inputFeeder != null)
          {
            inputFeeder.close();
            if (success)
            {
              success = false;
              CommandLineUtils.access$000(inputFeeder, "stdin");
              success = true;
            }
          }
        }
        finally
        {
          try
          {
            if (outputPumper != null)
            {
              outputPumper.close();
              if (success)
              {
                success = false;
                CommandLineUtils.access$100(outputPumper, "stdout");
                success = true;
              }
            }
          }
          finally
          {
            if (errorPumper != null)
            {
              errorPumper.close();
              if (success) {
                CommandLineUtils.access$100(errorPumper, "stderr");
              }
            }
          }
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.CommandLineUtils.2
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */