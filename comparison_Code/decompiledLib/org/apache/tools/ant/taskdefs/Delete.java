package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.PatternSet.NameEntry;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Sort;
import org.apache.tools.ant.types.resources.comparators.FileSystem;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.resources.comparators.Reverse;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;
import org.apache.tools.ant.util.FileUtils;

public class Delete
  extends MatchingTask
{
  private static final ResourceComparator REVERSE_FILESYSTEM = new Reverse(new FileSystem());
  private static final ResourceSelector EXISTS = new Exists();
  private static FileUtils FILE_UTILS = FileUtils.getFileUtils();
  
  private static class ReverseDirs
    implements ResourceCollection
  {
    private Project project;
    private File basedir;
    private String[] dirs;
    
    ReverseDirs(Project project, File basedir, String[] dirs)
    {
      this.project = project;
      this.basedir = basedir;
      this.dirs = dirs;
      Arrays.sort(this.dirs, Comparator.reverseOrder());
    }
    
    public Iterator<Resource> iterator()
    {
      return new FileResourceIterator(project, basedir, dirs);
    }
    
    public boolean isFilesystemOnly()
    {
      return true;
    }
    
    public int size()
    {
      return dirs.length;
    }
  }
  
  protected File file = null;
  protected File dir = null;
  protected Vector<FileSet> filesets = new Vector();
  protected boolean usedMatchingTask = false;
  protected boolean includeEmpty = false;
  private int verbosity = 3;
  private boolean quiet = false;
  private boolean failonerror = true;
  private boolean deleteOnExit = false;
  private boolean removeNotFollowedSymlinks = false;
  private Resources rcs = null;
  private boolean performGc = Os.isFamily("windows");
  
  public void setFile(File file)
  {
    this.file = file;
  }
  
  public void setDir(File dir)
  {
    this.dir = dir;
    getImplicitFileSet().setDir(dir);
  }
  
  public void setVerbose(boolean verbose)
  {
    if (verbose) {
      verbosity = 2;
    } else {
      verbosity = 3;
    }
  }
  
  public void setQuiet(boolean quiet)
  {
    this.quiet = quiet;
    if (quiet) {
      failonerror = false;
    }
  }
  
  public void setFailOnError(boolean failonerror)
  {
    this.failonerror = failonerror;
  }
  
  public void setDeleteOnExit(boolean deleteOnExit)
  {
    this.deleteOnExit = deleteOnExit;
  }
  
  public void setIncludeEmptyDirs(boolean includeEmpty)
  {
    this.includeEmpty = includeEmpty;
  }
  
  public void setPerformGcOnFailedDelete(boolean b)
  {
    performGc = b;
  }
  
  public void addFileset(FileSet set)
  {
    filesets.addElement(set);
  }
  
  public void add(ResourceCollection rc)
  {
    if (rc == null) {
      return;
    }
    if (rcs == null)
    {
      rcs = new Resources();
      rcs.setCache(true);
    }
    rcs.add(rc);
  }
  
  public PatternSet.NameEntry createInclude()
  {
    usedMatchingTask = true;
    return super.createInclude();
  }
  
  public PatternSet.NameEntry createIncludesFile()
  {
    usedMatchingTask = true;
    return super.createIncludesFile();
  }
  
  public PatternSet.NameEntry createExclude()
  {
    usedMatchingTask = true;
    return super.createExclude();
  }
  
  public PatternSet.NameEntry createExcludesFile()
  {
    usedMatchingTask = true;
    return super.createExcludesFile();
  }
  
  public PatternSet createPatternSet()
  {
    usedMatchingTask = true;
    return super.createPatternSet();
  }
  
  public void setIncludes(String includes)
  {
    usedMatchingTask = true;
    super.setIncludes(includes);
  }
  
  public void setExcludes(String excludes)
  {
    usedMatchingTask = true;
    super.setExcludes(excludes);
  }
  
  public void setDefaultexcludes(boolean useDefaultExcludes)
  {
    usedMatchingTask = true;
    super.setDefaultexcludes(useDefaultExcludes);
  }
  
  public void setIncludesfile(File includesfile)
  {
    usedMatchingTask = true;
    super.setIncludesfile(includesfile);
  }
  
  public void setExcludesfile(File excludesfile)
  {
    usedMatchingTask = true;
    super.setExcludesfile(excludesfile);
  }
  
  public void setCaseSensitive(boolean isCaseSensitive)
  {
    usedMatchingTask = true;
    super.setCaseSensitive(isCaseSensitive);
  }
  
  public void setFollowSymlinks(boolean followSymlinks)
  {
    usedMatchingTask = true;
    super.setFollowSymlinks(followSymlinks);
  }
  
  public void setRemoveNotFollowedSymlinks(boolean b)
  {
    removeNotFollowedSymlinks = b;
  }
  
  public void addSelector(SelectSelector selector)
  {
    usedMatchingTask = true;
    super.addSelector(selector);
  }
  
  public void addAnd(AndSelector selector)
  {
    usedMatchingTask = true;
    super.addAnd(selector);
  }
  
  public void addOr(OrSelector selector)
  {
    usedMatchingTask = true;
    super.addOr(selector);
  }
  
  public void addNot(NotSelector selector)
  {
    usedMatchingTask = true;
    super.addNot(selector);
  }
  
  public void addNone(NoneSelector selector)
  {
    usedMatchingTask = true;
    super.addNone(selector);
  }
  
  public void addMajority(MajoritySelector selector)
  {
    usedMatchingTask = true;
    super.addMajority(selector);
  }
  
  public void addDate(DateSelector selector)
  {
    usedMatchingTask = true;
    super.addDate(selector);
  }
  
  public void addSize(SizeSelector selector)
  {
    usedMatchingTask = true;
    super.addSize(selector);
  }
  
  public void addFilename(FilenameSelector selector)
  {
    usedMatchingTask = true;
    super.addFilename(selector);
  }
  
  public void addCustom(ExtendSelector selector)
  {
    usedMatchingTask = true;
    super.addCustom(selector);
  }
  
  public void addContains(ContainsSelector selector)
  {
    usedMatchingTask = true;
    super.addContains(selector);
  }
  
  public void addPresent(PresentSelector selector)
  {
    usedMatchingTask = true;
    super.addPresent(selector);
  }
  
  public void addDepth(DepthSelector selector)
  {
    usedMatchingTask = true;
    super.addDepth(selector);
  }
  
  public void addDepend(DependSelector selector)
  {
    usedMatchingTask = true;
    super.addDepend(selector);
  }
  
  public void addContainsRegexp(ContainsRegexpSelector selector)
  {
    usedMatchingTask = true;
    super.addContainsRegexp(selector);
  }
  
  public void addModified(ModifiedSelector selector)
  {
    usedMatchingTask = true;
    super.addModified(selector);
  }
  
  public void add(FileSelector selector)
  {
    usedMatchingTask = true;
    super.add(selector);
  }
  
  public void execute()
    throws BuildException
  {
    if (usedMatchingTask) {
      log("DEPRECATED - Use of the implicit FileSet is deprecated.  Use a nested fileset element instead.", 
        quiet ? 3 : verbosity);
    }
    if ((file == null) && (dir == null) && (filesets.isEmpty()) && (rcs == null)) {
      throw new BuildException("At least one of the file or dir attributes, or a nested resource collection, must be set.");
    }
    if ((quiet) && (failonerror)) {
      throw new BuildException("quiet and failonerror cannot both be set to true", getLocation());
    }
    if (file != null) {
      if (file.exists())
      {
        if (file.isDirectory())
        {
          log("Directory " + file.getAbsolutePath() + " cannot be removed using the file attribute.  Use dir instead.", 
          
            quiet ? 3 : verbosity);
        }
        else
        {
          log("Deleting: " + file.getAbsolutePath());
          if (!delete(file)) {
            handle("Unable to delete file " + file.getAbsolutePath());
          }
        }
      }
      else if (isDanglingSymlink(file))
      {
        log("Trying to delete file " + file.getAbsolutePath() + " which looks like a broken symlink.", 
        
          quiet ? 3 : verbosity);
        if (!delete(file)) {
          handle("Unable to delete file " + file.getAbsolutePath());
        }
      }
      else
      {
        log("Could not find file " + file.getAbsolutePath() + " to delete.", 
          quiet ? 3 : verbosity);
      }
    }
    if ((dir != null) && (!usedMatchingTask)) {
      if ((dir.exists()) && (dir.isDirectory()))
      {
        if (verbosity == 3) {
          log("Deleting directory " + dir.getAbsolutePath());
        }
        removeDir(dir);
      }
      else if (isDanglingSymlink(dir))
      {
        log("Trying to delete directory " + dir.getAbsolutePath() + " which looks like a broken symlink.", 
        
          quiet ? 3 : verbosity);
        if (!delete(dir)) {
          handle("Unable to delete directory " + dir.getAbsolutePath());
        }
      }
    }
    Resources resourcesToDelete = new Resources();
    resourcesToDelete.setProject(getProject());
    resourcesToDelete.setCache(true);
    Resources filesetDirs = new Resources();
    filesetDirs.setProject(getProject());
    filesetDirs.setCache(true);
    FileSet implicit = null;
    if ((usedMatchingTask) && (dir != null) && (dir.isDirectory()))
    {
      implicit = getImplicitFileSet();
      implicit.setProject(getProject());
      filesets.add(implicit);
    }
    for (FileSet fs : filesets)
    {
      if (fs.getProject() == null)
      {
        log("Deleting fileset with no project specified; assuming executing project", 3);
        
        fs = (FileSet)fs.clone();
        fs.setProject(getProject());
      }
      final File fsDir = fs.getDir();
      if ((fs.getErrorOnMissingDir()) || ((fsDir != null) && (fsDir.exists())))
      {
        if (fsDir == null) {
          throw new BuildException("File or Resource without directory or file specified");
        }
        if (!fsDir.isDirectory())
        {
          handle("Directory does not exist: " + fsDir);
        }
        else
        {
          DirectoryScanner ds = fs.getDirectoryScanner();
          
          final String[] files = ds.getIncludedFiles();
          resourcesToDelete.add(new ResourceCollection()
          {
            public boolean isFilesystemOnly()
            {
              return true;
            }
            
            public int size()
            {
              return files.length;
            }
            
            public Iterator<Resource> iterator()
            {
              return new FileResourceIterator(this$0.getProject(), fsDir, files);
            }
          });
          if (includeEmpty) {
            filesetDirs.add(new ReverseDirs(getProject(), fsDir, ds
              .getIncludedDirectories()));
          }
          if (removeNotFollowedSymlinks)
          {
            String[] n = ds.getNotFollowedSymlinks();
            if (n.length > 0)
            {
              String[] links = new String[n.length];
              System.arraycopy(n, 0, links, 0, n.length);
              Arrays.sort(links, Comparator.reverseOrder());
              for (String link : links)
              {
                Path filePath = Paths.get(link, new String[0]);
                if (Files.isSymbolicLink(filePath))
                {
                  boolean deleted = filePath.toFile().delete();
                  if (!deleted) {
                    handle("Could not delete symbolic link at " + filePath);
                  }
                }
              }
            }
          }
        }
      }
    }
    resourcesToDelete.add(filesetDirs);
    Object exists;
    if (rcs != null)
    {
      exists = new Restrict();
      ((Restrict)exists).add(EXISTS);
      ((Restrict)exists).add(rcs);
      Sort s = new Sort();
      s.add(REVERSE_FILESYSTEM);
      s.add((ResourceCollection)exists);
      resourcesToDelete.add(s);
    }
    try
    {
      if (resourcesToDelete.isFilesystemOnly()) {
        for (exists = resourcesToDelete.iterator(); ((Iterator)exists).hasNext();)
        {
          Resource r = (Resource)((Iterator)exists).next();
          
          File f = ((FileProvider)r.as(FileProvider.class)).getFile();
          if (f.exists()) {
            if ((!f.isDirectory()) || (f.list().length == 0))
            {
              log("Deleting " + f, verbosity);
              if ((!delete(f)) && (failonerror)) {
                handle("Unable to delete " + (
                  f.isDirectory() ? "directory " : "file ") + f);
              }
            }
          }
        }
      } else {
        handle(getTaskName() + " handles only filesystem resources");
      }
    }
    catch (Exception e)
    {
      handle(e);
    }
    finally
    {
      if (implicit != null) {
        filesets.remove(implicit);
      }
    }
  }
  
  private void handle(String msg)
  {
    handle(new BuildException(msg));
  }
  
  private void handle(Exception e)
  {
    if (failonerror) {
      throw ((e instanceof BuildException) ? (BuildException)e : new BuildException(e));
    }
    log(e, quiet ? 3 : verbosity);
  }
  
  private boolean delete(File f)
  {
    if (!FILE_UTILS.tryHardToDelete(f, performGc))
    {
      if (deleteOnExit)
      {
        int level = quiet ? 3 : 2;
        log("Failed to delete " + f + ", calling deleteOnExit. This attempts to delete the file when the Ant jvm has exited and might not succeed.", level);
        
        f.deleteOnExit();
        return true;
      }
      return false;
    }
    return true;
  }
  
  protected void removeDir(File d)
  {
    String[] list = d.list();
    if (list == null) {
      list = new String[0];
    }
    for (String s : list)
    {
      File f = new File(d, s);
      if (f.isDirectory())
      {
        removeDir(f);
      }
      else
      {
        log("Deleting " + f.getAbsolutePath(), quiet ? 3 : verbosity);
        if (!delete(f)) {
          handle("Unable to delete file " + f.getAbsolutePath());
        }
      }
    }
    log("Deleting directory " + d.getAbsolutePath(), verbosity);
    if (!delete(d)) {
      handle("Unable to delete directory " + d.getAbsolutePath());
    }
  }
  
  protected void removeFiles(File d, String[] files, String[] dirs)
  {
    if (files.length > 0)
    {
      log("Deleting " + files.length + " files from " + d
        .getAbsolutePath(), quiet ? 3 : verbosity);
      for (String filename : files)
      {
        File f = new File(d, filename);
        log("Deleting " + f.getAbsolutePath(), 
          quiet ? 3 : verbosity);
        if (!delete(f)) {
          handle("Unable to delete file " + f.getAbsolutePath());
        }
      }
    }
    if ((dirs.length > 0) && (includeEmpty))
    {
      int dirCount = 0;
      for (int j = dirs.length - 1; j >= 0; j--)
      {
        File currDir = new File(d, dirs[j]);
        String[] dirFiles = currDir.list();
        if ((dirFiles == null) || (dirFiles.length == 0))
        {
          log("Deleting " + currDir.getAbsolutePath(), 
            quiet ? 3 : verbosity);
          if (!delete(currDir)) {
            handle("Unable to delete directory " + currDir.getAbsolutePath());
          } else {
            dirCount++;
          }
        }
      }
      if (dirCount > 0) {
        log("Deleted " + dirCount + " director" + (
          dirCount == 1 ? "y" : "ies") + " form " + d
          .getAbsolutePath(), 
          quiet ? 3 : verbosity);
      }
    }
  }
  
  private boolean isDanglingSymlink(File f)
  {
    if (!Files.isSymbolicLink(f.toPath())) {
      return false;
    }
    boolean targetFileExists = Files.exists(f.toPath(), new LinkOption[0]);
    return !targetFileExists;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Delete
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */