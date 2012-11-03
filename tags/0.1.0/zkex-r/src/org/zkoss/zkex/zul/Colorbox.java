package org.zkoss.zkex.zul;

import java.io.IOException;
import java.util.Map;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.XulElement;

public class Colorbox extends XulElement
  implements org.zkoss.zkex.zul.api.Colorbox
{
  private String _color;
  private int _rgb;
  private boolean _disabled;

  public Colorbox()
  {
    this._color = "#000000";
    this._rgb = 0;
  }

  public void setColor(String color)
  {
    if (!(Objects.equals(color, this._color))) {
      this._color = color;
      this._rgb = ((this._color == null) ? 0 : decode(this._color));
      smartUpdate("color", this._color);
    }
  }

  public String getColor()
  {
    return this._color;
  }

  public void setValue(String value)
  {
    setColor(value);
  }

  public String getValue()
  {
    return getColor();
  }

  public int getRGB()
  {
    return this._rgb;
  }

  public boolean isDisabled()
  {
    return this._disabled;
  }

  public void setDisabled(boolean disabled)
  {
    if (this._disabled != disabled) {
      this._disabled = disabled;
      smartUpdate("disabled", this._disabled);
    }
  }

  public void service(AuRequest request, boolean everError)
  {
    String cmd = request.getCommand();
    if ("onChange".equals(cmd)) {
      Map data = request.getData();

      disableClientUpdate(true);
      try {
        setColor((String)data.get("color"));
      } finally {
        disableClientUpdate(false);
      }

      Events.postEvent(Event.getEvent(request));
    } else {
      super.service(request, everError); }
  }

  public String getZclass() {
    return ((this._zclass == null) ? "z-colorbox" : this._zclass);
  }

  protected boolean isChildable() {
    return false;
  }

  protected void renderProperties(ContentRenderer renderer) throws IOException
  {
    super.renderProperties(renderer);
    renderer.render("color", getColor());
    render(renderer, "disabled", this._disabled);
  }

  private static int decode(String color) {
    if (color == null)
      return 0;

    if ((color.length() != 7) || (!(color.startsWith("#"))))
      throw new UiException("Incorrect color format (#RRGGBB) : " + color);

    return Integer.parseInt(color.substring(1), 16);
  }

  static
  {
    addClientEvent(Colorbox.class, "onChange", 1);
  }
}