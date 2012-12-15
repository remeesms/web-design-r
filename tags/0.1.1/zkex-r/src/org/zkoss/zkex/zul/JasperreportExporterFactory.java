package org.zkoss.zkex.zul;

import net.sf.jasperreports.engine.JRExporter;

public abstract interface JasperreportExporterFactory
{
  public abstract JRExporter getJRExporter(String paramString);

  public abstract String getFormat(String paramString);

  public abstract String getContentType(String paramString);
}