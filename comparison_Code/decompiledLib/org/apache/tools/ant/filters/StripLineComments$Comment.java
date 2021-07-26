package org.apache.tools.ant.filters;

public class StripLineComments$Comment
{
  private String value;
  
  public final void setValue(String comment)
  {
    if (value != null) {
      throw new IllegalStateException("Comment value already set.");
    }
    value = comment;
  }
  
  public final String getValue()
  {
    return value;
  }
  
  public void addText(String comment)
  {
    setValue(comment);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.StripLineComments.Comment
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */