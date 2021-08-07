package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class SelectorUtils
{
  public static final String PATTERN_HANDLER_PREFIX = "[";
  public static final String PATTERN_HANDLER_SUFFIX = "]";
  public static final String REGEX_HANDLER_PREFIX = "%regex[";
  public static final String ANT_HANDLER_PREFIX = "%ant[";
  private static SelectorUtils instance = new SelectorUtils();
  
  public static SelectorUtils getInstance()
  {
    return instance;
  }
  
  public static boolean matchPatternStart(String pattern, String str)
  {
    return matchPatternStart(pattern, str, true);
  }
  
  public static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive)
  {
    if (isRegexPrefixedPattern(pattern)) {
      return true;
    }
    if (isAntPrefixedPattern(pattern)) {
      pattern = pattern.substring("%ant[".length(), pattern.length() - "]".length());
    }
    String altStr = str.replace('\\', '/');
    
    return (matchAntPathPatternStart(pattern, str, File.separator, isCaseSensitive)) || (matchAntPathPatternStart(pattern, altStr, "/", isCaseSensitive));
  }
  
  static boolean isAntPrefixedPattern(String pattern)
  {
    return (pattern.length() > "%ant[".length() + "]".length() + 1) && (pattern.startsWith("%ant[")) && (pattern.endsWith("]"));
  }
  
  static boolean matchAntPathPatternStart(MatchPattern pattern, String str, String separator, boolean isCaseSensitive)
  {
    if (separatorPatternStartSlashMismatch(pattern, str, separator)) {
      return false;
    }
    return matchAntPathPatternStart(pattern.getTokenizedPathString(), str, separator, isCaseSensitive);
  }
  
  static boolean matchAntPathPatternStart(String pattern, String str, String separator, boolean isCaseSensitive)
  {
    if (separatorPatternStartSlashMismatch(pattern, str, separator)) {
      return false;
    }
    String[] patDirs = tokenizePathToString(pattern, separator);
    return matchAntPathPatternStart(patDirs, str, separator, isCaseSensitive);
  }
  
  private static boolean separatorPatternStartSlashMismatch(String pattern, String str, String separator)
  {
    return str.startsWith(separator) != pattern.startsWith(separator);
  }
  
  private static boolean separatorPatternStartSlashMismatch(MatchPattern matchPattern, String str, String separator)
  {
    return str.startsWith(separator) != matchPattern.startsWith(separator);
  }
  
  static boolean matchAntPathPatternStart(String[] patDirs, String str, String separator, boolean isCaseSensitive)
  {
    String[] strDirs = tokenizePathToString(str, separator);
    
    int patIdxStart = 0;
    int patIdxEnd = patDirs.length - 1;
    int strIdxStart = 0;
    int strIdxEnd = strDirs.length - 1;
    while ((patIdxStart <= patIdxEnd) && (strIdxStart <= strIdxEnd))
    {
      String patDir = patDirs[patIdxStart];
      if (patDir.equals("**")) {
        break;
      }
      if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
        return false;
      }
      patIdxStart++;
      strIdxStart++;
    }
    return (strIdxStart > strIdxEnd) || (patIdxStart <= patIdxEnd);
  }
  
  public static boolean matchPath(String pattern, String str)
  {
    return matchPath(pattern, str, true);
  }
  
  public static boolean matchPath(String pattern, String str, boolean isCaseSensitive)
  {
    return matchPath(pattern, str, File.separator, isCaseSensitive);
  }
  
  public static boolean matchPath(String pattern, String str, String separator, boolean isCaseSensitive)
  {
    if (isRegexPrefixedPattern(pattern))
    {
      pattern = pattern.substring("%regex[".length(), pattern.length() - "]".length());
      
      return str.matches(pattern);
    }
    if (isAntPrefixedPattern(pattern)) {
      pattern = pattern.substring("%ant[".length(), pattern.length() - "]".length());
    }
    return matchAntPathPattern(pattern, str, separator, isCaseSensitive);
  }
  
  static boolean isRegexPrefixedPattern(String pattern)
  {
    return (pattern.length() > "%regex[".length() + "]".length() + 1) && (pattern.startsWith("%regex[")) && (pattern.endsWith("]"));
  }
  
  static boolean matchAntPathPattern(MatchPattern matchPattern, String str, String separator, boolean isCaseSensitive)
  {
    if (separatorPatternStartSlashMismatch(matchPattern, str, separator)) {
      return false;
    }
    String[] patDirs = matchPattern.getTokenizedPathString();
    String[] strDirs = tokenizePathToString(str, separator);
    return matchAntPathPattern(patDirs, strDirs, isCaseSensitive);
  }
  
  static boolean matchAntPathPattern(String pattern, String str, String separator, boolean isCaseSensitive)
  {
    if (separatorPatternStartSlashMismatch(pattern, str, separator)) {
      return false;
    }
    String[] patDirs = tokenizePathToString(pattern, separator);
    String[] strDirs = tokenizePathToString(str, separator);
    return matchAntPathPattern(patDirs, strDirs, isCaseSensitive);
  }
  
  static boolean matchAntPathPattern(String[] patDirs, String[] strDirs, boolean isCaseSensitive)
  {
    int patIdxStart = 0;
    int patIdxEnd = patDirs.length - 1;
    int strIdxStart = 0;
    int strIdxEnd = strDirs.length - 1;
    while ((patIdxStart <= patIdxEnd) && (strIdxStart <= strIdxEnd))
    {
      String patDir = patDirs[patIdxStart];
      if (patDir.equals("**")) {
        break;
      }
      if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
        return false;
      }
      patIdxStart++;
      strIdxStart++;
    }
    if (strIdxStart > strIdxEnd)
    {
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (!patDirs[i].equals("**")) {
          return false;
        }
      }
      return true;
    }
    if (patIdxStart > patIdxEnd) {
      return false;
    }
    while ((patIdxStart <= patIdxEnd) && (strIdxStart <= strIdxEnd))
    {
      String patDir = patDirs[patIdxEnd];
      if (patDir.equals("**")) {
        break;
      }
      if (!match(patDir, strDirs[strIdxEnd], isCaseSensitive)) {
        return false;
      }
      patIdxEnd--;
      strIdxEnd--;
    }
    if (strIdxStart > strIdxEnd)
    {
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (!patDirs[i].equals("**")) {
          return false;
        }
      }
      return true;
    }
    while ((patIdxStart != patIdxEnd) && (strIdxStart <= strIdxEnd))
    {
      int patIdxTmp = -1;
      for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
        if (patDirs[i].equals("**"))
        {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == patIdxStart + 1)
      {
        patIdxStart++;
      }
      else
      {
        int patLength = patIdxTmp - patIdxStart - 1;
        int strLength = strIdxEnd - strIdxStart + 1;
        int foundIdx = -1;
        label376:
        for (int i = 0; i <= strLength - patLength; i++)
        {
          for (int j = 0; j < patLength; j++)
          {
            String subPat = patDirs[(patIdxStart + j + 1)];
            String subStr = strDirs[(strIdxStart + i + j)];
            if (!match(subPat, subStr, isCaseSensitive)) {
              break label376;
            }
          }
          foundIdx = strIdxStart + i;
          break;
        }
        if (foundIdx == -1) {
          return false;
        }
        patIdxStart = patIdxTmp;
        strIdxStart = foundIdx + patLength;
      }
    }
    for (int i = patIdxStart; i <= patIdxEnd; i++) {
      if (!patDirs[i].equals("**")) {
        return false;
      }
    }
    return true;
  }
  
  static boolean matchAntPathPattern(char[][] patDirs, char[][] strDirs, boolean isCaseSensitive)
  {
    int patIdxStart = 0;
    int patIdxEnd = patDirs.length - 1;
    int strIdxStart = 0;
    int strIdxEnd = strDirs.length - 1;
    while ((patIdxStart <= patIdxEnd) && (strIdxStart <= strIdxEnd))
    {
      char[] patDir = patDirs[patIdxStart];
      if (isDoubleStar(patDir)) {
        break;
      }
      if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
        return false;
      }
      patIdxStart++;
      strIdxStart++;
    }
    if (strIdxStart > strIdxEnd)
    {
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (!isDoubleStar(patDirs[i])) {
          return false;
        }
      }
      return true;
    }
    if (patIdxStart > patIdxEnd) {
      return false;
    }
    while ((patIdxStart <= patIdxEnd) && (strIdxStart <= strIdxEnd))
    {
      char[] patDir = patDirs[patIdxEnd];
      if (isDoubleStar(patDir)) {
        break;
      }
      if (!match(patDir, strDirs[strIdxEnd], isCaseSensitive)) {
        return false;
      }
      patIdxEnd--;
      strIdxEnd--;
    }
    if (strIdxStart > strIdxEnd)
    {
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (!isDoubleStar(patDirs[i])) {
          return false;
        }
      }
      return true;
    }
    while ((patIdxStart != patIdxEnd) && (strIdxStart <= strIdxEnd))
    {
      int patIdxTmp = -1;
      for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
        if (isDoubleStar(patDirs[i]))
        {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == patIdxStart + 1)
      {
        patIdxStart++;
      }
      else
      {
        int patLength = patIdxTmp - patIdxStart - 1;
        int strLength = strIdxEnd - strIdxStart + 1;
        int foundIdx = -1;
        label366:
        for (int i = 0; i <= strLength - patLength; i++)
        {
          for (int j = 0; j < patLength; j++)
          {
            char[] subPat = patDirs[(patIdxStart + j + 1)];
            char[] subStr = strDirs[(strIdxStart + i + j)];
            if (!match(subPat, subStr, isCaseSensitive)) {
              break label366;
            }
          }
          foundIdx = strIdxStart + i;
          break;
        }
        if (foundIdx == -1) {
          return false;
        }
        patIdxStart = patIdxTmp;
        strIdxStart = foundIdx + patLength;
      }
    }
    for (int i = patIdxStart; i <= patIdxEnd; i++) {
      if (!isDoubleStar(patDirs[i])) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean isDoubleStar(char[] patDir)
  {
    return (patDir != null) && (patDir.length == 2) && (patDir[0] == '*') && (patDir[1] == '*');
  }
  
  public static boolean match(String pattern, String str)
  {
    return match(pattern, str, true);
  }
  
  public static boolean match(String pattern, String str, boolean isCaseSensitive)
  {
    char[] patArr = pattern.toCharArray();
    char[] strArr = str.toCharArray();
    return match(patArr, strArr, isCaseSensitive);
  }
  
  public static boolean match(char[] patArr, char[] strArr, boolean isCaseSensitive)
  {
    int patIdxStart = 0;
    int patIdxEnd = patArr.length - 1;
    int strIdxStart = 0;
    int strIdxEnd = strArr.length - 1;
    
    boolean containsStar = false;
    for (char aPatArr : patArr) {
      if (aPatArr == '*')
      {
        containsStar = true;
        break;
      }
    }
    if (!containsStar)
    {
      if (patIdxEnd != strIdxEnd) {
        return false;
      }
      for (int i = 0; i <= patIdxEnd; i++)
      {
        char ch = patArr[i];
        if ((ch != '?') && (!equals(ch, strArr[i], isCaseSensitive))) {
          return false;
        }
      }
      return true;
    }
    if (patIdxEnd == 0) {
      return true;
    }
    char ch;
    while (((ch = patArr[patIdxStart]) != '*') && (strIdxStart <= strIdxEnd))
    {
      if ((ch != '?') && (!equals(ch, strArr[strIdxStart], isCaseSensitive))) {
        return false;
      }
      patIdxStart++;
      strIdxStart++;
    }
    if (strIdxStart > strIdxEnd)
    {
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (patArr[i] != '*') {
          return false;
        }
      }
      return true;
    }
    while (((ch = patArr[patIdxEnd]) != '*') && (strIdxStart <= strIdxEnd))
    {
      if ((ch != '?') && (!equals(ch, strArr[strIdxEnd], isCaseSensitive))) {
        return false;
      }
      patIdxEnd--;
      strIdxEnd--;
    }
    if (strIdxStart > strIdxEnd)
    {
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (patArr[i] != '*') {
          return false;
        }
      }
      return true;
    }
    while ((patIdxStart != patIdxEnd) && (strIdxStart <= strIdxEnd))
    {
      int patIdxTmp = -1;
      for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
        if (patArr[i] == '*')
        {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == patIdxStart + 1)
      {
        patIdxStart++;
      }
      else
      {
        int patLength = patIdxTmp - patIdxStart - 1;
        int strLength = strIdxEnd - strIdxStart + 1;
        int foundIdx = -1;
        label464:
        for (int i = 0; i <= strLength - patLength; i++)
        {
          for (int j = 0; j < patLength; j++)
          {
            ch = patArr[(patIdxStart + j + 1)];
            if ((ch != '?') && (!equals(ch, strArr[(strIdxStart + i + j)], isCaseSensitive))) {
              break label464;
            }
          }
          foundIdx = strIdxStart + i;
          break;
        }
        if (foundIdx == -1) {
          return false;
        }
        patIdxStart = patIdxTmp;
        strIdxStart = foundIdx + patLength;
      }
    }
    for (int i = patIdxStart; i <= patIdxEnd; i++) {
      if (patArr[i] != '*') {
        return false;
      }
    }
    return true;
  }
  
  private static boolean equals(char c1, char c2, boolean isCaseSensitive)
  {
    if (c1 == c2) {
      return true;
    }
    if (!isCaseSensitive) {
      if ((Character.toUpperCase(c1) == Character.toUpperCase(c2)) || (Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
        return true;
      }
    }
    return false;
  }
  
  private static String[] tokenizePathToString(String path, String separator)
  {
    List<String> ret = new ArrayList();
    StringTokenizer st = new StringTokenizer(path, separator);
    while (st.hasMoreTokens()) {
      ret.add(st.nextToken());
    }
    return (String[])ret.toArray(new String[ret.size()]);
  }
  
  public static boolean isOutOfDate(File src, File target, int granularity)
  {
    if (!src.exists()) {
      return false;
    }
    if (!target.exists()) {
      return true;
    }
    if (src.lastModified() - granularity > target.lastModified()) {
      return true;
    }
    return false;
  }
  
  public static String removeWhitespace(String input)
  {
    StringBuilder result = new StringBuilder();
    if (input != null)
    {
      StringTokenizer st = new StringTokenizer(input);
      while (st.hasMoreTokens()) {
        result.append(st.nextToken());
      }
    }
    return result.toString();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.SelectorUtils
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */