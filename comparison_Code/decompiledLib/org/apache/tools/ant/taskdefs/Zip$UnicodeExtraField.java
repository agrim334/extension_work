package org.apache.tools.ant.taskdefs;

import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.zip.ZipOutputStream.UnicodeExtraFieldPolicy;

public final class Zip$UnicodeExtraField
  extends EnumeratedAttribute
{
  private static final Map<String, ZipOutputStream.UnicodeExtraFieldPolicy> POLICIES = new HashMap();
  private static final String NEVER_KEY = "never";
  private static final String ALWAYS_KEY = "always";
  private static final String N_E_KEY = "not-encodeable";
  
  static
  {
    POLICIES.put("never", ZipOutputStream.UnicodeExtraFieldPolicy.NEVER);
    
    POLICIES.put("always", ZipOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
    
    POLICIES.put("not-encodeable", ZipOutputStream.UnicodeExtraFieldPolicy.NOT_ENCODEABLE);
  }
  
  public String[] getValues()
  {
    return new String[] { "never", "always", "not-encodeable" };
  }
  
  public static final UnicodeExtraField NEVER = new UnicodeExtraField("never");
  
  private Zip$UnicodeExtraField(String name)
  {
    setValue(name);
  }
  
  public ZipOutputStream.UnicodeExtraFieldPolicy getPolicy()
  {
    return (ZipOutputStream.UnicodeExtraFieldPolicy)POLICIES.get(getValue());
  }
  
  public Zip$UnicodeExtraField() {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Zip.UnicodeExtraField
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */