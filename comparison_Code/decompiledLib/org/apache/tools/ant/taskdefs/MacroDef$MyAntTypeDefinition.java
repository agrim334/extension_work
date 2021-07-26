package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.Project;

class MacroDef$MyAntTypeDefinition
  extends AntTypeDefinition
{
  private MacroDef macroDef;
  
  public MacroDef$MyAntTypeDefinition(MacroDef macroDef)
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

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MacroDef.MyAntTypeDefinition
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */