package org.apache.tools.ant.taskdefs;

import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.zip.Zip64Mode;

public final class Zip$Zip64ModeAttribute
  extends EnumeratedAttribute
{
  private static final Map<String, Zip64Mode> MODES = new HashMap();
  private static final String NEVER_KEY = "never";
  private static final String ALWAYS_KEY = "always";
  private static final String A_N_KEY = "as-needed";
  
  static
  {
    MODES.put("never", Zip64Mode.Never);
    MODES.put("always", Zip64Mode.Always);
    MODES.put("as-needed", Zip64Mode.AsNeeded);
  }
  
  public String[] getValues()
  {
    return new String[] { "never", "always", "as-needed" };
  }
  
  public static final Zip64ModeAttribute NEVER = new Zip64ModeAttribute("never");
  public static final Zip64ModeAttribute AS_NEEDED = new Zip64ModeAttribute("as-needed");
  
  private Zip$Zip64ModeAttribute(String name)
  {
    setValue(name);
  }
  
  public Zip64Mode getMode()
  {
    return (Zip64Mode)MODES.get(getValue());
  }
  
  public Zip$Zip64ModeAttribute() {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Zip.Zip64ModeAttribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */