package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.Collections;

public class MajoritySelector
  extends BaseSelectorContainer
{
  private boolean allowtie = true;
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    if (hasSelectors())
    {
      buf.append("{majorityselect: ");
      buf.append(super.toString());
      buf.append("}");
    }
    return buf.toString();
  }
  
  public void setAllowtie(boolean tiebreaker)
  {
    allowtie = tiebreaker;
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    validate();
    int yesvotes = 0;
    int novotes = 0;
    for (FileSelector fs : Collections.list(selectorElements())) {
      if (fs.isSelected(basedir, filename, file)) {
        yesvotes++;
      } else {
        novotes++;
      }
    }
    if (yesvotes > novotes) {
      return true;
    }
    if (novotes > yesvotes) {
      return false;
    }
    return allowtie;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.MajoritySelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */