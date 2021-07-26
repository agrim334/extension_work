package org.apache.tools.ant;

import java.io.IOException;
import java.io.InputStream;

public class DemuxInputStream
  extends InputStream
{
  private static final int MASK_8BIT = 255;
  private Project project;
  
  public DemuxInputStream(Project project)
  {
    this.project = project;
  }
  
  public int read()
    throws IOException
  {
    byte[] buffer = new byte[1];
    if (project.demuxInput(buffer, 0, 1) == -1) {
      return -1;
    }
    return buffer[0] & 0xFF;
  }
  
  public int read(byte[] buffer, int offset, int length)
    throws IOException
  {
    return project.demuxInput(buffer, offset, length);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.DemuxInputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */