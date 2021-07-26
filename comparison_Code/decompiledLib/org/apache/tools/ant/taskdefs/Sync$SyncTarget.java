package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.selectors.FileSelector;

public class Sync$SyncTarget
  extends AbstractFileSet
{
  private Boolean preserveEmptyDirs;
  
  public void setDir(File dir)
    throws BuildException
  {
    throw new BuildException("preserveintarget doesn't support the dir attribute");
  }
  
  public void setPreserveEmptyDirs(boolean b)
  {
    preserveEmptyDirs = Boolean.valueOf(b);
  }
  
  public Boolean getPreserveEmptyDirs()
  {
    return preserveEmptyDirs;
  }
  
  private FileSet toFileSet(boolean withPatterns)
  {
    FileSet fs = new FileSet();
    fs.setCaseSensitive(isCaseSensitive());
    fs.setFollowSymlinks(isFollowSymlinks());
    fs.setMaxLevelsOfSymlinks(getMaxLevelsOfSymlinks());
    fs.setProject(getProject());
    if (withPatterns)
    {
      PatternSet ps = mergePatterns(getProject());
      fs.appendIncludes(ps.getIncludePatterns(getProject()));
      fs.appendExcludes(ps.getExcludePatterns(getProject()));
      for (FileSelector sel : getSelectors(getProject())) {
        fs.appendSelector(sel);
      }
      fs.setDefaultexcludes(getDefaultexcludes());
    }
    return fs;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Sync.SyncTarget
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */