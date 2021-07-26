package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.attribute.AttributeNamespace;

public final class AttributeNamespaceDef
  extends AntlibDefinition
{
  public void execute()
  {
    String componentName = ProjectHelper.nsToComponentName(
      getURI());
    AntTypeDefinition def = new AntTypeDefinition();
    def.setName(componentName);
    def.setClassName(AttributeNamespace.class.getName());
    def.setClass(AttributeNamespace.class);
    def.setRestrict(true);
    def.setClassLoader(AttributeNamespace.class.getClassLoader());
    ComponentHelper.getComponentHelper(getProject())
      .addDataTypeDefinition(def);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.AttributeNamespaceDef
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */