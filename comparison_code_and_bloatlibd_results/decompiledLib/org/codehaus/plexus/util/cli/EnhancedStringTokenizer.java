package org.codehaus.plexus.util.cli;

import java.util.StringTokenizer;

public final class EnhancedStringTokenizer
{
  private StringTokenizer cst = null;
  String cdelim;
  final boolean cdelimSingleChar;
  final char cdelimChar;
  boolean creturnDelims;
  String lastToken = null;
  boolean delimLast = true;
  
  public EnhancedStringTokenizer(String str)
  {
    this(str, " \t\n\r\f", false);
  }
  
  public EnhancedStringTokenizer(String str, String delim)
  {
    this(str, delim, false);
  }
  
  public EnhancedStringTokenizer(String str, String delim, boolean returnDelims)
  {
    cst = new StringTokenizer(str, delim, true);
    cdelim = delim;
    creturnDelims = returnDelims;
    cdelimSingleChar = (delim.length() == 1);
    cdelimChar = delim.charAt(0);
  }
  
  public boolean hasMoreTokens()
  {
    return cst.hasMoreTokens();
  }
  
  private String internalNextToken()
  {
    if (lastToken != null)
    {
      String last = lastToken;
      lastToken = null;
      return last;
    }
    String token = cst.nextToken();
    if (isDelim(token))
    {
      if (delimLast)
      {
        lastToken = token;
        return "";
      }
      delimLast = true;
      return token;
    }
    delimLast = false;
    return token;
  }
  
  public String nextToken()
  {
    String token = internalNextToken();
    if (creturnDelims) {
      return token;
    }
    if (isDelim(token)) {
      return hasMoreTokens() ? internalNextToken() : "";
    }
    return token;
  }
  
  private boolean isDelim(String str)
  {
    if (str.length() == 1)
    {
      char ch = str.charAt(0);
      if (cdelimSingleChar)
      {
        if (cdelimChar == ch) {
          return true;
        }
      }
      else if (cdelim.indexOf(ch) >= 0) {
        return true;
      }
    }
    return false;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.EnhancedStringTokenizer
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */