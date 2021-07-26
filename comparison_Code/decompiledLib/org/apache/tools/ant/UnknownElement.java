package org.apache.tools.ant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.tools.ant.taskdefs.PreSetDef.PreSetDefinition;

public class UnknownElement
  extends Task
{
  private final String elementName;
  private String namespace = "";
  private String qname;
  private Object realThing;
  private List<UnknownElement> children = null;
  private boolean presetDefed = false;
  
  public UnknownElement(String elementName)
  {
    this.elementName = elementName;
  }
  
  public List<UnknownElement> getChildren()
  {
    return children;
  }
  
  public String getTag()
  {
    return elementName;
  }
  
  public String getNamespace()
  {
    return namespace;
  }
  
  public void setNamespace(String namespace)
  {
    if (namespace.equals("ant:current"))
    {
      ComponentHelper helper = ComponentHelper.getComponentHelper(
        getProject());
      namespace = helper.getCurrentAntlibUri();
    }
    this.namespace = (namespace == null ? "" : namespace);
  }
  
  public String getQName()
  {
    return qname;
  }
  
  public void setQName(String qname)
  {
    this.qname = qname;
  }
  
  public RuntimeConfigurable getWrapper()
  {
    return super.getWrapper();
  }
  
  public void maybeConfigure()
    throws BuildException
  {
    if (realThing != null) {
      return;
    }
    configure(makeObject(this, getWrapper()));
  }
  
  public void configure(Object realObject)
  {
    if (realObject == null) {
      return;
    }
    realThing = realObject;
    
    getWrapper().setProxy(realThing);
    Task task = null;
    if ((realThing instanceof Task))
    {
      task = (Task)realThing;
      
      task.setRuntimeConfigurableWrapper(getWrapper());
      if (getWrapper().getId() != null) {
        getOwningTarget().replaceChild(this, (Task)realThing);
      }
    }
    if (task != null) {
      task.maybeConfigure();
    } else {
      getWrapper().maybeConfigure(getProject());
    }
    handleChildren(realThing, getWrapper());
  }
  
  protected void handleOutput(String output)
  {
    if ((realThing instanceof Task)) {
      ((Task)realThing).handleOutput(output);
    } else {
      super.handleOutput(output);
    }
  }
  
  protected int handleInput(byte[] buffer, int offset, int length)
    throws IOException
  {
    if ((realThing instanceof Task)) {
      return ((Task)realThing).handleInput(buffer, offset, length);
    }
    return super.handleInput(buffer, offset, length);
  }
  
  protected void handleFlush(String output)
  {
    if ((realThing instanceof Task)) {
      ((Task)realThing).handleFlush(output);
    } else {
      super.handleFlush(output);
    }
  }
  
  protected void handleErrorOutput(String output)
  {
    if ((realThing instanceof Task)) {
      ((Task)realThing).handleErrorOutput(output);
    } else {
      super.handleErrorOutput(output);
    }
  }
  
  protected void handleErrorFlush(String output)
  {
    if ((realThing instanceof Task)) {
      ((Task)realThing).handleErrorFlush(output);
    } else {
      super.handleErrorFlush(output);
    }
  }
  
  /* Error */
  public void execute()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 57	org/apache/tools/ant/UnknownElement:realThing	Ljava/lang/Object;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 57	org/apache/tools/ant/UnknownElement:realThing	Ljava/lang/Object;
    //   12: instanceof 2
    //   15: ifeq +13 -> 28
    //   18: aload_0
    //   19: getfield 57	org/apache/tools/ant/UnknownElement:realThing	Ljava/lang/Object;
    //   22: checkcast 2	org/apache/tools/ant/Task
    //   25: invokevirtual 119	org/apache/tools/ant/Task:execute	()V
    //   28: aload_0
    //   29: invokevirtual 61	org/apache/tools/ant/UnknownElement:getWrapper	()Lorg/apache/tools/ant/RuntimeConfigurable;
    //   32: invokevirtual 79	org/apache/tools/ant/RuntimeConfigurable:getId	()Ljava/lang/String;
    //   35: ifnonnull +45 -> 80
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 57	org/apache/tools/ant/UnknownElement:realThing	Ljava/lang/Object;
    //   43: aload_0
    //   44: invokevirtual 61	org/apache/tools/ant/UnknownElement:getWrapper	()Lorg/apache/tools/ant/RuntimeConfigurable;
    //   47: aconst_null
    //   48: invokevirtual 70	org/apache/tools/ant/RuntimeConfigurable:setProxy	(Ljava/lang/Object;)V
    //   51: goto +29 -> 80
    //   54: astore_1
    //   55: aload_0
    //   56: invokevirtual 61	org/apache/tools/ant/UnknownElement:getWrapper	()Lorg/apache/tools/ant/RuntimeConfigurable;
    //   59: invokevirtual 79	org/apache/tools/ant/RuntimeConfigurable:getId	()Ljava/lang/String;
    //   62: ifnonnull +16 -> 78
    //   65: aload_0
    //   66: aconst_null
    //   67: putfield 57	org/apache/tools/ant/UnknownElement:realThing	Ljava/lang/Object;
    //   70: aload_0
    //   71: invokevirtual 61	org/apache/tools/ant/UnknownElement:getWrapper	()Lorg/apache/tools/ant/RuntimeConfigurable;
    //   74: aconst_null
    //   75: invokevirtual 70	org/apache/tools/ant/RuntimeConfigurable:setProxy	(Ljava/lang/Object;)V
    //   78: aload_1
    //   79: athrow
    //   80: return
    // Line number table:
    //   Java source line #286	-> byte code offset #0
    //   Java source line #288	-> byte code offset #7
    //   Java source line #291	-> byte code offset #8
    //   Java source line #292	-> byte code offset #18
    //   Java source line #299	-> byte code offset #28
    //   Java source line #300	-> byte code offset #38
    //   Java source line #301	-> byte code offset #43
    //   Java source line #299	-> byte code offset #54
    //   Java source line #300	-> byte code offset #65
    //   Java source line #301	-> byte code offset #70
    //   Java source line #303	-> byte code offset #78
    //   Java source line #304	-> byte code offset #80
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	UnknownElement
    //   54	25	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	28	54	finally
  }
  
  public void addChild(UnknownElement child)
  {
    if (children == null) {
      children = new ArrayList();
    }
    children.add(child);
  }
  
  protected void handleChildren(Object parent, RuntimeConfigurable parentWrapper)
    throws BuildException
  {
    if ((parent instanceof TypeAdapter)) {
      parent = ((TypeAdapter)parent).getProxy();
    }
    String parentUri = getNamespace();
    Class<?> parentClass = parent.getClass();
    IntrospectionHelper ih = IntrospectionHelper.getHelper(getProject(), parentClass);
    if (children != null)
    {
      Iterator<UnknownElement> it = children.iterator();
      for (int i = 0; it.hasNext(); i++)
      {
        RuntimeConfigurable childWrapper = parentWrapper.getChild(i);
        UnknownElement child = (UnknownElement)it.next();
        try
        {
          if ((childWrapper.isEnabled(child)) || 
            (!ih.supportsNestedElement(parentUri, 
            ProjectHelper.genComponentName(child
            .getNamespace(), child.getTag())))) {
            if (!handleChild(parentUri, ih, parent, child, childWrapper)) {
              if (!(parent instanceof TaskContainer))
              {
                ih.throwNotSupported(getProject(), parent, child
                  .getTag());
              }
              else
              {
                TaskContainer container = (TaskContainer)parent;
                container.addTask(child);
              }
            }
          }
        }
        catch (UnsupportedElementException ex)
        {
          throw new BuildException(parentWrapper.getElementTag() + " doesn't support the nested \"" + ex.getElement() + "\" element.", ex);
        }
      }
    }
  }
  
  protected String getComponentName()
  {
    return ProjectHelper.genComponentName(getNamespace(), getTag());
  }
  
  public void applyPreSet(UnknownElement u)
  {
    if (presetDefed) {
      return;
    }
    getWrapper().applyPreSet(u.getWrapper());
    if (children != null)
    {
      List<UnknownElement> newChildren = new ArrayList(children);
      if (children != null) {
        newChildren.addAll(children);
      }
      children = newChildren;
    }
    presetDefed = true;
  }
  
  protected Object makeObject(UnknownElement ue, RuntimeConfigurable w)
  {
    if (!w.isEnabled(ue)) {
      return null;
    }
    ComponentHelper helper = ComponentHelper.getComponentHelper(
      getProject());
    String name = ue.getComponentName();
    Object o = helper.createComponent(ue, ue.getNamespace(), name);
    if (o == null) {
      throw getNotFoundException("task or type", name);
    }
    if ((o instanceof PreSetDef.PreSetDefinition))
    {
      PreSetDef.PreSetDefinition def = (PreSetDef.PreSetDefinition)o;
      o = def.createObject(ue.getProject());
      if (o == null) {
        throw getNotFoundException("preset " + name, def
        
          .getPreSets().getComponentName());
      }
      ue.applyPreSet(def.getPreSets());
      if ((o instanceof Task))
      {
        Task task = (Task)o;
        task.setTaskType(ue.getTaskType());
        task.setTaskName(ue.getTaskName());
        task.init();
      }
    }
    if ((o instanceof UnknownElement)) {
      o = ((UnknownElement)o).makeObject((UnknownElement)o, w);
    }
    if ((o instanceof Task)) {
      ((Task)o).setOwningTarget(getOwningTarget());
    }
    if ((o instanceof ProjectComponent)) {
      ((ProjectComponent)o).setLocation(getLocation());
    }
    return o;
  }
  
  protected Task makeTask(UnknownElement ue, RuntimeConfigurable w)
  {
    Task task = getProject().createTask(ue.getTag());
    if (task != null)
    {
      task.setLocation(getLocation());
      
      task.setOwningTarget(getOwningTarget());
      task.init();
    }
    return task;
  }
  
  protected BuildException getNotFoundException(String what, String name)
  {
    ComponentHelper helper = ComponentHelper.getComponentHelper(getProject());
    String msg = helper.diagnoseCreationFailure(name, what);
    return new BuildException(msg, getLocation());
  }
  
  public String getTaskName()
  {
    return !(realThing instanceof Task) ? super.getTaskName() : 
      ((Task)realThing).getTaskName();
  }
  
  public Task getTask()
  {
    if ((realThing instanceof Task)) {
      return (Task)realThing;
    }
    return null;
  }
  
  public Object getRealThing()
  {
    return realThing;
  }
  
  public void setRealThing(Object realThing)
  {
    this.realThing = realThing;
  }
  
  private boolean handleChild(String parentUri, IntrospectionHelper ih, Object parent, UnknownElement child, RuntimeConfigurable childWrapper)
  {
    String childName = ProjectHelper.genComponentName(child
      .getNamespace(), child.getTag());
    if (ih.supportsNestedElement(parentUri, childName, getProject(), parent))
    {
      IntrospectionHelper.Creator creator = null;
      try
      {
        creator = ih.getElementCreator(getProject(), parentUri, parent, childName, child);
      }
      catch (UnsupportedElementException use)
      {
        if (!ih.isDynamic()) {
          throw use;
        }
        return false;
      }
      creator.setPolyType(childWrapper.getPolyType());
      Object realChild = creator.create();
      if ((realChild instanceof PreSetDef.PreSetDefinition))
      {
        PreSetDef.PreSetDefinition def = (PreSetDef.PreSetDefinition)realChild;
        
        realChild = creator.getRealObject();
        child.applyPreSet(def.getPreSets());
      }
      childWrapper.setCreator(creator);
      childWrapper.setProxy(realChild);
      if ((realChild instanceof Task))
      {
        Task childTask = (Task)realChild;
        childTask.setRuntimeConfigurableWrapper(childWrapper);
        childTask.setTaskName(childName);
        childTask.setTaskType(childName);
      }
      if ((realChild instanceof ProjectComponent)) {
        ((ProjectComponent)realChild).setLocation(child.getLocation());
      }
      childWrapper.maybeConfigure(getProject());
      child.handleChildren(realChild, childWrapper);
      creator.store();
      return true;
    }
    return false;
  }
  
  public boolean similar(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (!getClass().getName().equals(obj.getClass().getName())) {
      return false;
    }
    UnknownElement other = (UnknownElement)obj;
    if (!Objects.equals(elementName, elementName)) {
      return false;
    }
    if (!namespace.equals(namespace)) {
      return false;
    }
    if (!qname.equals(qname)) {
      return false;
    }
    if (!getWrapper().getAttributeMap().equals(other
      .getWrapper().getAttributeMap())) {
      return false;
    }
    if (!getWrapper().getText().toString().equals(other
      .getWrapper().getText().toString())) {
      return false;
    }
    int childrenSize = children == null ? 0 : children.size();
    if (childrenSize == 0) {
      return (children == null) || (children.isEmpty());
    }
    if (children == null) {
      return false;
    }
    if (childrenSize != children.size()) {
      return false;
    }
    for (int i = 0; i < childrenSize; i++)
    {
      UnknownElement child = (UnknownElement)children.get(i);
      if (!child.similar(children.get(i))) {
        return false;
      }
    }
    return true;
  }
  
  public UnknownElement copy(Project newProject)
  {
    UnknownElement ret = new UnknownElement(getTag());
    ret.setNamespace(getNamespace());
    ret.setProject(newProject);
    ret.setQName(getQName());
    ret.setTaskType(getTaskType());
    ret.setTaskName(getTaskName());
    ret.setLocation(getLocation());
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
    RuntimeConfigurable copyRC = new RuntimeConfigurable(ret, getTaskName());
    copyRC.setPolyType(getWrapper().getPolyType());
    Map<String, Object> m = getWrapper().getAttributeMap();
    for (Map.Entry<String, Object> entry : m.entrySet()) {
      copyRC.setAttribute((String)entry.getKey(), (String)entry.getValue());
    }
    copyRC.addText(getWrapper().getText().toString());
    for (RuntimeConfigurable r : Collections.list(getWrapper().getChildren()))
    {
      UnknownElement ueChild = (UnknownElement)r.getProxy();
      UnknownElement copyChild = ueChild.copy(newProject);
      copyRC.addChild(copyChild.getWrapper());
      ret.addChild(copyChild);
    }
    return ret;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.UnknownElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */