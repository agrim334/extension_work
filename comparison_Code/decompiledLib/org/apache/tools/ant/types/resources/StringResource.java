package org.apache.tools.ant.types.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;

public class StringResource
  extends Resource
{
  private static final int STRING_MAGIC = Resource.getMagicNumber("StringResource".getBytes());
  private static final String DEFAULT_ENCODING = "UTF-8";
  private String encoding = "UTF-8";
  
  public StringResource() {}
  
  public StringResource(String value)
  {
    this(null, value);
  }
  
  public StringResource(Project project, String value)
  {
    setProject(project);
    setValue(project == null ? value : project.replaceProperties(value));
  }
  
  public synchronized void setName(String s)
  {
    if (getName() != null) {
      throw new BuildException(new ImmutableResourceException());
    }
    super.setName(s);
  }
  
  public synchronized void setValue(String s)
  {
    setName(s);
  }
  
  public synchronized String getName()
  {
    return super.getName();
  }
  
  public synchronized String getValue()
  {
    return getName();
  }
  
  public boolean isExists()
  {
    return getValue() != null;
  }
  
  public void addText(String text)
  {
    checkChildrenAllowed();
    setValue(getProject().replaceProperties(text));
  }
  
  public synchronized void setEncoding(String s)
  {
    checkAttributesAllowed();
    encoding = s;
  }
  
  public synchronized String getEncoding()
  {
    return encoding;
  }
  
  public synchronized long getSize()
  {
    return isReference() ? getRef().getSize() : 
      getContent().length();
  }
  
  public synchronized int hashCode()
  {
    if (isReference()) {
      return getRef().hashCode();
    }
    return super.hashCode() * STRING_MAGIC;
  }
  
  public String toString()
  {
    return String.valueOf(getContent());
  }
  
  public synchronized InputStream getInputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getInputStream();
    }
    String content = getContent();
    if (content == null) {
      throw new IllegalStateException("unset string value");
    }
    return new ByteArrayInputStream(encoding == null ? 
      content.getBytes() : content.getBytes(encoding));
  }
  
  public synchronized OutputStream getOutputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getOutputStream();
    }
    if (getValue() != null) {
      throw new ImmutableResourceException();
    }
    return new StringResourceFilterOutputStream();
  }
  
  public void setRefid(Reference r)
  {
    if (encoding != "UTF-8") {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  protected synchronized String getContent()
  {
    return getValue();
  }
  
  protected StringResource getRef()
  {
    return (StringResource)getCheckedRef(StringResource.class);
  }
  
  private class StringResourceFilterOutputStream
    extends FilterOutputStream
  {
    private final ByteArrayOutputStream baos;
    
    public StringResourceFilterOutputStream()
    {
      super();
      baos = ((ByteArrayOutputStream)out);
    }
    
    public void close()
      throws IOException
    {
      super.close();
      
      String result = encoding == null ? baos.toString() : baos.toString(encoding);
      
      setValueFromOutputStream(result);
    }
    
    private void setValueFromOutputStream(String output)
    {
      String value;
      String value;
      if (getProject() != null) {
        value = getProject().replaceProperties(output);
      } else {
        value = output;
      }
      setValue(value);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.StringResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */