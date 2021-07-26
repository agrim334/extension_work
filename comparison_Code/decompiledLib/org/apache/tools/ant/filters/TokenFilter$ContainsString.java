package org.apache.tools.ant.filters;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

public class TokenFilter$ContainsString
  extends ProjectComponent
  implements TokenFilter.Filter
{
  private String contains;
  
  public void setContains(String contains)
  {
    this.contains = contains;
  }
  
  public String filter(String string)
  {
    if (contains == null) {
      throw new BuildException("Missing contains in containsstring");
    }
    if (string.contains(contains)) {
      return string;
    }
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter.ContainsString
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */