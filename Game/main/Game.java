package com.devour.all.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.devour.all.handlers.GameStateManager;
import com.devour.all.handlers.PlayerInputProcessor;
import com.devour.all.handlers.ResourceManager;


public class Game extends ApplicationAdapter {

    public static final String TITLE = "Deavall";
    private static SpriteBatch sb;
    private static OrthographicCamera mainCamera;
    private static OrthographicCamera hudCamera;
    private static OrthographicCamera overlayCamera;

    public static final float STEP = 1/60f;
    public static PlayerInputProcessor input;
    private float accum;

    public static float WIDTH;
    public static float HEIGHT;

    public static SpriteBatch getSpriteBatch() { return sb; }
    public static OrthographicCamera getMainCamera() { return mainCamera; }
    public static OrthographicCamera getHudCamera() { return hudCamera; }
    public static OrthographicCamera getOverlayCamera() { return overlayCamera; }

    private GameStateManager gsm;
    public static ResourceManager res;

    @Override
    public void create() {

        input = new PlayerInputProcessor();
        Gdx.input.setInputProcessor(input);

        sb = new SpriteBatch();
        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false);
        overlayCamera = new OrthographicCamera();
        overlayCamera.setToOrtho(false);

        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        // Easy switch for dev between desktop and android
        boolean android = false;
        String filePath = "";

        if(!android){
            filePath = "android/assets/";
        }

        res = new ResourceManager();
        res.loadTexture(filePath+"buttonLong_grey.png", "loadingBar");
        res.loadTextureAtlas(filePath+"defaultButton.atlas", "defaultButton");
        res.loadTexture(filePath+"grid.png", "background");
        res.loadTexture(filePath+"glassPanel_projection.png", "loadBackground");
        res.loadFont(filePath+"visitor.fnt", "mainFont");

        gsm = new GameStateManager(this);

    }

    @Override
    public void render() {

        accum += Gdx.graphics.getDeltaTime();
        while(accum >= STEP){
            accum -= STEP;
            gsm.update(STEP);
            gsm.render();

        }

    }

    public void dispose() {
        sb.dispose();
    }

    public void pause() {}
    public void resume() {}
}
