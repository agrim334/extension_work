package org.apache.tools.ant.taskdefs.email;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.UUEncoder;

class UUMailer
  extends PlainMailer
{
  protected void attach(File file, PrintStream out)
    throws IOException
  {
    if ((!file.exists()) || (!file.canRead())) {
      throw new BuildException("File \"%s\" does not exist or is not readable.", new Object[] {file.getAbsolutePath() });
    }
    InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]));
    try
    {
      new UUEncoder(file.getName()).encode(in, out);
      in.close();
    }
    catch (Throwable localThrowable) {}
    try
    {
      in.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable.addSuppressed(localThrowable1);
    }
    throw localThrowable;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.email.UUMailer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */