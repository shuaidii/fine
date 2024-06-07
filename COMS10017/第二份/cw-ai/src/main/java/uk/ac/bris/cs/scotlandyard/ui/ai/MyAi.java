package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.graph.ImmutableValueGraph;
import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;

public class MyAi implements Ai {
    private static final int MAX_DEPTH = 5;

    @Nonnull
    @Override
    public String name() {
        return "MyAi";
    }

    @Nonnull
    @Override
    public Move pickMove(@Nonnull Board board, Pair<Long, TimeUnit> timeoutPair) {
        int alpha = -100;
        int beta = 400;
        Map<Integer, Integer> scores = new HashMap<>();
        Board.GameState gameState = (Board.GameState) board;
        ImmutableList<Move> moveList = board.getAvailableMoves().asList();
        int index = 0;
        int maxScore = -100;
        for (Move move : moveList) {
            Board.GameState next = gameState.advance(move);
            if (alpha >= beta) {
                break;
            }
            int score = minMax(next, alpha, beta, move, 0, moveList);
            scores.put(score, index);
            if (score > maxScore) {
                maxScore = score;
                alpha = maxScore;
            }
            index++;
        }
        Optional<Integer> max = scores.keySet().stream().max(Integer::compareTo);
        return moveList.get(scores.get(max.get()));
    }

    private Integer minMax(Board.GameState state, Integer alpha, Integer beta, Move preMove, Integer depth, ImmutableList<Move> moveList) {
        GameSetup gameSetup = state.getSetup();
        if (state.getWinner().size() > 0) {
            if (!state.getWinner().contains(Piece.MrX.MRX)) {
                return 0;
            }
            return getScore(getDL(state), preMove.accept(new MLVisitor()), gameSetup.graph) + additionScore(moveList) + 200;
        }
        if (depth == MAX_DEPTH) {
            return getScore(getDL(state), preMove.accept(new MLVisitor()), gameSetup.graph) + additionScore(moveList);
        }

        ImmutableList<Move> availableMoves = state.getAvailableMoves().asList();
        if (availableMoves.get(0).commencedBy() == Piece.MrX.MRX) {
            return max(state, alpha, beta, preMove, depth, availableMoves);
        }
        return min(state, alpha, beta, preMove, depth, availableMoves);
    }

    private int min(Board.GameState state, int alpha, int beta, Move preMove, int depth, ImmutableList<Move> availableMoves) {
        int minScore = 1000;
        for (Move move : availableMoves) {
            if (availableMoves.get(0).commencedBy() != move.commencedBy()) {
                continue;
            }
            Board.GameState next = state.advance(move);
            int newScore = minMax(next, alpha, beta, preMove, depth + 1, availableMoves);
            if (newScore < minScore) {
                minScore = newScore;
                beta = minScore;
            }
            if (alpha >= beta) {
                break;
            }
        }
        return minScore;
    }

    private int max(Board.GameState state, int alpha, int beta, Move preMove, int depth, ImmutableList<Move> availableMoves) {
        int maxScore = -100;
        for (Move move : availableMoves) {
            Board.GameState next = state.advance(move);
            int newScore = minMax(next, alpha, beta, move, depth + 1, availableMoves);
            if (newScore > maxScore) {
                maxScore = newScore;
                alpha = maxScore;
            }
            if (alpha >= beta) {
                break;
            }
        }
        return maxScore;
    }

    private Integer additionScore(ImmutableList<Move> moves) {
        int score = 0;
        MyVisitor myVisitor = new MyVisitor();
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            int scoreTmp = move.accept(myVisitor);
            if (scoreTmp == 50 && i == 0) {
                score += scoreTmp;
            } else {
                if (scoreTmp != 50) {
                    score += scoreTmp;
                }
            }
        }
        return score;
    }

    private List<Integer> getDL(Board.GameState board) {
        var P = board.getPlayers();
        List<Integer> DLocations = new ArrayList<>();

        for (Piece p : P) {
            if (p.isDetective()) {
                DLocations.add(board.getDetectiveLocation((Piece.Detective) p).get());
            }
        }
        return DLocations;
    }

    private Integer getScore(List<Integer> dLocations, int mLocation,
                             ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph) {
        Map<Integer, Integer> mLocationMap = new HashMap<>();
        List<Integer> mLocationList = new ArrayList<>();
        mLocationMap.put(mLocation, 0);
        mLocationList.add(mLocation);
        int threadhold = 1000;
        int index = 0;
        List<Integer> unsetList = new ArrayList<>();
        while (index < mLocationList.size()) {
            int current = mLocationList.get(index);
            for (Integer destination : graph.adjacentNodes(current)) {
                int value = getValue(destination, current, graph) + mLocationMap.get(current);
                if (mLocationMap.containsKey(destination)) {
                    if (mLocationMap.get(destination) > value) {
                        mLocationMap.put(destination, value);
                    }
                } else {
                    mLocationMap.put(destination, value);
                    unsetList.add(destination);
                }
            }

            index++;
            threadhold = getNewThreadhold(mLocationMap, mLocationList, unsetList, threadhold);
            if (new HashSet<>(mLocationList).containsAll(dLocations)) {
                return dLocations.stream().mapToInt(mLocationMap::get).sum();
            }
        }
        return 0;
    }

    private static int getNewThreadhold(Map<Integer, Integer> mLocationMap, List<Integer> mLocationList,
                                        List<Integer> unsetList, int threadhold) {
        List<Integer> list = new ArrayList<>();
        boolean update = false;
        for (Integer k : unsetList) {
            int value = mLocationMap.get(k);
            if (value == threadhold) {
                list.add(k);
            } else if (value < threadhold) {
                threadhold = value;
                list.clear();
                list.add(k);
                update = true;
            }
        }
        if (update) {
            threadhold = 1000;
            mLocationList.addAll(list);
            unsetList.removeAll(list);
        }
        return threadhold;
    }

    private int getValue(Integer destination, Integer source, ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph) {
        int value = 0;
        ImmutableSet<ScotlandYard.Transport> transports = graph.edgeValueOrDefault(source, destination, ImmutableSet.of());
        assert transports != null;
        UnmodifiableIterator<ScotlandYard.Transport> iterator = transports.iterator();
        while (iterator.hasNext()) {
            ScotlandYard.Transport transport = iterator.next();
            switch (transport.requiredTicket()) {
                case TAXI -> value = value + 1;
                case BUS -> value = value + 2;
                case UNDERGROUND -> value = value + 3;
                case SECRET -> value = value + 4;
            }
        }
        return value;
    }

    static class MyVisitor implements Move.Visitor<Integer> {
        @Override
        public Integer visit(Move.SingleMove move) {
            return switch (move.ticket) {
                case TAXI -> 4;
                case BUS -> 8;
                case UNDERGROUND -> 15;
                case SECRET -> 30;
                default -> 0;
            };
        }

        @Override
        public Integer visit(Move.DoubleMove move) {
            return 100;
        }
    }

    static class MLVisitor implements Move.Visitor<Integer> {
        @Override
        public Integer visit(Move.SingleMove move) {
            return move.destination;
        }

        @Override
        public Integer visit(Move.DoubleMove move) {
            return move.destination2;
        }
    }
}
