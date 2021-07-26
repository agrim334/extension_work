package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.util.FileNameMapper;

class Native2Ascii$ExtMapper
  implements FileNameMapper
{
  private Native2Ascii$ExtMapper(Native2Ascii paramNative2Ascii) {}
  
  public void setFrom(String s) {}
  
  public void setTo(String s) {}
  
  public String[] mapFileName(String fileName)
  {
    int lastDot = fileName.lastIndexOf('.');
    if (lastDot >= 0) {
      return new String[] { fileName.substring(0, lastDot) + Native2Ascii.access$100(this$0) };
    }
    return new String[] { fileName + Native2Ascii.access$100(this$0) };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.Native2Ascii.ExtMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */