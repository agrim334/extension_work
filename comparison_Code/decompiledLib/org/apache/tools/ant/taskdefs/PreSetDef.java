package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;

public class PreSetDef
  extends AntlibDefinition
  implements TaskContainer
{
  private UnknownElement nestedTask;
  private String name;
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void addTask(Task nestedTask)
  {
    if (this.nestedTask != null) {
      throw new BuildException("Only one nested element allowed");
    }
    if (!(nestedTask instanceof UnknownElement)) {
      throw new BuildException("addTask called with a task that is not an unknown element");
    }
    this.nestedTask = ((UnknownElement)nestedTask);
  }
  
  public void execute()
  {
    if (nestedTask == null) {
      throw new BuildException("Missing nested element");
    }
    if (name == null) {
      throw new BuildException("Name not specified");
    }
    name = ProjectHelper.genComponentName(getURI(), name);
    
    ComponentHelper helper = ComponentHelper.getComponentHelper(
      getProject());
    
    String componentName = ProjectHelper.genComponentName(nestedTask
      .getNamespace(), nestedTask.getTag());
    
    AntTypeDefinition def = helper.getDefinition(componentName);
    if (def == null) {
      throw new BuildException("Unable to find typedef %s", new Object[] { componentName });
    }
    PreSetDefinition newDef = new PreSetDefinition(def, nestedTask);
    
    newDef.setName(name);
    
    helper.addDataTypeDefinition(newDef);
    log("defining preset " + name, 3);
  }
  
  public static class PreSetDefinition
    extends AntTypeDefinition
  {
    private AntTypeDefinition parent;
    private UnknownElement element;
    
    public PreSetDefinition(AntTypeDefinition parent, UnknownElement el)
    {
      if ((parent instanceof PreSetDefinition))
      {
        PreSetDefinition p = (PreSetDefinition)parent;
        el.applyPreSet(element);
        parent = parent;
      }
      this.parent = parent;
      element = el;
    }
    
    public void setClass(Class<?> clazz)
    {
      throw new BuildException("Not supported");
    }
    
    public void setClassName(String className)
    {
      throw new BuildException("Not supported");
    }
    
    public String getClassName()
    {
      return parent.getClassName();
    }
    
    public void setAdapterClass(Class<?> adapterClass)
    {
      throw new BuildException("Not supported");
    }
    
    public void setAdaptToClass(Class<?> adaptToClass)
    {
      throw new BuildException("Not supported");
    }
    
    public void setClassLoader(ClassLoader classLoader)
    {
      throw new BuildException("Not supported");
    }
    
    public ClassLoader getClassLoader()
    {
      return parent.getClassLoader();
    }
    
    public Class<?> getExposedClass(Project project)
    {
      return parent.getExposedClass(project);
    }
    
    public Class<?> getTypeClass(Project project)
    {
      return parent.getTypeClass(project);
    }
    
    public void checkClass(Project project)
    {
      parent.checkClass(project);
    }
    
    public Object createObject(Project project)
    {
      return parent.create(project);
    }
    
    public UnknownElement getPreSets()
    {
      return element;
    }
    
    public Object create(Project project)
    {
      return this;
    }
    
    public boolean sameDefinition(AntTypeDefinition other, Project project)
    {
      return (other != null) && (other.getClass() == getClass()) && (parent != null) && 
        (parent.sameDefinition(parent, project)) && 
        (element.similar(element));
    }
    
    public boolean similarDefinition(AntTypeDefinition other, Project project)
    {
      if ((other != null) && (other.getClass().getName().equals(
        getClass().getName())) && (parent != null)) {}
      return 
      
        (parent.similarDefinition(parent, project)) && 
        (element.similar(element));
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.PreSetDef
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */