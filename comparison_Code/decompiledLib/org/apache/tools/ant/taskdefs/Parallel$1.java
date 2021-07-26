package org.apache.tools.ant.taskdefs;

class Parallel$1
  extends Thread
{
  Parallel$1(Parallel this$0) {}
  
  public synchronized void run()
  {
    try
    {
      long start = System.currentTimeMillis();
      long end = start + Parallel.access$100(this$0);
      long now = System.currentTimeMillis();
      while (now < end)
      {
        wait(end - now);
        now = System.currentTimeMillis();
      }
      synchronized (Parallel.access$200(this$0))
      {
        Parallel.access$302(this$0, false);
        Parallel.access$402(this$0, true);
        Parallel.access$200(this$0).notifyAll();
      }
    }
    catch (InterruptedException localInterruptedException) {}
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Parallel.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */