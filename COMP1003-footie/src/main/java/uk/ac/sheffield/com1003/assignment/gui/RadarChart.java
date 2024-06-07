package uk.ac.sheffield.com1003.assignment.gui;

import uk.ac.sheffield.com1003.assignment.codeprovided.AbstractPlayerCatalog;
import uk.ac.sheffield.com1003.assignment.codeprovided.PlayerEntry;
import uk.ac.sheffield.com1003.assignment.codeprovided.PlayerProperty;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractRadarChart;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.RadarAxisValues;

import java.util.*;

/**
 * SKELETON IMPLEMENTATION
 */

public class RadarChart extends AbstractRadarChart {
    public RadarChart(AbstractPlayerCatalog playerCatalog, List<PlayerEntry> filteredPlayerEntriesList,
                      List<PlayerProperty> playerRadarChartProperties) {
        super(playerCatalog, filteredPlayerEntriesList, playerRadarChartProperties);
    }

    /**
     * This method should completely update (i.e. reset) the radar chart,
     * based on a newly selected category and player entries.
     * Since these values may have changed completely, it is recommended that you generate an entirely new
     * set of RadarAxisValues with the appropriate values according to the properties in each category.
     * PlayerPropertyMap may give you some hints on how to use the Map interface.
     *
     * @param radarChartPlayerProperties the list of PlayerProperty that the radar chart should plot
     * @param filteredPlayerEntriesList  the PlayerEntry that have currently been filtered by the GUI
     */
    @Override
    public void updateRadarChartContents(List<PlayerProperty> radarChartPlayerProperties,
                                         List<PlayerEntry> filteredPlayerEntriesList) {
        this.playerRadarChartProperties = radarChartPlayerProperties;
        this.filteredPlayerEntries = filteredPlayerEntriesList;

        radarAxesValues.clear();
        for (PlayerProperty playerProperty : playerRadarChartProperties) {
            if (filteredPlayerEntriesList.size() > 0) {
                double min = playerCatalog.getMinimumValue(playerProperty, filteredPlayerEntriesList);
                double max = playerCatalog.getMaximumValue(playerProperty, filteredPlayerEntriesList);
                double mean = playerCatalog.getMeanAverageValue(playerProperty, filteredPlayerEntriesList);
                RadarAxisValues radarAxisValues = new RadarAxisValues(min, max, mean);
                radarAxesValues.put(playerProperty, radarAxisValues);
            }
        }

    }

    /**
     * @return the list of player entry properties displayed by the radar chart
     */
    @Override
    public List<PlayerProperty> getPlayerRadarChartProperties() throws NoSuchElementException {
        return playerRadarChartProperties;
    }

    @Override
    public Map<PlayerProperty, RadarAxisValues> getRadarPlotAxesValues() throws NoSuchElementException {
        return radarAxesValues;
    }

    @Override
    public AbstractPlayerCatalog getPlayerCatalog() {
        return playerCatalog;
    }

    @Override
    public List<PlayerEntry> getFilteredPlayerEntries() {
        return filteredPlayerEntries;
    }

}

