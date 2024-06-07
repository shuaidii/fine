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
    public Model build(GameSetup setup,
                       Player mrX,
                       ImmutableList<Player> detectives) {
        MyGameStateFactory stateFactory = new MyGameStateFactory();
        return new MyModel(stateFactory.build(setup, mrX, detectives));
    }

    static class MyModel implements Model {
        private final Set<Observer> observerSet = new HashSet<>();
        private Board.GameState gameState;

        public MyModel(Board.GameState gameState) {
            this.gameState = gameState;
        }

        @Nonnull
        @Override
        public Board getCurrentBoard() {
            return gameState;
        }

        @Override
        public void registerObserver(@Nonnull Observer observer) {
            if (observer == null) {
                throw new NullPointerException();
            } else if (observerSet.contains(observer)) {
                throw new IllegalArgumentException();
            } else {
                observerSet.add(observer);
            }
        }

        @Override
        public void unregisterObserver(@Nonnull Observer observer) {
            if (observer == null) {
                throw new NullPointerException();
            } else if (!observerSet.contains(observer)) {
                throw new IllegalArgumentException();
            } else {
                observerSet.remove(observer);
            }
        }

        @Nonnull
        @Override
        public ImmutableSet<Observer> getObserverSet() {
            return ImmutableSet.copyOf(observerSet);
        }

        @Override
        public void chooseMove(@Nonnull Move move) {
            this.gameState = gameState.advance(move);
            this.observerSet.forEach(observer -> {
                if (gameState.getWinner().isEmpty()) {
                    observer.onModelChanged(getCurrentBoard(), Observer.Event.MOVE_MADE);
                } else {
                    observer.onModelChanged(getCurrentBoard(), Observer.Event.GAME_OVER);
                }
            });
        }
    }
}
