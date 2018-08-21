package io.github.mcvlaga.moonlauncher.home;

public enum HomeViewType {
    WEATHER(true), TASKS(true), APPS(true), SEARCH(true);

    private boolean show;

    HomeViewType(boolean show) {
        this.show = show;
    }

    public boolean isShown() {
        return show;
    }

    public void setShown(boolean show) {
        this.show = show;
    }
}
