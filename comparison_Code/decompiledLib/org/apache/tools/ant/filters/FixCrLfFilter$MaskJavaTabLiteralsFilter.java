package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

class FixCrLfFilter$MaskJavaTabLiteralsFilter
  extends FixCrLfFilter.SimpleFilterReader
{
  private boolean editsBlocked = false;
  private static final int JAVA = 1;
  private static final int IN_CHAR_CONST = 2;
  private static final int IN_STR_CONST = 3;
  private static final int IN_SINGLE_COMMENT = 4;
  private static final int IN_MULTI_COMMENT = 5;
  private static final int TRANS_TO_COMMENT = 6;
  private static final int TRANS_FROM_MULTI = 8;
  private int state;
  
  public FixCrLfFilter$MaskJavaTabLiteralsFilter(Reader in)
  {
    super(in);
    state = 1;
  }
  
  public boolean editsBlocked()
  {
    return (editsBlocked) || (super.editsBlocked());
  }
  
  public int read()
    throws IOException
  {
    int thisChar = super.read();
    
    editsBlocked = ((state == 2) || (state == 3));
    switch (state)
    {
    case 1: 
      switch (thisChar)
      {
      case 39: 
        state = 2;
        break;
      case 34: 
        state = 3;
        break;
      case 47: 
        state = 6;
      }
      break;
    case 2: 
      switch (thisChar)
      {
      case 39: 
        state = 1;
      }
      break;
    case 3: 
      switch (thisChar)
      {
      case 34: 
        state = 1;
      }
      break;
    case 4: 
      switch (thisChar)
      {
      case 10: 
      case 13: 
        state = 1;
      }
      break;
    case 5: 
      switch (thisChar)
      {
      case 42: 
        state = 8;
      }
      break;
    case 6: 
      switch (thisChar)
      {
      case 42: 
        state = 5;
        break;
      case 47: 
        state = 4;
        break;
      case 39: 
        state = 2;
        break;
      case 34: 
        state = 3;
        break;
      default: 
        state = 1;
      }
      break;
    case 8: 
      switch (thisChar)
      {
      case 47: 
        state = 1;
      }
      break;
    }
    return thisChar;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.MaskJavaTabLiteralsFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */