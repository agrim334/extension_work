package org.apache.tools.ant.types;

import java.util.Arrays;
import org.apache.tools.ant.BuildException;

public class Comparison
  extends EnumeratedAttribute
{
  private static final String[] VALUES = { "equal", "greater", "less", "ne", "ge", "le", "eq", "gt", "lt", "more" };
  public static final Comparison EQUAL = new Comparison("equal");
  public static final Comparison NOT_EQUAL = new Comparison("ne");
  public static final Comparison GREATER = new Comparison("greater");
  public static final Comparison LESS = new Comparison("less");
  public static final Comparison GREATER_EQUAL = new Comparison("ge");
  public static final Comparison LESS_EQUAL = new Comparison("le");
  private static final int[] EQUAL_INDEX = { 0, 4, 5, 6 };
  private static final int[] LESS_INDEX = { 2, 3, 5, 8 };
  private static final int[] GREATER_INDEX = { 1, 3, 4, 7, 9 };
  
  public Comparison() {}
  
  public Comparison(String value)
  {
    setValue(value);
  }
  
  public String[] getValues()
  {
    return VALUES;
  }
  
  public boolean evaluate(int comparisonResult)
  {
    if (getIndex() == -1) {
      throw new BuildException("Comparison value not set.");
    }
    int[] i = comparisonResult > 0 ? GREATER_INDEX : comparisonResult < 0 ? LESS_INDEX : EQUAL_INDEX;
    return Arrays.binarySearch(i, getIndex()) >= 0;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Comparison
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */