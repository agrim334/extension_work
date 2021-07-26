package org.apache.tools.ant.types.selectors;

public class NotSelector
  extends NoneSelector
{
  public NotSelector() {}
  
  public NotSelector(FileSelector other)
  {
    this();
    appendSelector(other);
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    if (hasSelectors())
    {
      buf.append("{notselect: ");
      buf.append(super.toString());
      buf.append("}");
    }
    return buf.toString();
  }
  
  public void verifySettings()
  {
    if (selectorCount() != 1) {
      setError("One and only one selector is allowed within the <not> tag");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.NotSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */