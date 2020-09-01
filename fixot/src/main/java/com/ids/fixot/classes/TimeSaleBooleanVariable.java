package com.ids.fixot.classes;

public class TimeSaleBooleanVariable {
    private boolean isRetreived = false;
    private ChangeListener listener;

    public boolean isRetreived() {
        return isRetreived;
    }

    public void setRetreived(boolean retreived) {
        this.isRetreived = retreived;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}