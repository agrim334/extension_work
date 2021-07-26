package org.apache.tools.ant.helper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

public class ProjectHelper2$ProjectHandler
  extends ProjectHelper2.AntHandler
{
  public void onStartElement(String uri, String tag, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {
    String baseDir = null;
    boolean nameAttributeSet = false;
    
    Project project = context.getProject();
    
    context.getImplicitTarget().setLocation(new Location(context.getLocator()));
    for (int i = 0; i < attrs.getLength(); i++)
    {
      String attrUri = attrs.getURI(i);
      if ((attrUri == null) || (attrUri.isEmpty()) || (attrUri.equals(uri)))
      {
        String value = attrs.getValue(i);
        switch (attrs.getLocalName(i))
        {
        case "default": 
          if ((value != null) && (!value.isEmpty()) && 
            (!context.isIgnoringProjectTag())) {
            project.setDefault(value);
          }
          break;
        case "name": 
          if (value != null)
          {
            context.setCurrentProjectName(value);
            nameAttributeSet = true;
            if (!context.isIgnoringProjectTag())
            {
              project.setName(value);
              project.addReference(value, project);
            }
            else if ((ProjectHelper.isInIncludeMode()) && 
              (!value.isEmpty()) && (ProjectHelper.getCurrentTargetPrefix() != null) && 
              (ProjectHelper.getCurrentTargetPrefix().endsWith("USE_PROJECT_NAME_AS_TARGET_PREFIX")))
            {
              String newTargetPrefix = ProjectHelper.getCurrentTargetPrefix().replace("USE_PROJECT_NAME_AS_TARGET_PREFIX", value);
              
              ProjectHelper.setCurrentTargetPrefix(newTargetPrefix);
            }
          }
          break;
        case "id": 
          if (value != null) {
            if (!context.isIgnoringProjectTag()) {
              project.addReference(value, project);
            }
          }
          break;
        case "basedir": 
          if (!context.isIgnoringProjectTag()) {
            baseDir = value;
          }
          break;
        default: 
          throw new SAXParseException("Unexpected attribute \"" + attrs.getQName(i) + "\"", context.getLocator());
        }
      }
    }
    String antFileProp = "ant.file." + context.getCurrentProjectName();
    String dup = project.getProperty(antFileProp);
    
    String typeProp = "ant.file.type." + context.getCurrentProjectName();
    String dupType = project.getProperty(typeProp);
    if ((dup != null) && (nameAttributeSet))
    {
      Object dupFile = null;
      Object contextFile = null;
      if ("url".equals(dupType))
      {
        try
        {
          dupFile = new URL(dup);
        }
        catch (MalformedURLException mue)
        {
          throw new BuildException("failed to parse " + dup + " as URL while looking at a duplicate project name.", mue);
        }
        contextFile = context.getBuildFileURL();
      }
      else
      {
        dupFile = new File(dup);
        contextFile = context.getBuildFile();
      }
      if ((context.isIgnoringProjectTag()) && (!dupFile.equals(contextFile))) {
        project.log("Duplicated project name in import. Project " + context
          .getCurrentProjectName() + " defined first in " + dup + " and again in " + contextFile, 1);
      }
    }
    if (nameAttributeSet) {
      if (context.getBuildFile() != null)
      {
        project.setUserProperty(antFileProp, context
          .getBuildFile().toString());
        project.setUserProperty(typeProp, "file");
      }
      else if (context.getBuildFileURL() != null)
      {
        project.setUserProperty(antFileProp, context
          .getBuildFileURL().toString());
        project.setUserProperty(typeProp, "url");
      }
    }
    if (context.isIgnoringProjectTag()) {
      return;
    }
    if (project.getProperty("basedir") != null) {
      project.setBasedir(project.getProperty("basedir"));
    } else if (baseDir == null) {
      project.setBasedir(context.getBuildFileParent().getAbsolutePath());
    } else if (new File(baseDir).isAbsolute()) {
      project.setBasedir(baseDir);
    } else {
      project.setBaseDir(ProjectHelper2.access$100().resolveFile(context.getBuildFileParent(), baseDir));
    }
    project.addTarget("", context.getImplicitTarget());
    context.setCurrentTarget(context.getImplicitTarget());
  }
  
  public ProjectHelper2.AntHandler onStartChild(String uri, String name, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {
    return ((
      "target".equals(name)) || ("extension-point".equals(name))) && ((uri.isEmpty()) || (uri.equals("antlib:org.apache.tools.ant"))) ? 
      ProjectHelper2.access$300() : ProjectHelper2.access$400();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelper2.ProjectHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */