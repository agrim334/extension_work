package org.apache.tools.ant.taskdefs;

public abstract interface Get$DownloadProgress
{
  public abstract void beginDownload();
  
  public abstract void onTick();
  
  public abstract void endDownload();
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Get.DownloadProgress
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */