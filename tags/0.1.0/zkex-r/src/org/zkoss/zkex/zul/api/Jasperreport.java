package org.zkoss.zkex.zul.api;

import java.sql.Connection;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import org.zkoss.zul.api.Iframe;

public abstract interface Jasperreport extends Iframe
{
  public abstract Map getParameters();

  public abstract void setParameters(Map paramMap);

  public abstract JRDataSource getDatasource();

  public abstract void setDatasource(JRDataSource paramJRDataSource);

  public abstract String getType();

  public abstract void setType(String paramString);

  public abstract Locale getLocale();

  public abstract void setLocale(Locale paramLocale);

  public abstract void setHibernate(boolean paramBoolean);

  public abstract boolean isHibernate();

  public abstract void setDataConnection(Connection paramConnection);

  public abstract Connection getDataConnection();
}