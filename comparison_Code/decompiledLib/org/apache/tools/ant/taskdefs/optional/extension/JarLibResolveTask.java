package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.AntResolver;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.LocationResolver;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.URLResolver;

public class JarLibResolveTask
  extends Task
{
  private String propertyName;
  private Extension requiredExtension;
  private final List<ExtensionResolver> resolvers = new ArrayList();
  private boolean checkExtension = true;
  private boolean failOnError = true;
  
  public void setProperty(String property)
  {
    propertyName = property;
  }
  
  public void setCheckExtension(boolean checkExtension)
  {
    this.checkExtension = checkExtension;
  }
  
  public void setFailOnError(boolean failOnError)
  {
    this.failOnError = failOnError;
  }
  
  public void addConfiguredLocation(LocationResolver loc)
  {
    resolvers.add(loc);
  }
  
  public void addConfiguredUrl(URLResolver url)
  {
    resolvers.add(url);
  }
  
  public void addConfiguredAnt(AntResolver ant)
  {
    resolvers.add(ant);
  }
  
  public void addConfiguredExtension(ExtensionAdapter extension)
  {
    if (null != requiredExtension) {
      throw new BuildException("Can not specify extension to resolve multiple times.");
    }
    requiredExtension = extension.toExtension();
  }
  
  public void execute()
    throws BuildException
  {
    validate();
    
    getProject().log("Resolving extension: " + requiredExtension, 3);
    
    String candidate = getProject().getProperty(propertyName);
    if (null != candidate)
    {
      message = "Property Already set to: " + candidate;
      if (failOnError) {
        throw new BuildException(message);
      }
      getProject().log(message, 0);
      return;
    }
    String message = resolvers.iterator();
    for (;;)
    {
      if (message.hasNext())
      {
        ExtensionResolver resolver = (ExtensionResolver)message.next();
        getProject().log("Searching for extension using Resolver:" + resolver, 3);
        try
        {
          File file = resolver.resolve(requiredExtension, getProject());
          try
          {
            checkExtension(file);
            return;
          }
          catch (BuildException be)
          {
            getProject().log("File " + file + " returned by resolver failed to satisfy extension due to: " + be
            
              .getMessage(), 1);
          }
        }
        catch (BuildException be)
        {
          getProject().log("Failed to resolve extension to file using resolver " + resolver + " due to: " + be, 1);
        }
      }
    }
    missingExtension();
  }
  
  private void missingExtension()
  {
    String message = "Unable to resolve extension to a file";
    if (failOnError) {
      throw new BuildException("Unable to resolve extension to a file");
    }
    getProject().log("Unable to resolve extension to a file", 0);
  }
  
  private void checkExtension(File file)
  {
    if (!file.exists()) {
      throw new BuildException("File %s does not exist", new Object[] { file });
    }
    if (!file.isFile()) {
      throw new BuildException("File %s is not a file", new Object[] { file });
    }
    if (!checkExtension)
    {
      getProject().log("Setting property to " + file + " without verifying library satisfies extension", 3);
      
      setLibraryProperty(file);
    }
    else
    {
      getProject().log("Checking file " + file + " to see if it satisfies extension", 3);
      
      Manifest manifest = ExtensionUtil.getManifest(file);
      for (Extension extension : Extension.getAvailable(manifest)) {
        if (extension.isCompatibleWith(requiredExtension))
        {
          setLibraryProperty(file);
          return;
        }
      }
      String message = "File " + file + " skipped as it does not satisfy extension";
      
      getProject().log(message, 3);
      throw new BuildException(message);
    }
  }
  
  private void setLibraryProperty(File file)
  {
    getProject().setNewProperty(propertyName, file.getAbsolutePath());
  }
  
  private void validate()
    throws BuildException
  {
    if (null == propertyName) {
      throw new BuildException("Property attribute must be specified.");
    }
    if (null == requiredExtension) {
      throw new BuildException("Extension element must be specified.");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.JarLibResolveTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */