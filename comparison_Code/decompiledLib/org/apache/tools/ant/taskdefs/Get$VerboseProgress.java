package org.apache.tools.ant.taskdefs;

import java.io.PrintStream;

public class Get$VerboseProgress
  implements Get.DownloadProgress
{
  private int dots = 0;
  PrintStream out;
  
  public Get$VerboseProgress(PrintStream out)
  {
    this.out = out;
  }
  
  public void beginDownload()
  {
    dots = 0;
  }
  
  public void onTick()
  {
    out.print(".");
    if (dots++ > 50)
    {
      out.flush();
      dots = 0;
    }
  }
  
  public void endDownload()
  {
    out.println();
    out.flush();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Get.VerboseProgress
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */