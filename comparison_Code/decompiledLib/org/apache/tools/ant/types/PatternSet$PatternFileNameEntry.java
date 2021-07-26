package org.apache.tools.ant.types;

public class PatternSet$PatternFileNameEntry
  extends PatternSet.NameEntry
{
  private String encoding;
  
  public PatternSet$PatternFileNameEntry(PatternSet this$0)
  {
    super(this$0);
  }
  
  public final void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public final String getEncoding()
  {
    return encoding;
  }
  
  public String toString()
  {
    String baseString = super.toString();
    return 
      baseString + ";encoding->" + encoding;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.PatternSet.PatternFileNameEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */