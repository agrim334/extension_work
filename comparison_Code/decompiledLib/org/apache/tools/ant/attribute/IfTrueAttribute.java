package org.apache.tools.ant.attribute;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.UnknownElement;

public class IfTrueAttribute
  extends BaseIfAttribute
{
  public static class Unless
    extends IfTrueAttribute
  {
    public Unless()
    {
      setPositive(false);
    }
  }
  
  public boolean isEnabled(UnknownElement el, String value)
  {
    return convertResult(Project.toBoolean(value));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.attribute.IfTrueAttribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */