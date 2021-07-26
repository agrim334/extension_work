package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class SizeSelector$ByteUnits
  extends EnumeratedAttribute
{
  public String[] getValues()
  {
    return new String[] { "K", "k", "kilo", "KILO", "Ki", "KI", "ki", "kibi", "KIBI", "M", "m", "mega", "MEGA", "Mi", "MI", "mi", "mebi", "MEBI", "G", "g", "giga", "GIGA", "Gi", "GI", "gi", "gibi", "GIBI", "T", "t", "tera", "TERA", "Ti", "TI", "ti", "tebi", "TEBI" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.SizeSelector.ByteUnits
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */