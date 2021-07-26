package org.apache.tools.ant.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.apache.tools.ant.types.resources.FileProvider;

public class Resource
  extends DataType
  implements Comparable<Resource>, ResourceCollection
{
  public static final long UNKNOWN_SIZE = -1L;
  public static final long UNKNOWN_DATETIME = 0L;
  protected static final int MAGIC = getMagicNumber("Resource".getBytes());
  private static final int NULL_NAME = getMagicNumber("null name".getBytes());
  
  protected static int getMagicNumber(byte[] seed)
  {
    return new BigInteger(seed).intValue();
  }
  
  private String name = null;
  private Boolean exists = null;
  private Long lastmodified = null;
  private Boolean directory = null;
  private Long size = null;
  
  public Resource() {}
  
  public Resource(String name)
  {
    this(name, false, 0L, false);
  }
  
  public Resource(String name, boolean exists, long lastmodified)
  {
    this(name, exists, lastmodified, false);
  }
  
  public Resource(String name, boolean exists, long lastmodified, boolean directory)
  {
    this(name, exists, lastmodified, directory, -1L);
  }
  
  public Resource(String name, boolean exists, long lastmodified, boolean directory, long size)
  {
    this.name = name;
    setName(name);
    setExists(exists);
    setLastModified(lastmodified);
    setDirectory(directory);
    setSize(size);
  }
  
  public String getName()
  {
    return isReference() ? getRef().getName() : name;
  }
  
  public void setName(String name)
  {
    checkAttributesAllowed();
    this.name = name;
  }
  
  public boolean isExists()
  {
    if (isReference()) {
      return getRef().isExists();
    }
    return (exists == null) || (exists.booleanValue());
  }
  
  public void setExists(boolean exists)
  {
    checkAttributesAllowed();
    this.exists = (exists ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public long getLastModified()
  {
    if (isReference()) {
      return getRef().getLastModified();
    }
    if ((!isExists()) || (lastmodified == null)) {
      return 0L;
    }
    long result = lastmodified.longValue();
    return result < 0L ? 0L : result;
  }
  
  public void setLastModified(long lastmodified)
  {
    checkAttributesAllowed();
    this.lastmodified = Long.valueOf(lastmodified);
  }
  
  public boolean isDirectory()
  {
    if (isReference()) {
      return getRef().isDirectory();
    }
    return (directory != null) && (directory.booleanValue());
  }
  
  public void setDirectory(boolean directory)
  {
    checkAttributesAllowed();
    this.directory = (directory ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public void setSize(long size)
  {
    checkAttributesAllowed();
    this.size = Long.valueOf(size > -1L ? size : -1L);
  }
  
  public long getSize()
  {
    if (isReference()) {
      return getRef().getSize();
    }
    return isExists() ? 
      -1L : size != null ? size.longValue() : 
      0L;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new UnsupportedOperationException("CloneNotSupportedException for a Resource caught. Derived classes must support cloning.");
    }
  }
  
  public int compareTo(Resource other)
  {
    if (isReference()) {
      return getRef().compareTo(other);
    }
    return toString().compareTo(other.toString());
  }
  
  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    if (isReference()) {
      return getRef().equals(other);
    }
    return (other != null) && (other.getClass().equals(getClass())) && 
      (compareTo((Resource)other) == 0);
  }
  
  public int hashCode()
  {
    if (isReference()) {
      return getRef().hashCode();
    }
    String name = getName();
    return MAGIC * (name == null ? NULL_NAME : name.hashCode());
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getInputStream();
    }
    throw new UnsupportedOperationException();
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getOutputStream();
    }
    throw new UnsupportedOperationException();
  }
  
  public Iterator<Resource> iterator()
  {
    return isReference() ? getRef().iterator() : 
      Collections.singleton(this).iterator();
  }
  
  public int size()
  {
    return isReference() ? getRef().size() : 1;
  }
  
  public boolean isFilesystemOnly()
  {
    return ((isReference()) && (getRef().isFilesystemOnly())) || 
      (as(FileProvider.class) != null);
  }
  
  public String toString()
  {
    if (isReference()) {
      return getRef().toString();
    }
    String n = getName();
    return n == null ? "(anonymous)" : n;
  }
  
  public final String toLongString()
  {
    return 
      getDataTypeName() + " \"" + toString() + '"';
  }
  
  public void setRefid(Reference r)
  {
    if ((name != null) || (exists != null) || (lastmodified != null) || (directory != null) || (size != null)) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public <T> T as(Class<T> clazz)
  {
    return (T)(clazz.isAssignableFrom(getClass()) ? clazz.cast(this) : null);
  }
  
  public <T> Optional<T> asOptional(Class<T> clazz)
  {
    return Optional.ofNullable(as(clazz));
  }
  
  protected Resource getRef()
  {
    return (Resource)getCheckedRef(Resource.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Resource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */