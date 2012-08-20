package org.zkoss.zssex.util;

public class PictureHelper
{
  public static int getPictureFormat(String ext)
  {
    if (("jpg".equals(ext)) || ("jpeg".equals(ext)))
      return 5;
    if ("png".equals(ext)) {
      return 6;
    }

    throw new RuntimeException("Unsupport picture format:" + ext);
  }
}