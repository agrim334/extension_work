package org.apache.tools.ant.types;

public class FilterSet$Filter
{
  String token;
  String value;
  
  public FilterSet$Filter(String token, String value)
  {
    setToken(token);
    setValue(value);
  }
  
  public FilterSet$Filter() {}
  
  public void setToken(String token)
  {
    this.token = token;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String getToken()
  {
    return token;
  }
  
  public String getValue()
  {
    return value;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.FilterSet.Filter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */