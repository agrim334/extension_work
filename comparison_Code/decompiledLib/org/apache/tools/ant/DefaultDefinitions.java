package org.apache.tools.ant;

public final class DefaultDefinitions
{
  private static final String IF_NAMESPACE = "ant:if";
  private static final String UNLESS_NAMESPACE = "ant:unless";
  private static final String OATA = "org.apache.tools.ant.";
  private final ComponentHelper componentHelper;
  
  public DefaultDefinitions(ComponentHelper componentHelper)
  {
    this.componentHelper = componentHelper;
  }
  
  public void execute()
  {
    attributeNamespaceDef("ant:if");
    attributeNamespaceDef("ant:unless");
    
    ifUnlessDef("true", "IfTrueAttribute");
    ifUnlessDef("set", "IfSetAttribute");
    ifUnlessDef("blank", "IfBlankAttribute");
  }
  
  private void attributeNamespaceDef(String ns)
  {
    AntTypeDefinition def = new AntTypeDefinition();
    def.setName(ProjectHelper.nsToComponentName(ns));
    def.setClassName("org.apache.tools.ant.attribute.AttributeNamespace");
    def.setClassLoader(getClass().getClassLoader());
    def.setRestrict(true);
    componentHelper.addDataTypeDefinition(def);
  }
  
  private void ifUnlessDef(String name, String base)
  {
    String classname = "org.apache.tools.ant.attribute." + base;
    componentDef("ant:if", name, classname);
    componentDef("ant:unless", name, classname + "$Unless");
  }
  
  private void componentDef(String ns, String name, String classname)
  {
    AntTypeDefinition def = new AntTypeDefinition();
    def.setName(ProjectHelper.genComponentName(ns, name));
    def.setClassName(classname);
    def.setClassLoader(getClass().getClassLoader());
    def.setRestrict(true);
    componentHelper.addDataTypeDefinition(def);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.DefaultDefinitions
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */