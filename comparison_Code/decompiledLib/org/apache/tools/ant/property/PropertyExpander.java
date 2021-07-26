package org.apache.tools.ant.property;

import java.text.ParsePosition;
import org.apache.tools.ant.PropertyHelper.Delegate;

public abstract interface PropertyExpander
  extends PropertyHelper.Delegate
{
  public abstract String parsePropertyName(String paramString, ParsePosition paramParsePosition, ParseNextProperty paramParseNextProperty);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.property.PropertyExpander
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */