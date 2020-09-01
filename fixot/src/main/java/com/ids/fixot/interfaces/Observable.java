package com.ids.fixot.interfaces;

public interface Observable {
    void addObserver(Observer myObserver);
    void removeObserver(Observer myObserver);
    void notifyObservers(boolean change);// params need to change as per your Observer.
}