package org.apache.tools.ant.helper;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExtensionPoint;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelper.OnMissingExtensionPoint;
import org.apache.tools.ant.Target;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

public class ProjectHelper2$TargetHandler
  extends ProjectHelper2.AntHandler
{
  public void onStartElement(String uri, String tag, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {
    String name = null;
    String depends = "";
    String extensionPoint = null;
    ProjectHelper.OnMissingExtensionPoint extensionPointMissing = null;
    
    Project project = context.getProject();
    
    Target target = "target".equals(tag) ? new Target() : new ExtensionPoint();
    target.setProject(project);
    target.setLocation(new Location(context.getLocator()));
    context.addTarget(target);
    for (int i = 0; i < attrs.getLength(); i++)
    {
      String attrUri = attrs.getURI(i);
      if ((attrUri == null) || (attrUri.isEmpty()) || (attrUri.equals(uri)))
      {
        String value = attrs.getValue(i);
        switch (attrs.getLocalName(i))
        {
        case "name": 
          name = value;
          if (name.isEmpty()) {
            throw new BuildException("name attribute must not be empty");
          }
          break;
        case "depends": 
          depends = value;
          break;
        case "if": 
          target.setIf(value);
          break;
        case "unless": 
          target.setUnless(value);
          break;
        case "id": 
          if ((value != null) && (!value.isEmpty())) {
            context.getProject().addReference(value, target);
          }
          break;
        case "description": 
          target.setDescription(value);
          break;
        case "extensionOf": 
          extensionPoint = value;
          break;
        case "onMissingExtensionPoint": 
          try
          {
            extensionPointMissing = ProjectHelper.OnMissingExtensionPoint.valueOf(value);
          }
          catch (IllegalArgumentException e)
          {
            throw new BuildException("Invalid onMissingExtensionPoint " + value);
          }
        default: 
          throw new SAXParseException("Unexpected attribute \"" + attrs.getQName(i) + "\"", context.getLocator());
        }
      }
    }
    if (name == null) {
      throw new SAXParseException("target element appears without a name attribute", context.getLocator());
    }
    String prefix = null;
    
    boolean isInIncludeMode = (context.isIgnoringProjectTag()) && (ProjectHelper.isInIncludeMode());
    String sep = ProjectHelper.getCurrentPrefixSeparator();
    if (isInIncludeMode)
    {
      prefix = getTargetPrefix(context);
      if (prefix == null) {
        throw new BuildException("can't include build file " + context.getBuildFileURL() + ", no as attribute has been given and the project tag doesn't specify a name attribute");
      }
      name = prefix + sep + name;
    }
    if (context.getCurrentTargets().get(name) != null) {
      throw new BuildException("Duplicate target '" + name + "'", target.getLocation());
    }
    Object projectTargets = project.getTargets();
    boolean usedTarget = false;
    if (((Hashtable)projectTargets).containsKey(name))
    {
      project.log("Already defined in main or a previous import, ignore " + name, 3);
    }
    else
    {
      target.setName(name);
      context.getCurrentTargets().put(name, target);
      project.addOrReplaceTarget(name, target);
      usedTarget = true;
    }
    if (!depends.isEmpty()) {
      if (!isInIncludeMode) {
        target.setDepends(depends);
      } else {
        for (String string : Target.parseDepends(depends, name, "depends")) {
          target.addDependency(prefix + sep + string);
        }
      }
    }
    Target newTarget;
    if ((!isInIncludeMode) && (context.isIgnoringProjectTag()) && 
      ((prefix = getTargetPrefix(context)) != null))
    {
      String newName = prefix + sep + name;
      newTarget = target;
      if (usedTarget) {
        newTarget = "target".equals(tag) ? new Target(target) : new ExtensionPoint(target);
      }
      newTarget.setName(newName);
      context.getCurrentTargets().put(newName, newTarget);
      project.addOrReplaceTarget(newName, newTarget);
    }
    if ((extensionPointMissing != null) && (extensionPoint == null)) {
      throw new BuildException("onMissingExtensionPoint attribute cannot be specified unless extensionOf is specified", target.getLocation());
    }
    ProjectHelper helper;
    if (extensionPoint != null)
    {
      helper = (ProjectHelper)context.getProject().getReference("ant.projectHelper");
      for (String extPointName : Target.parseDepends(extensionPoint, name, "extensionOf"))
      {
        if (extensionPointMissing == null) {
          extensionPointMissing = ProjectHelper.OnMissingExtensionPoint.FAIL;
        }
        if (ProjectHelper.isInIncludeMode()) {
          helper.getExtensionStack().add(new String[] { extPointName, target
            .getName(), extensionPointMissing
            .name(), prefix + sep });
        } else {
          helper.getExtensionStack().add(new String[] { extPointName, target
            .getName(), extensionPointMissing
            .name() });
        }
      }
    }
  }
  
  private String getTargetPrefix(AntXMLContext context)
  {
    String configuredValue = ProjectHelper.getCurrentTargetPrefix();
    if ((configuredValue != null) && (configuredValue.isEmpty())) {
      configuredValue = null;
    }
    if (configuredValue != null) {
      return configuredValue;
    }
    String projectName = context.getCurrentProjectName();
    if ((projectName != null) && (projectName.isEmpty())) {
      projectName = null;
    }
    return projectName;
  }
  
  public ProjectHelper2.AntHandler onStartChild(String uri, String name, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {
    return ProjectHelper2.access$400();
  }
  
  public void onEndElement(String uri, String tag, AntXMLContext context)
  {
    context.setCurrentTarget(context.getImplicitTarget());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelper2.TargetHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */