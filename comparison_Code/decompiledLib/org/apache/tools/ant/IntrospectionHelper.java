package org.apache.tools.ant;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.taskdefs.PreSetDef.PreSetDefinition;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.StringUtils;

public final class IntrospectionHelper
{
  private static final Map<String, IntrospectionHelper> HELPERS = new Hashtable();
  private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAP = new HashMap(8);
  private static final int MAX_REPORT_NESTED_TEXT = 20;
  private static final String ELLIPSIS = "...";
  
  static
  {
    Class<?>[] primitives = { Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE };
    
    Class<?>[] wrappers = { Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class };
    for (int i = 0; i < primitives.length; i++) {
      PRIMITIVE_TYPE_MAP.put(primitives[i], wrappers[i]);
    }
  }
  
  private final Map<String, Class<?>> attributeTypes = new Hashtable();
  private final Map<String, AttributeSetter> attributeSetters = new Hashtable();
  private final Map<String, Class<?>> nestedTypes = new Hashtable();
  private final Map<String, NestedCreator> nestedCreators = new Hashtable();
  private final List<Method> addTypeMethods = new ArrayList();
  private final Method addText;
  private final Class<?> bean;
  protected static final String NOT_SUPPORTED_CHILD_PREFIX = " doesn't support the nested \"";
  protected static final String NOT_SUPPORTED_CHILD_POSTFIX = "\" element.";
  
  private IntrospectionHelper(Class<?> bean)
  {
    this.bean = bean;
    Method addTextMethod = null;
    for (Method m : bean.getMethods())
    {
      String name = m.getName();
      Class<?> returnType = m.getReturnType();
      Class<?>[] args = m.getParameterTypes();
      if ((args.length == 1) && (Void.TYPE.equals(returnType)) && (
        ("add".equals(name)) || ("addConfigured".equals(name)))) {
        insertAddTypeMethod(m);
      } else if ((!ProjectComponent.class.isAssignableFrom(bean)) || (args.length != 1) || 
        (!isHiddenSetMethod(name, args[0]))) {
        if ((!isContainer()) || (args.length != 1) || (!"addTask".equals(name)) || 
          (!Task.class.equals(args[0]))) {
          if (("addText".equals(name)) && (Void.TYPE.equals(returnType)) && (args.length == 1) && 
            (String.class.equals(args[0])))
          {
            addTextMethod = m;
          }
          else if ((name.startsWith("set")) && (Void.TYPE.equals(returnType)) && (args.length == 1) && 
            (!args[0].isArray()))
          {
            String propName = getPropertyName(name, "set");
            AttributeSetter as = (AttributeSetter)attributeSetters.get(propName);
            if (as != null)
            {
              if (!String.class.equals(args[0])) {
                if ((File.class.equals(args[0])) && (
                
                  (Resource.class.equals(type)) || (FileProvider.class.equals(type)))) {}
              }
            }
            else
            {
              as = createAttributeSetter(m, args[0], propName);
              if (as != null)
              {
                attributeTypes.put(propName, args[0]);
                attributeSetters.put(propName, as);
              }
            }
          }
          else if ((name.startsWith("create")) && (!returnType.isArray()) && 
            (!returnType.isPrimitive()) && (args.length == 0))
          {
            String propName = getPropertyName(name, "create");
            if (nestedCreators.get(propName) == null)
            {
              nestedTypes.put(propName, returnType);
              nestedCreators.put(propName, new CreateNestedCreator(m));
            }
          }
          else if ((name.startsWith("addConfigured")) && 
            (Void.TYPE.equals(returnType)) && (args.length == 1) && 
            (!String.class.equals(args[0])) && 
            (!args[0].isArray()) && (!args[0].isPrimitive()))
          {
            try
            {
              Constructor<?> constructor = null;
              try
              {
                constructor = args[0].getConstructor(new Class[0]);
              }
              catch (NoSuchMethodException ex)
              {
                constructor = args[0].getConstructor(new Class[] { Project.class });
              }
              String propName = getPropertyName(name, "addConfigured");
              nestedTypes.put(propName, args[0]);
              nestedCreators.put(propName, new AddNestedCreator(m, constructor, 2));
            }
            catch (NoSuchMethodException localNoSuchMethodException1) {}
          }
          else if ((name.startsWith("add")) && 
            (Void.TYPE.equals(returnType)) && (args.length == 1) && 
            (!String.class.equals(args[0])) && 
            (!args[0].isArray()) && (!args[0].isPrimitive()))
          {
            try
            {
              Constructor<?> constructor = null;
              try
              {
                constructor = args[0].getConstructor(new Class[0]);
              }
              catch (NoSuchMethodException ex)
              {
                constructor = args[0].getConstructor(new Class[] { Project.class });
              }
              String propName = getPropertyName(name, "add");
              if (nestedTypes.get(propName) == null)
              {
                nestedTypes.put(propName, args[0]);
                nestedCreators.put(propName, new AddNestedCreator(m, constructor, 1));
              }
            }
            catch (NoSuchMethodException localNoSuchMethodException2) {}
          }
        }
      }
    }
    addText = addTextMethod;
  }
  
  private boolean isHiddenSetMethod(String name, Class<?> type)
  {
    return (("setLocation".equals(name)) && (Location.class.equals(type))) || (
      ("setTaskType".equals(name)) && (String.class.equals(type)));
  }
  
  public static synchronized IntrospectionHelper getHelper(Class<?> c)
  {
    return getHelper(null, c);
  }
  
  public static synchronized IntrospectionHelper getHelper(Project p, Class<?> c)
  {
    IntrospectionHelper ih = (IntrospectionHelper)HELPERS.get(c.getName());
    if ((ih == null) || (bean != c))
    {
      ih = new IntrospectionHelper(c);
      if (p != null) {
        HELPERS.put(c.getName(), ih);
      }
    }
    return ih;
  }
  
  public void setAttribute(Project p, Object element, String attributeName, Object value)
    throws BuildException
  {
    AttributeSetter as = (AttributeSetter)attributeSetters.get(attributeName
      .toLowerCase(Locale.ENGLISH));
    if ((as == null) && (value != null))
    {
      if ((element instanceof DynamicAttributeNS))
      {
        DynamicAttributeNS dc = (DynamicAttributeNS)element;
        String uriPlusPrefix = ProjectHelper.extractUriFromComponentName(attributeName);
        String uri = ProjectHelper.extractUriFromComponentName(uriPlusPrefix);
        String localName = ProjectHelper.extractNameFromComponentName(attributeName);
        String qName = uri + ":" + localName;
        dc.setDynamicAttribute(uri, localName, qName, value.toString());
        return;
      }
      if ((element instanceof DynamicObjectAttribute))
      {
        DynamicObjectAttribute dc = (DynamicObjectAttribute)element;
        dc.setDynamicAttribute(attributeName.toLowerCase(Locale.ENGLISH), value);
        return;
      }
      if ((element instanceof DynamicAttribute))
      {
        DynamicAttribute dc = (DynamicAttribute)element;
        dc.setDynamicAttribute(attributeName.toLowerCase(Locale.ENGLISH), value.toString());
        return;
      }
      if (attributeName.contains(":")) {
        return;
      }
      String msg = getElementName(p, element) + " doesn't support the \"" + attributeName + "\" attribute.";
      
      throw new UnsupportedAttributeException(msg, attributeName);
    }
    if (as != null) {
      try
      {
        as.setObject(p, element, value);
      }
      catch (IllegalAccessException ie)
      {
        throw new BuildException(ie);
      }
      catch (InvocationTargetException ite)
      {
        throw extractBuildException(ite);
      }
    }
  }
  
  public void setAttribute(Project p, Object element, String attributeName, String value)
    throws BuildException
  {
    setAttribute(p, element, attributeName, value);
  }
  
  public void addText(Project project, Object element, String text)
    throws BuildException
  {
    if (addText == null)
    {
      text = text.trim();
      if (text.isEmpty()) {
        return;
      }
      throw new BuildException(project.getElementName(element) + " doesn't support nested text data (\"" + condenseText(text) + "\").");
    }
    try
    {
      addText.invoke(element, new Object[] { text });
    }
    catch (IllegalAccessException ie)
    {
      throw new BuildException(ie);
    }
    catch (InvocationTargetException ite)
    {
      throw extractBuildException(ite);
    }
  }
  
  public void throwNotSupported(Project project, Object parent, String elementName)
  {
    String msg = project.getElementName(parent) + " doesn't support the nested \"" + elementName + "\" element.";
    
    throw new UnsupportedElementException(msg, elementName);
  }
  
  private NestedCreator getNestedCreator(Project project, String parentUri, Object parent, String elementName, UnknownElement child)
    throws BuildException
  {
    String uri = ProjectHelper.extractUriFromComponentName(elementName);
    String name = ProjectHelper.extractNameFromComponentName(elementName);
    if (uri.equals("antlib:org.apache.tools.ant")) {
      uri = "";
    }
    if (parentUri.equals("antlib:org.apache.tools.ant")) {
      parentUri = "";
    }
    NestedCreator nc = null;
    if ((uri.equals(parentUri)) || (uri.isEmpty())) {
      nc = (NestedCreator)nestedCreators.get(name.toLowerCase(Locale.ENGLISH));
    }
    if (nc == null) {
      nc = createAddTypeCreator(project, parent, elementName);
    }
    if ((nc == null) && (((parent instanceof DynamicElementNS)) || ((parent instanceof DynamicElement))))
    {
      String qName = child == null ? name : child.getQName();
      final Object nestedElement = createDynamicElement(parent, 
        child == null ? "" : child.getNamespace(), name, qName);
      if (nestedElement != null) {
        nc = new NestedCreator(null)
        {
          Object create(Project project, Object parent, Object ignore)
          {
            return nestedElement;
          }
        };
      }
    }
    if (nc == null) {
      throwNotSupported(project, parent, elementName);
    }
    return nc;
  }
  
  private Object createDynamicElement(Object parent, String ns, String localName, String qName)
  {
    Object nestedElement = null;
    if ((parent instanceof DynamicElementNS))
    {
      DynamicElementNS dc = (DynamicElementNS)parent;
      nestedElement = dc.createDynamicElement(ns, localName, qName);
    }
    if ((nestedElement == null) && ((parent instanceof DynamicElement)))
    {
      DynamicElement dc = (DynamicElement)parent;
      
      nestedElement = dc.createDynamicElement(localName.toLowerCase(Locale.ENGLISH));
    }
    return nestedElement;
  }
  
  @Deprecated
  public Object createElement(Project project, Object parent, String elementName)
    throws BuildException
  {
    NestedCreator nc = getNestedCreator(project, "", parent, elementName, null);
    try
    {
      Object nestedElement = nc.create(project, parent, null);
      if (project != null) {
        project.setProjectReference(nestedElement);
      }
      return nestedElement;
    }
    catch (IllegalAccessException|InstantiationException ie)
    {
      throw new BuildException(ie);
    }
    catch (InvocationTargetException ite)
    {
      throw extractBuildException(ite);
    }
  }
  
  public Creator getElementCreator(Project project, String parentUri, Object parent, String elementName, UnknownElement ue)
  {
    NestedCreator nc = getNestedCreator(project, parentUri, parent, elementName, ue);
    return new Creator(project, parent, nc, null);
  }
  
  public boolean isDynamic()
  {
    return (DynamicElement.class.isAssignableFrom(bean)) || 
      (DynamicElementNS.class.isAssignableFrom(bean));
  }
  
  public boolean isContainer()
  {
    return TaskContainer.class.isAssignableFrom(bean);
  }
  
  public boolean supportsNestedElement(String elementName)
  {
    return supportsNestedElement("", elementName);
  }
  
  public boolean supportsNestedElement(String parentUri, String elementName)
  {
    return (isDynamic()) || (!addTypeMethods.isEmpty()) || 
      (supportsReflectElement(parentUri, elementName));
  }
  
  public boolean supportsNestedElement(String parentUri, String elementName, Project project, Object parent)
  {
    return ((!addTypeMethods.isEmpty()) && 
      (createAddTypeCreator(project, parent, elementName) != null)) || 
      (isDynamic()) || (supportsReflectElement(parentUri, elementName));
  }
  
  public boolean supportsReflectElement(String parentUri, String elementName)
  {
    String name = ProjectHelper.extractNameFromComponentName(elementName);
    if (!nestedCreators.containsKey(name.toLowerCase(Locale.ENGLISH))) {
      return false;
    }
    String uri = ProjectHelper.extractUriFromComponentName(elementName);
    if ((uri.equals("antlib:org.apache.tools.ant")) || (uri.isEmpty())) {
      return true;
    }
    if (parentUri.equals("antlib:org.apache.tools.ant")) {
      parentUri = "";
    }
    return uri.equals(parentUri);
  }
  
  public void storeElement(Project project, Object parent, Object child, String elementName)
    throws BuildException
  {
    if (elementName == null) {
      return;
    }
    NestedCreator ns = (NestedCreator)nestedCreators.get(elementName.toLowerCase(Locale.ENGLISH));
    if (ns == null) {
      return;
    }
    try
    {
      ns.store(parent, child);
    }
    catch (IllegalAccessException|InstantiationException ie)
    {
      throw new BuildException(ie);
    }
    catch (InvocationTargetException ite)
    {
      throw extractBuildException(ite);
    }
  }
  
  private static BuildException extractBuildException(InvocationTargetException ite)
  {
    Throwable t = ite.getTargetException();
    if ((t instanceof BuildException)) {
      return (BuildException)t;
    }
    return new BuildException(t);
  }
  
  public Class<?> getElementType(String elementName)
    throws BuildException
  {
    Class<?> nt = (Class)nestedTypes.get(elementName);
    if (nt == null) {
      throw new UnsupportedElementException("Class " + bean.getName() + " doesn't support the nested \"" + elementName + "\" element.", elementName);
    }
    return nt;
  }
  
  public Class<?> getAttributeType(String attributeName)
    throws BuildException
  {
    Class<?> at = (Class)attributeTypes.get(attributeName);
    if (at == null) {
      throw new UnsupportedAttributeException("Class " + bean.getName() + " doesn't support the \"" + attributeName + "\" attribute.", attributeName);
    }
    return at;
  }
  
  public Method getAddTextMethod()
    throws BuildException
  {
    if (!supportsCharacters()) {
      throw new BuildException("Class " + bean.getName() + " doesn't support nested text data.");
    }
    return addText;
  }
  
  public Method getElementMethod(String elementName)
    throws BuildException
  {
    NestedCreator creator = (NestedCreator)nestedCreators.get(elementName);
    if (creator == null) {
      throw new UnsupportedElementException("Class " + bean.getName() + " doesn't support the nested \"" + elementName + "\" element.", elementName);
    }
    return method;
  }
  
  public Method getAttributeMethod(String attributeName)
    throws BuildException
  {
    AttributeSetter setter = (AttributeSetter)attributeSetters.get(attributeName);
    if (setter == null) {
      throw new UnsupportedAttributeException("Class " + bean.getName() + " doesn't support the \"" + attributeName + "\" attribute.", attributeName);
    }
    return method;
  }
  
  public boolean supportsCharacters()
  {
    return addText != null;
  }
  
  public Enumeration<String> getAttributes()
  {
    return Collections.enumeration(attributeSetters.keySet());
  }
  
  public Map<String, Class<?>> getAttributeMap()
  {
    return attributeTypes.isEmpty() ? 
      Collections.emptyMap() : Collections.unmodifiableMap(attributeTypes);
  }
  
  public Enumeration<String> getNestedElements()
  {
    return Collections.enumeration(nestedTypes.keySet());
  }
  
  public Map<String, Class<?>> getNestedElementMap()
  {
    return nestedTypes.isEmpty() ? 
      Collections.emptyMap() : Collections.unmodifiableMap(nestedTypes);
  }
  
  public List<Method> getExtensionPoints()
  {
    return addTypeMethods.isEmpty() ? 
      Collections.emptyList() : Collections.unmodifiableList(addTypeMethods);
  }
  
  private AttributeSetter createAttributeSetter(final Method m, Class<?> arg, final String attrName)
  {
    final Class<?> reflectedArg = (Class)PRIMITIVE_TYPE_MAP.getOrDefault(arg, arg);
    if (Object.class == reflectedArg) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException
        {
          throw new BuildException("Internal ant problem - this should not get called");
        }
      };
    }
    if (String.class.equals(reflectedArg)) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException
        {
          m.invoke(parent, (Object[])new String[] { value });
        }
      };
    }
    if (Character.class.equals(reflectedArg)) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException
        {
          if (value.isEmpty()) {
            throw new BuildException("The value \"\" is not a legal value for attribute \"" + attrName + "\"");
          }
          m.invoke(parent, (Object[])new Character[] { Character.valueOf(value.charAt(0)) });
        }
      };
    }
    if (Boolean.class.equals(reflectedArg)) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException
        {
          m.invoke(parent, 
            (Object[])new Boolean[] {Project.toBoolean(value) ? Boolean.TRUE : Boolean.FALSE });
        }
      };
    }
    if (Class.class.equals(reflectedArg)) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException, BuildException
        {
          try
          {
            m.invoke(parent, new Object[] { Class.forName(value) });
          }
          catch (ClassNotFoundException ce)
          {
            throw new BuildException(ce);
          }
        }
      };
    }
    if (File.class.equals(reflectedArg)) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException
        {
          m.invoke(parent, new Object[] { p.resolveFile(value) });
        }
      };
    }
    if (Path.class.equals(reflectedArg)) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException
        {
          m.invoke(parent, new Object[] { p.resolveFile(value).toPath() });
        }
      };
    }
    if ((Resource.class.equals(reflectedArg)) || (FileProvider.class.equals(reflectedArg))) {
      new AttributeSetter(m, arg)
      {
        void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException, BuildException
        {
          m.invoke(parent, new Object[] { new FileResource(p, p.resolveFile(value)) });
        }
      };
    }
    if (EnumeratedAttribute.class.isAssignableFrom(reflectedArg)) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException, BuildException
        {
          try
          {
            EnumeratedAttribute ea = (EnumeratedAttribute)reflectedArg.newInstance();
            ea.setValue(value);
            m.invoke(parent, new Object[] { ea });
          }
          catch (InstantiationException ie)
          {
            throw new BuildException(ie);
          }
        }
      };
    }
    AttributeSetter setter = getEnumSetter(reflectedArg, m, arg);
    if (setter != null) {
      return setter;
    }
    if (Long.class.equals(reflectedArg)) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException, BuildException
        {
          try
          {
            m.invoke(parent, new Object[] { Long.valueOf(StringUtils.parseHumanSizes(value)) });
          }
          catch (NumberFormatException e)
          {
            throw new BuildException("Can't assign non-numeric value '" + value + "' to attribute " + attrName);
          }
          catch (InvocationTargetException|IllegalAccessException e)
          {
            throw e;
          }
          catch (Exception e)
          {
            throw new BuildException(e);
          }
        }
      };
    }
    try
    {
      Constructor<?> c = reflectedArg.getConstructor(new Class[] { Project.class, String.class });
      includeProject = true;
    }
    catch (NoSuchMethodException nme)
    {
      try
      {
        boolean includeProject;
        Constructor<?> c = reflectedArg.getConstructor(new Class[] { String.class });
        includeProject = false;
      }
      catch (NoSuchMethodException nme2)
      {
        boolean includeProject;
        return null;
      }
    }
    Constructor<?> c;
    boolean includeProject;
    final boolean finalIncludeProject = includeProject;
    final Constructor<?> finalConstructor = c;
    
    new AttributeSetter(m, arg)
    {
      public void set(Project p, Object parent, String value)
        throws InvocationTargetException, IllegalAccessException, BuildException
      {
        try
        {
          Object[] args = { finalIncludeProject ? new Object[] { p, value } : value };
          
          Object attribute = finalConstructor.newInstance(args);
          if (p != null) {
            p.setProjectReference(attribute);
          }
          m.invoke(parent, new Object[] { attribute });
        }
        catch (InvocationTargetException e)
        {
          Throwable cause = e.getCause();
          if ((cause instanceof IllegalArgumentException)) {
            throw new BuildException("Can't assign value '" + value + "' to attribute " + attrName + ", reason: " + cause.getClass() + " with message '" + cause.getMessage() + "'");
          }
          throw e;
        }
        catch (InstantiationException ie)
        {
          throw new BuildException(ie);
        }
      }
    };
  }
  
  private AttributeSetter getEnumSetter(final Class<?> reflectedArg, final Method m, Class<?> arg)
  {
    if (reflectedArg.isEnum()) {
      new AttributeSetter(m, arg)
      {
        public void set(Project p, Object parent, String value)
          throws InvocationTargetException, IllegalAccessException, BuildException
        {
          try
          {
            Enum<?> enumValue = Enum.valueOf(reflectedArg, value);
            
            setValue = enumValue;
          }
          catch (IllegalArgumentException e)
          {
            Enum<?> setValue;
            throw new BuildException("'" + value + "' is not a permitted value for " + reflectedArg.getName());
          }
          Enum<?> setValue;
          m.invoke(parent, new Object[] { setValue });
        }
      };
    }
    return null;
  }
  
  private String getElementName(Project project, Object element)
  {
    return project.getElementName(element);
  }
  
  private static String getPropertyName(String methodName, String prefix)
  {
    return methodName.substring(prefix.length()).toLowerCase(Locale.ENGLISH);
  }
  
  public static final class Creator
  {
    private final IntrospectionHelper.NestedCreator nestedCreator;
    private final Object parent;
    private final Project project;
    private Object nestedObject;
    private String polyType;
    
    private Creator(Project project, Object parent, IntrospectionHelper.NestedCreator nestedCreator)
    {
      this.project = project;
      this.parent = parent;
      this.nestedCreator = nestedCreator;
    }
    
    public void setPolyType(String polyType)
    {
      this.polyType = polyType;
    }
    
    public Object create()
    {
      if (polyType != null)
      {
        if (!nestedCreator.isPolyMorphic()) {
          throw new BuildException("Not allowed to use the polymorphic form for this element");
        }
        ComponentHelper helper = ComponentHelper.getComponentHelper(project);
        nestedObject = helper.createComponent(polyType);
        if (nestedObject == null) {
          throw new BuildException("Unable to create object of type " + polyType);
        }
      }
      try
      {
        nestedObject = nestedCreator.create(project, parent, nestedObject);
        if (project != null) {
          project.setProjectReference(nestedObject);
        }
        return nestedObject;
      }
      catch (IllegalAccessException|InstantiationException ex)
      {
        throw new BuildException(ex);
      }
      catch (IllegalArgumentException ex)
      {
        if (polyType == null) {
          throw ex;
        }
        throw new BuildException("Invalid type used " + polyType);
      }
      catch (InvocationTargetException ex)
      {
        throw IntrospectionHelper.extractBuildException(ex);
      }
    }
    
    public Object getRealObject()
    {
      return nestedCreator.getRealObject();
    }
    
    public void store()
    {
      try
      {
        nestedCreator.store(parent, nestedObject);
      }
      catch (IllegalAccessException|InstantiationException ex)
      {
        throw new BuildException(ex);
      }
      catch (IllegalArgumentException ex)
      {
        if (polyType == null) {
          throw ex;
        }
        throw new BuildException("Invalid type used " + polyType);
      }
      catch (InvocationTargetException ex)
      {
        throw IntrospectionHelper.extractBuildException(ex);
      }
    }
  }
  
  private static abstract class NestedCreator
  {
    private final Method method;
    
    protected NestedCreator(Method m)
    {
      method = m;
    }
    
    Method getMethod()
    {
      return method;
    }
    
    boolean isPolyMorphic()
    {
      return false;
    }
    
    Object getRealObject()
    {
      return null;
    }
    
    abstract Object create(Project paramProject, Object paramObject1, Object paramObject2)
      throws InvocationTargetException, IllegalAccessException, InstantiationException;
    
    void store(Object parent, Object child)
      throws InvocationTargetException, IllegalAccessException, InstantiationException
    {}
  }
  
  private static class CreateNestedCreator
    extends IntrospectionHelper.NestedCreator
  {
    CreateNestedCreator(Method m)
    {
      super();
    }
    
    Object create(Project project, Object parent, Object ignore)
      throws InvocationTargetException, IllegalAccessException
    {
      return getMethod().invoke(parent, new Object[0]);
    }
  }
  
  private static class AddNestedCreator
    extends IntrospectionHelper.NestedCreator
  {
    static final int ADD = 1;
    static final int ADD_CONFIGURED = 2;
    private final Constructor<?> constructor;
    private final int behavior;
    
    AddNestedCreator(Method m, Constructor<?> c, int behavior)
    {
      super();
      constructor = c;
      this.behavior = behavior;
    }
    
    boolean isPolyMorphic()
    {
      return true;
    }
    
    Object create(Project project, Object parent, Object child)
      throws InvocationTargetException, IllegalAccessException, InstantiationException
    {
      if (child == null) {
        child = constructor.newInstance(
        
          new Object[] {constructor.getParameterTypes().length == 0 ? new Object[0] : project });
      }
      if ((child instanceof PreSetDef.PreSetDefinition)) {
        child = ((PreSetDef.PreSetDefinition)child).createObject(project);
      }
      if (behavior == 1) {
        istore(parent, child);
      }
      return child;
    }
    
    void store(Object parent, Object child)
      throws InvocationTargetException, IllegalAccessException, InstantiationException
    {
      if (behavior == 2) {
        istore(parent, child);
      }
    }
    
    private void istore(Object parent, Object child)
      throws InvocationTargetException, IllegalAccessException
    {
      getMethod().invoke(parent, new Object[] { child });
    }
  }
  
  private static abstract class AttributeSetter
  {
    private final Method method;
    private final Class<?> type;
    
    protected AttributeSetter(Method m, Class<?> type)
    {
      method = m;
      this.type = type;
    }
    
    void setObject(Project p, Object parent, Object value)
      throws InvocationTargetException, IllegalAccessException, BuildException
    {
      if (type != null)
      {
        Class<?> useType = type;
        if (type.isPrimitive())
        {
          if (value == null) {
            throw new BuildException("Attempt to set primitive " + IntrospectionHelper.getPropertyName(method.getName(), "set") + " to null on " + parent);
          }
          useType = (Class)IntrospectionHelper.PRIMITIVE_TYPE_MAP.get(type);
        }
        if ((value == null) || (useType.isInstance(value)))
        {
          method.invoke(parent, new Object[] { value });
          return;
        }
      }
      set(p, parent, value.toString());
    }
    
    abstract void set(Project paramProject, Object paramObject, String paramString)
      throws InvocationTargetException, IllegalAccessException, BuildException;
  }
  
  public static synchronized void clearCache()
  {
    HELPERS.clear();
  }
  
  private NestedCreator createAddTypeCreator(Project project, Object parent, String elementName)
    throws BuildException
  {
    if (addTypeMethods.isEmpty()) {
      return null;
    }
    ComponentHelper helper = ComponentHelper.getComponentHelper(project);
    
    MethodAndObject restricted = createRestricted(helper, elementName, addTypeMethods);
    MethodAndObject topLevel = createTopLevel(helper, elementName, addTypeMethods);
    if ((restricted == null) && (topLevel == null)) {
      return null;
    }
    if ((restricted != null) && (topLevel != null)) {
      throw new BuildException("ambiguous: type and component definitions for " + elementName);
    }
    MethodAndObject methodAndObject = restricted == null ? topLevel : restricted;
    
    Object rObject = object;
    if ((object instanceof PreSetDef.PreSetDefinition)) {
      rObject = ((PreSetDef.PreSetDefinition)object).createObject(project);
    }
    final Object nestedObject = object;
    final Object realObject = rObject;
    
    new NestedCreator(method)
    {
      Object create(Project project, Object parent, Object ignore)
        throws InvocationTargetException, IllegalAccessException
      {
        if (!getMethod().getName().endsWith("Configured")) {
          getMethod().invoke(parent, new Object[] { realObject });
        }
        return nestedObject;
      }
      
      Object getRealObject()
      {
        return realObject;
      }
      
      void store(Object parent, Object child)
        throws InvocationTargetException, IllegalAccessException, InstantiationException
      {
        if (getMethod().getName().endsWith("Configured")) {
          getMethod().invoke(parent, new Object[] { realObject });
        }
      }
    };
  }
  
  private void insertAddTypeMethod(Method method)
  {
    Class<?> argClass = method.getParameterTypes()[0];
    int size = addTypeMethods.size();
    for (int c = 0; c < size; c++)
    {
      Method current = (Method)addTypeMethods.get(c);
      if (current.getParameterTypes()[0].equals(argClass))
      {
        if ("addConfigured".equals(method.getName())) {
          addTypeMethods.set(c, method);
        }
        return;
      }
      if (current.getParameterTypes()[0].isAssignableFrom(argClass))
      {
        addTypeMethods.add(c, method);
        return;
      }
    }
    addTypeMethods.add(method);
  }
  
  private Method findMatchingMethod(Class<?> paramClass, List<Method> methods)
  {
    if (paramClass == null) {
      return null;
    }
    Class<?> matchedClass = null;
    Method matchedMethod = null;
    for (Method method : methods)
    {
      Class<?> methodClass = method.getParameterTypes()[0];
      if (methodClass.isAssignableFrom(paramClass)) {
        if (matchedClass == null)
        {
          matchedClass = methodClass;
          matchedMethod = method;
        }
        else if (!methodClass.isAssignableFrom(matchedClass))
        {
          throw new BuildException("ambiguous: types " + matchedClass.getName() + " and " + methodClass.getName() + " match " + paramClass.getName());
        }
      }
    }
    return matchedMethod;
  }
  
  private String condenseText(String text)
  {
    if (text.length() <= 20) {
      return text;
    }
    int ends = (20 - "...".length()) / 2;
    return new StringBuffer(text).replace(ends, text.length() - ends, "...").toString();
  }
  
  private static class MethodAndObject
  {
    private final Method method;
    private final Object object;
    
    public MethodAndObject(Method method, Object object)
    {
      this.method = method;
      this.object = object;
    }
  }
  
  private AntTypeDefinition findRestrictedDefinition(ComponentHelper helper, String componentName, List<Method> methods)
  {
    AntTypeDefinition definition = null;
    Class<?> matchedDefinitionClass = null;
    
    List<AntTypeDefinition> definitions = helper.getRestrictedDefinitions(componentName);
    if (definitions == null) {
      return null;
    }
    synchronized (definitions)
    {
      for (AntTypeDefinition d : definitions)
      {
        Class<?> exposedClass = d.getExposedClass(helper.getProject());
        if (exposedClass != null)
        {
          Method method = findMatchingMethod(exposedClass, methods);
          if (method != null)
          {
            if (matchedDefinitionClass != null) {
              throw new BuildException("ambiguous: restricted definitions for " + componentName + " " + matchedDefinitionClass + " and " + exposedClass);
            }
            matchedDefinitionClass = exposedClass;
            definition = d;
          }
        }
      }
    }
    return definition;
  }
  
  private MethodAndObject createRestricted(ComponentHelper helper, String elementName, List<Method> addTypeMethods)
  {
    Project project = helper.getProject();
    
    AntTypeDefinition restrictedDefinition = findRestrictedDefinition(helper, elementName, addTypeMethods);
    if (restrictedDefinition == null) {
      return null;
    }
    Method addMethod = findMatchingMethod(restrictedDefinition
      .getExposedClass(project), addTypeMethods);
    if (addMethod == null) {
      throw new BuildException("Ant Internal Error - contract mismatch for " + elementName);
    }
    Object addedObject = restrictedDefinition.create(project);
    if (addedObject == null) {
      throw new BuildException("Failed to create object " + elementName + " of type " + restrictedDefinition.getTypeClass(project));
    }
    return new MethodAndObject(addMethod, addedObject);
  }
  
  private MethodAndObject createTopLevel(ComponentHelper helper, String elementName, List<Method> methods)
  {
    Class<?> clazz = helper.getComponentClass(elementName);
    if (clazz == null) {
      return null;
    }
    Method addMethod = findMatchingMethod(clazz, addTypeMethods);
    if (addMethod == null) {
      return null;
    }
    Object addedObject = helper.createComponent(elementName);
    return new MethodAndObject(addMethod, addedObject);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */