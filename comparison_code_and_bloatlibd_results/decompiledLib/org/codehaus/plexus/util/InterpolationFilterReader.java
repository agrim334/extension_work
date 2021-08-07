package org.codehaus.plexus.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class InterpolationFilterReader
  extends FilterReader
{
  private String replaceData = null;
  private int replaceIndex = -1;
  private int previousIndex = -1;
  private Map<?, Object> variables = new HashMap();
  private String beginToken;
  private String endToken;
  private int beginTokenLength;
  private int endTokenLength;
  private static final String DEFAULT_BEGIN_TOKEN = "${";
  private static final String DEFAULT_END_TOKEN = "}";
  
  public InterpolationFilterReader(Reader in, Map<?, Object> variables, String beginToken, String endToken)
  {
    super(in);
    
    this.variables = variables;
    this.beginToken = beginToken;
    this.endToken = endToken;
    
    beginTokenLength = beginToken.length();
    endTokenLength = endToken.length();
  }
  
  public InterpolationFilterReader(Reader in, Map<String, Object> variables)
  {
    this(in, variables, "${", "}");
  }
  
  public long skip(long n)
    throws IOException
  {
    if (n < 0L) {
      throw new IllegalArgumentException("skip value is negative");
    }
    for (long i = 0L; i < n; i += 1L) {
      if (read() == -1) {
        return i;
      }
    }
    return n;
  }
  
  public int read(char[] cbuf, int off, int len)
    throws IOException
  {
    for (int i = 0; i < len; i++)
    {
      int ch = read();
      if (ch == -1)
      {
        if (i == 0) {
          return -1;
        }
        return i;
      }
      cbuf[(off + i)] = ((char)ch);
    }
    return len;
  }
  
  public int read()
    throws IOException
  {
    if ((replaceIndex != -1) && (replaceIndex < replaceData.length()))
    {
      int ch = replaceData.charAt(replaceIndex++);
      if (replaceIndex >= replaceData.length()) {
        replaceIndex = -1;
      }
      return ch;
    }
    int ch;
    int ch;
    if ((previousIndex != -1) && (previousIndex < endTokenLength)) {
      ch = endToken.charAt(previousIndex++);
    } else {
      ch = in.read();
    }
    if (ch == beginToken.charAt(0))
    {
      StringBuilder key = new StringBuilder();
      
      int beginTokenMatchPos = 1;
      do
      {
        if ((previousIndex != -1) && (previousIndex < endTokenLength)) {
          ch = endToken.charAt(previousIndex++);
        } else {
          ch = in.read();
        }
        if (ch == -1) {
          break;
        }
        key.append((char)ch);
        if ((beginTokenMatchPos < beginTokenLength) && (ch != beginToken.charAt(beginTokenMatchPos++)))
        {
          ch = -1;
          break;
        }
      } while (ch != endToken.charAt(0));
      if ((ch != -1) && (endTokenLength > 1))
      {
        int endTokenMatchPos = 1;
        do
        {
          if ((previousIndex != -1) && (previousIndex < endTokenLength)) {
            ch = endToken.charAt(previousIndex++);
          } else {
            ch = in.read();
          }
          if (ch == -1) {
            break;
          }
          key.append((char)ch);
          if (ch != endToken.charAt(endTokenMatchPos++))
          {
            ch = -1;
            break;
          }
        } while (endTokenMatchPos < endTokenLength);
      }
      if (ch == -1)
      {
        replaceData = key.toString();
        replaceIndex = 0;
        return beginToken.charAt(0);
      }
      String variableKey = key.substring(beginTokenLength - 1, key.length() - endTokenLength);
      
      Object o = variables.get(variableKey);
      if (o != null)
      {
        String value = o.toString();
        if (value.length() != 0)
        {
          replaceData = value;
          replaceIndex = 0;
        }
        return read();
      }
      previousIndex = 0;
      replaceData = key.substring(0, key.length() - endTokenLength);
      replaceIndex = 0;
      return beginToken.charAt(0);
    }
    return ch;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.InterpolationFilterReader
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */