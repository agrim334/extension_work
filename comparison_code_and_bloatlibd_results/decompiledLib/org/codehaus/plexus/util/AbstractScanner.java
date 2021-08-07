package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractScanner
  implements Scanner
{
  public static final String[] DEFAULTEXCLUDES = { "**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*", "**/CVS", "**/CVS/**", "**/.cvsignore", "**/RCS", "**/RCS/**", "**/SCCS", "**/SCCS/**", "**/vssver.scc", "**/project.pj", "**/.svn", "**/.svn/**", "**/.arch-ids", "**/.arch-ids/**", "**/.bzr", "**/.bzr/**", "**/.MySCMServerInfo", "**/.DS_Store", "**/.metadata", "**/.metadata/**", "**/.hg", "**/.hgignore", "**/.hg/**", "**/.git", "**/.gitignore", "**/.gitattributes", "**/.git/**", "**/BitKeeper", "**/BitKeeper/**", "**/ChangeSet", "**/ChangeSet/**", "**/_darcs", "**/_darcs/**", "**/.darcsrepo", "**/.darcsrepo/**", "**/-darcs-backup*", "**/.darcs-temp-mail" };
  protected String[] includes;
  private MatchPatterns includesPatterns;
  protected String[] excludes;
  private MatchPatterns excludesPatterns;
  protected boolean isCaseSensitive = true;
  protected Comparator<String> filenameComparator;
  
  public void setCaseSensitive(boolean isCaseSensitive)
  {
    this.isCaseSensitive = isCaseSensitive;
  }
  
  protected static boolean matchPatternStart(String pattern, String str)
  {
    return SelectorUtils.matchPatternStart(pattern, str);
  }
  
  protected static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive)
  {
    return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
  }
  
  protected static boolean matchPath(String pattern, String str)
  {
    return SelectorUtils.matchPath(pattern, str);
  }
  
  protected static boolean matchPath(String pattern, String str, boolean isCaseSensitive)
  {
    return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
  }
  
  public static boolean match(String pattern, String str)
  {
    return SelectorUtils.match(pattern, str);
  }
  
  protected static boolean match(String pattern, String str, boolean isCaseSensitive)
  {
    return SelectorUtils.match(pattern, str, isCaseSensitive);
  }
  
  public void setIncludes(String[] includes)
  {
    if (includes == null)
    {
      this.includes = null;
    }
    else
    {
      List<String> list = new ArrayList(includes.length);
      for (String include : includes) {
        if (include != null) {
          list.add(normalizePattern(include));
        }
      }
      this.includes = ((String[])list.toArray(new String[list.size()]));
    }
  }
  
  public void setExcludes(String[] excludes)
  {
    if (excludes == null)
    {
      this.excludes = null;
    }
    else
    {
      List<String> list = new ArrayList(excludes.length);
      for (String exclude : excludes) {
        if (exclude != null) {
          list.add(normalizePattern(exclude));
        }
      }
      this.excludes = ((String[])list.toArray(new String[list.size()]));
    }
  }
  
  private String normalizePattern(String pattern)
  {
    pattern = pattern.trim();
    if (pattern.startsWith("%regex["))
    {
      if (File.separatorChar == '\\') {
        pattern = StringUtils.replace(pattern, "/", "\\\\");
      } else {
        pattern = StringUtils.replace(pattern, "\\\\", "/");
      }
    }
    else
    {
      pattern = pattern.replace(File.separatorChar == '/' ? '\\' : '/', File.separatorChar);
      if (pattern.endsWith(File.separator)) {
        pattern = pattern + "**";
      }
    }
    return pattern;
  }
  
  protected boolean isIncluded(String name)
  {
    return includesPatterns.matches(name, isCaseSensitive);
  }
  
  protected boolean isIncluded(String name, String[] tokenizedName)
  {
    return includesPatterns.matches(name, tokenizedName, isCaseSensitive);
  }
  
  protected boolean couldHoldIncluded(String name)
  {
    return includesPatterns.matchesPatternStart(name, isCaseSensitive);
  }
  
  protected boolean isExcluded(String name)
  {
    return excludesPatterns.matches(name, isCaseSensitive);
  }
  
  protected boolean isExcluded(String name, String[] tokenizedName)
  {
    return excludesPatterns.matches(name, tokenizedName, isCaseSensitive);
  }
  
  public void addDefaultExcludes()
  {
    int excludesLength = excludes == null ? 0 : excludes.length;
    
    String[] newExcludes = new String[excludesLength + DEFAULTEXCLUDES.length];
    if (excludesLength > 0) {
      System.arraycopy(excludes, 0, newExcludes, 0, excludesLength);
    }
    for (int i = 0; i < DEFAULTEXCLUDES.length; i++) {
      newExcludes[(i + excludesLength)] = DEFAULTEXCLUDES[i].replace('/', File.separatorChar);
    }
    excludes = newExcludes;
  }
  
  protected void setupDefaultFilters()
  {
    if (includes == null)
    {
      includes = new String[1];
      includes[0] = "**";
    }
    if (excludes == null) {
      excludes = new String[0];
    }
  }
  
  protected void setupMatchPatterns()
  {
    includesPatterns = MatchPatterns.from(includes);
    excludesPatterns = MatchPatterns.from(excludes);
  }
  
  public void setFilenameComparator(Comparator<String> filenameComparator)
  {
    this.filenameComparator = filenameComparator;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.AbstractScanner
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */