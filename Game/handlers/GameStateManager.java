package com.devour.all.handlers;

import com.devour.all.main.Game;
import com.devour.all.states.GameState;
import com.devour.all.states.Menu;
import com.devour.all.states.Play;

import java.util.Stack;

/**
 * Created by Jacob on 6/28/2015.
 */
public class GameStateManager {
    private Game game;

    private Stack<GameState> gameStates;

    public static final int PLAY = 16384;
    public static final int MENU = 8192;

    public GameStateManager(Game game){
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(MENU);
    }

    public Game game() { return game; }

    public void create(){}

    public void update(float dt){
        gameStates.peek().update(dt);
    }

    public void render(){
        gameStates.peek().render();
    }

    private GameState getState(int state){
        if(state == PLAY) return new Play(this);
        if(state == MENU) return new Menu(this);
        return null;
    }

    public void setState(int state){
        popState();
        pushState(state);
    }

    public void pushState(int state){
        gameStates.push(getState(state));
    }

    public void popState(){
        GameState state = gameStates.pop();
        game.dispose();
    }


}
