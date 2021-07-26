package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;

public abstract class MappingSelector
  extends BaseSelector
{
  private static final FileUtils FILE_UTILS = ;
  protected File targetdir = null;
  protected Mapper mapperElement = null;
  protected FileNameMapper map = null;
  protected int granularity = (int)FILE_UTILS.getFileTimestampGranularity();
  
  public void setTargetdir(File targetdir)
  {
    this.targetdir = targetdir;
  }
  
  public Mapper createMapper()
    throws BuildException
  {
    if ((map != null) || (mapperElement != null)) {
      throw new BuildException("Cannot define more than one mapper");
    }
    mapperElement = new Mapper(getProject());
    return mapperElement;
  }
  
  public void addConfigured(FileNameMapper fileNameMapper)
  {
    if ((map != null) || (mapperElement != null)) {
      throw new BuildException("Cannot define more than one mapper");
    }
    map = fileNameMapper;
  }
  
  public void verifySettings()
  {
    if (targetdir == null) {
      setError("The targetdir attribute is required.");
    }
    if (map == null) {
      if (mapperElement == null)
      {
        map = new IdentityMapper();
      }
      else
      {
        map = mapperElement.getImplementation();
        if (map == null) {
          setError("Could not set <mapper> element.");
        }
      }
    }
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    validate();
    
    String[] destfiles = map.mapFileName(filename);
    if (destfiles == null) {
      return false;
    }
    if ((destfiles.length != 1) || (destfiles[0] == null)) {
      throw new BuildException("Invalid destination file results for " + targetdir.getName() + " with filename " + filename);
    }
    String destname = destfiles[0];
    File destfile = FILE_UTILS.resolveFile(targetdir, destname);
    
    return selectionTest(file, destfile);
  }
  
  protected abstract boolean selectionTest(File paramFile1, File paramFile2);
  
  public void setGranularity(int granularity)
  {
    this.granularity = granularity;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.MappingSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */