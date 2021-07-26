package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.Task;

class BorlandDeploymentTool$1
  extends DescriptorHandler
{
  BorlandDeploymentTool$1(BorlandDeploymentTool this$0, Task task, File srcDir, File paramFile1)
  {
    super(task, srcDir);
  }
  
  protected void processElement()
  {
    if ("type-storage".equals(currentElement)) {
      ejbFiles.put(currentText, new File(val$srcDir, currentText
        .substring("META-INF/".length())));
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.BorlandDeploymentTool.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */