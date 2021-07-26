package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;

public class PathConvert
  extends Task
{
  private static boolean onWindows = Os.isFamily("dos");
  private Resources path = null;
  private Reference refid = null;
  private String targetOS = null;
  private boolean targetWindows = false;
  private boolean setonempty = true;
  private String property = null;
  private List<MapEntry> prefixMap = new Vector();
  private String pathSep = null;
  private String dirSep = null;
  private Mapper mapper = null;
  private boolean preserveDuplicates;
  
  public class MapEntry
  {
    private String from = null;
    private String to = null;
    
    public MapEntry() {}
    
    public void setFrom(String from)
    {
      this.from = from;
    }
    
    public void setTo(String to)
    {
      this.to = to;
    }
    
    public String apply(String elem)
    {
      if ((from == null) || (to == null)) {
        throw new BuildException("Both 'from' and 'to' must be set in a map entry");
      }
      String cmpElem = PathConvert.onWindows ? elem.toLowerCase().replace('\\', '/') : elem;
      
      String cmpFrom = PathConvert.onWindows ? from.toLowerCase().replace('\\', '/') : from;
      
      return cmpElem.startsWith(cmpFrom) ? 
        to + elem.substring(from.length()) : elem;
    }
  }
  
  public static class TargetOs
    extends EnumeratedAttribute
  {
    public String[] getValues()
    {
      return new String[] { "windows", "unix", "netware", "os/2", "tandem" };
    }
  }
  
  public Path createPath()
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    Path result = new Path(getProject());
    add(result);
    return result;
  }
  
  public void add(ResourceCollection rc)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    getPath().add(rc);
  }
  
  private synchronized Resources getPath()
  {
    if (path == null)
    {
      path = new Resources(getProject());
      path.setCache(true);
    }
    return path;
  }
  
  public MapEntry createMap()
  {
    MapEntry entry = new MapEntry();
    prefixMap.add(entry);
    return entry;
  }
  
  @Deprecated
  public void setTargetos(String target)
  {
    TargetOs to = new TargetOs();
    to.setValue(target);
    setTargetos(to);
  }
  
  public void setTargetos(TargetOs target)
  {
    targetOS = target.getValue();
    
    targetWindows = ((!"unix".equals(targetOS)) && (!"tandem".equals(targetOS)));
  }
  
  public void setSetonempty(boolean setonempty)
  {
    this.setonempty = setonempty;
  }
  
  public void setProperty(String p)
  {
    property = p;
  }
  
  public void setRefid(Reference r)
  {
    if (path != null) {
      throw noChildrenAllowed();
    }
    refid = r;
  }
  
  public void setPathSep(String sep)
  {
    pathSep = sep;
  }
  
  public void setDirSep(String sep)
  {
    dirSep = sep;
  }
  
  public void setPreserveDuplicates(boolean preserveDuplicates)
  {
    this.preserveDuplicates = preserveDuplicates;
  }
  
  public boolean isPreserveDuplicates()
  {
    return preserveDuplicates;
  }
  
  public boolean isReference()
  {
    return refid != null;
  }
  
  public void execute()
    throws BuildException
  {
    Resources savedPath = path;
    String savedPathSep = pathSep;
    String savedDirSep = dirSep;
    try
    {
      if (isReference())
      {
        Object o = refid.getReferencedObject(getProject());
        if (!(o instanceof ResourceCollection)) {
          throw new BuildException("refid '%s' does not refer to a resource collection.", new Object[] {refid.getRefId() });
        }
        getPath().add((ResourceCollection)o);
      }
      validateSetup();
      
      String fromDirSep = onWindows ? "\\" : "/";
      
      StringBuilder rslt = new StringBuilder();
      
      ResourceCollection resources = isPreserveDuplicates() ? path : new Union(path);
      List<String> ret = new ArrayList();
      FileNameMapper mapperImpl = mapper == null ? new IdentityMapper() : mapper.getImplementation();
      Resource r;
      for (Iterator localIterator = resources.iterator(); localIterator.hasNext(); goto 217)
      {
        r = (Resource)localIterator.next();
        String[] mapped = mapperImpl.mapFileName(String.valueOf(r));
        int m = 0;
        if ((mapped != null) && (m < mapped.length))
        {
          ret.add(mapped[m]);m++;
        }
      }
      boolean first = true;
      for (String string : ret)
      {
        String elem = mapElement(string);
        if (!first) {
          rslt.append(pathSep);
        }
        first = false;
        
        StringTokenizer stDirectory = new StringTokenizer(elem, fromDirSep, true);
        while (stDirectory.hasMoreTokens())
        {
          String token = stDirectory.nextToken();
          rslt.append(fromDirSep.equals(token) ? dirSep : token);
        }
      }
      if ((setonempty) || (rslt.length() > 0))
      {
        String value = rslt.toString();
        if (property == null)
        {
          log(value);
        }
        else
        {
          log("Set property " + property + " = " + value, 3);
          getProject().setNewProperty(property, value);
        }
      }
    }
    finally
    {
      path = savedPath;
      dirSep = savedDirSep;
      pathSep = savedPathSep;
    }
  }
  
  private String mapElement(String elem)
  {
    for (MapEntry entry : prefixMap)
    {
      String newElem = entry.apply(elem);
      if (newElem != elem) {
        return newElem;
      }
    }
    return elem;
  }
  
  public void addMapper(Mapper mapper)
  {
    if (this.mapper != null) {
      throw new BuildException("Cannot define more than one mapper");
    }
    this.mapper = mapper;
  }
  
  public void add(FileNameMapper fileNameMapper)
  {
    Mapper m = new Mapper(getProject());
    m.add(fileNameMapper);
    addMapper(m);
  }
  
  private void validateSetup()
    throws BuildException
  {
    if (path == null) {
      throw new BuildException("You must specify a path to convert");
    }
    String dsep = File.separator;
    String psep = File.pathSeparator;
    if (targetOS != null)
    {
      psep = targetWindows ? ";" : ":";
      dsep = targetWindows ? "\\" : "/";
    }
    if (pathSep != null) {
      psep = pathSep;
    }
    if (dirSep != null) {
      dsep = dirSep;
    }
    pathSep = psep;
    dirSep = dsep;
  }
  
  private BuildException noChildrenAllowed()
  {
    return new BuildException("You must not specify nested elements when using the refid attribute.");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.PathConvert
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */