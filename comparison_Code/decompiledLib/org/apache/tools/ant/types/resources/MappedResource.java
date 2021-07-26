package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileNameMapper;

public class MappedResource
  extends ResourceDecorator
{
  private final FileNameMapper mapper;
  
  public MappedResource(Resource r, FileNameMapper m)
  {
    super(r);
    mapper = m;
  }
  
  public String getName()
  {
    String name = getResource().getName();
    if (isReference()) {
      return name;
    }
    String[] mapped = mapper.mapFileName(name);
    return (mapped != null) && (mapped.length > 0) ? mapped[0] : null;
  }
  
  public void setRefid(Reference r)
  {
    if (mapper != null) {
      throw noChildrenAllowed();
    }
    super.setRefid(r);
  }
  
  public <T> T as(Class<T> clazz)
  {
    return FileProvider.class.isAssignableFrom(clazz) ? 
      null : getResource().as(clazz);
  }
  
  public int hashCode()
  {
    String n = getName();
    return n == null ? super.hashCode() : n.hashCode();
  }
  
  public boolean equals(Object other)
  {
    if ((other == null) || (other.getClass() != getClass())) {
      return false;
    }
    MappedResource m = (MappedResource)other;
    String myName = getName();
    String otherName = m.getName();
    return (myName == null ? otherName == null : myName.equals(otherName)) && 
      (getResource().equals(m.getResource()));
  }
  
  public String toString()
  {
    if (isReference()) {
      return getRef().toString();
    }
    return getName();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.MappedResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */