package org.apache.tools.ant.taskdefs;

import java.text.MessageFormat;
import java.util.HashMap;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class Checksum$FormatElement
  extends EnumeratedAttribute
{
  private static HashMap<String, MessageFormat> formatMap = new HashMap();
  private static final String CHECKSUM = "CHECKSUM";
  private static final String MD5SUM = "MD5SUM";
  private static final String SVF = "SVF";
  
  static
  {
    formatMap.put("CHECKSUM", new MessageFormat("{0}"));
    formatMap.put("MD5SUM", new MessageFormat("{0} *{1}"));
    formatMap.put("SVF", new MessageFormat("MD5 ({1}) = {0}"));
  }
  
  public static FormatElement getDefault()
  {
    FormatElement e = new FormatElement();
    e.setValue("CHECKSUM");
    return e;
  }
  
  public MessageFormat getFormat()
  {
    return (MessageFormat)formatMap.get(getValue());
  }
  
  public String[] getValues()
  {
    return new String[] { "CHECKSUM", "MD5SUM", "SVF" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Checksum.FormatElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */