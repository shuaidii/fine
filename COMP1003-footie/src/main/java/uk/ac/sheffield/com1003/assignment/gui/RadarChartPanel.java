package uk.ac.sheffield.com1003.assignment.gui;

import uk.ac.sheffield.com1003.assignment.codeprovided.PlayerProperty;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractRadarChart;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractRadarChartPanel;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractPlayerDashboardPanel;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.RadarAxisValues;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * SKELETON IMPLEMENTATION
 */

public class RadarChartPanel extends AbstractRadarChartPanel {
    public RadarChartPanel(AbstractPlayerDashboardPanel parentPanel, AbstractRadarChart radarPlot) {
        super(parentPanel, radarPlot);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // TODO remove code below and implement
        super.paintComponent(g);

//        Dimension d = getSize();
//        Graphics2D g2 = (Graphics2D) g;
//
//        Line2D l = new Line2D.Double(
//                0,
//                0,
//                d.width,
//                d.height
//        );
//        g2.draw(l);

        Dimension d = getSize();
        Graphics2D g2 = (Graphics2D) g;
        int radius = 0;
        for (int i = 0; i < 10; i++) {
            radius = 25 * (i + 1);
            int x = d.width / 2 - radius;
            int y = d.height / 2 - radius;

            Ellipse2D ellipse2D = new Ellipse2D.Double(x, y, radius * 2, radius * 2);
            g2.draw(ellipse2D);
        }

        int propertiesCount = getRadarChart().getPlayerRadarChartProperties().size();
        double angle = 2 * Math.PI / propertiesCount;
        for (int i = 0; i < propertiesCount; i++) {
            g2.rotate(angle, d.width / 2.0, d.height / 2.0);
            Line2D line = new Line2D.Double(
                    d.width / 2.0, d.height / 2.0, d.width / 2.0, d.height / 2.0 - radius
            );

            PlayerProperty playerProperty = getRadarChart().getPlayerRadarChartProperties().get(i);
            g2.draw(line);
            g2.drawString(playerProperty.getName(), d.width / 2, d.height / 2 - radius);
        }

        Map<PlayerProperty, RadarAxisValues> radarAxisValuesMap = getRadarChart().getRadarPlotAxesValues();

//        ArrayList<Double> valueList = new ArrayList<>();
//
//        if (getParentPanel().isMinCheckBoxSelected()) {
//            g2.setColor(Color.RED);
//            for (int i = 0; i < propertiesCount; i++) {
//                PlayerProperty playerProperty = getRadarChart().getPlayerRadarChartProperties().get(i);
//                valueList.add(radarAxisValuesMap.get(playerProperty).getMin());
//            }
//        }
//        if (valueList.size() == 0) {
//            return;
//        }
//        double startX = d.width / 2.0;
//        double startY = d.width / 2.0 - valueList.get(0) * 30;
//        for (int i = 1; i < 2; i++) {
//            double value = valueList.get(i);
//            double angelTmp = angle * i + 0.5 * Math.PI;
//            double endX = d.width / 2.0 + value * Math.cos(angelTmp)*30;
//            double endY = d.height / 2.0 - value * Math.sin(angelTmp)*30;
//
//            Line2D line = new Line2D.Double(startX, startY, endX, endY);
//            g2.draw(line);
//        }
    }


}

