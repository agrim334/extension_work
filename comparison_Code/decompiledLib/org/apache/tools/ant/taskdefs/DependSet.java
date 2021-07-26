package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.resources.comparators.Reverse;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.selectors.Not;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.util.StreamUtils;

public class DependSet
  extends MatchingTask
{
  private static final ResourceSelector NOT_EXISTS = new Not(new Exists());
  private static final ResourceComparator DATE = new org.apache.tools.ant.types.resources.comparators.Date();
  private static final ResourceComparator REVERSE_DATE = new Reverse(DATE);
  
  private static final class NonExistent
    extends Restrict
  {
    private NonExistent(ResourceCollection rc)
    {
      super.add(rc);
      super.add(DependSet.NOT_EXISTS);
    }
  }
  
  private static final class HideMissingBasedir
    implements ResourceCollection
  {
    private FileSet fs;
    
    private HideMissingBasedir(FileSet fs)
    {
      this.fs = fs;
    }
    
    public Iterator<Resource> iterator()
    {
      return basedirExists() ? fs.iterator() : Resources.EMPTY_ITERATOR;
    }
    
    public int size()
    {
      return basedirExists() ? fs.size() : 0;
    }
    
    public boolean isFilesystemOnly()
    {
      return true;
    }
    
    private boolean basedirExists()
    {
      File basedir = fs.getDir();
      
      return (basedir == null) || (basedir.exists());
    }
  }
  
  private Union sources = null;
  private Path targets = null;
  private boolean verbose;
  
  public synchronized Union createSources()
  {
    sources = (sources == null ? new Union() : sources);
    return sources;
  }
  
  public void addSrcfileset(FileSet fs)
  {
    createSources().add(fs);
  }
  
  public void addSrcfilelist(FileList fl)
  {
    createSources().add(fl);
  }
  
  public synchronized Path createTargets()
  {
    targets = (targets == null ? new Path(getProject()) : targets);
    return targets;
  }
  
  public void addTargetfileset(FileSet fs)
  {
    createTargets().add(new HideMissingBasedir(fs, null));
  }
  
  public void addTargetfilelist(FileList fl)
  {
    createTargets().add(fl);
  }
  
  public void setVerbose(boolean b)
  {
    verbose = b;
  }
  
  public void execute()
    throws BuildException
  {
    if (sources == null) {
      throw new BuildException("At least one set of source resources must be specified");
    }
    if (targets == null) {
      throw new BuildException("At least one set of target files must be specified");
    }
    if ((!sources.isEmpty()) && (!targets.isEmpty()) && (!uptodate(sources, targets)))
    {
      log("Deleting all target files.", 3);
      if (verbose) {
        for (String t : targets.list()) {
          log("Deleting " + t);
        }
      }
      Delete delete = new Delete();
      delete.bindToOwner(this);
      delete.add(targets);
      delete.perform();
    }
  }
  
  private boolean uptodate(ResourceCollection src, ResourceCollection target)
  {
    org.apache.tools.ant.types.resources.selectors.Date datesel = new org.apache.tools.ant.types.resources.selectors.Date();
    
    datesel.setMillis(System.currentTimeMillis());
    datesel.setWhen(TimeComparison.AFTER);
    
    datesel.setGranularity(0L);
    logFuture(targets, datesel);
    
    NonExistent missingTargets = new NonExistent(targets, null);
    int neTargets = missingTargets.size();
    if (neTargets > 0)
    {
      log(neTargets + " nonexistent targets", 3);
      logMissing(missingTargets, "target");
      return false;
    }
    Resource oldestTarget = getOldest(targets);
    logWithModificationTime(oldestTarget, "oldest target file");
    
    logFuture(sources, datesel);
    
    NonExistent missingSources = new NonExistent(sources, null);
    int neSources = missingSources.size();
    if (neSources > 0)
    {
      log(neSources + " nonexistent sources", 3);
      logMissing(missingSources, "source");
      return false;
    }
    Resource newestSource = getNewest(sources);
    logWithModificationTime(newestSource, "newest source");
    return oldestTarget.getLastModified() >= newestSource.getLastModified();
  }
  
  private void logFuture(ResourceCollection rc, ResourceSelector rsel)
  {
    Restrict r = new Restrict();
    r.add(rsel);
    r.add(rc);
    for (Resource res : r) {
      log("Warning: " + res + " modified in the future.", 1);
    }
  }
  
  private Resource getXest(ResourceCollection rc, ResourceComparator c)
  {
    return (Resource)StreamUtils.iteratorAsStream(rc.iterator()).max(c).orElse(null);
  }
  
  private Resource getOldest(ResourceCollection rc)
  {
    return getXest(rc, REVERSE_DATE);
  }
  
  private Resource getNewest(ResourceCollection rc)
  {
    return getXest(rc, DATE);
  }
  
  private void logWithModificationTime(Resource r, String what)
  {
    log(r.toLongString() + " is " + what + ", modified at " + new java.util.Date(r
      .getLastModified()), 
      verbose ? 2 : 3);
  }
  
  private void logMissing(ResourceCollection missing, String what)
  {
    if (verbose) {
      for (Resource r : missing) {
        log("Expected " + what + " " + r.toLongString() + " is missing.");
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.DependSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */