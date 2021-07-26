package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

public class SchemaValidate$SchemaLocation
{
  private String namespace;
  private File file;
  private String url;
  public static final String ERROR_NO_URI = "No namespace URI";
  public static final String ERROR_TWO_LOCATIONS = "Both URL and File were given for schema ";
  public static final String ERROR_NO_FILE = "File not found: ";
  public static final String ERROR_NO_URL_REPRESENTATION = "Cannot make a URL of ";
  public static final String ERROR_NO_LOCATION = "No file or URL supplied for the schema ";
  
  public String getNamespace()
  {
    return namespace;
  }
  
  public void setNamespace(String namespace)
  {
    this.namespace = namespace;
  }
  
  public File getFile()
  {
    return file;
  }
  
  public void setFile(File file)
  {
    this.file = file;
  }
  
  public String getUrl()
  {
    return url;
  }
  
  public void setUrl(String url)
  {
    this.url = url;
  }
  
  public String getSchemaLocationURL()
  {
    boolean hasFile = file != null;
    boolean hasURL = isSet(url);
    if ((!hasFile) && (!hasURL)) {
      throw new BuildException("No file or URL supplied for the schema " + namespace);
    }
    if ((hasFile) && (hasURL)) {
      throw new BuildException("Both URL and File were given for schema " + namespace);
    }
    String schema = url;
    if (hasFile)
    {
      if (!file.exists()) {
        throw new BuildException("File not found: " + file);
      }
      try
      {
        schema = FileUtils.getFileUtils().getFileURL(file).toString();
      }
      catch (MalformedURLException e)
      {
        throw new BuildException("Cannot make a URL of " + file, e);
      }
    }
    return schema;
  }
  
  public String getURIandLocation()
    throws BuildException
  {
    validateNamespace();
    return namespace + ' ' + getSchemaLocationURL();
  }
  
  public void validateNamespace()
  {
    if (!isSet(getNamespace())) {
      throw new BuildException("No namespace URI");
    }
  }
  
  private boolean isSet(String property)
  {
    return (property != null) && (!property.isEmpty());
  }
  
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SchemaLocation)) {
      return false;
    }
    SchemaLocation schemaLocation = (SchemaLocation)o;
    
    return (file == null ? file == null : file.equals(file)) && (namespace == null ? namespace == null : namespace
      .equals(namespace)) && (url == null ? url == null : url
      .equals(url));
  }
  
  public int hashCode()
  {
    int result = namespace == null ? 0 : namespace.hashCode();
    result = 29 * result + (file == null ? 0 : file.hashCode());
    result = 29 * result + (url == null ? 0 : url.hashCode());
    
    return result;
  }
  
  public String toString()
  {
    return 
    
      (namespace == null ? "(anonymous)" : namespace) + (url == null ? "" : new StringBuilder().append(" ").append(url).toString()) + (file == null ? "" : new StringBuilder().append(" ").append(file.getAbsolutePath()).toString());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.SchemaValidate.SchemaLocation
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */