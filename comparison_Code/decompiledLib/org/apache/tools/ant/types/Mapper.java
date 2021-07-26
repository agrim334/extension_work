package org.apache.tools.ant.types;

import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.CompositeMapper;
import org.apache.tools.ant.util.ContainerMapper;
import org.apache.tools.ant.util.FileNameMapper;

public class Mapper
  extends DataType
{
  protected MapperType type = null;
  protected String classname = null;
  protected Path classpath = null;
  protected String from = null;
  protected String to = null;
  private ContainerMapper container = null;
  
  public Mapper(Project p)
  {
    setProject(p);
  }
  
  public void setType(MapperType type)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.type = type;
  }
  
  public void addConfigured(FileNameMapper fileNameMapper)
  {
    add(fileNameMapper);
  }
  
  public void add(FileNameMapper fileNameMapper)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (container == null) {
      if ((type == null) && (classname == null))
      {
        container = new CompositeMapper();
      }
      else
      {
        FileNameMapper m = getImplementation();
        if ((m instanceof ContainerMapper)) {
          container = ((ContainerMapper)m);
        } else {
          throw new BuildException(String.valueOf(m) + " mapper implementation does not support nested mappers!");
        }
      }
    }
    container.add(fileNameMapper);
    setChecked(false);
  }
  
  public void addConfiguredMapper(Mapper mapper)
  {
    add(mapper.getImplementation());
  }
  
  public void setClassname(String classname)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.classname = classname;
  }
  
  public void setClasspath(Path classpath)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    if (this.classpath == null) {
      this.classpath = classpath;
    } else {
      this.classpath.append(classpath);
    }
  }
  
  public Path createClasspath()
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (classpath == null) {
      classpath = new Path(getProject());
    }
    setChecked(false);
    return classpath.createPath();
  }
  
  public void setClasspathRef(Reference ref)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    createClasspath().setRefid(ref);
  }
  
  public void setFrom(String from)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.from = from;
  }
  
  public void setTo(String to)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.to = to;
  }
  
  public void setRefid(Reference r)
    throws BuildException
  {
    if ((type != null) || (from != null) || (to != null)) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public FileNameMapper getImplementation()
    throws BuildException
  {
    if (isReference())
    {
      dieOnCircularReference();
      Reference r = getRefid();
      Object o = r.getReferencedObject(getProject());
      if ((o instanceof FileNameMapper)) {
        return (FileNameMapper)o;
      }
      if ((o instanceof Mapper)) {
        return ((Mapper)o).getImplementation();
      }
      String od = o == null ? "null" : o.getClass().getName();
      
      throw new BuildException(od + " at reference '" + r.getRefId() + "' is not a valid mapper reference.");
    }
    if ((type == null) && (classname == null) && (container == null)) {
      throw new BuildException("nested mapper or one of the attributes type or classname is required");
    }
    if (container != null) {
      return container;
    }
    if ((type != null) && (classname != null)) {
      throw new BuildException("must not specify both type and classname attribute");
    }
    try
    {
      FileNameMapper m = (FileNameMapper)getImplementationClass().newInstance();
      Project p = getProject();
      if (p != null) {
        p.setProjectReference(m);
      }
      m.setFrom(from);
      m.setTo(to);
      
      return m;
    }
    catch (BuildException be)
    {
      throw be;
    }
    catch (Throwable t)
    {
      throw new BuildException(t);
    }
  }
  
  protected Class<? extends FileNameMapper> getImplementationClass()
    throws ClassNotFoundException
  {
    String cName = classname;
    if (type != null) {
      cName = type.getImplementation();
    }
    ClassLoader loader = classpath == null ? getClass().getClassLoader() : getProject().createClassLoader(classpath);
    
    return Class.forName(cName, true, loader).asSubclass(FileNameMapper.class);
  }
  
  @Deprecated
  protected Mapper getRef()
  {
    return (Mapper)getCheckedRef(Mapper.class);
  }
  
  public static class MapperType
    extends EnumeratedAttribute
  {
    private Properties implementations;
    
    public MapperType()
    {
      implementations = new Properties();
      implementations.put("identity", "org.apache.tools.ant.util.IdentityMapper");
      
      implementations.put("flatten", "org.apache.tools.ant.util.FlatFileNameMapper");
      
      implementations.put("glob", "org.apache.tools.ant.util.GlobPatternMapper");
      
      implementations.put("merge", "org.apache.tools.ant.util.MergingMapper");
      
      implementations.put("regexp", "org.apache.tools.ant.util.RegexpPatternMapper");
      
      implementations.put("package", "org.apache.tools.ant.util.PackageNameMapper");
      
      implementations.put("unpackage", "org.apache.tools.ant.util.UnPackageNameMapper");
    }
    
    public String[] getValues()
    {
      return new String[] { "identity", "flatten", "glob", "merge", "regexp", "package", "unpackage" };
    }
    
    public String getImplementation()
    {
      return implementations.getProperty(getValue());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Mapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */