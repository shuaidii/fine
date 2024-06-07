package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

    @Nonnull
    @Override
    public GameState build(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
        return new MyGameState(setup, ImmutableSet.of(MrX.MRX), ImmutableList.of(), mrX, detectives);
    }

    static class MyGameState implements GameState {
        private final GameSetup setup;
        private final ImmutableList<LogEntry> logEntries;
        private final List<Player> detectives;
        private ImmutableSet<Piece> remainingPieces;
        private Player mrX;
        private ImmutableSet<Piece> winner;

        private MyGameState(GameSetup setup, ImmutableSet<Piece> remainingPieces,
                            ImmutableList<LogEntry> logEntries, Player mrX, List<Player> detectives) {
            checkData(setup, remainingPieces, mrX, detectives);

            this.setup = setup;
            this.remainingPieces = remainingPieces;
            this.logEntries = logEntries;
            this.mrX = mrX;
            this.detectives = detectives;

            this.winner = ImmutableSet.of();
            if (!getWinner().isEmpty() || !getAvailableMoves().isEmpty()) {
                return;
            }
            this.remainingPieces = this.remainingPieces.contains(MrX.MRX) ?
                    ImmutableSet.copyOf(this.detectives.stream().map(Player::piece).collect(Collectors.toList())) :
                    ImmutableSet.of(MrX.MRX);
        }

        private static void checkData(GameSetup setup, ImmutableSet<Piece> remainingPieces, Player mrX, List<Player> detectives) {
            if (setup.moves.isEmpty() || remainingPieces.isEmpty() || mrX.isDetective() || mrX == null
                    || detectives.isEmpty() || setup.graph.nodes().isEmpty()) {
                throw new IllegalArgumentException();
            }

            for (Player player : detectives) {
                if (player.has(Ticket.DOUBLE) || player.has(Ticket.SECRET)) {
                    throw new IllegalArgumentException();
                }
            }

            for (int i = 0; i < detectives.size(); i++) {
                Player player1 = detectives.get(i);
                for (int j = i + 1; j < detectives.size(); j++) {
                    Player player2 = detectives.get(j);
                    if (player1.location() == player2.location()) {
                        throw new IllegalArgumentException("Duplicate location");
                    }
                }
            }
        }

        @Nonnull
        @Override
        public GameSetup getSetup() {
            return setup;
        }

        @Nonnull
        @Override
        public ImmutableSet<Piece> getPlayers() {
            Set<Piece> players = this.detectives.stream().map(Player::piece).collect(Collectors.toSet());
            players.add(this.mrX.piece());
            return ImmutableSet.copyOf(players);
        }

        @Nonnull
        @Override
        public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
            return this.detectives.stream()
                    .filter(player -> player.piece() == detective)
                    .findFirst().map(Player::location);
        }

        @Nonnull
        @Override
        public Optional<TicketBoard> getPlayerTickets(Piece piece) {
            for (Player player : this.detectives) {
                if (player.piece() == piece) {
                    return Optional.of((ticket) -> player.tickets().get(ticket));
                }
            }
            if (piece == this.mrX.piece()) {
                return Optional.of((ticket) -> mrX.tickets().get(ticket));
            } else {
                return Optional.empty();
            }
        }

        @Nonnull
        @Override
        public ImmutableList<LogEntry> getMrXTravelLog() {
            return logEntries;
        }

        @Nonnull
        @Override
        public ImmutableSet<Piece> getWinner() {
            List<Piece> detectivePieces = this.detectives.stream().map(Player::piece).collect(Collectors.toList());
            for (Player player : this.detectives) {
                if (player.location() == this.mrX.location()) {
                    this.winner = ImmutableSet.copyOf(detectivePieces);
                    return this.winner;
                }
            }

            if (this.remainingPieces.contains(MrX.MRX) && logEntries.size() == setup.moves.size()) {
                this.winner = ImmutableSet.of(this.mrX.piece());
                return this.winner;
            }

            ImmutableSet<Piece> remainingPiecesTmp = this.remainingPieces;
            this.remainingPieces = ImmutableSet.copyOf(detectivePieces);
            if (getAvailableMoves().isEmpty()) {
                this.remainingPieces = remainingPiecesTmp;
                this.winner = ImmutableSet.of(this.mrX.piece());
                return this.winner;
            }

            this.remainingPieces = ImmutableSet.of(this.mrX.piece());
            if (this.getAvailableMoves().isEmpty() && remainingPiecesTmp.contains(MrX.MRX)) {
                this.winner = ImmutableSet.copyOf(detectivePieces);
            }
            this.remainingPieces = remainingPiecesTmp;
            return this.winner;
        }

        private ImmutableSet<SingleMove> getSingleMoves(GameSetup setup, Player player, int source) {
            List<Integer> locationList = this.detectives.stream().map(Player::location).toList();

            List<SingleMove> singleMoves = new ArrayList<>();
            for (int destination : setup.graph.adjacentNodes(source)) {
                if (locationList.contains(destination)) {
                    continue;
                }
                ImmutableSet<Transport> transports = setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of());
                transports.stream()
                        .filter(transport -> player.has(transport.requiredTicket()))
                        .map(transport -> new SingleMove(player.piece(), source, transport.requiredTicket(), destination))
                        .forEach(singleMoves::add);
                if (player.has(Ticket.SECRET)) {
                    singleMoves.add(new SingleMove(player.piece(), source, Ticket.SECRET, destination));
                }
            }
            return ImmutableSet.copyOf(singleMoves);
        }


        private ImmutableSet<DoubleMove> getDoubleMoves(GameSetup setup, Player player, int source) {
            if (!player.has(Ticket.DOUBLE) || setup.moves.size() - logEntries.size() <= 1) {
                return ImmutableSet.of();
            }

            List<DoubleMove> doubleMoves = new ArrayList<>();
            ImmutableSet<SingleMove> firstSingleMoves = getSingleMoves(setup, player, source);
            firstSingleMoves.forEach(first -> {
                ImmutableSet<SingleMove> secondSingleMoves = getSingleMoves(setup, player, first.destination);
                secondSingleMoves.stream().filter(second -> first.ticket != second.ticket || player.hasAtLeast(first.ticket, 2))
                        .map(second -> new DoubleMove(player.piece(), first.source(), first.ticket, first.destination,
                                second.ticket, second.destination)).forEach(doubleMoves::add);
            });
            return ImmutableSet.copyOf(doubleMoves);
        }

        @Nonnull
        @Override
        public ImmutableSet<Move> getAvailableMoves() {
            if (!winner.isEmpty()) {
                return ImmutableSet.of();
            } else if (remainingPieces.contains(this.mrX.piece())) {
                List<Move> moves = new ArrayList<>();
                moves.addAll(getSingleMoves(setup, mrX, mrX.location()));
                moves.addAll(List.copyOf(getDoubleMoves(setup, mrX, mrX.location())));
                return ImmutableSet.copyOf(moves);
            }

            return ImmutableSet.copyOf(detectives.stream()
                    .filter(player -> remainingPieces.contains(player.piece()))
                    .flatMap(player -> getSingleMoves(setup, player, player.location()).stream())
                    .collect(Collectors.toList()));
        }

        @Nonnull
        @Override
        public GameState advance(Move move) {
            ImmutableSet<Move> availableMoves = this.getAvailableMoves();
            if (!availableMoves.contains(move)) {
                throw new IllegalArgumentException("Illegal move: " + move);
            }

            List<Integer> destination = moveDestination(move);
            List<Ticket> ticket = moveTicket(move);
            if (move.commencedBy() == MrX.MRX) {
                return mrxMove(move, new ArrayList<>(logEntries), destination, ticket);
            }
            return playerMove(move, destination.get(0));
        }

        private MyGameState playerMove(Move move, int newDestination) {
            List<Player> detectiveList = new ArrayList<>();
            detectives.forEach(player -> {
                if (player.piece() == move.commencedBy()) {
                    player = player.use(move.tickets());
                    player = player.at(newDestination);
                }
                detectiveList.add(player);
            });

            mrX = mrX.give(move.tickets());
            List<Piece> remainingList = remainingPieces.stream()
                    .filter(piece -> piece != move.commencedBy())
                    .collect(Collectors.toList());

            return remainingList.size() > 0 ?
                    new MyGameState(setup, ImmutableSet.copyOf(remainingList), logEntries, mrX, detectiveList) :
                    new MyGameState(setup, ImmutableSet.of(MrX.MRX), logEntries, mrX, detectiveList);
        }

        private MyGameState mrxMove(Move move, List<LogEntry> logEntryList, List<Integer> destination, List<Ticket> ticket) {
            if (destination.size() <= 1) {
                boolean res = setup.moves.get(logEntries.size());
                if (res) {
                    logEntryList.add(LogEntry.reveal(ticket.get(0), destination.get(0)));
                } else {
                    logEntryList.add(LogEntry.hidden(ticket.get(0)));
                }
            } else {
                boolean res1 = setup.moves.get(logEntries.size());
                boolean res2 = setup.moves.get(logEntries.size() + 1);
                if (res1 && res2) {
                    logEntryList.add(LogEntry.reveal(ticket.get(0), destination.get(0)));
                    logEntryList.add(LogEntry.reveal(ticket.get(1), destination.get(1)));
                } else if (res1) {
                    logEntryList.add(LogEntry.reveal(ticket.get(0), destination.get(0)));
                    logEntryList.add(LogEntry.hidden(ticket.get(1)));
                } else if (res2) {
                    logEntryList.add(LogEntry.hidden(ticket.get(0)));
                    logEntryList.add(LogEntry.reveal(ticket.get(1), destination.get(1)));
                } else {
                    logEntryList.add(LogEntry.hidden(ticket.get(0)));
                    logEntryList.add(LogEntry.hidden(ticket.get(1)));
                }
            }

            mrX = mrX.use(move.tickets());
            int newDestination = destination.size() > 1 ? destination.get(1) : destination.get(0);

            List<Piece> newRemaining = detectives.stream().map(Player::piece).collect(Collectors.toList());
            return new MyGameState(setup, ImmutableSet.copyOf(newRemaining), ImmutableList.copyOf(logEntryList), mrX.at(newDestination), detectives);
        }

        private static List<Ticket> moveTicket(Move move) {
            return move.accept(new Visitor<>() {
                @Override
                public List<Ticket> visit(SingleMove move1) {
                    return List.of(move1.ticket);
                }
                @Override
                public List<Ticket> visit(DoubleMove move1) {
                    return List.of(move1.ticket1, move1.ticket2);
                }
            });
        }

        private static List<Integer> moveDestination(Move move) {
            return move.accept(new Visitor<>() {
                @Override
                public List<Integer> visit(SingleMove move1) {
                    return List.of(move1.destination);
                }
                @Override
                public List<Integer> visit(DoubleMove move1) {
                    return List.of(move1.destination1, move1.destination2);
                }
            });
        }
    }

}
