package org.zkoss.zkex.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.XulElement;

public class Fisheyebar extends XulElement
  implements org.zkoss.zkex.zul.api.Fisheyebar
{
  private int _itemwd = 50;
  private int _itemhgh = 50;
  private int _itemmaxwd = 200;
  private int _itemmaxhgh = 200;
  private int _itemPadding = 10;
  private String _orient = "horizontal";
  private String _attachEdge = "center";
  private String _labelEdge = "bottom";

  public int getItemWidth()
  {
    return this._itemwd;
  }

  public void setItemWidth(int itemwd) throws WrongValueException
  {
    if (itemwd <= 0)
      throw new WrongValueException("Positive is required: " + itemwd);

    if (this._itemwd != itemwd) {
      this._itemwd = itemwd;
      smartUpdate("itemWidth", itemwd);
    }
  }

  public int getItemHeight()
  {
    return this._itemhgh;
  }

  public void setItemHeight(int itemhgh) throws WrongValueException
  {
    if (itemhgh <= 0)
      throw new WrongValueException("Positive is required: " + itemhgh);

    if (this._itemhgh != itemhgh) {
      this._itemhgh = itemhgh;
      smartUpdate("itemHeight", itemhgh);
    }
  }

  public int getItemMaxWidth()
  {
    return this._itemmaxwd;
  }

  public void setItemMaxWidth(int itemmaxwd) throws WrongValueException
  {
    if (itemmaxwd <= 0)
      throw new WrongValueException("Positive is required: " + itemmaxwd);

    if (this._itemmaxwd != itemmaxwd) {
      this._itemmaxwd = itemmaxwd;
      smartUpdate("itemMaxWidth", itemmaxwd);
    }
  }

  public int getItemMaxHeight()
  {
    return this._itemmaxhgh;
  }

  public void setItemMaxHeight(int itemmaxhgh) throws WrongValueException
  {
    if (itemmaxhgh <= 0)
      throw new WrongValueException("Positive is required: " + itemmaxhgh);

    if (this._itemmaxhgh != itemmaxhgh) {
      this._itemmaxhgh = itemmaxhgh;
      smartUpdate("itemMaxHeight", itemmaxhgh);
    }
  }

  public int getItemPadding()
  {
    return this._itemPadding;
  }

  public void setItemPadding(int itemPadding) throws WrongValueException
  {
    if (itemPadding <= 0)
      throw new WrongValueException("Positive is required: " + itemPadding);

    if (this._itemPadding != itemPadding) {
      this._itemPadding = itemPadding;
      smartUpdate("itemPadding", itemPadding);
    }
  }

  public String getOrient()
  {
    return this._orient;
  }

  public void setOrient(String orient)
    throws WrongValueException
  {
    if ((!("horizontal".equals(orient))) && (!("vertical".equals(orient))))
      throw new WrongValueException(orient);

    if (!(Objects.equals(this._orient, orient))) {
      this._orient = orient;
      smartUpdate("orient", orient);
    }
  }

  public String getAttachEdge()
  {
    return this._attachEdge;
  }

  public void setAttachEdge(String attachEdge)
    throws WrongValueException
  {
    if ((attachEdge == null) || (attachEdge.length() == 0))
      throw new WrongValueException("Empty attachEdge not allowed");
    if (!(this._attachEdge.equals(attachEdge))) {
      this._attachEdge = attachEdge;
      smartUpdate("attachEdge", attachEdge);
    }
  }

  public String getLabelEdge()
  {
    return this._labelEdge;
  }

  public void setLabelEdge(String labelEdge)
    throws WrongValueException
  {
    if ((labelEdge == null) || (labelEdge.length() == 0))
      throw new WrongValueException("Empty labelEdge not allowed");
    if (!(this._labelEdge.equals(labelEdge))) {
      this._labelEdge = labelEdge;
      smartUpdate("labelEdge", labelEdge);
    }
  }

  public String getZclass() {
    return ((this._zclass == null) ? "z-fisheyebar" : this._zclass);
  }

  public void beforeChildAdded(Component child, Component refChild) {
    if (!(child instanceof Fisheye))
      throw new UiException("Unsupported child for fisheyebar: " + child);
    super.beforeChildAdded(child, refChild);
  }

  protected void renderProperties(ContentRenderer renderer) throws IOException
  {
//    Runtime.init(this);
    super.renderProperties(renderer);
    if (this._itemwd != 50)
      renderer.render("itemWidth", this._itemwd);
    if (this._itemhgh != 50)
      renderer.render("itemHeight", this._itemhgh);
    if (this._itemmaxwd != 200)
      renderer.render("itemMaxWidth", this._itemmaxwd);
    if (this._itemmaxhgh != 200)
      renderer.render("itemMaxHeight", this._itemmaxhgh);
    if (this._itemPadding != 10)
      renderer.render("itemPadding", this._itemPadding);
    if (!(this._orient.equals("horizontal")))
      render(renderer, "orient", this._orient);
    if (!(this._attachEdge.equals("center")))
      render(renderer, "attachEdge", this._attachEdge);
    if (!(this._labelEdge.equals("bottom")))
      render(renderer, "labelEdge", this._labelEdge);
  }
}