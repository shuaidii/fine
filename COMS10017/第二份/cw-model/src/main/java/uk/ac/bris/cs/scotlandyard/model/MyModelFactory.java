package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.HashSet;
import java.util.Set;

/**
 * cw-model
 * Stage 2: Complete this class
 */
public final class MyModelFactory implements Factory<Model> {

    @Nonnull
    @Override
    public Model build(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
        return new MyModel(setup, mrX, detectives);
    }

    static class MyModel implements Model {
        private final Set<Observer> observers = new HashSet<>();
        private Board.GameState currentBoard;

        private MyModel(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
            this.currentBoard = new MyGameStateFactory().build(setup, mrX, detectives);
        }

        @Nonnull
        @Override
        public Board getCurrentBoard() {
            return currentBoard;
        }

        @Override
        public void registerObserver(@Nonnull Observer observer) {
            if (observer == null) {
                throw new NullPointerException();
            }
            if (observers.contains(observer)) {
                throw new IllegalArgumentException("Already registered");
            }
            observers.add(observer);
        }

        @Override
        public void unregisterObserver(@Nonnull Observer observer) {
            if (observer == null) {
                throw new NullPointerException();
            }
            if (observers.contains(observer)) {
                observers.remove(observer);
            } else {
                throw new IllegalArgumentException("No registered");
            }
        }

        @Nonnull
        @Override
        public ImmutableSet<Observer> getObservers() {
            return ImmutableSet.copyOf(observers);
        }

        @Override
        public void chooseMove(@Nonnull Move move) {
            this.currentBoard = this.currentBoard.advance(move);
            Observer.Event event = currentBoard.getWinner().isEmpty() ? Observer.Event.MOVE_MADE : Observer.Event.GAME_OVER;
            this.observers.forEach(observer -> observer.onModelChanged(this.currentBoard, event));
        }
    }
}