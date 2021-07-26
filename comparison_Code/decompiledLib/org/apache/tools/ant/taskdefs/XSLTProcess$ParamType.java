package org.apache.tools.ant.taskdefs;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

public enum XSLTProcess$ParamType
{
  STRING,  BOOLEAN,  INT,  LONG,  DOUBLE,  XPATH_STRING,  XPATH_BOOLEAN,  XPATH_NUMBER,  XPATH_NODE,  XPATH_NODESET;
  
  public static final Map<ParamType, QName> XPATH_TYPES;
  
  private XSLTProcess$ParamType() {}
  
  static
  {
    Map<ParamType, QName> m = new EnumMap(ParamType.class);
    m.put(XPATH_STRING, XPathConstants.STRING);
    m.put(XPATH_BOOLEAN, XPathConstants.BOOLEAN);
    m.put(XPATH_NUMBER, XPathConstants.NUMBER);
    m.put(XPATH_NODE, XPathConstants.NODE);
    m.put(XPATH_NODESET, XPathConstants.NODESET);
    XPATH_TYPES = Collections.unmodifiableMap(m);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.XSLTProcess.ParamType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */