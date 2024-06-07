package invaders.observer;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-10-12
 * Time: 23:17
 */
public interface Subject {
    void registerObserver(Observer observer);

    void notifyObservers(int incrScore);
}
