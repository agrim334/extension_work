package org.apache.tools.ant.attribute;

import org.apache.tools.ant.UnknownElement;

public class IfBlankAttribute
  extends BaseIfAttribute
{
  public static class Unless
    extends IfBlankAttribute
  {
    public Unless()
    {
      setPositive(false);
    }
  }
  
  public boolean isEnabled(UnknownElement el, String value)
  {
    return convertResult((value == null) || (value.isEmpty()));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.attribute.IfBlankAttribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */