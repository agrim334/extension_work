package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;

public class UpToDate
  extends Task
  implements Condition
{
  private String property;
  private String value;
  private File sourceFile;
  private File targetFile;
  private List<FileSet> sourceFileSets = new Vector();
  private Union sourceResources = new Union();
  protected Mapper mapperElement = null;
  
  public void setProperty(String property)
  {
    this.property = property;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  private String getValue()
  {
    return value != null ? value : "true";
  }
  
  public void setTargetFile(File file)
  {
    targetFile = file;
  }
  
  public void setSrcfile(File file)
  {
    sourceFile = file;
  }
  
  public void addSrcfiles(FileSet fs)
  {
    sourceFileSets.add(fs);
  }
  
  public Union createSrcResources()
  {
    return sourceResources;
  }
  
  public Mapper createMapper()
    throws BuildException
  {
    if (mapperElement != null) {
      throw new BuildException("Cannot define more than one mapper", getLocation());
    }
    mapperElement = new Mapper(getProject());
    return mapperElement;
  }
  
  public void add(FileNameMapper fileNameMapper)
  {
    createMapper().add(fileNameMapper);
  }
  
  public boolean eval()
  {
    if ((sourceFileSets.isEmpty()) && (sourceResources.isEmpty()) && (sourceFile == null)) {
      throw new BuildException("At least one srcfile or a nested <srcfiles> or <srcresources> element must be set.");
    }
    if (((!sourceFileSets.isEmpty()) || (!sourceResources.isEmpty())) && (sourceFile != null)) {
      throw new BuildException("Cannot specify both the srcfile attribute and a nested <srcfiles> or <srcresources> element.");
    }
    if ((targetFile == null) && (mapperElement == null)) {
      throw new BuildException("The targetfile attribute or a nested mapper element must be set.");
    }
    if ((targetFile != null) && (!targetFile.exists()))
    {
      log("The targetfile \"" + targetFile.getAbsolutePath() + "\" does not exist.", 3);
      
      return false;
    }
    if ((sourceFile != null) && (!sourceFile.exists())) {
      throw new BuildException("%s not found.", new Object[] {sourceFile.getAbsolutePath() });
    }
    boolean upToDate = true;
    SourceFileScanner sfs;
    if (sourceFile != null)
    {
      if (mapperElement == null)
      {
        upToDate = targetFile.lastModified() >= sourceFile.lastModified();
      }
      else
      {
        sfs = new SourceFileScanner(this);
        upToDate = sfs.restrict(new String[] { sourceFile.getAbsolutePath() }, null, null, mapperElement
        
          .getImplementation()).length == 0;
      }
      if (!upToDate) {
        log(sourceFile.getAbsolutePath() + " is newer than (one of) its target(s).", 3);
      }
    }
    for (FileSet fs : sourceFileSets) {
      if (!scanDir(fs.getDir(getProject()), fs
        .getDirectoryScanner(getProject()).getIncludedFiles()))
      {
        upToDate = false;
        break;
      }
    }
    if (upToDate)
    {
      Resource[] r = sourceResources.listResources();
      if (r.length > 0) {
        upToDate = ResourceUtils.selectOutOfDateSources(this, r, 
          getMapper(), getProject()).length == 0;
      }
    }
    return upToDate;
  }
  
  public void execute()
    throws BuildException
  {
    if (property == null) {
      throw new BuildException("property attribute is required.", getLocation());
    }
    boolean upToDate = eval();
    if (upToDate)
    {
      getProject().setNewProperty(property, getValue());
      if (mapperElement == null) {
        log("File \"" + targetFile.getAbsolutePath() + "\" is up-to-date.", 3);
      } else {
        log("All target files are up-to-date.", 3);
      }
    }
  }
  
  protected boolean scanDir(File srcDir, String[] files)
  {
    SourceFileScanner sfs = new SourceFileScanner(this);
    FileNameMapper mapper = getMapper();
    File dir = srcDir;
    if (mapperElement == null) {
      dir = null;
    }
    return sfs.restrict(files, srcDir, dir, mapper).length == 0;
  }
  
  private FileNameMapper getMapper()
  {
    if (mapperElement == null)
    {
      MergingMapper mm = new MergingMapper();
      mm.setTo(targetFile.getAbsolutePath());
      return mm;
    }
    return mapperElement.getImplementation();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.UpToDate
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */