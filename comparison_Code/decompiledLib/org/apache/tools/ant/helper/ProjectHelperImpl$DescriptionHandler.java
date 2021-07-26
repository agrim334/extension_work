package org.apache.tools.ant.helper;

import org.apache.tools.ant.Project;
import org.xml.sax.DocumentHandler;

class ProjectHelperImpl$DescriptionHandler
  extends ProjectHelperImpl.AbstractHandler
{
  public ProjectHelperImpl$DescriptionHandler(ProjectHelperImpl helperImpl, DocumentHandler parentHandler)
  {
    super(helperImpl, parentHandler);
  }
  
  public void characters(char[] buf, int start, int count)
  {
    String text = new String(buf, start, count);
    String currentDescription = ProjectHelperImpl.access$200(helperImpl).getDescription();
    if (currentDescription == null) {
      ProjectHelperImpl.access$200(helperImpl).setDescription(text);
    } else {
      ProjectHelperImpl.access$200(helperImpl).setDescription(currentDescription + text);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelperImpl.DescriptionHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */