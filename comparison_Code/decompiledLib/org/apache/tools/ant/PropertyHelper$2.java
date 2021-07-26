package org.apache.tools.ant;

class PropertyHelper$2
  implements PropertyHelper.PropertyEvaluator
{
  private final String PREFIX = "ant.refid:";
  private final int PREFIX_LEN = "ant.refid:".length();
  
  public Object evaluate(String prop, PropertyHelper helper)
  {
    return (prop.startsWith("ant.refid:")) && (helper.getProject() != null) ? 
      helper.getProject().getReference(prop.substring(PREFIX_LEN)) : 
      null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.PropertyHelper.2
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */