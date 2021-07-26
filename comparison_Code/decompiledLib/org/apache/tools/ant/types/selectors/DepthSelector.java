package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;

public class DepthSelector
  extends BaseExtendSelector
{
  public static final String MIN_KEY = "min";
  public static final String MAX_KEY = "max";
  public int min = -1;
  public int max = -1;
  
  public String toString()
  {
    return "{depthselector min: " + min + " max: " + max + "}";
  }
  
  public void setMin(int min)
  {
    this.min = min;
  }
  
  public void setMax(int max)
  {
    this.max = max;
  }
  
  public void setParameters(Parameter... parameters)
  {
    super.setParameters(parameters);
    if (parameters != null) {
      for (Parameter parameter : parameters)
      {
        String paramname = parameter.getName();
        if ("min".equalsIgnoreCase(paramname)) {
          try
          {
            setMin(Integer.parseInt(parameter.getValue()));
          }
          catch (NumberFormatException nfe1)
          {
            setError("Invalid minimum value " + parameter
              .getValue());
          }
        } else if ("max".equalsIgnoreCase(paramname)) {
          try
          {
            setMax(Integer.parseInt(parameter.getValue()));
          }
          catch (NumberFormatException nfe1)
          {
            setError("Invalid maximum value " + parameter
              .getValue());
          }
        } else {
          setError("Invalid parameter " + paramname);
        }
      }
    }
  }
  
  public void verifySettings()
  {
    if ((min < 0) && (max < 0)) {
      setError("You must set at least one of the min or the max levels.");
    }
    if ((max < min) && (max > -1)) {
      setError("The maximum depth is lower than the minimum.");
    }
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    validate();
    
    int depth = -1;
    
    String absBase = basedir.getAbsolutePath();
    String absFile = file.getAbsolutePath();
    StringTokenizer tokBase = new StringTokenizer(absBase, File.separator);
    StringTokenizer tokFile = new StringTokenizer(absFile, File.separator);
    while (tokFile.hasMoreTokens())
    {
      String filetoken = tokFile.nextToken();
      if (tokBase.hasMoreTokens())
      {
        String basetoken = tokBase.nextToken();
        if (!basetoken.equals(filetoken)) {
          throw new BuildException("File %s does not appear within %s directory", new Object[] { filename, absBase });
        }
      }
      else
      {
        depth++;
        if ((max > -1) && (depth > max)) {
          return false;
        }
      }
    }
    if (tokBase.hasMoreTokens()) {
      throw new BuildException("File %s is outside of %s directory tree", new Object[] { filename, absBase });
    }
    return (min <= -1) || (depth >= min);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.DepthSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */