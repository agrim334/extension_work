package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.selectors.AbstractSelectorContainer;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.util.FileUtils;

public class IsFileSelected
  extends AbstractSelectorContainer
  implements Condition
{
  private static final FileUtils FILE_UTILS = ;
  private File file;
  private File baseDir;
  
  public void setFile(File file)
  {
    this.file = file;
  }
  
  public void setBaseDir(File baseDir)
  {
    this.baseDir = baseDir;
  }
  
  public void validate()
  {
    if (selectorCount() != 1) {
      throw new BuildException("Only one selector allowed");
    }
    super.validate();
  }
  
  public boolean eval()
  {
    if (file == null) {
      throw new BuildException("file attribute not set");
    }
    validate();
    File myBaseDir = baseDir;
    if (myBaseDir == null) {
      myBaseDir = getProject().getBaseDir();
    }
    FileSelector f = getSelectors(getProject())[0];
    return f.isSelected(myBaseDir, FILE_UTILS
      .removeLeadingPath(myBaseDir, file), file);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.IsFileSelected
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */