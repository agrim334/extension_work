package org.apache.tools.ant.taskdefs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

final class StreamPumper$PostStopHandle
{
  private boolean inPostStopTasks = true;
  private final CountDownLatch latch = new CountDownLatch(1);
  
  StreamPumper$PostStopHandle(StreamPumper this$0) {}
  
  boolean isInPostStopTasks()
  {
    return inPostStopTasks;
  }
  
  boolean awaitPostStopCompletion(long timeout, TimeUnit timeUnit)
    throws InterruptedException
  {
    return latch.await(timeout, timeUnit);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.StreamPumper.PostStopHandle
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */