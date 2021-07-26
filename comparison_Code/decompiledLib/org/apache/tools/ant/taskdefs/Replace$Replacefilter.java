package org.apache.tools.ant.taskdefs;

import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;

public class Replace$Replacefilter
{
  private Replace.NestedString token;
  private Replace.NestedString value;
  private String replaceValue;
  private String property;
  private StringBuffer inputBuffer;
  private StringBuffer outputBuffer = new StringBuffer();
  
  public Replace$Replacefilter(Replace this$0) {}
  
  public void validate()
    throws BuildException
  {
    if (token == null) {
      throw new BuildException("token is a mandatory for replacefilter.");
    }
    if (token.getText().isEmpty()) {
      throw new BuildException("The token must not be an empty string.");
    }
    if ((value != null) && (property != null)) {
      throw new BuildException("Either value or property can be specified, but a replacefilter element cannot have both.");
    }
    if (property != null)
    {
      if (Replace.access$000(this$0) == null) {
        throw new BuildException("The replacefilter's property attribute can only be used with the replacetask's propertyFile/Resource attribute.");
      }
      if ((Replace.access$100(this$0) == null) || 
        (Replace.access$100(this$0).getProperty(property) == null)) {
        throw new BuildException("property \"%s\" was not found in %s", new Object[] { property, Replace.access$000(this$0).getName() });
      }
    }
    replaceValue = getReplaceValue();
  }
  
  public String getReplaceValue()
  {
    if (property != null) {
      return Replace.access$100(this$0).getProperty(property);
    }
    if (value != null) {
      return value.getText();
    }
    if (Replace.access$200(this$0) != null) {
      return Replace.access$200(this$0).getText();
    }
    return "";
  }
  
  public void setToken(String t)
  {
    createReplaceToken().addText(t);
  }
  
  public String getToken()
  {
    return token.getText();
  }
  
  public void setValue(String value)
  {
    createReplaceValue().addText(value);
  }
  
  public String getValue()
  {
    return value.getText();
  }
  
  public void setProperty(String property)
  {
    this.property = property;
  }
  
  public String getProperty()
  {
    return property;
  }
  
  public Replace.NestedString createReplaceToken()
  {
    if (token == null) {
      token = new Replace.NestedString(this$0);
    }
    return token;
  }
  
  public Replace.NestedString createReplaceValue()
  {
    if (value == null) {
      value = new Replace.NestedString(this$0);
    }
    return value;
  }
  
  StringBuffer getOutputBuffer()
  {
    return outputBuffer;
  }
  
  void setInputBuffer(StringBuffer input)
  {
    inputBuffer = input;
  }
  
  boolean process()
  {
    String t = getToken();
    if (inputBuffer.length() > t.length())
    {
      int pos = replace();
      pos = Math.max(inputBuffer.length() - t.length(), pos);
      outputBuffer.append(inputBuffer.substring(0, pos));
      inputBuffer.delete(0, pos);
      return true;
    }
    return false;
  }
  
  void flush()
  {
    replace();
    outputBuffer.append(inputBuffer);
    inputBuffer.delete(0, inputBuffer.length());
  }
  
  private int replace()
  {
    String t = getToken();
    int found = inputBuffer.indexOf(t);
    int pos = -1;
    int tokenLength = t.length();
    int replaceValueLength = replaceValue.length();
    while (found >= 0)
    {
      inputBuffer.replace(found, found + tokenLength, replaceValue);
      pos = found + replaceValueLength;
      found = inputBuffer.indexOf(t, pos);
      Replace.access$304(this$0);
    }
    return pos;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Replace.Replacefilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */