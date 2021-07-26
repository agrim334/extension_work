package org.apache.tools.ant.taskdefs;

public class PumpStreamHandler$ThreadWithPumper
  extends Thread
{
  private final StreamPumper pumper;
  
  public PumpStreamHandler$ThreadWithPumper(StreamPumper p)
  {
    super(p);
    pumper = p;
  }
  
  protected StreamPumper getPumper()
  {
    return pumper;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.PumpStreamHandler.ThreadWithPumper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */