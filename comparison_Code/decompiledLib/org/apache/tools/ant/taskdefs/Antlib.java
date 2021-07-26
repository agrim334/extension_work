package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.types.resources.URLResource;

public class Antlib
  extends Task
  implements TaskContainer
{
  public static final String TAG = "antlib";
  private ClassLoader classLoader;
  
  public static Antlib createAntlib(Project project, URL antlibUrl, String uri)
  {
    try
    {
      URLConnection conn = antlibUrl.openConnection();
      conn.setUseCaches(false);
      conn.connect();
    }
    catch (IOException ex)
    {
      throw new BuildException("Unable to find " + antlibUrl, ex);
    }
    ComponentHelper helper = ComponentHelper.getComponentHelper(project);
    helper.enterAntLib(uri);
    URLResource antlibResource = new URLResource(antlibUrl);
    try
    {
      ProjectHelper parser = null;
      
      Object p = project.getReference("ant.projectHelper");
      if ((p instanceof ProjectHelper))
      {
        parser = (ProjectHelper)p;
        if (!parser.canParseAntlibDescriptor(antlibResource)) {
          parser = null;
        }
      }
      if (parser == null)
      {
        ProjectHelperRepository helperRepository = ProjectHelperRepository.getInstance();
        parser = helperRepository.getProjectHelperForAntlib(antlibResource);
      }
      UnknownElement ue = parser.parseAntlibDescriptor(project, antlibResource);
      if (!"antlib".equals(ue.getTag())) {
        throw new BuildException("Unexpected tag " + ue.getTag() + " expecting " + "antlib", ue.getLocation());
      }
      Antlib antlib = new Antlib();
      antlib.setProject(project);
      antlib.setLocation(ue.getLocation());
      antlib.setTaskName("antlib");
      antlib.init();
      ue.configure(antlib);
      return antlib;
    }
    finally
    {
      helper.exitAntLib();
    }
  }
  
  private String uri = "";
  private List<Task> tasks = new ArrayList();
  
  protected void setClassLoader(ClassLoader classLoader)
  {
    this.classLoader = classLoader;
  }
  
  protected void setURI(String uri)
  {
    this.uri = uri;
  }
  
  private ClassLoader getClassLoader()
  {
    if (classLoader == null) {
      classLoader = Antlib.class.getClassLoader();
    }
    return classLoader;
  }
  
  public void addTask(Task nestedTask)
  {
    tasks.add(nestedTask);
  }
  
  public void execute()
  {
    for (Task task : tasks)
    {
      UnknownElement ue = (UnknownElement)task;
      setLocation(ue.getLocation());
      ue.maybeConfigure();
      Object configuredObject = ue.getRealThing();
      if (configuredObject != null)
      {
        if (!(configuredObject instanceof AntlibDefinition)) {
          throw new BuildException("Invalid task in antlib %s %s does not extend %s", new Object[] {ue.getTag(), configuredObject.getClass(), AntlibDefinition.class.getName() });
        }
        AntlibDefinition def = (AntlibDefinition)configuredObject;
        def.setURI(uri);
        def.setAntlibClassLoader(getClassLoader());
        def.init();
        def.execute();
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Antlib
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */