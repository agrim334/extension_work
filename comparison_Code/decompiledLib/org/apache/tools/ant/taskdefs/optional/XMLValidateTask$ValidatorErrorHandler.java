package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class XMLValidateTask$ValidatorErrorHandler
  implements ErrorHandler
{
  protected File currentFile = null;
  protected String lastErrorMessage = null;
  protected boolean failed = false;
  
  protected XMLValidateTask$ValidatorErrorHandler(XMLValidateTask this$0) {}
  
  public void init(File file)
  {
    currentFile = file;
    failed = false;
  }
  
  public boolean getFailure()
  {
    return failed;
  }
  
  public void fatalError(SAXParseException exception)
  {
    failed = true;
    doLog(exception, 0);
  }
  
  public void error(SAXParseException exception)
  {
    failed = true;
    doLog(exception, 0);
  }
  
  public void warning(SAXParseException exception)
  {
    if (this$0.warn) {
      doLog(exception, 1);
    }
  }
  
  private void doLog(SAXParseException e, int logLevel)
  {
    this$0.log(getMessage(e), logLevel);
  }
  
  private String getMessage(SAXParseException e)
  {
    String sysID = e.getSystemId();
    if (sysID != null)
    {
      String name = sysID;
      if (sysID.startsWith("file:")) {
        try
        {
          name = XMLValidateTask.access$000().fromURI(sysID);
        }
        catch (Exception localException) {}
      }
      int line = e.getLineNumber();
      int col = e.getColumnNumber();
      return name + (
        line == -1 ? 
        "" : 
        new StringBuilder().append(":").append(line).append(col == -1 ? "" : new StringBuilder().append(":").append(col).toString()).toString()) + ": " + e
        
        .getMessage();
    }
    return e.getMessage();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.XMLValidateTask.ValidatorErrorHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */