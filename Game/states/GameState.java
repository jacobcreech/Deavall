package com.devour.all.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.devour.all.handlers.GameStateManager;
import com.devour.all.main.Game;


/**
 * Created by Jacob on 6/28/2015.
 */
public abstract class GameState {

    protected GameStateManager gsm;
    protected Game game;

    protected SpriteBatch sb;
    protected OrthographicCamera mainCamera;
    protected OrthographicCamera hudCamera;
    protected OrthographicCamera overlayCamera;

    protected GameState(GameStateManager gsm){
        this.gsm = gsm;
        game = gsm.game();
        sb = game.getSpriteBatch();
        mainCamera = game.getMainCamera();
        hudCamera = game.getHudCamera();
        overlayCamera = game.getOverlayCamera();
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render();
    public abstract void dispose();

}
