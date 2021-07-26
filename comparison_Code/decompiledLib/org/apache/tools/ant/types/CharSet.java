package org.apache.tools.ant.types;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

public class CharSet
  extends EnumeratedAttribute
{
  private static final List<String> VALUES = new ArrayList();
  
  static
  {
    for (Map.Entry<String, Charset> entry : Charset.availableCharsets().entrySet())
    {
      VALUES.add((String)entry.getKey());
      VALUES.addAll(((Charset)entry.getValue()).aliases());
    }
  }
  
  public CharSet(String value)
  {
    setValue(value);
  }
  
  public static CharSet getDefault()
  {
    return new CharSet(Charset.defaultCharset().name());
  }
  
  public static CharSet getAscii()
  {
    return new CharSet(StandardCharsets.US_ASCII.name());
  }
  
  public static CharSet getUtf8()
  {
    return new CharSet(StandardCharsets.UTF_8.name());
  }
  
  public boolean equivalent(CharSet cs)
  {
    return getCharset().name().equals(cs.getCharset().name());
  }
  
  public Charset getCharset()
  {
    return Charset.forName(getValue());
  }
  
  public String[] getValues()
  {
    return (String[])VALUES.toArray(new String[0]);
  }
  
  public final void setValue(String value)
  {
    String realValue = value;
    if ((value == null) || (value.isEmpty())) {
      realValue = Charset.defaultCharset().name();
    } else {
      for (String v : Arrays.asList(new String[] { value, value.toLowerCase(), value.toUpperCase() })) {
        if (VALUES.contains(v))
        {
          realValue = v;
          break;
        }
      }
    }
    super.setValue(realValue);
  }
  
  public CharSet() {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.CharSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */