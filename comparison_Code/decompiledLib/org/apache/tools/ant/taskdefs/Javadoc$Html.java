package org.apache.tools.ant.taskdefs;

public class Javadoc$Html
{
  private final StringBuffer text = new StringBuffer();
  
  public void addText(String t)
  {
    text.append(t);
  }
  
  public String getText()
  {
    return text.substring(0);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Javadoc.Html
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */