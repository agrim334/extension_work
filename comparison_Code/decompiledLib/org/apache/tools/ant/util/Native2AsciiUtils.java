package org.apache.tools.ant.util;

public class Native2AsciiUtils
{
  private static final int MAX_ASCII = 127;
  
  public static String native2ascii(String line)
  {
    StringBuilder sb = new StringBuilder();
    for (char c : line.toCharArray()) {
      if (c <= '') {
        sb.append(c);
      } else {
        sb.append(String.format("\\u%04x", new Object[] { Integer.valueOf(c) }));
      }
    }
    return sb.toString();
  }
  
  public static String ascii2native(String line)
  {
    StringBuilder sb = new StringBuilder();
    int inputLen = line.length();
    for (int i = 0; i < inputLen; i++)
    {
      char c = line.charAt(i);
      if ((c != '\\') || (i >= inputLen - 5))
      {
        sb.append(c);
      }
      else
      {
        char u = line.charAt(++i);
        if (u == 'u')
        {
          int unescaped = tryParse(line, i + 1);
          if (unescaped >= 0)
          {
            sb.append((char)unescaped);
            i += 4;
            continue;
          }
        }
        sb.append(c).append(u);
      }
    }
    return sb.toString();
  }
  
  private static int tryParse(String line, int startIdx)
  {
    try
    {
      return Integer.parseInt(line.substring(startIdx, startIdx + 4), 16);
    }
    catch (NumberFormatException ex) {}
    return -1;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.Native2AsciiUtils
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */