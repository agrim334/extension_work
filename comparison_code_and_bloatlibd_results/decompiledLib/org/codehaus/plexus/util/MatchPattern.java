package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MatchPattern
{
  private final String source;
  private final String regexPattern;
  private final String separator;
  private final String[] tokenized;
  private final char[][] tokenizedChar;
  
  private MatchPattern(String source, String separator)
  {
    regexPattern = (SelectorUtils.isRegexPrefixedPattern(source) ? source.substring("%regex[".length(), source.length() - "]".length()) : null);
    
    this.source = (SelectorUtils.isAntPrefixedPattern(source) ? source.substring("%ant[".length(), source.length() - "]".length()) : source);
    
    this.separator = separator;
    tokenized = tokenizePathToString(this.source, separator);
    tokenizedChar = new char[tokenized.length][];
    for (int i = 0; i < tokenized.length; i++) {
      tokenizedChar[i] = tokenized[i].toCharArray();
    }
  }
  
  public boolean matchPath(String str, boolean isCaseSensitive)
  {
    if (regexPattern != null) {
      return str.matches(regexPattern);
    }
    return SelectorUtils.matchAntPathPattern(this, str, separator, isCaseSensitive);
  }
  
  boolean matchPath(String str, char[][] strDirs, boolean isCaseSensitive)
  {
    if (regexPattern != null) {
      return str.matches(regexPattern);
    }
    return SelectorUtils.matchAntPathPattern(getTokenizedPathChars(), strDirs, isCaseSensitive);
  }
  
  public boolean matchPatternStart(String str, boolean isCaseSensitive)
  {
    if (regexPattern != null) {
      return true;
    }
    String altStr = str.replace('\\', '/');
    
    return (SelectorUtils.matchAntPathPatternStart(this, str, File.separator, isCaseSensitive)) || (SelectorUtils.matchAntPathPatternStart(this, altStr, "/", isCaseSensitive));
  }
  
  public String[] getTokenizedPathString()
  {
    return tokenized;
  }
  
  public char[][] getTokenizedPathChars()
  {
    return tokenizedChar;
  }
  
  public boolean startsWith(String string)
  {
    return source.startsWith(string);
  }
  
  static String[] tokenizePathToString(String path, String separator)
  {
    List<String> ret = new ArrayList();
    StringTokenizer st = new StringTokenizer(path, separator);
    while (st.hasMoreTokens()) {
      ret.add(st.nextToken());
    }
    return (String[])ret.toArray(new String[ret.size()]);
  }
  
  public static MatchPattern fromString(String source)
  {
    return new MatchPattern(source, File.separator);
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.MatchPattern
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */