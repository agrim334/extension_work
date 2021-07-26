package org.apache.tools.ant;

class PropertyHelper$1
  implements PropertyHelper.PropertyEvaluator
{
  private final String PREFIX = "toString:";
  private final int PREFIX_LEN = "toString:".length();
  
  public Object evaluate(String property, PropertyHelper propertyHelper)
  {
    Object o = null;
    if ((property.startsWith("toString:")) && (propertyHelper.getProject() != null)) {
      o = propertyHelper.getProject().getReference(property.substring(PREFIX_LEN));
    }
    return o == null ? null : o.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.PropertyHelper.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */