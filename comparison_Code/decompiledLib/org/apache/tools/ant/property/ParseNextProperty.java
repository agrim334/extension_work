package org.apache.tools.ant.property;

import java.text.ParsePosition;
import org.apache.tools.ant.Project;

public abstract interface ParseNextProperty
{
  public abstract Project getProject();
  
  public abstract Object parseNextProperty(String paramString, ParsePosition paramParsePosition);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.property.ParseNextProperty
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */