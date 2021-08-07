package org.codehaus.plexus.util;

class SweeperPool$Sweeper
  implements Runnable
{
  private final transient SweeperPool pool;
  private transient boolean service = false;
  private final transient int sweepInterval;
  private transient Thread t = null;
  
  public SweeperPool$Sweeper(SweeperPool pool, int sweepInterval)
  {
    this.sweepInterval = sweepInterval;
    this.pool = pool;
  }
  
  public void run()
  {
    debug("started");
    if (sweepInterval > 0) {
      synchronized (this)
      {
        while (service)
        {
          try
          {
            wait(sweepInterval * 1000);
          }
          catch (InterruptedException localInterruptedException) {}
          runSweep();
        }
      }
    }
    debug("stopped");
  }
  
  public void start()
  {
    if (!service)
    {
      service = true;
      t = new Thread(this);
      t.setName("Sweeper");
      t.start();
    }
  }
  
  public synchronized void stop()
  {
    service = false;
    notifyAll();
  }
  
  void join()
    throws InterruptedException
  {
    t.join();
  }
  
  boolean hasStopped()
  {
    return (!service) && (!t.isAlive());
  }
  
  private final void debug(String msg) {}
  
  private void runSweep()
  {
    debug("runningSweep. time=" + System.currentTimeMillis());
    pool.trim();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.SweeperPool.Sweeper
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */