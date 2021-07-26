package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.FileNameMapper;

class XSLTProcess$StyleMapper
  implements FileNameMapper
{
  private XSLTProcess$StyleMapper(XSLTProcess paramXSLTProcess) {}
  
  public void setFrom(String from) {}
  
  public void setTo(String to) {}
  
  public String[] mapFileName(String xmlFile)
  {
    int dotPos = xmlFile.lastIndexOf('.');
    if (dotPos > 0) {
      xmlFile = xmlFile.substring(0, dotPos);
    }
    return new String[] { xmlFile + XSLTProcess.access$100(this$0) };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.XSLTProcess.StyleMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */