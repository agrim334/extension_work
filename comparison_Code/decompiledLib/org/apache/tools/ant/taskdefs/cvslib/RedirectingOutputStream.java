package org.apache.tools.ant.taskdefs.cvslib;

import org.apache.tools.ant.util.LineOrientedOutputStream;

class RedirectingOutputStream
  extends LineOrientedOutputStream
{
  private final ChangeLogParser parser;
  
  public RedirectingOutputStream(ChangeLogParser parser)
  {
    this.parser = parser;
  }
  
  protected void processLine(String line)
  {
    parser.stdout(line);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.RedirectingOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */