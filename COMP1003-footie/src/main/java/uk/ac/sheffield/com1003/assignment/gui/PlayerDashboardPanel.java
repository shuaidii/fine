package uk.ac.sheffield.com1003.assignment.gui;

import uk.ac.sheffield.com1003.assignment.codeprovided.*;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractPlayerDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * SKELETON IMPLEMENTATION
 */

public class PlayerDashboardPanel extends AbstractPlayerDashboardPanel {

    // Constructor
    public PlayerDashboardPanel(AbstractPlayerCatalog playerCatalog) {
        super(playerCatalog);
    }

    /**
     * populatePlayerDetailsComboBoxes method - dynamically populates the player detail comboboxes:
     * comboPlayerNames, comboNations, comboPositions, and comboTeams.
     */
    @Override
    public void populatePlayerDetailsComboBoxes() {
        Set<String> playerNames = new HashSet<>();
        Set<String> nations = new HashSet<>();
        Set<String> positions = new HashSet<>();
        Set<String> teams = new HashSet<>();
        List<PlayerEntry> playerEntryList = playerCatalog.getPlayerEntriesList(League.ALL);
        playerEntryList.forEach(playerEntry -> {
            playerNames.add(playerEntry.getPlayerName());
            nations.add(playerEntry.getNation());
            positions.add(playerEntry.getPosition());
            teams.add(playerEntry.getTeam());
        });

        playerNamesList.clear();
        playerNamesList.addAll(playerNames);
        Collections.sort(playerNamesList);
        playerNamesList.add(0, "");
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboPlayerNames.getModel();
        model.addAll(playerNamesList);

        nationList.clear();
        nationList.addAll(nations);
        Collections.sort(nationList);
        nationList.add(0, "");
        DefaultComboBoxModel<String> nationsModel = (DefaultComboBoxModel<String>) comboNations.getModel();
        nationsModel.addAll(nationList);

        positionList.clear();
        positionList.addAll(positions);
        Collections.sort(positionList);
        positionList.add(0, "");
        DefaultComboBoxModel<String> positionsModel = (DefaultComboBoxModel<String>) comboPositions.getModel();
        positionsModel.addAll(positionList);

        teamList.clear();
        teamList.addAll(teams);
        Collections.sort(teamList);
        teamList.add(0, "");
        DefaultComboBoxModel<String> teamsModel = (DefaultComboBoxModel<String>) comboTeams.getModel();
        teamsModel.addAll(teamList);
    }

    /**
     * addListeners method - adds relevant actionListeners to the GUI components
     * You will need to listen (at least) to the following:
     * - buttonAddFilter
     * - buttonClearFilters
     * - comboLeagueTypes, comboPlayerNames, comboNations, comboPositions, and comboTeams,
     * if you want the filteredPlayerEntriesTextArea to be updated
     * to show only the player entries specified by these comboboxes
     * - comboRadarChartCategories, to update the properties that the radar chart should display
     */
    @SuppressWarnings("unused")
    @Override
    public void addListeners() {
        buttonAddFilter.addActionListener(e -> addFilter());
        buttonClearFilters.addActionListener(e -> clearFilters());

        comboLeagueTypes.addActionListener(e -> executeQuery());
        comboPlayerNames.addActionListener(e -> executeQuery());
        comboNations.addActionListener(e -> executeQuery());
        comboPositions.addActionListener(e -> executeQuery());
        comboTeams.addActionListener(e -> executeQuery());

        comboRadarChartCategories.addActionListener(e -> updateRadarChart());
        minCheckBox.addActionListener(e -> updateRadarChart());
        maxCheckBox.addActionListener(e -> updateRadarChart());
        averageCheckBox.addActionListener(e -> updateRadarChart());
    }

    /**
     * clearFilters method - clears all filters from the subQueryList ArrayList and updates
     * the relevant GUI components when the button buttonClearFilters is clicked
     */
    @Override
    public void clearFilters() {
        subQueryList.clear();
//        comboLeagueTypes.setSelectedIndex(0);
//        comboPlayerNames.setSelectedIndex(0);
//        comboNations.setSelectedIndex(0);
//        comboPositions.setSelectedIndex(0);
//        comboTeams.setSelectedIndex(0);
        updateSubQueriesTextArea();
    }

    @Override
    public void updateRadarChart() {
        String categoryName = (String) comboRadarChartCategories.getSelectedItem();
        Category cat = Category.getCategoryFromName(categoryName);
        List<PlayerProperty> listProperties = Arrays.asList(cat.getProperties());
        radarChart.updateRadarChartContents(listProperties, filteredPlayerEntriesList);

    }

    /**
     * updateStats method - updates the table with statistics after any changes which may
     * affect the JTable which holds the statistics
     */
    @Override
    public void updateStatistics() {
        statisticsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statisticsTextArea.setText("");
        String format = "%-20s";
        String[] headValues = new String[PlayerProperty.values().length + 1];
        String[] minValues = new String[PlayerProperty.values().length + 1];
        String[] maxValues = new String[PlayerProperty.values().length + 1];
        String[] averageValues = new String[PlayerProperty.values().length + 1];
        headValues[0] = "";
        minValues[0] = "Minimum:";
        maxValues[0] = "Maximum:";
        averageValues[0] = "Mean:";
        int index = 1;
        for (PlayerProperty playerProperty : PlayerProperty.values()) {
            int size = playerProperty.getName().length() + 10;
            format += "%-" + size + "s";
            headValues[index] = playerProperty.getName();
            minValues[index] = String.format("%.2f", playerCatalog.getMinimumValue(playerProperty, filteredPlayerEntriesList));
            maxValues[index] = String.format("%.2f", playerCatalog.getMaximumValue(playerProperty, filteredPlayerEntriesList));
            averageValues[index] = String.format("%.2f", playerCatalog.getMeanAverageValue(playerProperty, filteredPlayerEntriesList));
            index++;
        }
        format += "\n";
        statisticsTextArea.append(String.format(format, headValues));
        statisticsTextArea.append(String.format(format, minValues));
        statisticsTextArea.append(String.format(format, maxValues));
        statisticsTextArea.append(String.format(format, averageValues));
    }

    /**
     * updatePlayerCatalogDetailsBox method - updates the list of players when changes are made
     */
    @Override
    public void updatePlayerCatalogDetailsBox() {
        filteredPlayerEntriesTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        filteredPlayerEntriesTextArea.setText("");

        String format = "%-30s%-20s";
        String[] headValues = new String[PlayerDetail.values().length + PlayerProperty.values().length + 2];
        List<String[]> dataValeuList = new ArrayList<>();
        filteredPlayerEntriesList.forEach(playerEntry -> dataValeuList.add(new String[PlayerDetail.values().length + PlayerProperty.values().length + 2]));
        headValues[0] = "League Type";
        headValues[1] = "ID";
        for (int i = 0; i < filteredPlayerEntriesList.size(); i++) {
            String[] playerValue = dataValeuList.get(i);
            PlayerEntry playerEntry = filteredPlayerEntriesList.get(i);
            playerValue[0] = playerEntry.getLeagueType().getName();
            playerValue[1] = String.valueOf(playerEntry.getId());
        }
        int index = 2;
        for (PlayerDetail playerDetail : PlayerDetail.values()) {
            int size = playerDetail.getName().length() + 20;
            format += "%-" + size + "s";
            headValues[index] = playerDetail.getName();
            for (int i = 0; i < filteredPlayerEntriesList.size(); i++) {
                String[] playerValue = dataValeuList.get(i);
                PlayerEntry playerEntry = filteredPlayerEntriesList.get(i);
                playerValue[index] = playerEntry.getDetail(playerDetail);
            }
            index++;
        }
        for (PlayerProperty playerProperty : PlayerProperty.values()) {
            int size = playerProperty.getName().length() + 10;
            format += "%-" + size + "s";
            headValues[index] = playerProperty.getName();
            for (int i = 0; i < filteredPlayerEntriesList.size(); i++) {
                String[] playerValue = dataValeuList.get(i);
                PlayerEntry playerEntry = filteredPlayerEntriesList.get(i);
                playerValue[index] = String.format("%.2f", playerEntry.getProperty(playerProperty));
            }
            index++;
        }
        format += "\n";
        filteredPlayerEntriesTextArea.append(String.format(format, headValues));
        for (String[] playerValue : dataValeuList) {
            filteredPlayerEntriesTextArea.append(String.format(format, playerValue));
        }
    }

    /**
     * executeQuery method - applies chosen query to the relevant list
     */
    @Override
    public void executeQuery() {
        League league = League.fromName(getSelect(comboLeagueTypes));
        String playerName = getSelect(comboPlayerNames);
        String nation = getSelect(comboNations);
        String position = getSelect(comboPositions);
        String team = getSelect(comboTeams);

        Query query = new Query(subQueryList, league);
        filteredPlayerEntriesList = query.executeQuery(playerCatalog);
        if (playerName.length() > 0) {
            filteredPlayerEntriesList.removeIf(playerEntry -> !playerEntry.getPlayerName().equals(playerName));
        }
        if (nation.length() > 0) {
            filteredPlayerEntriesList.removeIf(playerEntry -> !playerEntry.getNation().equals(nation));
        }
        if (position.length() > 0) {
            filteredPlayerEntriesList.removeIf(playerEntry -> !playerEntry.getPosition().equals(position));
        }
        if (team.length() > 0) {
            filteredPlayerEntriesList.removeIf(playerEntry -> !playerEntry.getTeam().equals(team));
        }

        updateStatistics();
        updatePlayerCatalogDetailsBox();
        updateRadarChart();
    }

    private String getSelect(JComboBox<String> box) {
        if (box.getSelectedIndex() == -1) {
            return "";
        }
        return String.valueOf(box.getSelectedItem());
    }

    /**
     * addFilter method -
     * 1- this method is called when the JButton buttonAddFilter is clicked
     * 2- adds a new filter (a SubQuery object) to subQueryList ArrayList
     * 3- updates the GUI results accordingly, i.e. updates the three JTextAreas as follows:
     * 3a- subQueriesTextArea will show the new SubQuery
     * 3b- statisticsTextArea will show the updated statistics for the results after applying this filter
     * 3c- filteredPlayerEntriesTextArea will show the contents of filteredPlayerEntriesList (the results after applying this filter)
     * 3d- the radar chart is updated to display the newly filtered player entries (Note: this can alternatively be done
     * in another method)
     */
    @Override
    public void addFilter() {
        try {
            PlayerProperty playerProperty = PlayerProperty.fromPropertyName(String.valueOf(comboQueryProperties.getSelectedItem()));
            String operators = String.valueOf(comboOperators.getSelectedItem());
            double valueD = Double.parseDouble(value.getText());
            SubQuery query = new SubQuery(playerProperty, operators, valueD);
            subQueryList.add(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateSubQueriesTextArea();
    }

    private void updateSubQueriesTextArea() {
        subQueriesTextArea.setText("");
        for (SubQuery subQuery : subQueryList) {
            subQueriesTextArea.append(subQuery.toString() + "; ");
        }
        executeQuery();
    }

    /**
     * isMinCheckBoxSelected method - executes the complete query to the relevant player list
     */
    @Override
    public boolean isMinCheckBoxSelected() {
        return minCheckBox.isSelected();
    }

    @Override
    public boolean isMaxCheckBoxSelected() {
        return maxCheckBox.isSelected();
    }

    @Override
    public boolean isAverageCheckBoxSelected() {
        return averageCheckBox.isSelected();
    }

}
