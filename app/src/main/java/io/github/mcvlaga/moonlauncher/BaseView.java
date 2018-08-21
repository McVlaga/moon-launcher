package io.github.mcvlaga.moonlauncher;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}