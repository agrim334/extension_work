package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Parameter;

public class SizeSelector
  extends BaseExtendSelector
{
  private static final int KILO = 1000;
  private static final int KIBI = 1024;
  private static final int KIBI_POS = 4;
  private static final int MEGA = 1000000;
  private static final int MEGA_POS = 9;
  private static final int MEBI = 1048576;
  private static final int MEBI_POS = 13;
  private static final long GIGA = 1000000000L;
  private static final int GIGA_POS = 18;
  private static final long GIBI = 1073741824L;
  private static final int GIBI_POS = 22;
  private static final long TERA = 1000000000000L;
  private static final int TERA_POS = 27;
  private static final long TEBI = 1099511627776L;
  private static final int TEBI_POS = 31;
  private static final int END_POS = 36;
  public static final String SIZE_KEY = "value";
  public static final String UNITS_KEY = "units";
  public static final String WHEN_KEY = "when";
  private long size = -1L;
  private long multiplier = 1L;
  private long sizelimit = -1L;
  private Comparison when = Comparison.EQUAL;
  
  public String toString()
  {
    return String.format("{sizeselector value: %d compare: %s}", new Object[] {
      Long.valueOf(sizelimit), when.getValue() });
  }
  
  public void setValue(long size)
  {
    this.size = size;
    if ((multiplier != 0L) && (size > -1L)) {
      sizelimit = (size * multiplier);
    }
  }
  
  public void setUnits(ByteUnits units)
  {
    int i = units.getIndex();
    multiplier = 0L;
    if ((i > -1) && (i < 4)) {
      multiplier = 1000L;
    } else if (i < 9) {
      multiplier = 1024L;
    } else if (i < 13) {
      multiplier = 1000000L;
    } else if (i < 18) {
      multiplier = 1048576L;
    } else if (i < 22) {
      multiplier = 1000000000L;
    } else if (i < 27) {
      multiplier = 1073741824L;
    } else if (i < 31) {
      multiplier = 1000000000000L;
    } else if (i < 36) {
      multiplier = 1099511627776L;
    }
    if ((multiplier > 0L) && (size > -1L)) {
      sizelimit = (size * multiplier);
    }
  }
  
  public void setWhen(SizeComparisons when)
  {
    this.when = when;
  }
  
  public void setParameters(Parameter... parameters)
  {
    super.setParameters(parameters);
    if (parameters != null) {
      for (Parameter parameter : parameters)
      {
        String paramname = parameter.getName();
        if ("value".equalsIgnoreCase(paramname))
        {
          try
          {
            setValue(Long.parseLong(parameter.getValue()));
          }
          catch (NumberFormatException nfe)
          {
            setError("Invalid size setting " + parameter
              .getValue());
          }
        }
        else if ("units".equalsIgnoreCase(paramname))
        {
          ByteUnits units = new ByteUnits();
          units.setValue(parameter.getValue());
          setUnits(units);
        }
        else if ("when".equalsIgnoreCase(paramname))
        {
          SizeComparisons scmp = new SizeComparisons();
          scmp.setValue(parameter.getValue());
          setWhen(scmp);
        }
        else
        {
          setError("Invalid parameter " + paramname);
        }
      }
    }
  }
  
  public void verifySettings()
  {
    if (size < 0L) {
      setError("The value attribute is required, and must be positive");
    } else if (multiplier < 1L) {
      setError("Invalid Units supplied, must be K,Ki,M,Mi,G,Gi,T,or Ti");
    } else if (sizelimit < 0L) {
      setError("Internal error: Code is not setting sizelimit correctly");
    }
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    validate();
    if (file.isDirectory()) {
      return true;
    }
    long diff = file.length() - sizelimit;
    return when.evaluate(diff == 0L ? 0 : (int)(diff / Math.abs(diff)));
  }
  
  public static class ByteUnits
    extends EnumeratedAttribute
  {
    public String[] getValues()
    {
      return new String[] { "K", "k", "kilo", "KILO", "Ki", "KI", "ki", "kibi", "KIBI", "M", "m", "mega", "MEGA", "Mi", "MI", "mi", "mebi", "MEBI", "G", "g", "giga", "GIGA", "Gi", "GI", "gi", "gibi", "GIBI", "T", "t", "tera", "TERA", "Ti", "TI", "ti", "tebi", "TEBI" };
    }
  }
  
  public static class SizeComparisons
    extends Comparison
  {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.SizeSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */