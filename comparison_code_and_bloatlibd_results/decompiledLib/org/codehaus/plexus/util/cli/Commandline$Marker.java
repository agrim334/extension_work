package org.codehaus.plexus.util.cli;

import java.util.Vector;

public class Commandline$Marker
{
  private int position;
  private int realPos = -1;
  
  Commandline$Marker(Commandline paramCommandline, int position)
  {
    this.position = position;
  }
  
  public int getPosition()
  {
    if (realPos == -1)
    {
      realPos = (this$0.getLiteralExecutable() == null ? 0 : 1);
      for (int i = 0; i < position; i++)
      {
        Arg arg = (Arg)this$0.arguments.elementAt(i);
        realPos += arg.getParts().length;
      }
    }
    return realPos;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.Commandline.Marker
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */