package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicAttribute;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.property.LocalProperties;

public class MacroInstance
  extends Task
  implements DynamicAttribute, TaskContainer
{
  private MacroDef macroDef;
  private Map<String, String> map = new HashMap();
  private Map<String, MacroDef.TemplateElement> nsElements = null;
  private Map<String, UnknownElement> presentElements;
  private Map<String, String> localAttributes;
  private String text = null;
  private String implicitTag = null;
  private List<Task> unknownElements = new ArrayList();
  private static final int STATE_NORMAL = 0;
  private static final int STATE_EXPECT_BRACKET = 1;
  private static final int STATE_EXPECT_NAME = 2;
  
  public void setMacroDef(MacroDef macroDef)
  {
    this.macroDef = macroDef;
  }
  
  public MacroDef getMacroDef()
  {
    return macroDef;
  }
  
  public void setDynamicAttribute(String name, String value)
  {
    map.put(name.toLowerCase(Locale.ENGLISH), value);
  }
  
  @Deprecated
  public Object createDynamicElement(String name)
    throws BuildException
  {
    throw new BuildException("Not implemented any more");
  }
  
  private Map<String, MacroDef.TemplateElement> getNsElements()
  {
    if (nsElements == null)
    {
      nsElements = new HashMap();
      for (Map.Entry<String, MacroDef.TemplateElement> entry : macroDef
        .getElements().entrySet())
      {
        nsElements.put((String)entry.getKey(), (MacroDef.TemplateElement)entry.getValue());
        MacroDef.TemplateElement te = (MacroDef.TemplateElement)entry.getValue();
        if (te.isImplicit()) {
          implicitTag = te.getName();
        }
      }
    }
    return nsElements;
  }
  
  public void addTask(Task nestedTask)
  {
    unknownElements.add(nestedTask);
  }
  
  private void processTasks()
  {
    if (implicitTag != null) {
      return;
    }
    for (Task task : unknownElements)
    {
      UnknownElement ue = (UnknownElement)task;
      
      String name = ProjectHelper.extractNameFromComponentName(ue.getTag()).toLowerCase(Locale.ENGLISH);
      if (getNsElements().get(name) == null) {
        throw new BuildException("unsupported element %s", new Object[] { name });
      }
      if (presentElements.get(name) != null) {
        throw new BuildException("Element %s already present", new Object[] { name });
      }
      presentElements.put(name, ue);
    }
  }
  
  public static class Element
    implements TaskContainer
  {
    private List<Task> unknownElements = new ArrayList();
    
    public void addTask(Task nestedTask)
    {
      unknownElements.add(nestedTask);
    }
    
    public List<Task> getUnknownElements()
    {
      return unknownElements;
    }
  }
  
  private String macroSubs(String s, Map<String, String> macroMapping)
  {
    if (s == null) {
      return null;
    }
    StringBuilder ret = new StringBuilder();
    StringBuilder macroName = null;
    
    int state = 0;
    for (char ch : s.toCharArray()) {
      switch (state)
      {
      case 0: 
        if (ch == '@') {
          state = 1;
        } else {
          ret.append(ch);
        }
        break;
      case 1: 
        if (ch == '{')
        {
          state = 2;
          macroName = new StringBuilder();
        }
        else if (ch == '@')
        {
          state = 0;
          ret.append('@');
        }
        else
        {
          state = 0;
          ret.append('@');
          ret.append(ch);
        }
        break;
      case 2: 
        if (ch == '}')
        {
          state = 0;
          String name = macroName.toString().toLowerCase(Locale.ENGLISH);
          String value = (String)macroMapping.get(name);
          if (value == null)
          {
            ret.append("@{");
            ret.append(name);
            ret.append("}");
          }
          else
          {
            ret.append(value);
          }
          macroName = null;
        }
        else
        {
          macroName.append(ch);
        }
        break;
      }
    }
    switch (state)
    {
    case 0: 
      break;
    case 1: 
      ret.append('@');
      break;
    case 2: 
      ret.append("@{");
      ret.append(macroName.toString());
      break;
    }
    return ret.toString();
  }
  
  public void addText(String text)
  {
    this.text = text;
  }
  
  private UnknownElement copy(UnknownElement ue, boolean nested)
  {
    UnknownElement ret = new UnknownElement(ue.getTag());
    ret.setNamespace(ue.getNamespace());
    ret.setProject(getProject());
    ret.setQName(ue.getQName());
    ret.setTaskType(ue.getTaskType());
    ret.setTaskName(ue.getTaskName());
    ret.setLocation(
      macroDef.getBackTrace() ? ue.getLocation() : getLocation());
    if (getOwningTarget() == null)
    {
      Target t = new Target();
      t.setProject(getProject());
      ret.setOwningTarget(t);
    }
    else
    {
      ret.setOwningTarget(getOwningTarget());
    }
    RuntimeConfigurable rc = new RuntimeConfigurable(ret, ue.getTaskName());
    rc.setPolyType(ue.getWrapper().getPolyType());
    Map<String, Object> m = ue.getWrapper().getAttributeMap();
    for (Map.Entry<String, Object> entry : m.entrySet()) {
      rc.setAttribute(
        (String)entry.getKey(), 
        macroSubs((String)entry.getValue(), localAttributes));
    }
    rc.addText(macroSubs(ue.getWrapper().getText().toString(), localAttributes));
    for (RuntimeConfigurable r : Collections.list(ue.getWrapper().getChildren()))
    {
      UnknownElement unknownElement = (UnknownElement)r.getProxy();
      String tag = unknownElement.getTaskType();
      if (tag != null) {
        tag = tag.toLowerCase(Locale.ENGLISH);
      }
      MacroDef.TemplateElement templateElement = (MacroDef.TemplateElement)getNsElements().get(tag);
      UnknownElement child;
      if ((templateElement == null) || (nested))
      {
        child = copy(unknownElement, nested);
        rc.addChild(child.getWrapper());
        ret.addChild(child);
      }
      else if (templateElement.isImplicit())
      {
        if ((unknownElements.isEmpty()) && (!templateElement.isOptional())) {
          throw new BuildException("Missing nested elements for implicit element %s", new Object[] {templateElement.getName() });
        }
        for (Task task : unknownElements)
        {
          UnknownElement child = copy((UnknownElement)task, true);
          rc.addChild(child.getWrapper());
          ret.addChild(child);
        }
      }
      else
      {
        UnknownElement presentElement = (UnknownElement)presentElements.get(tag);
        if (presentElement == null)
        {
          if (!templateElement.isOptional()) {
            throw new BuildException("Required nested element %s missing", new Object[] {templateElement.getName() });
          }
        }
        else
        {
          String presentText = presentElement.getWrapper().getText().toString();
          if (!presentText.isEmpty()) {
            rc.addText(macroSubs(presentText, localAttributes));
          }
          List<UnknownElement> list = presentElement.getChildren();
          if (list != null) {
            for (UnknownElement unknownElement2 : list)
            {
              UnknownElement child = copy(unknownElement2, true);
              rc.addChild(child.getWrapper());
              ret.addChild(child);
            }
          }
        }
      }
    }
    return ret;
  }
  
  public void execute()
  {
    presentElements = new HashMap();
    getNsElements();
    processTasks();
    localAttributes = new Hashtable();
    Set<String> copyKeys = new HashSet(map.keySet());
    for (MacroDef.Attribute attribute : macroDef.getAttributes())
    {
      String value = (String)map.get(attribute.getName());
      if ((value == null) && ("description".equals(attribute.getName()))) {
        value = getDescription();
      }
      if (value == null)
      {
        value = attribute.getDefault();
        value = macroSubs(value, localAttributes);
      }
      if (value == null) {
        throw new BuildException("required attribute %s not set", new Object[] {attribute.getName() });
      }
      localAttributes.put(attribute.getName(), value);
      copyKeys.remove(attribute.getName());
    }
    copyKeys.remove("id");
    if (macroDef.getText() != null)
    {
      if (text == null)
      {
        String defaultText = macroDef.getText().getDefault();
        if ((!macroDef.getText().getOptional()) && (defaultText == null)) {
          throw new BuildException("required text missing");
        }
        text = (defaultText == null ? "" : defaultText);
      }
      if (macroDef.getText().getTrim()) {
        text = text.trim();
      }
      localAttributes.put(macroDef.getText().getName(), text);
    }
    else if ((text != null) && (!text.trim().isEmpty()))
    {
      throw new BuildException("The \"%s\" macro does not support nested text data.", new Object[] {getTaskName() });
    }
    if (!copyKeys.isEmpty()) {
      throw new BuildException("Unknown attribute" + (copyKeys.size() > 1 ? "s " : " ") + copyKeys);
    }
    UnknownElement c = copy(macroDef.getNestedTask(), false);
    c.init();
    LocalProperties localProperties = LocalProperties.get(getProject());
    localProperties.enterScope();
    try
    {
      c.perform();
    }
    catch (BuildException ex)
    {
      if (macroDef.getBackTrace()) {
        throw ProjectHelper.addLocationToBuildException(ex, 
          getLocation());
      }
      ex.setLocation(getLocation());
      throw ex;
    }
    finally
    {
      presentElements = null;
      localAttributes = null;
      localProperties.exitScope();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MacroInstance
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */