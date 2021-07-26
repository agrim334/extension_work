package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Enumeration;
import java.util.StringTokenizer;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.PatternSet.NameEntry;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.DifferentSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SelectorContainer;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.TypeSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;

public abstract class MatchingTask
  extends Task
  implements SelectorContainer
{
  protected FileSet fileset = new FileSet();
  
  public void setProject(Project project)
  {
    super.setProject(project);
    fileset.setProject(project);
  }
  
  public PatternSet.NameEntry createInclude()
  {
    return fileset.createInclude();
  }
  
  public PatternSet.NameEntry createIncludesFile()
  {
    return fileset.createIncludesFile();
  }
  
  public PatternSet.NameEntry createExclude()
  {
    return fileset.createExclude();
  }
  
  public PatternSet.NameEntry createExcludesFile()
  {
    return fileset.createExcludesFile();
  }
  
  public PatternSet createPatternSet()
  {
    return fileset.createPatternSet();
  }
  
  public void setIncludes(String includes)
  {
    fileset.setIncludes(includes);
  }
  
  public void XsetItems(String itemString)
  {
    log("The items attribute is deprecated. Please use the includes attribute.", 1);
    if ((itemString == null) || ("*".equals(itemString)) || 
      (".".equals(itemString)))
    {
      createInclude().setName("**");
    }
    else
    {
      StringTokenizer tok = new StringTokenizer(itemString, ", ");
      while (tok.hasMoreTokens())
      {
        String pattern = tok.nextToken().trim();
        if (!pattern.isEmpty()) {
          createInclude().setName(pattern + "/**");
        }
      }
    }
  }
  
  public void setExcludes(String excludes)
  {
    fileset.setExcludes(excludes);
  }
  
  public void XsetIgnore(String ignoreString)
  {
    log("The ignore attribute is deprecated.Please use the excludes attribute.", 1);
    if ((ignoreString != null) && (!ignoreString.isEmpty()))
    {
      StringTokenizer tok = new StringTokenizer(ignoreString, ", ", false);
      while (tok.hasMoreTokens()) {
        createExclude().setName("**/" + tok.nextToken().trim() + "/**");
      }
    }
  }
  
  public void setDefaultexcludes(boolean useDefaultExcludes)
  {
    fileset.setDefaultexcludes(useDefaultExcludes);
  }
  
  protected DirectoryScanner getDirectoryScanner(File baseDir)
  {
    fileset.setDir(baseDir);
    return fileset.getDirectoryScanner(getProject());
  }
  
  public void setIncludesfile(File includesfile)
  {
    fileset.setIncludesfile(includesfile);
  }
  
  public void setExcludesfile(File excludesfile)
  {
    fileset.setExcludesfile(excludesfile);
  }
  
  public void setCaseSensitive(boolean isCaseSensitive)
  {
    fileset.setCaseSensitive(isCaseSensitive);
  }
  
  public void setFollowSymlinks(boolean followSymlinks)
  {
    fileset.setFollowSymlinks(followSymlinks);
  }
  
  public boolean hasSelectors()
  {
    return fileset.hasSelectors();
  }
  
  public int selectorCount()
  {
    return fileset.selectorCount();
  }
  
  public FileSelector[] getSelectors(Project p)
  {
    return fileset.getSelectors(p);
  }
  
  public Enumeration<FileSelector> selectorElements()
  {
    return fileset.selectorElements();
  }
  
  public void appendSelector(FileSelector selector)
  {
    fileset.appendSelector(selector);
  }
  
  public void addSelector(SelectSelector selector)
  {
    fileset.addSelector(selector);
  }
  
  public void addAnd(AndSelector selector)
  {
    fileset.addAnd(selector);
  }
  
  public void addOr(OrSelector selector)
  {
    fileset.addOr(selector);
  }
  
  public void addNot(NotSelector selector)
  {
    fileset.addNot(selector);
  }
  
  public void addNone(NoneSelector selector)
  {
    fileset.addNone(selector);
  }
  
  public void addMajority(MajoritySelector selector)
  {
    fileset.addMajority(selector);
  }
  
  public void addDate(DateSelector selector)
  {
    fileset.addDate(selector);
  }
  
  public void addSize(SizeSelector selector)
  {
    fileset.addSize(selector);
  }
  
  public void addFilename(FilenameSelector selector)
  {
    fileset.addFilename(selector);
  }
  
  public void addCustom(ExtendSelector selector)
  {
    fileset.addCustom(selector);
  }
  
  public void addContains(ContainsSelector selector)
  {
    fileset.addContains(selector);
  }
  
  public void addPresent(PresentSelector selector)
  {
    fileset.addPresent(selector);
  }
  
  public void addDepth(DepthSelector selector)
  {
    fileset.addDepth(selector);
  }
  
  public void addDepend(DependSelector selector)
  {
    fileset.addDepend(selector);
  }
  
  public void addContainsRegexp(ContainsRegexpSelector selector)
  {
    fileset.addContainsRegexp(selector);
  }
  
  public void addDifferent(DifferentSelector selector)
  {
    fileset.addDifferent(selector);
  }
  
  public void addType(TypeSelector selector)
  {
    fileset.addType(selector);
  }
  
  public void addModified(ModifiedSelector selector)
  {
    fileset.addModified(selector);
  }
  
  public void add(FileSelector selector)
  {
    fileset.add(selector);
  }
  
  protected final FileSet getImplicitFileSet()
  {
    return fileset;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MatchingTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */