package uk.ac.sheffield.com1003.assignment;

import uk.ac.sheffield.com1003.assignment.codeprovided.*;

import java.util.*;

/**
 * SKELETON IMPLEMENTATION
 */
public class QueryParser extends AbstractQueryParser {

    // Default implementation to be provided
    @Override
    public List<Query> readQueries(List<String> queryTokens) throws IllegalArgumentException {
        List<Query> list = new ArrayList<>();
        int index = 0;
        while (index < queryTokens.size()) {
            index = parseQuery(queryTokens, index, list);
        }
        return list;
    }

    private int parseQuery(List<String> queryTokens, int startIndex, List<Query> list) {
        try {
            String select = queryTokens.get(startIndex++);
            if (!select.equalsIgnoreCase("select")) {
                throw new IllegalArgumentException("No select");
            }
            String leagueName1 = queryTokens.get(startIndex++);
            League leagueType = League.valueOf(leagueName1.toUpperCase());
            String next = queryTokens.get(startIndex++);
            if (next.equalsIgnoreCase("or")) {
                String leagueName2 = queryTokens.get(startIndex++);
                League leagueType2 = League.valueOf(leagueName2.toUpperCase());
                if (leagueType != leagueType2) {
                    leagueType = League.ALL;
                }
                next = queryTokens.get(startIndex++);
            }

            List<SubQuery> subQueryList = new ArrayList<>();
            if (next.equalsIgnoreCase("where")) {
                while (startIndex < queryTokens.size()) {
                    String playerProperty = queryTokens.get(startIndex++);
                    String operator = queryTokens.get(startIndex++);
                    String value = queryTokens.get(startIndex++);
                    subQueryList.add(new SubQuery(PlayerProperty.fromName(playerProperty), operator, Double.parseDouble(value)));

                    if (startIndex == queryTokens.size()) {
                        break;
                    }
                    next = queryTokens.get(startIndex++);
                    if (next.equalsIgnoreCase("select")) {
                        startIndex--;
                        break;
                    }
                }
            }

            list.add(new Query(subQueryList, leagueType));
            return startIndex;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }

    }


}

