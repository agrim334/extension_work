package org.codehaus.plexus.util;

import java.io.PrintStream;
import java.util.ArrayList;

public class SweeperPool
{
  private static final boolean DEBUG = false;
  private transient Sweeper sweeper;
  private transient int maxSize;
  private transient int minSize;
  private int triggerSize;
  private ArrayList<Object> pooledObjects;
  private boolean shuttingDown = false;
  
  public SweeperPool(int maxSize, int minSize, int intialCapacity, int sweepInterval, int triggerSize)
  {
    this.maxSize = saneConvert(maxSize);
    this.minSize = saneConvert(minSize);
    this.triggerSize = saneConvert(triggerSize);
    pooledObjects = new ArrayList(intialCapacity);
    if (sweepInterval > 0)
    {
      sweeper = new Sweeper(this, sweepInterval);
      sweeper.start();
    }
  }
  
  private int saneConvert(int value)
  {
    if (value < 0) {
      return 0;
    }
    return value;
  }
  
  public synchronized Object get()
  {
    if ((pooledObjects.size() == 0) || (shuttingDown)) {
      return null;
    }
    Object obj = pooledObjects.remove(0);
    objectRetrieved(obj);
    
    return obj;
  }
  
  public synchronized boolean put(Object obj)
  {
    objectAdded(obj);
    if ((obj != null) && (pooledObjects.size() < maxSize) && (!shuttingDown))
    {
      pooledObjects.add(obj);
      
      return true;
    }
    if (obj != null) {
      objectDisposed(obj);
    }
    return false;
  }
  
  public synchronized int getSize()
  {
    return pooledObjects.size();
  }
  
  public void dispose()
  {
    shuttingDown = true;
    if (sweeper != null)
    {
      sweeper.stop();
      try
      {
        sweeper.join();
      }
      catch (InterruptedException e)
      {
        System.err.println("Unexpected exception occurred: ");
        e.printStackTrace();
      }
    }
    synchronized (this)
    {
      Object[] objects = pooledObjects.toArray();
      for (Object object : objects) {
        objectDisposed(object);
      }
      pooledObjects.clear();
    }
  }
  
  boolean isDisposed()
  {
    if (!shuttingDown) {
      return false;
    }
    if (sweeper == null) {
      return true;
    }
    return sweeper.hasStopped();
  }
  
  public synchronized void trim()
  {
    if (((triggerSize > 0) && (pooledObjects.size() >= triggerSize)) || ((maxSize > 0) && (pooledObjects.size() >= maxSize))) {
      while (pooledObjects.size() > minSize) {
        objectDisposed(pooledObjects.remove(0));
      }
    }
  }
  
  public void objectDisposed(Object obj) {}
  
  public void objectAdded(Object obj) {}
  
  public void objectRetrieved(Object obj) {}
  
  private static class Sweeper
    implements Runnable
  {
    private final transient SweeperPool pool;
    private transient boolean service = false;
    private final transient int sweepInterval;
    private transient Thread t = null;
    
    public Sweeper(SweeperPool pool, int sweepInterval)
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
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.SweeperPool
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */