package org.apache.tools.ant.taskdefs.modules;

import org.apache.tools.ant.BuildException;

public class Jmod$ResolutionWarningSpec
{
  private Jmod.ResolutionWarningReason reason;
  
  public Jmod$ResolutionWarningSpec(Jmod this$0) {}
  
  public Jmod$ResolutionWarningSpec(Jmod this$0, String reason)
  {
    setReason(Jmod.ResolutionWarningReason.valueOf(reason));
  }
  
  public Jmod.ResolutionWarningReason getReason()
  {
    return reason;
  }
  
  public void setReason(Jmod.ResolutionWarningReason reason)
  {
    this.reason = reason;
  }
  
  public void validate()
  {
    if (reason == null) {
      throw new BuildException("reason attribute is required", this$0.getLocation());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Jmod.ResolutionWarningSpec
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */