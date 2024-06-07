package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

    @Nonnull
    @Override
    public GameState build(
            GameSetup setup,
            Player mrX,
            ImmutableList<Player> detectives) {

        List<Piece> pieces = Lists.newArrayList(mrX.piece());
        detectives.stream().map(Player::piece).forEach(pieces::add);
        return new MyGameState(setup, ImmutableSet.copyOf(pieces), ImmutableList.of(), mrX, detectives);
    }


    static class MyGameState implements GameState {
        private final GameSetup gameSetup;
        private final ImmutableSet<Piece> remainPieces;
        private final ImmutableList<LogEntry> mrxTravelLog;
        private final Player MRX;
        private final List<Player> detectiveList;

        private ImmutableSet<Move> availableMoves;
        private ImmutableSet<Piece> winner;

        private MyGameState(GameSetup gameSetup, ImmutableSet<Piece> remainPieces, ImmutableList<LogEntry> mrxTravelLog,
                            Player MRX, List<Player> detectiveList) {
            checkData(gameSetup, MRX, detectiveList);

            this.gameSetup = gameSetup;
            this.mrxTravelLog = mrxTravelLog;
            this.MRX = MRX;
            this.detectiveList = detectiveList;
            this.remainPieces = checkRemainPieces(gameSetup, remainPieces, mrxTravelLog, MRX, detectiveList);

            List<Move> moveList = new ArrayList<>();
            if (!this.remainPieces.contains(MRX.piece())) {
                moveList = detectiveList.stream().filter(player -> this.remainPieces.contains(player.piece()))
                        .flatMap(player -> getSingleMoves(gameSetup, detectiveList, player, player.location()).stream())
                        .collect(Collectors.toList());
            } else {
                moveList.addAll(getSingleMoves(gameSetup, detectiveList, MRX, MRX.location()));
                moveList.addAll(getDoubleMoves(gameSetup, detectiveList, MRX, MRX.location(), mrxTravelLog));
            }
            this.availableMoves = ImmutableSet.copyOf(moveList);

            this.winner = hasWinner(mrxTravelLog, gameSetup, detectiveList, MRX, availableMoves, remainPieces);
            this.availableMoves = winner.size() > 0 ? ImmutableSet.of() : availableMoves;
        }

        private ImmutableSet<Piece> checkRemainPieces(GameSetup gameSetup, ImmutableSet<Piece> remainPieces,
                                                      ImmutableList<LogEntry> logEntryList, Player MRX, List<Player> detectiveList) {
            ImmutableList<Player> playerList = ImmutableList.<Player>builder().addAll(detectiveList).add(MRX).build();
            List<Piece> remainPiecesTmp = new ArrayList<>();
            playerList.stream().filter(player -> remainPieces.contains(player.piece())).forEach(player -> {
                if (player.piece().isMrX()
                        && (!(getSingleMoves(gameSetup, detectiveList, player, player.location()).isEmpty())
                        || !(getDoubleMoves(gameSetup, detectiveList, MRX, MRX.location(), logEntryList)).isEmpty())) {
                    remainPiecesTmp.add(player.piece());

                } else if (player.piece().isDetective()
                        && (!(getSingleMoves(gameSetup, detectiveList, player, player.location()).isEmpty()))) {
                    remainPiecesTmp.add(player.piece());

                }
            });
            return ImmutableSet.copyOf(remainPiecesTmp);
        }

        private void checkData(GameSetup gameSetup, Player mrX, List<Player> detectiveList) {
            if (mrX == null || detectiveList == null) {
                throw new NullPointerException();
            }
            if (!mrX.isMrX() || gameSetup.moves.isEmpty() || gameSetup.graph.nodes().isEmpty()) {
                throw new IllegalArgumentException();
            }

            for (Player player : detectiveList) {
                if (player.isMrX() || player.has(ScotlandYard.Ticket.SECRET) || player.has(ScotlandYard.Ticket.DOUBLE)) {
                    throw new IllegalArgumentException();
                }
            }

            for (int i = 0; i < detectiveList.size(); i++) {
                for (int j = 0; j < detectiveList.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    Player player1 = detectiveList.get(i);
                    Player player2 = detectiveList.get(j);
                    if (player1.piece().equals(player2.piece()) || player1.location() == player2.location()) {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }

        @Nonnull
        @Override
        public ImmutableSet<Piece> getPlayerList() {
            List<Piece> pieceList = detectiveList.stream().map(Player::piece).collect(Collectors.toList());
            pieceList.add(MRX.piece());
            return ImmutableSet.copyOf(pieceList);
        }

        @Nonnull
        @Override
        public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
            Optional<Player> playerOptional = detectiveList.stream()
                    .filter(detectiveTmp -> detectiveTmp.piece().equals(detective))
                    .findFirst();
            return playerOptional.map(Player::location);
        }

        @Nonnull
        @Override
        public Optional<TicketBoard> getPlayerTickets(Piece piece) {
            if (piece.isMrX() || detectiveList.stream().anyMatch(detective -> detective.piece().equals(piece))) {
                TicketBoard ticketBoard = (ticket) -> {
                    Optional<Player> playerOptional = detectiveList.stream()
                            .filter(detective -> detective.piece().equals(piece))
                            .findFirst();
                    return playerOptional.isPresent() ? playerOptional.get().tickets().get(ticket)
                            : MRX.tickets().get(ticket);
                };
                return Optional.of(ticketBoard);
            }
            return Optional.empty();
        }

        @Nonnull
        @Override
        public GameSetup getGameSetup() {
            return gameSetup;
        }

        @Nonnull
        @Override
        public ImmutableList<LogEntry> getMrXTravelLog() {
            return mrxTravelLog;
        }

        @Nonnull
        @Override
        public ImmutableSet<Piece> getWinner() {
            return winner;
        }

        @Nonnull
        @Override
        public ImmutableSet<Move> getAvailableMoves() {
            return availableMoves;
        }

        class MyMoveVisitor implements Move.Visitor<Move> {
            private Player player;
            private List<LogEntry> logEntries = new ArrayList<>();

            @Override
            public Move visit(Move.SingleMove move) {
                if (!move.commencedBy().isMrX()) {
                    for (Player detective : detectiveList) {
                        if (move.commencedBy().equals(detective.piece())) {
                            this.player = new Player(move.commencedBy(), detective.tickets(), move.destination);
                        }
                    }
                    this.player = this.player.use(move.tickets());
                } else {
                    this.player = new Player(move.commencedBy(), MRX.tickets(), move.destination);
                    LogEntry tmp = LogEntry.hidden(move.ticket);
                    if (gameSetup.moves.get(mrxTravelLog.size())) {
                        tmp = LogEntry.reveal(move.ticket, move.destination);
                    }
                    logEntries.add(tmp);
                    this.player = this.player.use(move.tickets());
                }
                return move;
            }

            @Override
            public Move visit(Move.DoubleMove move) {
                this.player = new Player(move.commencedBy(), MRX.tickets(), move.destination2).use(move.tickets());

                LogEntry tmp = LogEntry.hidden(move.ticket1);
                if (gameSetup.moves.get(mrxTravelLog.size())) {
                    tmp = LogEntry.reveal(move.ticket1, move.destination1);
                }
                logEntries.add(tmp);

                tmp = LogEntry.hidden(move.ticket2);
                if (gameSetup.moves.get(mrxTravelLog.size() + 1)) {
                    tmp = LogEntry.reveal(move.ticket2, move.destination2);
                }
                logEntries.add(tmp);

                return move;
            }
        }

        @Nonnull
        @Override
        public GameState advance(Move move) {
            if (!availableMoves.contains(move)) {
                throw new IllegalArgumentException("Error Move: " + move);
            }

            List<Player> detectiveListTmp = new ArrayList<>(detectiveList);
            List<LogEntry> mrxTravelLogTmp = new ArrayList<>(mrxTravelLog);

            MyMoveVisitor myMoveVisitor = new MyMoveVisitor();
            move.accept(myMoveVisitor);
            Player next = MRX;

            mrxTravelLogTmp.addAll(myMoveVisitor.logEntries);
            if (!move.commencedBy().isMrX()) {
                for (Player detective : detectiveList) {
                    if (!move.commencedBy().equals(detective.piece())) {
                        continue;
                    }
                    detectiveListTmp.remove(detective);
                    detectiveListTmp.add(myMoveVisitor.player);
                }
            } else {
                next = myMoveVisitor.player;
            }

            List<Piece> remainPiecesTmp = new ArrayList<>(remainPieces);
            if (move.commencedBy().isDetective()) {
                next = next.give(move.tickets());
                for (Player detective : detectiveList) {
                    if (!move.commencedBy().equals(detective.piece())) {
                        continue;
                    }
                    remainPiecesTmp.remove(detective.piece());
                }
                if (!remainPiecesTmp.isEmpty() || gameSetup.moves.size() <= mrxTravelLogTmp.size()) {
                    return new MyGameState(gameSetup, ImmutableSet.copyOf(remainPiecesTmp),
                            ImmutableList.copyOf(mrxTravelLogTmp), next, ImmutableList.copyOf(detectiveListTmp));
                }

                remainPiecesTmp.add(next.piece());
            }

            if (move.commencedBy().isMrX()) {
                remainPiecesTmp.remove(MRX.piece());
            }
            for (Player detective : detectiveListTmp) {
                remainPiecesTmp.add(detective.piece());
            }
            return new MyGameState(gameSetup, ImmutableSet.copyOf(remainPiecesTmp),
                    ImmutableList.copyOf(mrxTravelLogTmp), next, ImmutableList.copyOf(detectiveListTmp));
        }

        private static ImmutableSet<Move.SingleMove> getSingleMoves(GameSetup setup, List<Player> detectives,
                                                                    Player player, int source) {
            List<Move.SingleMove> moveList = new ArrayList<>();
            Set<Integer> adjacentNodes = setup.graph.adjacentNodes(source);
            for (int adjacentNode : adjacentNodes) {
                if (isOccupied(detectives, adjacentNode)) {
                    continue;
                }

                setup.graph.edgeValueOrDefault(source, adjacentNode, ImmutableSet.of()).forEach(transport -> {
                    if (player.has(transport.requiredTicket())) {
                        moveList.add(new Move.SingleMove(player.piece(), source, transport.requiredTicket(), adjacentNode));
                    }
                    if (player.isMrX() && player.has(ScotlandYard.Ticket.SECRET)) {
                        moveList.add(new Move.SingleMove(player.piece(), source, ScotlandYard.Ticket.SECRET, adjacentNode));
                    }
                });
            }

            return ImmutableSet.copyOf(moveList);
        }

        private static boolean isOccupied(List<Player> detectives, int location) {
            for (Player d : detectives) {
                if (d.location() == location) {
                    return true;
                }
            }
            return false;
        }

        private static ImmutableSet<Move.DoubleMove> getDoubleMoves(GameSetup setup, List<Player> detectives,
                                                                    Player player, int source, ImmutableList<LogEntry> log) {
            if (!player.has(ScotlandYard.Ticket.DOUBLE) || setup.moves.size() - 2 < log.size()) {
                return ImmutableSet.copyOf(new ArrayList<>());
            }

            List<Move.DoubleMove> moveList = new ArrayList<>();
            Set<Integer> adjacentNodes = setup.graph.adjacentNodes(source);
            for (int moveFirst : adjacentNodes) {
                if (isOccupied(detectives, moveFirst)) {
                    continue;
                }

                boolean firstTicket = false;
                ScotlandYard.Transport transportFirst = null;
                for (ScotlandYard.Transport transport : setup.graph.edgeValueOrDefault(source, moveFirst, ImmutableSet.of())) {
                    if ((player.isMrX() && player.has(ScotlandYard.Ticket.SECRET))
                            || player.has(transport.requiredTicket())) {
                        transportFirst = transport;
                        firstTicket = true;
                    }
                }
                if (!firstTicket) {
                    continue;
                }

                moveSecondCheck(setup, detectives, player, source, moveList, moveFirst, transportFirst);
            }
            return ImmutableSet.copyOf(moveList);
        }

        private static void moveSecondCheck(GameSetup setup, List<Player> detectives, Player player, int source,
                                            List<Move.DoubleMove> moveList, int moveFirst, ScotlandYard.Transport transportFirst) {
            Set<Integer> adjacentNodeTmps = setup.graph.adjacentNodes(moveFirst);
            for (int moveSecond : adjacentNodeTmps) {
                if (isOccupied(detectives, moveSecond)) {
                    continue;
                }

                for (ScotlandYard.Transport transport : setup.graph.edgeValueOrDefault(moveFirst, moveSecond, ImmutableSet.of())) {
                    if (player.has(transport.requiredTicket())) {
                        if (!transportFirst.requiredTicket().equals(transport.requiredTicket())
                                || player.hasAtLeast(transportFirst.requiredTicket(), 2)) {
                            moveList.add(new Move.DoubleMove(player.piece(), source, transportFirst.requiredTicket(),
                                    moveFirst, transport.requiredTicket(), moveSecond));
                        }
                    }

                    if (!player.isMrX()) {
                        continue;
                    }
                    if (player.has(ScotlandYard.Ticket.SECRET)) {
                        moveList.add(new Move.DoubleMove(player.piece(), source, transportFirst.requiredTicket(),
                                moveFirst, ScotlandYard.Ticket.SECRET, moveSecond));
                    }
                    if (player.hasAtLeast(ScotlandYard.Ticket.SECRET, 2)) {
                        moveList.add(new Move.DoubleMove(player.piece(), source, ScotlandYard.Ticket.SECRET,
                                moveFirst, ScotlandYard.Ticket.SECRET, moveSecond));
                    }
                    if (player.has(ScotlandYard.Ticket.SECRET) && player.has(transport.requiredTicket())) {
                        moveList.add(new Move.DoubleMove(player.piece(), source, ScotlandYard.Ticket.SECRET,
                                moveFirst, transport.requiredTicket(), moveSecond));
                    }
                }
            }
        }

        static ImmutableSet<Piece> hasWinner(ImmutableList<LogEntry> log, GameSetup setup, List<Player> detectives,
                                             Player mrX, ImmutableSet<Move> moves, ImmutableSet<Piece> remaining) {
            if (remaining.isEmpty() || setup.moves.size() < log.size()) {
                return ImmutableSet.of(mrX.piece());
            }
            if (isMrXWin(setup, detectives, mrX, remaining)) {
                return ImmutableSet.of(mrX.piece());
            }
            if (isDetectiveWin(detectives, mrX, moves, remaining)) {
                return ImmutableSet.copyOf(detectives.stream().map(Player::piece).collect(Collectors.toList()));
            }
            return ImmutableSet.of();
        }

        private static boolean isDetectiveWin(List<Player> detectives, Player mrX, ImmutableSet<Move> moves, ImmutableSet<Piece> remaining) {
            for (Player detective : detectives) {
                if (detective.location() == mrX.location()) {
                    return true;
                }
            }
            if (remaining.contains(mrX.piece())) {
                return moves.stream().noneMatch(move -> move.commencedBy().isMrX());
            }
            return false;
        }

        private static boolean isMrXWin(GameSetup setup, List<Player> detectives, Player mrX, ImmutableSet<Piece> remaining) {
            boolean mrXWin = true;
            boolean hasX = remaining.contains(mrX.piece());
            for (Player player : detectives) {
                if (!hasX && getSingleMoves(setup, detectives, player, player.location()).size() > 0) {
                    mrXWin = false;
                    break;
                }
                for (Integer ticket : player.tickets().values()) {
                    if (ticket != 0) {
                        mrXWin = false;
                        break;
                    }
                }
            }
            return mrXWin;
        }
    }

}
