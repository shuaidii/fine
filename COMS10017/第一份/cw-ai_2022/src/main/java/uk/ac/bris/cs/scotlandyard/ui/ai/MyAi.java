package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;

public class MyAi implements Ai {
    private static final Random RANDOM = new Random();
    private static final int DEPTH = 2;

    @Nonnull
    @Override
    public String name() {
        return "MyAi name.";
    }

    /**
     * alpha beta 剪枝
     *
     * @param board
     * @param timeoutPair
     * @return
     */
    @Nonnull
    @Override
    public Move pickMove(@Nonnull Board board, Pair<Long, TimeUnit> timeoutPair) {
        int maxEval = Integer.MIN_VALUE;
        List<Move> moveList = new ArrayList<>();
        for (Move move : board.getAvailableMoves()) {
            move(board, move);
            int eval = minimax(board, DEPTH, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (eval >= maxEval) {
                if (eval > maxEval) {
                    moveList.clear();
                }
                maxEval = eval;
                moveList.add(move);
            }
        }
        return moveList.get(RANDOM.nextInt(moveList.size()));
    }

    /**
     * Location of MRX
     *
     * @param board
     * @return
     */
    private int getLoc(Board board) {
        ImmutableList<LogEntry> logs = board.getMrXTravelLog();
        LogEntry logEntry = logs.get(logs.size() - 1);
        return logEntry.location().orElse(-1);
    }

    private int checkDetectives(Board board) {
        int loc = getLoc(board);
        if (loc == -1) {
            return 0;
        }

        GameSetup gameSetup = board.getSetup();
        Set<Piece> detectives = board.getPlayers().stream().filter(Piece::isDetective)
                .collect(Collectors.toSet());

        AtomicInteger count = new AtomicInteger();
        Set<Integer> adjacentNodes = gameSetup.graph.adjacentNodes(loc);
        adjacentNodes.forEach(checkNode -> {
            detectives.stream().mapToInt(detective -> board.getDetectiveLocation((Piece.Detective) detective).orElse(-1))
                    .filter(locTmp -> checkNode == locTmp).map(locTmp -> 1).forEach(count::addAndGet);
        });
        return count.get();
    }

    private Board move(Board board, Move move) {
        return ((Board.GameState) board).advance(move);
    }

    private List<Board> detectiveMoves(Board board) {
        if (board.getAvailableMoves().stream().anyMatch(move -> move.commencedBy().isMrX())) {
            return Lists.newArrayList(board);
        }

        List<Board> boards = new ArrayList<>();
        for (Move move : board.getAvailableMoves()) {
            boards.addAll(detectiveMoves(move(board, move)));
        }
        return boards;
    }

    private int minimax(Board board, int depth, boolean isMRX, int alpha, int beta) {
        if (depth == 0 || board.getWinner().size() > 0) {
            return board.getAvailableMoves().size() + RANDOM.nextInt(5) - checkDetectives(board) * 10;
        }
        if (!isMRX) {
            return min(board, depth, alpha, beta);
        }
        return max(board, depth, alpha, beta);
    }

    private int max(Board board, int depth, int alpha, int beta) {
        int maxEval = Integer.MIN_VALUE;
        for (Move move : board.getAvailableMoves()) {
            Board boardTmp = move(board, move);
            int eval = minimax(boardTmp, depth - 1, false, alpha, beta);
            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(alpha, eval);
            if (alpha >= beta) {
                break;
            }
        }
        return maxEval;
    }

    private int min(Board board, int depth, int alpha, int beta) {
        int minEval = Integer.MAX_VALUE;
        for (Board boardTmp : detectiveMoves(board)) {
            int eval = minimax(boardTmp, depth - 1, true, alpha, beta);
            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            if (alpha >= beta) {
                break;
            }
        }
        return minEval;
    }

}