package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Parameter;

public class TypeSelector
  extends BaseExtendSelector
{
  public static final String TYPE_KEY = "type";
  private String type = null;
  
  public String toString()
  {
    return "{typeselector type: " + type + "}";
  }
  
  public void setType(FileType fileTypes)
  {
    type = fileTypes.getValue();
  }
  
  public void setParameters(Parameter... parameters)
  {
    super.setParameters(parameters);
    if (parameters != null) {
      for (Parameter parameter : parameters)
      {
        String paramname = parameter.getName();
        if ("type".equalsIgnoreCase(paramname))
        {
          FileType t = new FileType();
          t.setValue(parameter.getValue());
          setType(t);
        }
        else
        {
          setError("Invalid parameter " + paramname);
        }
      }
    }
  }
  
  public void verifySettings()
  {
    if (type == null) {
      setError("The type attribute is required");
    }
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    validate();
    if (file.isDirectory()) {
      return type.equals("dir");
    }
    return type.equals("file");
  }
  
  public static class FileType
    extends EnumeratedAttribute
  {
    public static final String FILE = "file";
    public static final String DIR = "dir";
    
    public String[] getValues()
    {
      return new String[] { "file", "dir" };
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.TypeSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */