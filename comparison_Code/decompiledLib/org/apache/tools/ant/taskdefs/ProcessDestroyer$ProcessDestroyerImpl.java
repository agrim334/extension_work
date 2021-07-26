package org.apache.tools.ant.taskdefs;

class ProcessDestroyer$ProcessDestroyerImpl
  extends Thread
{
  private boolean shouldDestroy = true;
  
  public ProcessDestroyer$ProcessDestroyerImpl(ProcessDestroyer paramProcessDestroyer)
  {
    super("ProcessDestroyer Shutdown Hook");
  }
  
  public void run()
  {
    if (shouldDestroy) {
      this$0.run();
    }
  }
  
  public void setShouldDestroy(boolean shouldDestroy)
  {
    this.shouldDestroy = shouldDestroy;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ProcessDestroyer.ProcessDestroyerImpl
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */