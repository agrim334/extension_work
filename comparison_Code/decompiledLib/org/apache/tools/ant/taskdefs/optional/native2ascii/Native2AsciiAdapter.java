package org.apache.tools.ant.taskdefs.optional.native2ascii;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;

public abstract interface Native2AsciiAdapter
{
  public abstract boolean convert(Native2Ascii paramNative2Ascii, File paramFile1, File paramFile2)
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */