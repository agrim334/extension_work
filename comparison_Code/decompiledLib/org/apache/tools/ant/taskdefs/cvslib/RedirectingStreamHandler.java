package org.apache.tools.ant.taskdefs.cvslib;

import java.io.ByteArrayOutputStream;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.util.FileUtils;

class RedirectingStreamHandler
  extends PumpStreamHandler
{
  RedirectingStreamHandler(ChangeLogParser parser)
  {
    super(new RedirectingOutputStream(parser), new ByteArrayOutputStream());
  }
  
  String getErrors()
  {
    try
    {
      ByteArrayOutputStream error = (ByteArrayOutputStream)getErr();
      
      return error.toString("ASCII");
    }
    catch (Exception e) {}
    return null;
  }
  
  public void stop()
  {
    super.stop();
    FileUtils.close(getErr());
    FileUtils.close(getOut());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.RedirectingStreamHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */