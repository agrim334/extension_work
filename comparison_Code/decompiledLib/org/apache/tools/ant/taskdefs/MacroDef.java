package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;

public class MacroDef
  extends AntlibDefinition
{
  private NestedSequential nestedSequential;
  private String name;
  private boolean backTrace = true;
  private List<Attribute> attributes = new ArrayList();
  private Map<String, TemplateElement> elements = new HashMap();
  private String textName = null;
  private Text text = null;
  private boolean hasImplicitElement = false;
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void addConfiguredText(Text text)
  {
    if (this.text != null) {
      throw new BuildException("Only one nested text element allowed");
    }
    if (text.getName() == null) {
      throw new BuildException("the text nested element needed a \"name\" attribute");
    }
    for (Attribute attribute : attributes) {
      if (text.getName().equals(attribute.getName())) {
        throw new BuildException("the name \"%s\" is already used as an attribute", new Object[] {text.getName() });
      }
    }
    this.text = text;
    textName = text.getName();
  }
  
  public Text getText()
  {
    return text;
  }
  
  public void setBackTrace(boolean backTrace)
  {
    this.backTrace = backTrace;
  }
  
  public boolean getBackTrace()
  {
    return backTrace;
  }
  
  public NestedSequential createSequential()
  {
    if (nestedSequential != null) {
      throw new BuildException("Only one sequential allowed");
    }
    nestedSequential = new NestedSequential();
    return nestedSequential;
  }
  
  public static class NestedSequential
    implements TaskContainer
  {
    private List<Task> nested = new ArrayList();
    
    public void addTask(Task task)
    {
      nested.add(task);
    }
    
    public List<Task> getNested()
    {
      return nested;
    }
    
    public boolean similar(NestedSequential other)
    {
      int size = nested.size();
      if (size != nested.size()) {
        return false;
      }
      for (int i = 0; i < size; i++)
      {
        UnknownElement me = (UnknownElement)nested.get(i);
        UnknownElement o = (UnknownElement)nested.get(i);
        if (!me.similar(o)) {
          return false;
        }
      }
      return true;
    }
  }
  
  public UnknownElement getNestedTask()
  {
    UnknownElement ret = new UnknownElement("sequential");
    ret.setTaskName("sequential");
    ret.setNamespace("");
    ret.setQName("sequential");
    
    new RuntimeConfigurable(ret, "sequential");
    int size = nestedSequential.getNested().size();
    for (int i = 0; i < size; i++)
    {
      UnknownElement e = (UnknownElement)nestedSequential.getNested().get(i);
      ret.addChild(e);
      ret.getWrapper().addChild(e.getWrapper());
    }
    return ret;
  }
  
  public List<Attribute> getAttributes()
  {
    return attributes;
  }
  
  public Map<String, TemplateElement> getElements()
  {
    return elements;
  }
  
  public static boolean isValidNameCharacter(char c)
  {
    return (Character.isLetterOrDigit(c)) || (c == '.') || (c == '-');
  }
  
  private static boolean isValidName(String name)
  {
    if (name.isEmpty()) {
      return false;
    }
    for (int i = 0; i < name.length(); i++) {
      if (!isValidNameCharacter(name.charAt(i))) {
        return false;
      }
    }
    return true;
  }
  
  public void addConfiguredAttribute(Attribute attribute)
  {
    if (attribute.getName() == null) {
      throw new BuildException("the attribute nested element needed a \"name\" attribute");
    }
    if (attribute.getName().equals(textName)) {
      throw new BuildException("the name \"%s\" has already been used by the text element", new Object[] {attribute.getName() });
    }
    for (Attribute att : attributes) {
      if (att.getName().equals(attribute.getName())) {
        throw new BuildException("the name \"%s\" has already been used in another attribute element", new Object[] {attribute.getName() });
      }
    }
    attributes.add(attribute);
  }
  
  public void addConfiguredElement(TemplateElement element)
  {
    if (element.getName() == null) {
      throw new BuildException("the element nested element needed a \"name\" attribute");
    }
    if (elements.get(element.getName()) != null) {
      throw new BuildException("the element %s has already been specified", new Object[] {element.getName() });
    }
    if ((hasImplicitElement) || (
      (element.isImplicit()) && (!elements.isEmpty()))) {
      throw new BuildException("Only one element allowed when using implicit elements");
    }
    hasImplicitElement = element.isImplicit();
    elements.put(element.getName(), element);
  }
  
  public void execute()
  {
    if (nestedSequential == null) {
      throw new BuildException("Missing sequential element");
    }
    if (name == null) {
      throw new BuildException("Name not specified");
    }
    name = ProjectHelper.genComponentName(getURI(), name);
    
    MyAntTypeDefinition def = new MyAntTypeDefinition(this);
    def.setName(name);
    def.setClass(MacroInstance.class);
    
    ComponentHelper helper = ComponentHelper.getComponentHelper(
      getProject());
    
    helper.addDataTypeDefinition(def);
    log("creating macro  " + name, 3);
  }
  
  public static class Attribute
  {
    private String name;
    private String defaultValue;
    private String description;
    private boolean doubleExpanding = true;
    
    public void setName(String name)
    {
      if (!MacroDef.isValidName(name)) {
        throw new BuildException("Illegal name [%s] for attribute", new Object[] { name });
      }
      this.name = name.toLowerCase(Locale.ENGLISH);
    }
    
    public String getName()
    {
      return name;
    }
    
    public void setDefault(String defaultValue)
    {
      this.defaultValue = defaultValue;
    }
    
    public String getDefault()
    {
      return defaultValue;
    }
    
    public void setDescription(String desc)
    {
      description = desc;
    }
    
    public String getDescription()
    {
      return description;
    }
    
    public void setDoubleExpanding(boolean doubleExpanding)
    {
      this.doubleExpanding = doubleExpanding;
    }
    
    public boolean isDoubleExpanding()
    {
      return doubleExpanding;
    }
    
    public boolean equals(Object obj)
    {
      if (obj == null) {
        return false;
      }
      if (obj.getClass() != getClass()) {
        return false;
      }
      Attribute other = (Attribute)obj;
      if (name == null)
      {
        if (name != null) {
          return false;
        }
      }
      else if (!name.equals(name)) {
        return false;
      }
      return defaultValue == null ? false : defaultValue == null ? true : 
        defaultValue.equals(defaultValue);
    }
    
    public int hashCode()
    {
      return Objects.hashCode(defaultValue) + Objects.hashCode(name);
    }
  }
  
  public static class Text
  {
    private String name;
    private boolean optional;
    private boolean trim;
    private String description;
    private String defaultString;
    
    public void setName(String name)
    {
      if (!MacroDef.isValidName(name)) {
        throw new BuildException("Illegal name [%s] for element", new Object[] { name });
      }
      this.name = name.toLowerCase(Locale.ENGLISH);
    }
    
    public String getName()
    {
      return name;
    }
    
    public void setOptional(boolean optional)
    {
      this.optional = optional;
    }
    
    public boolean getOptional()
    {
      return optional;
    }
    
    public void setTrim(boolean trim)
    {
      this.trim = trim;
    }
    
    public boolean getTrim()
    {
      return trim;
    }
    
    public void setDescription(String desc)
    {
      description = desc;
    }
    
    public String getDescription()
    {
      return description;
    }
    
    public void setDefault(String defaultString)
    {
      this.defaultString = defaultString;
    }
    
    public String getDefault()
    {
      return defaultString;
    }
    
    public boolean equals(Object obj)
    {
      if (obj == null) {
        return false;
      }
      if (obj.getClass() != getClass()) {
        return false;
      }
      Text other = (Text)obj;
      if ((Objects.equals(name, name)) && (optional == optional) && (trim == trim)) {}
      return 
      
        Objects.equals(defaultString, defaultString);
    }
    
    public int hashCode()
    {
      return Objects.hashCode(name);
    }
  }
  
  public static class TemplateElement
  {
    private String name;
    private String description;
    private boolean optional = false;
    private boolean implicit = false;
    
    public void setName(String name)
    {
      if (!MacroDef.isValidName(name)) {
        throw new BuildException("Illegal name [%s] for macro element", new Object[] { name });
      }
      this.name = name.toLowerCase(Locale.ENGLISH);
    }
    
    public String getName()
    {
      return name;
    }
    
    public void setDescription(String desc)
    {
      description = desc;
    }
    
    public String getDescription()
    {
      return description;
    }
    
    public void setOptional(boolean optional)
    {
      this.optional = optional;
    }
    
    public boolean isOptional()
    {
      return optional;
    }
    
    public void setImplicit(boolean implicit)
    {
      this.implicit = implicit;
    }
    
    public boolean isImplicit()
    {
      return implicit;
    }
    
    public boolean equals(Object obj)
    {
      if (obj == this) {
        return true;
      }
      if ((obj == null) || (!obj.getClass().equals(getClass()))) {
        return false;
      }
      TemplateElement t = (TemplateElement)obj;
      return (name == null ? name == null : name
        .equals(name)) && (optional == optional) && (implicit == implicit);
    }
    
    public int hashCode()
    {
      return 
        Objects.hashCode(name) + (optional ? 1 : 0) + (implicit ? 1 : 0);
    }
  }
  
  private boolean sameOrSimilar(Object obj, boolean same)
  {
    if (obj == this) {
      return true;
    }
    if ((obj == null) || (!obj.getClass().equals(getClass()))) {
      return false;
    }
    MacroDef other = (MacroDef)obj;
    if (name == null) {
      return name == null;
    }
    if (!name.equals(name)) {
      return false;
    }
    if ((other.getLocation() != null) && 
      (other.getLocation().equals(getLocation())) && (!same)) {
      return true;
    }
    if (text == null)
    {
      if (text != null) {
        return false;
      }
    }
    else if (!text.equals(text)) {
      return false;
    }
    if ((getURI() == null) || (getURI().isEmpty()) || 
      (getURI().equals("antlib:org.apache.tools.ant")))
    {
      if ((other.getURI() != null) && (!other.getURI().isEmpty()) && 
        (!other.getURI().equals("antlib:org.apache.tools.ant"))) {
        return false;
      }
    }
    else if (!getURI().equals(other.getURI())) {
      return false;
    }
    return (nestedSequential.similar(nestedSequential)) && 
      (attributes.equals(attributes)) && (elements.equals(elements));
  }
  
  public boolean similar(Object obj)
  {
    return sameOrSimilar(obj, false);
  }
  
  public boolean sameDefinition(Object obj)
  {
    return sameOrSimilar(obj, true);
  }
  
  private static class MyAntTypeDefinition
    extends AntTypeDefinition
  {
    private MacroDef macroDef;
    
    public MyAntTypeDefinition(MacroDef macroDef)
    {
      this.macroDef = macroDef;
    }
    
    public Object create(Project project)
    {
      Object o = super.create(project);
      if (o == null) {
        return null;
      }
      ((MacroInstance)o).setMacroDef(macroDef);
      return o;
    }
    
    public boolean sameDefinition(AntTypeDefinition other, Project project)
    {
      if (!super.sameDefinition(other, project)) {
        return false;
      }
      MyAntTypeDefinition otherDef = (MyAntTypeDefinition)other;
      return macroDef.sameDefinition(macroDef);
    }
    
    public boolean similarDefinition(AntTypeDefinition other, Project project)
    {
      if (!super.similarDefinition(other, project)) {
        return false;
      }
      MyAntTypeDefinition otherDef = (MyAntTypeDefinition)other;
      return macroDef.similar(macroDef);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MacroDef
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */