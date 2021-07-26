package org.apache.tools.ant.attribute;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.UnknownElement;

public class IfSetAttribute
  extends BaseIfAttribute
{
  public static class Unless
    extends IfSetAttribute
  {
    public Unless()
    {
      setPositive(false);
    }
  }
  
  public boolean isEnabled(UnknownElement el, String value)
  {
    return convertResult(getProject().getProperty(value) != null);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.attribute.IfSetAttribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */