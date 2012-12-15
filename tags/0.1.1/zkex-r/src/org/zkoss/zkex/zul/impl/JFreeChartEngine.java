package org.zkoss.zkex.zul.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.CategoryLabelEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.JFreeChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.TickLabelEntity;
import org.jfree.chart.entity.TitleEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.WaferMapPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.WaferMapRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.DefaultWindDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.WindDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.zkoss.lang.Objects;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Area;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.ChartModel;
import org.zkoss.zul.DialModel;
import org.zkoss.zul.DialModelRange;
import org.zkoss.zul.DialModelScale;
import org.zkoss.zul.GanttModel;
import org.zkoss.zul.HiLoModel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.WaferMapModel;
import org.zkoss.zul.XYModel;
import org.zkoss.zul.XYZModel;
import org.zkoss.zul.impl.ChartEngine;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JFreeChartEngine implements ChartEngine, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	private static final String DEFAULT_HI_LO_SERIES = "High Low Data";
	private transient boolean _threeD;
	private transient String _type;
	private transient ChartImpl _chartImpl;
	private transient SimpleDateFormat _df;
	
	private static Map _periodMap = new HashMap(10);

	private ChartImpl getChartImpl(Chart chart) {
		if ((Objects.equals(chart.getType(), this._type))
				&& (this._threeD == chart.isThreeD())) {
			return this._chartImpl;
		}

		SimpleDateFormat df = getDateFormat();
		if ("pie".equals(chart.getType())) {
			this._chartImpl = (chart.isThreeD() ? new Pie3d(null) : new Pie(
					null));
		} else if ("ring".equals(chart.getType())) {
			this._chartImpl = new Ring(null);
		} else if ("bar".equals(chart.getType())) {
			this._chartImpl = (chart.isThreeD() ? new Bar3d(null) : new Bar(
					null));
		} else if ("line".equals(chart.getType())) {
			this._chartImpl = (chart.isThreeD() ? new Line3d(null) : new Line(
					null));
		} else if ("area".equals(chart.getType())) {
			this._chartImpl = new AreaImpl(null);
		} else if ("stacked_bar".equals(chart.getType())) {
			this._chartImpl = (chart.isThreeD() ? new StackedBar3d(null)
					: new StackedBar(null));
		} else if ("stacked_area".equals(chart.getType())) {
			this._chartImpl = new StackedArea(null);
		} else if ("waterfall".equals(chart.getType())) {
			this._chartImpl = new Waterfall(null);
		} else if ("polar".equals(chart.getType())) {
			this._chartImpl = new Polar(null);
		} else if ("scatter".equals(chart.getType())) {
			this._chartImpl = new Scatter(null);
		} else if ("time_series".equals(chart.getType())) {
			this._chartImpl = new TimeSeries(null);
			TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				df.setTimeZone(tz);
			}
			if (chart.getDateFormat() != null) {
				df.applyPattern(chart.getDateFormat());
			}
		} else if ("step_area".equals(chart.getType())) {
			this._chartImpl = new StepArea(null);
		} else if ("step".equals(chart.getType())) {
			this._chartImpl = new Step(null);
		} else if ("histogram".equals(chart.getType())) {
			this._chartImpl = new Histogram(null);
		} else if ("candlestick".equals(chart.getType())) {
			this._chartImpl = new Candlestick(null);
			TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				df.setTimeZone(tz);
			}
			if (chart.getDateFormat() != null) {
				df.applyPattern(chart.getDateFormat());
			}
		} else if ("highlow".equals(chart.getType())) {
			this._chartImpl = new Highlow(null);
			TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				df.setTimeZone(tz);
			}
			if (chart.getDateFormat() != null) {
				df.applyPattern(chart.getDateFormat());
			}
		} else if ("bubble".equals(chart.getType())) {
			this._chartImpl = new Bubble(null);
		} else if ("wafermap".equals(chart.getType())) {
			this._chartImpl = new Wafermap(null);
		} else if ("gantt".equals(chart.getType())) {
			this._chartImpl = new Gantt(null);
			TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				df.setTimeZone(tz);
			}
			if (chart.getDateFormat() == null)
				df.applyPattern("MMM d ''yy");
			else {
				df.applyPattern(chart.getDateFormat());
			}
		} else if ("wind".equals(chart.getType())) {
			this._chartImpl = new Wind(null);
			TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				df.setTimeZone(tz);
			}
			if (chart.getDateFormat() != null)
				df.applyPattern(chart.getDateFormat());
		} else if ("dial".equals(chart.getType())) {
			this._chartImpl = new Dial(null);
		} else {
			throw new UiException("Unsupported chart type yet: "
					+ chart.getType());
		}
		this._threeD = chart.isThreeD();
		this._type = chart.getType();
		return this._chartImpl;
	}

	private SimpleDateFormat getDateFormat() {
		if (this._df == null) {
			this._df = new SimpleDateFormat();
			this._df.setTimeZone(TimeZones.getCurrent());
		}
		return this._df;
	}

	protected boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
		return false;
	}

	public byte[] drawChart(Object data) {
		Chart chart = (Chart) data;
		ChartImpl impl = getChartImpl(chart);

		JFreeChart jfchart = impl.createChart(chart);

		if (!prepareJFreeChart(jfchart, chart)) {
			Plot plot = jfchart.getPlot();
			float alpha = chart.getFgAlpha() / 255.0F;
			plot.setForegroundAlpha(alpha);

			alpha = chart.getBgAlpha() / 255.0F;
			plot.setBackgroundAlpha(alpha);

			int[] bgRGB = chart.getBgRGB();
			if (bgRGB != null) {
				plot.setBackgroundPaint(new Color(bgRGB[0], bgRGB[1], bgRGB[2],
						chart.getBgAlpha()));
			}

			int[] paneRGB = chart.getPaneRGB();
			if (paneRGB != null) {
				jfchart.setBackgroundPaint(new Color(paneRGB[0], paneRGB[1],
						paneRGB[2], chart.getPaneAlpha()));
			}

			Font tfont = chart.getTitleFont();
			if (tfont != null) {
				jfchart.getTitle().setFont(tfont);
			}

			Font lfont = chart.getLegendFont();
			if (lfont != null) {
				jfchart.getLegend().setItemFont(lfont);
			}

			if ((plot instanceof PiePlot)) {
				PiePlot pplot = (PiePlot) plot;

				Font xlbfont = chart.getXAxisFont();
				if (xlbfont != null)
					pplot.setLabelFont(xlbfont);
			} else if ((plot instanceof CategoryPlot)) {
				CategoryPlot cplot = (CategoryPlot) plot;
				cplot.setRangeGridlinePaint(new Color(192, 192, 192));

				Font xlbfont = chart.getXAxisFont();
				Font xtkfont = chart.getXAxisTickFont();
				if (xlbfont != null) {
					cplot.getDomainAxis().setLabelFont(xlbfont);
				}
				if (xtkfont != null) {
					cplot.getDomainAxis().setTickLabelFont(xtkfont);
					cplot.getDomainAxis().setCategoryMargin(0.2);
					cplot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
				}

				Font ylbfont = chart.getYAxisFont();
				Font ytkfont = chart.getYAxisTickFont();
				if (ylbfont != null) {
					cplot.getRangeAxis().setLabelFont(ylbfont);
				}
				if (ytkfont != null)
					cplot.getRangeAxis().setTickLabelFont(ytkfont);
			} else if ((plot instanceof XYPlot)) {
				XYPlot xyplot = (XYPlot) plot;
				xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
				xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);

				Font xlbfont = chart.getXAxisFont();
				Font xtkfont = chart.getXAxisTickFont();
				if (xlbfont != null) {
					xyplot.getDomainAxis().setLabelFont(xlbfont);
				}
				if (xtkfont != null) {
					xyplot.getDomainAxis().setTickLabelFont(xtkfont);
				}

				Font ylbfont = chart.getYAxisFont();
				Font ytkfont = chart.getYAxisTickFont();
				if (ylbfont != null) {
					xyplot.getRangeAxis().setLabelFont(ylbfont);
				}
				if (ytkfont != null)
					xyplot.getRangeAxis().setTickLabelFont(ytkfont);
			} else if ((plot instanceof PolarPlot)) {
				PolarPlot pplot = (PolarPlot) plot;
				pplot.setAngleGridlinePaint(Color.LIGHT_GRAY);
				pplot.setRadiusGridlinePaint(Color.LIGHT_GRAY);

				Font xlbfont = chart.getXAxisFont();
				if (xlbfont != null) {
					pplot.setAngleLabelFont(xlbfont);
				}
			}

		}

		ChartRenderingInfo jfinfo = new ChartRenderingInfo();
		BufferedImage bi = jfchart.createBufferedImage(chart.getIntWidth(),
				chart.getIntHeight(), 3, jfinfo);

		if (chart.getChildren().size() > 20)
			chart.invalidate();
		chart.getChildren().clear();
		String preUrl;
		Iterator it;
		if ((Events.isListened(chart, "onClick", false))
				|| (chart.isShowTooltiptext())) {
//			int j = 0;
			preUrl = null;
			for (it = jfinfo.getEntityCollection().iterator(); it.hasNext();) {
				ChartEntity ce = (ChartEntity) it.next();
				String url = ce.getURLText();

				if (url != null) {
					if (preUrl == null)
						preUrl = url;
					else if (url.equals(preUrl)) {
						break;
					}

				}

				if ((!(ce instanceof JFreeChartEntity))
						&& ((!(ce instanceof TitleEntity)) || (!(((TitleEntity) ce)
								.getTitle() instanceof LegendTitle)))
						&& (!(ce instanceof PlotEntity))) {
					Area area = new Area();
					area.setParent(chart);
					area.setCoords(ce.getShapeCoords());
					area.setShape(ce.getShapeType());
					if ((chart.isShowTooltiptext())
							&& (ce.getToolTipText() != null)) {
						area.setTooltiptext(ce.getToolTipText());
					}
					area.setAttribute("url", ce.getURLText());
					impl.render(chart, area, ce);
					if (chart.getAreaListener() != null) {
						try {
							chart.getAreaListener().onRender(area, ce);
						} catch (Exception ex) {
							throw UiException.Aide.wrap(ex);
						}
					}
				}

			}

		}

		chart.removeAttribute("LEGEND_SEQ");
		chart.removeAttribute("TICK_SEQ");
		try {
			return EncoderUtil.encode(bi, "png", true);
		} catch (IOException ex) {
		}
		
		throw new RuntimeException("Can not draw chart.");
	}

	private PieDataset PieModelToPieDataset(PieModel model) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Iterator it = model.getCategories().iterator(); it.hasNext();) {
			Comparable category = (Comparable) it.next();
			Number value = model.getValue(category);
			dataset.setValue(category, value);
		}
		return dataset;
	}

	private PieDataset CategoryModelToPieDataset(CategoryModel model) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		Comparable defaultSeries = null;
		int max = 0;
		for (Iterator it = model.getKeys().iterator(); it.hasNext();) {
			List key = (List) it.next();
			Comparable series = (Comparable) key.get(0);
			if (defaultSeries == null) {
				defaultSeries = series;
				max = model.getCategories().size();
			}
			if (!Objects.equals(defaultSeries, series)) {
				continue;
			}
			Comparable category = (Comparable) key.get(1);
			Number value = model.getValue(series, category);
			dataset.setValue(category, value);

			max--;
			if (max == 0)
				break;
		}
		return dataset;
	}

	private CategoryDataset CategoryModelToCategoryDataset(CategoryModel model) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Iterator it = model.getKeys().iterator(); it.hasNext();) {
			List key = (List) it.next();
			Comparable series = (Comparable) key.get(0);
			Comparable category = (Comparable) key.get(1);
			Number value = model.getValue(series, category);
			dataset.setValue(value, series, category);
		}
		return dataset;
	}

	private XYDataset XYModelToXYDataset(XYModel model) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		for (Iterator it = model.getSeries().iterator(); it.hasNext();) {
			Comparable series = (Comparable) it.next();
			XYSeries xyser = new XYSeries(series, model.isAutoSort());
			int size = model.getDataCount(series);
			for (int j = 0; j < size; j++) {
				xyser.add(model.getX(series, j), model.getY(series, j), false);
			}
			dataset.addSeries(xyser);
		}
		return dataset;
	}

	private TableXYDataset XYModelToTableXYDataset(XYModel model) {
		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		for (Iterator it = model.getSeries().iterator(); it.hasNext();) {
			Comparable series = (Comparable) it.next();
			XYSeries xyser = new XYSeries(series, false, false);
			int size = model.getDataCount(series);
			for (int j = 0; j < size; j++) {
				xyser.add(model.getX(series, j), model.getY(series, j), false);
			}
			dataset.addSeries(xyser);
		}
		return dataset;
	}

	private XYDataset XYModelToTimeDataset(XYModel model, Chart chart) {
		TimeZone tz = chart.getTimeZone();
		if (tz == null)
			tz = TimeZones.getCurrent();
		String p = chart.getPeriod();
		if (p == null)
			p = "millisecond";
		Class pclass = (Class) _periodMap.get(p);
		if (pclass == null) {
			throw new UiException("Unsupported period for Time Series chart: "
					+ p);
		}
		TimeSeriesCollection dataset = new TimeSeriesCollection(tz);

		for (Iterator it = model.getSeries().iterator(); it.hasNext();) {
			Comparable series = (Comparable) it.next();
			org.jfree.data.time.TimeSeries tser = new org.jfree.data.time.TimeSeries(
					series);

			int size = model.getDataCount(series);
			for (int j = 0; j < size; j++) {
				RegularTimePeriod period = RegularTimePeriod
						.createInstance(pclass, new Date(model.getX(series, j)
								.longValue()), tz);

				tser.addOrUpdate(period, model.getY(series, j));
			}
			dataset.addSeries(tser);
		}
		return dataset;
	}

	private XYZDataset XYZModelToXYZDataset(XYZModel model) {
		DefaultXYZDataset dataset = new DefaultXYZDataset();
		for (Iterator it = model.getSeries().iterator(); it.hasNext();) {
			Comparable seriesKey = (Comparable) it.next();
			int size = model.getDataCount(seriesKey);
			double[][] data = new double[3][size];
			for (int j = 0; j < size; j++) {
				data[0][j] = model.getX(seriesKey, j).doubleValue();
				data[1][j] = model.getY(seriesKey, j).doubleValue();
				data[2][j] = model.getZ(seriesKey, j).doubleValue();
			}
			dataset.addSeries(seriesKey, data);
		}
		return dataset;
	}

	private WaferMapDataset WaferMapModelToWaferMapDataset(WaferMapModel model) {
		WaferMapDataset dataset = new WaferMapDataset(model.getXsize(),
				model.getYsize(), new Double(model.getSpace()));
		for (Iterator it = model.getEntrySet().iterator(); it.hasNext();) {
			Map.Entry me = (Map.Entry) it.next();
			WaferMapModel.IntPair ip = (WaferMapModel.IntPair) me.getKey();
			Number val = (Number) me.getValue();
			int x = ip.getX() + 1;
			int y = ip.getY();
			dataset.addValue(val.intValue(), x, y);
		}
		return dataset;
	}

	private GanttCategoryDataset GanttModelToGanttDataset(GanttModel model) {
		TaskSeriesCollection dataset = new TaskSeriesCollection();
		Comparable[] allseries = model.getAllSeries();
		int sz = allseries.length;
		for (int j = 0; j < sz; j++) {
			Comparable series = allseries[j];
			TaskSeries taskseries = new TaskSeries(String.valueOf(series));
			GanttModel.GanttTask[] tasks = model.getTasks(series);
			int tsz = tasks.length;
			for (int k = 0; k < tsz; k++) {
				GanttModel.GanttTask task = tasks[k];
				Task jtask = newTask(task);
				addSubtask(jtask, task);
				taskseries.add(jtask);
			}
			dataset.add(taskseries);
		}

		return dataset;
	}

	private Task newTask(GanttModel.GanttTask task) {
		Task jtask = new Task(task.getDescription(), task.getStart(),
				task.getEnd());
		jtask.setPercentComplete(task.getPercent());
		return jtask;
	}

	private void addSubtask(Task jtask, GanttModel.GanttTask task) {
		GanttModel.GanttTask[] tasks = task.getSubtasks();
		int sz = tasks.length;
		for (int j = 0; j < sz; j++) {
			GanttModel.GanttTask subtask = tasks[j];
			Task jsubtask = newTask(subtask);
			jtask.addSubtask(jsubtask);
			addSubtask(jsubtask, subtask);
		}
	}

	private WindDataset XYZModelToWindDataset(XYZModel model) {
		Collection allseries = model.getSeries();
		int ssize = allseries.size();
		Object[][][] wobjs = new Object[ssize][][];
		int k = 0;
		for (Iterator it = allseries.iterator(); it.hasNext(); k++) {
			Comparable seriesKey = (Comparable) it.next();
			int size = model.getDataCount(seriesKey);
			Object[][] data = new Object[size][3];
			wobjs[k] = data;
			for (int j = 0; j < size; j++) {
				data[j][0] = model.getX(seriesKey, j);
				data[j][1] = model.getY(seriesKey, j);
				data[j][2] = model.getZ(seriesKey, j);
			}
		}
		return new DefaultWindDataset((List) allseries, wobjs);
	}

	private OHLCDataset HiLoModelToOHLCDataset(HiLoModel model) {
		int size = model.getDataCount();
		OHLCDataItem[] items = new OHLCDataItem[size];

		for (int j = 0; j < size; j++) {
			Date date = model.getDate(j);
			Number open = model.getOpen(j);
			Number high = model.getHigh(j);
			Number low = model.getLow(j);
			Number close = model.getClose(j);
			Number volume = model.getVolume(j);

			OHLCDataItem item = new OHLCDataItem(date, doubleValue(open),
					doubleValue(high), doubleValue(low), doubleValue(close),
					doubleValue(volume));

			items[j] = item;
		}

		Comparable series = model.getSeries();
		if (series == null) {
			series = "High Low Data";
		}
		return new DefaultOHLCDataset(series, items);
	}

	private double doubleValue(Number n) {
		return n == null ? 0.0D : n.doubleValue();
	}

	private void decodeLegendInfo(Area area, LegendItemEntity info, Chart chart) {
		if (info == null) {
			return;
		}
		ChartModel model = chart.getModel();
		int seq = ((Integer) chart.getAttribute("LEGEND_SEQ")).intValue();

		if ((model instanceof PieModel)) {
			Comparable category = ((PieModel) model).getCategory(seq);
			area.setAttribute("category", category);
			area.setAttribute("value", ((PieModel) model).getValue(category));
			if ((chart.isShowTooltiptext()) && (info.getToolTipText() == null))
				area.setTooltiptext(category.toString());
		} else if ((model instanceof CategoryModel)) {
			Comparable series = ((CategoryModel) model).getSeries(seq);
			area.setAttribute("series", series);
			if ((chart.isShowTooltiptext()) && (info.getToolTipText() == null))
				area.setTooltiptext(series.toString());
		} else if ((model instanceof XYModel)) {
			Comparable series = ((XYModel) model).getSeries(seq);
			area.setAttribute("series", series);
			if ((chart.isShowTooltiptext()) && (info.getToolTipText() == null))
				area.setTooltiptext(series.toString());
		} else if ((model instanceof HiLoModel)) {
			Comparable series = ((HiLoModel) model).getSeries();
			if (series == null) {
				series = "High Low Data";
			}
			area.setAttribute("series", series);
			if ((chart.isShowTooltiptext()) && (info.getToolTipText() == null))
				area.setTooltiptext(series.toString());
		} else if ((model instanceof GanttModel)) {
			Comparable series = info.getSeriesKey();
			area.setAttribute("series", series);
			if ((chart.isShowTooltiptext()) && (info.getToolTipText() == null))
				area.setTooltiptext(series.toString());
		}
	}

	private void decodeTickLabelInfo(Area area, TickLabelEntity info,
			Chart chart) {
		if (info == null) {
			return;
		}
		ChartModel model = chart.getModel();
		int seq = ((Integer) chart.getAttribute("TICK_SEQ")).intValue();

		if ((model instanceof CategoryModel)) {
			Comparable category = ((CategoryModel) model).getCategory(seq);
			area.setAttribute("category", category);
			if ((chart.isShowTooltiptext()) && (info.getToolTipText() == null))
				area.setTooltiptext(category.toString());
		}
	}

	private void decodeCategoryLabelInfo(Area area, CategoryLabelEntity info,
			Chart chart) {
		Comparable category = info.getKey();
		area.setAttribute("category", category);
		if ((chart.isShowTooltiptext()) && (info.getToolTipText() == null))
			area.setTooltiptext(category.toString());
	}

	private void decodePieInfo(Area area, PieSectionEntity info) {
		if (info == null) {
			return;
		}

		PieDataset dataset = info.getDataset();
		Comparable category = info.getSectionKey();
		area.setAttribute("category", category);
		area.setAttribute("value", dataset.getValue(category));
	}

	private void decodeCategoryInfo(Area area, CategoryItemEntity info) {
		if (info == null) {
			return;
		}

		CategoryDataset dataset = info.getDataset();
		Comparable category = info.getColumnKey();
		Comparable series = info.getRowKey();

		area.setAttribute("series", series);
		area.setAttribute("category", category);

		if ((dataset instanceof GanttCategoryDataset)) {
			GanttCategoryDataset gd = (GanttCategoryDataset) dataset;
			area.setAttribute("start", gd.getStartValue(series, category));
			area.setAttribute("end", gd.getEndValue(series, category));
			area.setAttribute("percent",
					gd.getPercentComplete(series, category));
		} else {
			area.setAttribute("value", dataset.getValue(series, category));
		}
	}

	private void decodeXYInfo(Area area, XYItemEntity info, Chart chart) {
		if (info == null) {
			return;
		}
		TimeZone tz = chart.getTimeZone();
		if (tz == null)
			tz = TimeZones.getCurrent();

		XYDataset dataset = info.getDataset();
		int si = info.getSeriesIndex();
		int ii = info.getItem();

		area.setAttribute("series", dataset.getSeriesKey(si));

		if ((dataset instanceof OHLCDataset)) {
			OHLCDataset ds = (OHLCDataset) dataset;
			area.setAttribute("date", new Date(ds.getX(si, ii).longValue()));
			area.setAttribute("open", ds.getOpen(si, ii));
			area.setAttribute("high", ds.getHigh(si, ii));
			area.setAttribute("low", ds.getLow(si, ii));
			area.setAttribute("close", ds.getClose(si, ii));
			area.setAttribute("volume", ds.getVolume(si, ii));
		} else if ((dataset instanceof XYZDataset)) {
			XYZDataset ds = (XYZDataset) dataset;
			area.setAttribute("x", ds.getX(si, ii));
			area.setAttribute("y", ds.getY(si, ii));
			area.setAttribute("z", ds.getZ(si, ii));
		} else {
			area.setAttribute("x", dataset.getX(si, ii));
			area.setAttribute("y", dataset.getY(si, ii));
		}
	}

	private void setupDateAxis(JFreeChart jchart, Chart chart) {
		Plot plot = jchart.getPlot();
		DateAxis axisX = (DateAxis) ((XYPlot) plot).getDomainAxis();
		TimeZone zone = chart.getTimeZone();
		if (zone != null) {
			axisX.setTimeZone(zone);
		}
		if (chart.getDateFormat() != null)
			axisX.setDateFormatOverride(getDateFormat());
	}

	protected String getGanttTaskTooltip(Date start, Date end, Number percent) {
		return new MessageFormat("{2,number,0.0%}, {0} ~ {1}")
				.format(new Object[] { getDateFormat().format(start),
						getDateFormat().format(end), percent });
	}

	private PlotOrientation getOrientation(String orient) {
		return "horizontal".equals(orient) ? PlotOrientation.HORIZONTAL
				: PlotOrientation.VERTICAL;
	}

	static {
		_periodMap.put("millisecond", Millisecond.class);
		_periodMap.put("second", Second.class);
		_periodMap.put("minute", Minute.class);
		_periodMap.put("hour", Hour.class);
		_periodMap.put("day", Day.class);
		_periodMap.put("week", Week.class);
		_periodMap.put("month", Month.class);
		_periodMap.put("quarter", Quarter.class);
		_periodMap.put("year", Year.class);
	}

	private class Dial extends ChartImpl {

		private Dial() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model0 = chart.getModel();
			if (!(model0 instanceof DialModel)) {
				throw new UiException("model must be a org.zkoss.zul.DialModel");
			}
			DialPlot plot = new DialPlot();
			plot.setView(0.0D, 0.0D, 1.0D, 1.0D);
			DialModel model = (DialModel) model0;

			StandardDialFrame dialFrame = new StandardDialFrame();
			int[] bgRGB = model.getFrameBgRGB();
			if (bgRGB != null) {
				dialFrame.setBackgroundPaint(new Color(bgRGB[0], bgRGB[1],
						bgRGB[2], model.getFrameBgAlpha()));
			}
			int[] fgRGB = model.getFrameBgRGB();
			if (fgRGB != null) {
				dialFrame.setForegroundPaint(new Color(fgRGB[0], fgRGB[1],
						fgRGB[2]));
			}

			plot.setDialFrame(dialFrame);

			int[] bgRGB2 = model.getFrameBgRGB2();
			if (bgRGB2 != null) {
				GradientPaint gp = new GradientPaint(new Point(), new Color(
						bgRGB[0], bgRGB[1], bgRGB[2]), new Point(), new Color(
						bgRGB2[0], bgRGB2[1], bgRGB2[2]));

				DialBackground db = new DialBackground(gp);
				String direction = model.getGradientDirection();
				GradientPaintTransformType type = GradientPaintTransformType.VERTICAL;
				if ("vertical".equalsIgnoreCase(direction))
					type = GradientPaintTransformType.VERTICAL;
				else if ("horizontal".equalsIgnoreCase(direction))
					type = GradientPaintTransformType.HORIZONTAL;
				else if ("center_vertical".equalsIgnoreCase(direction))
					type = GradientPaintTransformType.CENTER_VERTICAL;
				else if ("center_horizontal".equalsIgnoreCase(direction))
					type = GradientPaintTransformType.CENTER_HORIZONTAL;
				else {
					type = GradientPaintTransformType.VERTICAL;
				}
				db.setGradientPaintTransformer(new StandardGradientPaintTransformer(
						type));
				plot.setBackground(db);
			}

			DialCap cap = new DialCap();
			double capRadius = model.getCapRadius();
			cap.setRadius(capRadius);
			plot.setCap(cap);

			int j = 0;
			for (int len = model.size(); j < len; j++) {
				DialModelScale scale = model.getScale(j);

				DefaultValueDataset dataset0 = new DefaultValueDataset(
						scale.getValue());
				plot.setDataset(0, dataset0);

				double textRadius = scale.getTextRadius();
				String text = scale.getText();
				if ((text != null) && (text.length() > 0)
						&& (textRadius > 0.0D)) {
					DialTextAnnotation annotation0 = new DialTextAnnotation(
							text);
					Font font = scale.getTextFont();
					if (font == null) {
						font = new Font("Dialog", 1, 14);
					}
					annotation0.setFont(font);
					annotation0.setRadius(textRadius);
					plot.addLayer(annotation0);
				}

				double valueRadius = scale.getValueRadius();
				if (valueRadius > 0.0D) {
					DialValueIndicator dvi = new DialValueIndicator(j);
					Font font = scale.getValueFont();
					if (font == null) {
						font = new Font("Dialog", 0, 10);
					}
					dvi.setFont(font);
					dvi.setOutlinePaint(Color.darkGray);
					dvi.setRadius(valueRadius);
					dvi.setAngle(scale.getValueAngle());
					plot.addLayer(dvi);
				}

				double low = scale.getScaleLowerBound();
				double up = scale.getScaleUpperBound();
				double sa = scale.getScaleStartAngle();
				double ext = scale.getScaleExtent();
				double ti = scale.getMajorTickInterval();
				int tc = scale.getMinorTickCount();

				StandardDialScale dscale = new StandardDialScale(low, up, sa,
						ext, ti, tc);

				dscale.setTickRadius(scale.getTickRadius());
				dscale.setTickLabelOffset(0.15D);
				Font font = scale.getValueFont();
				if (font == null) {
					font = new Font("Dialog", 0, 14);
				}
				dscale.setTickLabelFont(font);
				int[] tickRGB = scale.getTickRGB();
				if (tickRGB != null) {
					Paint tickColor = new Color(tickRGB[0], tickRGB[1],
							tickRGB[2]);
					dscale.setMajorTickPaint(tickColor);
					dscale.setMinorTickPaint(tickColor);
				}
				plot.addScale(j, dscale);
				plot.mapDatasetToScale(j, j);

				int k = 0;
				for (int klen = scale.rangeSize(); k < klen; k++) {
					DialModelRange rng = scale.getRange(k);
					int[] rngRGB = rng.getRangeRGB();
					if (rngRGB == null) {
						rngRGB = new int[] { 0, 0, 255 };
					}
					StandardDialRange range = new StandardDialRange(
							rng.getLowerBound(), rng.getUpperBound(),
							new Color(rngRGB[0], rngRGB[1], rngRGB[2]));
					range.setInnerRadius(rng.getInnerRadius());
					range.setOuterRadius(rng.getOuterRadius());
					plot.addLayer(range);
				}

				String type = scale.getNeedleType();
				double needleRadius = scale.getNeedleRadius();
				int[] needleRGB = scale.getNeedleRGB();
				if (needleRGB == null) {
					needleRGB = model.getFrameFgRGB();
				}
				DialPointer needle = new DialPointer.Pointer(j);
				needle.setRadius(needleRadius);
				if ("pin".equalsIgnoreCase(type)) {
					needle = new DialPointer.Pin(j);
					((DialPointer.Pin) needle).setPaint(new Color(needleRGB[0],
							needleRGB[1], needleRGB[2]));
				} else {
					((DialPointer.Pointer) needle).setFillPaint(new Color(
							needleRGB[0], needleRGB[1], needleRGB[2]));
				}

				plot.addLayer(needle);
			}

			JFreeChart jchart = new JFreeChart(plot);

			return jchart;
		}

		Dial(JFreeChartEngine e) {
			this();
		}
	}

	private class Wind extends ChartImpl {

		private Wind() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof XYZModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYZModel");
			}
			JFreeChart jchart = ChartFactory.createWindPlot(chart.getTitle(),
					chart.getXAxis(), chart.getYAxis(), JFreeChartEngine.this
							.XYZModelToWindDataset((XYZModel) model), chart
							.isShowLegend(), chart.isShowTooltiptext(), true);

			JFreeChartEngine.this.setupDateAxis(jchart, chart);
			return jchart;
		}

		Wind(JFreeChartEngine e) {
			this();
		}
	}

	private class Gantt extends ChartImpl {

		private Gantt() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof CategoryLabelEntity)) {
				area.setAttribute("entity", "CATEGORY");
				JFreeChartEngine.this.decodeCategoryLabelInfo(area,
						(CategoryLabelEntity) info, chart);
			} else if ((info instanceof CategoryItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeCategoryInfo(area,
						(CategoryItemEntity) info);
				if (chart.isShowTooltiptext())
					area.setTooltiptext(ganttTooltip(chart, area));
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof GanttModel)) {
				throw new UiException(
						"model must be a org.zkoss.zul.GanttModel");
			}
			return ChartFactory.createGanttChart(chart.getTitle(), chart
					.getYAxis(), chart.getXAxis(), JFreeChartEngine.this
					.GanttModelToGanttDataset((GanttModel) model), chart
					.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		private String ganttTooltip(Chart chart, Area area) {
			long start = ((Number) area.getAttribute("start")).longValue();
			Date startDate = new Date(start);
			long end = ((Number) area.getAttribute("end")).longValue();
			Date endDate = new Date(end);
			Number percent = (Number) area.getAttribute("percent");
			return JFreeChartEngine.this.getGanttTaskTooltip(startDate,
					endDate, percent);
		}

		Gantt(JFreeChartEngine e) {
			this();
		}
	}

	private static class MyWaferMapRenderer extends WaferMapRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private boolean _inrender = false;

		private MyWaferMapRenderer() {
		}

		public Paint getSeriesPaint(int series) {
			if (this._inrender) {
				return null;
			}

			Paint paint = super.getSeriesPaint(series);
			if (paint != null) {
				return paint;
			}

			boolean pre = this._inrender;
			try {
				this._inrender = true;
				Paint localPaint1 = lookupSeriesPaint(series);
				return localPaint1;
			} finally {
				this._inrender = pre;
			}
		}

		MyWaferMapRenderer(JFreeChartEngine e) {
			this();
		}
	}

	private class Wafermap extends ChartImpl {

		private Wafermap() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof WaferMapModel)) {
				throw new UiException(
						"model must be a org.zkoss.zul.WaferMapModel");
			}
			JFreeChart jfchart = ChartFactory.createWaferMapChart(chart
					.getTitle(), JFreeChartEngine.this
					.WaferMapModelToWaferMapDataset((WaferMapModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);

			WaferMapRenderer renderer = new JFreeChartEngine.MyWaferMapRenderer(
					null);
			((WaferMapPlot) jfchart.getPlot()).setRenderer(renderer);
			return jfchart;
		}

		Wafermap(JFreeChartEngine e) {
			this();
		}
	}

	private class Bubble extends ChartImpl {

		private Bubble() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof XYZModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYZModel");
			}
			return ChartFactory.createBubbleChart(chart.getTitle(), chart
					.getXAxis(), chart.getYAxis(), JFreeChartEngine.this
					.XYZModelToXYZDataset((XYZModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		Bubble(JFreeChartEngine e) {
			this();
		}
	}

	private class Highlow extends ChartImpl {

		private Highlow() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof HiLoModel)) {
				throw new UiException("model must be a org.zkoss.zul.HiLoModel");
			}
			JFreeChart jchart = ChartFactory.createHighLowChart(chart
					.getTitle(), chart.getXAxis(), chart.getYAxis(),
					JFreeChartEngine.this
							.HiLoModelToOHLCDataset((HiLoModel) model), chart
							.isShowLegend());

			JFreeChartEngine.this.setupDateAxis(jchart, chart);
			return jchart;
		}

		Highlow(JFreeChartEngine e) {
			this();
		}
	}

	private class Candlestick extends ChartImpl {

		private Candlestick() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof HiLoModel)) {
				throw new UiException("model must be a org.zkoss.zul.HiLoModel");
			}
			JFreeChart jchart = ChartFactory.createCandlestickChart(chart
					.getTitle(), chart.getXAxis(), chart.getYAxis(),
					JFreeChartEngine.this
							.HiLoModelToOHLCDataset((HiLoModel) model), chart
							.isShowLegend());

			JFreeChartEngine.this.setupDateAxis(jchart, chart);
			return jchart;
		}

		Candlestick(JFreeChartEngine e) {
			this();
		}
	}

	private class Histogram extends ChartImpl {

		private Histogram() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createHistogram(chart.getTitle(), chart
					.getXAxis(), chart.getYAxis(),
					(IntervalXYDataset) JFreeChartEngine.this
							.XYModelToXYDataset((XYModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		Histogram(JFreeChartEngine e) {
			this();
		}
	}

	private class Step extends ChartImpl {

		private Step() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createXYStepChart(chart.getTitle(),
					chart.getXAxis(), chart.getYAxis(),
					JFreeChartEngine.this.XYModelToXYDataset((XYModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		Step(JFreeChartEngine e) {
			this();
		}
	}

	private class StepArea extends ChartImpl {

		private StepArea() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createXYStepAreaChart(chart.getTitle(),
					chart.getXAxis(), chart.getYAxis(),
					JFreeChartEngine.this.XYModelToXYDataset((XYModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		StepArea(JFreeChartEngine e) {
			this();
		}
	}

	private class TimeSeries extends ChartImpl {

		private TimeSeries() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			JFreeChart jchart = ChartFactory.createTimeSeriesChart(chart
					.getTitle(), chart.getXAxis(), chart.getYAxis(),
					JFreeChartEngine.this.XYModelToTimeDataset((XYModel) model,
							chart), chart.isShowLegend(), chart
							.isShowTooltiptext(), true);

			JFreeChartEngine.this.setupDateAxis(jchart, chart);
			return jchart;
		}

		TimeSeries(JFreeChartEngine e) {
			this();
		}
	}

	private class Scatter extends ChartImpl {

		private Scatter() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createScatterPlot(chart.getTitle(),
					chart.getXAxis(), chart.getYAxis(),
					JFreeChartEngine.this.XYModelToXYDataset((XYModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		Scatter(JFreeChartEngine e) {
			this();
		}
	}

	private class Polar extends ChartImpl {

		private Polar() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createPolarChart(chart.getTitle(),
					JFreeChartEngine.this.XYModelToXYDataset((XYModel) model),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		Polar(JFreeChartEngine e) {
			this();
		}
	}

	private class Waterfall extends ChartImpl {

		private Waterfall() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof CategoryItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeCategoryInfo(area,
						(CategoryItemEntity) info);
			} else if ((info instanceof TickLabelEntity)) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer) chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("TICK_SEQ", seq);
				JFreeChartEngine.this.decodeTickLabelInfo(area,
						(TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException(
						"model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createWaterfallChart(chart.getTitle(), chart
					.getXAxis(), chart.getYAxis(), JFreeChartEngine.this
					.CategoryModelToCategoryDataset((CategoryModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		Waterfall(JFreeChartEngine e) {
			this();
		}
	}

	private class StackedArea extends ChartImpl {

		private StackedArea() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof CategoryItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeCategoryInfo(area,
						(CategoryItemEntity) info);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else if ((info instanceof TickLabelEntity)) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer) chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("TICK_SEQ", seq);
				JFreeChartEngine.this.decodeTickLabelInfo(area,
						(TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if ((model instanceof CategoryModel)) {
				return ChartFactory
						.createStackedAreaChart(
								chart.getTitle(),
								chart.getXAxis(),
								chart.getYAxis(),
								JFreeChartEngine.this
										.CategoryModelToCategoryDataset((CategoryModel) model),
								JFreeChartEngine.this.getOrientation(chart
										.getOrient()), chart.isShowLegend(),
								chart.isShowTooltiptext(), true);
			}

			if ((model instanceof XYModel)) {
				return ChartFactory
						.createStackedXYAreaChart(
								chart.getTitle(),
								chart.getXAxis(),
								chart.getYAxis(),
								JFreeChartEngine.this
										.XYModelToTableXYDataset((XYModel) model),
								JFreeChartEngine.this.getOrientation(chart
										.getOrient()), chart.isShowLegend(),
								chart.isShowTooltiptext(), true);
			}

			throw new UiException(
					"model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
		}

		StackedArea(JFreeChartEngine e) {
			this();
		}
	}

	private class StackedBar3d extends StackedBar {

		private StackedBar3d() {
			super(null);
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException(
						"model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createStackedBarChart3D(chart.getTitle(), chart
					.getXAxis(), chart.getYAxis(), JFreeChartEngine.this
					.CategoryModelToCategoryDataset((CategoryModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		StackedBar3d(JFreeChartEngine e) {
			this();
		}
	}

	private class StackedBar extends ChartImpl {

		private StackedBar() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof CategoryItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeCategoryInfo(area,
						(CategoryItemEntity) info);
			} else if ((info instanceof TickLabelEntity)) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer) chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("TICK_SEQ", seq);
				JFreeChartEngine.this.decodeTickLabelInfo(area,
						(TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException(
						"model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createStackedBarChart(chart.getTitle(), chart
					.getXAxis(), chart.getYAxis(), JFreeChartEngine.this
					.CategoryModelToCategoryDataset((CategoryModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		StackedBar(JFreeChartEngine e) {
			this();
		}
	}

	private class Line3d extends Line {

		private Line3d() {
			super(null);
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException(
						"model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createLineChart3D(chart.getTitle(), chart
					.getXAxis(), chart.getYAxis(), JFreeChartEngine.this
					.CategoryModelToCategoryDataset((CategoryModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		Line3d(JFreeChartEngine e) {
			this();
		}
	}

	private class Line extends ChartImpl {

		private Line() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof CategoryItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeCategoryInfo(area,
						(CategoryItemEntity) info);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else if ((info instanceof TickLabelEntity)) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer) chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("TICK_SEQ", seq);
				JFreeChartEngine.this.decodeTickLabelInfo(area,
						(TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if ((model instanceof CategoryModel)) {
				return ChartFactory
						.createLineChart(
								chart.getTitle(),
								chart.getXAxis(),
								chart.getYAxis(),
								JFreeChartEngine.this
										.CategoryModelToCategoryDataset((CategoryModel) model),
								JFreeChartEngine.this.getOrientation(chart
										.getOrient()), chart.isShowLegend(),
								chart.isShowTooltiptext(), true);
			}

			if ((model instanceof XYModel)) {
				return ChartFactory
						.createXYLineChart(chart.getTitle(), chart.getXAxis(),
								chart.getYAxis(), JFreeChartEngine.this
										.XYModelToXYDataset((XYModel) model),
								JFreeChartEngine.this.getOrientation(chart
										.getOrient()), chart.isShowLegend(),
								chart.isShowTooltiptext(), true);
			}

			throw new UiException(
					"model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
		}

		Line(JFreeChartEngine e) {
			this();
		}
	}

	private class AreaImpl extends ChartImpl {

		private AreaImpl() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof CategoryItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeCategoryInfo(area,
						(CategoryItemEntity) info);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else if ((info instanceof TickLabelEntity)) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer) chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("TICK_SEQ", seq);
				JFreeChartEngine.this.decodeTickLabelInfo(area,
						(TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if ((model instanceof CategoryModel)) {
				return ChartFactory
						.createAreaChart(
								chart.getTitle(),
								chart.getXAxis(),
								chart.getYAxis(),
								JFreeChartEngine.this
										.CategoryModelToCategoryDataset((CategoryModel) model),
								JFreeChartEngine.this.getOrientation(chart
										.getOrient()), chart.isShowLegend(),
								chart.isShowTooltiptext(), true);
			}

			if ((model instanceof XYModel)) {
				return ChartFactory
						.createXYAreaChart(chart.getTitle(), chart.getXAxis(),
								chart.getYAxis(), JFreeChartEngine.this
										.XYModelToXYDataset((XYModel) model),
								JFreeChartEngine.this.getOrientation(chart
										.getOrient()), chart.isShowLegend(),
								chart.isShowTooltiptext(), true);
			}

			throw new UiException(
					"model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
		}

		AreaImpl(JFreeChartEngine e) {
			this();
		}
	}

	private class Bar3d extends Bar {

		private Bar3d() {
			super(null);
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException(
						"model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createBarChart3D(chart.getTitle(), chart
					.getXAxis(), chart.getYAxis(), JFreeChartEngine.this
					.CategoryModelToCategoryDataset((CategoryModel) model),
					JFreeChartEngine.this.getOrientation(chart.getOrient()),
					chart.isShowLegend(), chart.isShowTooltiptext(), true);
		}

		Bar3d(JFreeChartEngine e) {
			this();
		}
	}

	private class Bar extends ChartImpl {

		private Bar() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof CategoryItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeCategoryInfo(area,
						(CategoryItemEntity) info);
			} else if ((info instanceof XYItemEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodeXYInfo(area, (XYItemEntity) info,
						chart);
			} else if ((info instanceof TickLabelEntity)) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer) chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("TICK_SEQ", seq);
				JFreeChartEngine.this.decodeTickLabelInfo(area,
						(TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			if ((model instanceof CategoryModel)) {
				return ChartFactory
						.createBarChart(
								chart.getTitle(),
								chart.getXAxis(),
								chart.getYAxis(),
								JFreeChartEngine.this
										.CategoryModelToCategoryDataset((CategoryModel) model),
								JFreeChartEngine.this.getOrientation(chart
										.getOrient()), chart.isShowLegend(),
								chart.isShowTooltiptext(), true);
			}

			if ((model instanceof XYModel)) {
				return ChartFactory
						.createXYBarChart(chart.getTitle(), chart.getXAxis(),
								false, chart.getYAxis(),
								(IntervalXYDataset) JFreeChartEngine.this
										.XYModelToXYDataset((XYModel) model),
								JFreeChartEngine.this.getOrientation(chart
										.getOrient()), chart.isShowLegend(),
								chart.isShowTooltiptext(), true);
			}

			throw new UiException(
					"model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
		}

		Bar(JFreeChartEngine e) {
			this();
		}
	}

	private class Ring extends Pie {

		private Ring() {
			super(null);
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			return ChartFactory.createRingChart(chart.getTitle(),
					getDataset(model), chart.isShowLegend(),
					chart.isShowTooltiptext(), true);
		}

		Ring(JFreeChartEngine e) {
			this();
		}
	}

	private class Pie3d extends Pie {

		private Pie3d() {
			super(null);
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			return ChartFactory.createPieChart3D(chart.getTitle(),
					getDataset(model), chart.isShowLegend(),
					chart.isShowTooltiptext(), true);
		}

		Pie3d(JFreeChartEngine e) {
			this();
		}
	}

	private class Pie extends ChartImpl {

		private Pie() {
			super(null);
		}

		public void render(Chart chart, Area area, ChartEntity info) {
			if ((info instanceof LegendItemEntity)) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer) chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(
						seq.intValue() + 1);
				chart.setAttribute("LEGEND_SEQ", seq);
				JFreeChartEngine.this.decodeLegendInfo(area,
						(LegendItemEntity) info, chart);
			} else if ((info instanceof PieSectionEntity)) {
				area.setAttribute("entity", "DATA");
				JFreeChartEngine.this.decodePieInfo(area,
						(PieSectionEntity) info);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext())
					area.setTooltiptext(chart.getTitle());
			}
		}

		protected PieDataset getDataset(ChartModel model) {
			if ((model instanceof CategoryModel))
				return JFreeChartEngine.this
						.CategoryModelToPieDataset((CategoryModel) model);
			if ((model instanceof PieModel)) {
				return JFreeChartEngine.this
						.PieModelToPieDataset((PieModel) model);
			}
			throw new UiException(
					"model must be a org.zkoss.zul.PieModel or a org.zkoss.zul.CategoryModel");
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = chart.getModel();
			return ChartFactory.createPieChart(chart.getTitle(),
					getDataset(model), chart.isShowLegend(),
					chart.isShowTooltiptext(), true);
		}

		Pie(JFreeChartEngine e) {
			this();
		}
	}

	private abstract class ChartImpl {

		private ChartImpl() {
		}

		abstract void render(Chart paramChart, Area paramArea,
				ChartEntity paramChartEntity);

		abstract JFreeChart createChart(Chart paramChart);

		public ChartImpl(JFreeChartEngine e) {
			this();
		}
	}

}