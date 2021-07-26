package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;

public abstract class BaseSelector
  extends DataType
  implements FileSelector
{
  private String errmsg = null;
  private Throwable cause;
  
  public void setError(String msg)
  {
    if (errmsg == null) {
      errmsg = msg;
    }
  }
  
  public void setError(String msg, Throwable cause)
  {
    if (errmsg == null)
    {
      errmsg = msg;
      this.cause = cause;
    }
  }
  
  public String getError()
  {
    return errmsg;
  }
  
  public void verifySettings()
  {
    if (isReference()) {
      getRef().verifySettings();
    }
  }
  
  public void validate()
  {
    if (getError() == null) {
      verifySettings();
    }
    if (getError() != null) {
      throw new BuildException(errmsg, cause);
    }
    if (!isReference()) {
      dieOnCircularReference();
    }
  }
  
  public abstract boolean isSelected(File paramFile1, String paramString, File paramFile2);
  
  private BaseSelector getRef()
  {
    return (BaseSelector)getCheckedRef(BaseSelector.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.BaseSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */