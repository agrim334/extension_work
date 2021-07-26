package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;

public abstract class BaseExtendSelector
  extends BaseSelector
  implements ExtendFileSelector
{
  protected Parameter[] parameters = null;
  
  public void setParameters(Parameter... parameters)
  {
    this.parameters = parameters;
  }
  
  protected Parameter[] getParameters()
  {
    return parameters;
  }
  
  public abstract boolean isSelected(File paramFile1, String paramString, File paramFile2)
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.BaseExtendSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */