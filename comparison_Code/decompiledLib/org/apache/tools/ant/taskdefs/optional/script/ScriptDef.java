package org.apache.tools.ant.taskdefs.optional.script;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.DefBase;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

public class ScriptDef
  extends DefBase
{
  private ScriptRunnerHelper helper = new ScriptRunnerHelper();
  private String name;
  private List<Attribute> attributes = new ArrayList();
  private List<NestedElement> nestedElements = new ArrayList();
  private Set<String> attributeSet;
  private Map<String, NestedElement> nestedElementMap;
  
  public void setProject(Project project)
  {
    super.setProject(project);
    helper.setProjectComponent(this);
    helper.setSetBeans(false);
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public boolean isAttributeSupported(String attributeName)
  {
    return attributeSet.contains(attributeName);
  }
  
  public static class Attribute
  {
    private String name;
    
    public void setName(String name)
    {
      this.name = name.toLowerCase(Locale.ENGLISH);
    }
  }
  
  public void addAttribute(Attribute attribute)
  {
    attributes.add(attribute);
  }
  
  public static class NestedElement
  {
    private String name;
    private String type;
    private String className;
    
    public void setName(String name)
    {
      this.name = name.toLowerCase(Locale.ENGLISH);
    }
    
    public void setType(String type)
    {
      this.type = type;
    }
    
    public void setClassName(String className)
    {
      this.className = className;
    }
  }
  
  public void addElement(NestedElement nestedElement)
  {
    nestedElements.add(nestedElement);
  }
  
  public void execute()
  {
    if (name == null) {
      throw new BuildException("scriptdef requires a name attribute to name the script");
    }
    if (helper.getLanguage() == null) {
      throw new BuildException("scriptdef requires a language attribute to specify the script language");
    }
    if ((helper.getSrc() == null) && (helper.getEncoding() != null)) {
      throw new BuildException("scriptdef requires a src attribute if the encoding is set");
    }
    if ((getAntlibClassLoader() != null) || (hasCpDelegate())) {
      helper.setClassLoader(createLoader());
    }
    attributeSet = new HashSet();
    for (Attribute attribute : attributes)
    {
      if (name == null) {
        throw new BuildException("scriptdef <attribute> elements must specify an attribute name");
      }
      if (attributeSet.contains(name)) {
        throw new BuildException("scriptdef <%s> declares the %s attribute more than once", new Object[] { name, name });
      }
      attributeSet.add(name);
    }
    nestedElementMap = new HashMap();
    for (NestedElement nestedElement : nestedElements)
    {
      if (name == null) {
        throw new BuildException("scriptdef <element> elements must specify an element name");
      }
      if (nestedElementMap.containsKey(name)) {
        throw new BuildException("scriptdef <%s> declares the %s nested element more than once", new Object[] { name, name });
      }
      if ((className == null) && 
        (type == null)) {
        throw new BuildException("scriptdef <element> elements must specify either a classname or type attribute");
      }
      if ((className != null) && 
        (type != null)) {
        throw new BuildException("scriptdef <element> elements must specify only one of the classname and type attributes");
      }
      nestedElementMap.put(name, nestedElement);
    }
    Object scriptRepository = lookupScriptRepository();
    name = ProjectHelper.genComponentName(getURI(), name);
    ((Map)scriptRepository).put(name, this);
    AntTypeDefinition def = new AntTypeDefinition();
    def.setName(name);
    def.setClass(ScriptDefBase.class);
    ComponentHelper.getComponentHelper(
      getProject()).addDataTypeDefinition(def);
  }
  
  private Map<String, ScriptDef> lookupScriptRepository()
  {
    Project p = getProject();
    synchronized (p)
    {
      Map<String, ScriptDef> scriptRepository = (Map)p.getReference("org.apache.ant.scriptrepo");
      if (scriptRepository == null)
      {
        scriptRepository = new HashMap();
        p.addReference("org.apache.ant.scriptrepo", scriptRepository);
      }
    }
    Map<String, ScriptDef> scriptRepository;
    return scriptRepository;
  }
  
  public Object createNestedElement(String elementName)
  {
    NestedElement definition = (NestedElement)nestedElementMap.get(elementName);
    if (definition == null) {
      throw new BuildException("<%s> does not support the <%s> nested element", new Object[] { name, elementName });
    }
    String classname = className;
    Object instance;
    if (classname == null)
    {
      Object instance = getProject().createTask(type);
      if (instance == null) {
        instance = getProject().createDataType(type);
      }
    }
    else
    {
      ClassLoader loader = createLoader();
      try
      {
        instance = ClasspathUtils.newInstance(classname, loader);
      }
      catch (BuildException e)
      {
        Object instance;
        instance = ClasspathUtils.newInstance(classname, ScriptDef.class.getClassLoader());
      }
      getProject().setProjectReference(instance);
    }
    if (instance == null) {
      throw new BuildException("<%s> is unable to create the <%s> nested element", new Object[] { name, elementName });
    }
    return instance;
  }
  
  @Deprecated
  public void executeScript(Map<String, String> attributes, Map<String, List<Object>> elements)
  {
    executeScript(attributes, elements, null);
  }
  
  public void executeScript(Map<String, String> attributes, Map<String, List<Object>> elements, ScriptDefBase instance)
  {
    ScriptRunnerBase runner = helper.getScriptRunner();
    runner.addBean("attributes", attributes);
    runner.addBean("elements", elements);
    runner.addBean("project", getProject());
    if (instance != null) {
      runner.addBean("self", instance);
    }
    runner.executeScript("scriptdef_" + name);
  }
  
  public void setManager(String manager)
  {
    helper.setManager(manager);
  }
  
  public void setLanguage(String language)
  {
    helper.setLanguage(language);
  }
  
  public void setCompiled(boolean compiled)
  {
    helper.setCompiled(compiled);
  }
  
  public void setSrc(File file)
  {
    helper.setSrc(file);
  }
  
  public void setEncoding(String encoding)
  {
    helper.setEncoding(encoding);
  }
  
  public void addText(String text)
  {
    helper.addText(text);
  }
  
  public void add(ResourceCollection resource)
  {
    helper.add(resource);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.script.ScriptDef
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */