package com.devour.all.states;

import com.badlogic.gdx.Gdx;
import static com.devour.all.handlers.Box2DVars.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.devour.all.entities.Enemy;
import com.devour.all.entities.Food;
import com.devour.all.entities.Player;
import com.devour.all.handlers.Background;
import com.devour.all.handlers.EntityContactListener;
import com.devour.all.handlers.EventHandler;
import com.devour.all.handlers.GameStateManager;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Jacob on 6/28/2015.
 */
public class Play extends GameState {

    private static World world;
    private Box2DDebugRenderer b2dr;
    private static final float WIDTH = Gdx.graphics.getWidth();
    private static final float HEIGHT = Gdx.graphics.getHeight();

    private ArrayList<Enemy> enemies;
    private static ArrayList<Food> foods;
    private static Player player;
    private EventHandler eventHandler;

    public static Player getPlayer() { return player; }

    private OrthographicCamera b2dcam;
    private Background background;

    public Play(GameStateManager gsm){
        super(gsm);

        // Create the world with no gravity
        world = new World(new Vector2(0, 0), true);
        eventHandler = new EventHandler();
        world.setContactListener(new EntityContactListener(eventHandler));

        b2dr = new Box2DDebugRenderer();
        b2dcam = new OrthographicCamera();
        b2dcam.setToOrtho(false, (WIDTH/2) / PPM, (WIDTH/2) / PPM);

        // Initialize arrayLists
        enemies = new ArrayList<Enemy>();
        foods = new ArrayList<Food>();

        // Create the area for the entities
        createArea();

        // Create the Player
        createPlayer();

        // Create food
        for(int i = 0; i < 100; i++){
            createFood();
        }

        // Create Background
        Texture texture = new Texture(Gdx.files.internal("android/assets/grid.png"));
        TextureRegion textureRegion = new TextureRegion(texture, 0, 0, 49, 49);
        background = new Background(textureRegion, mainCamera, .31f);

    }

    public void createArea(){
        /*
        * The area is the room that which each body can move around in.
        * Barriers on all four sides shall be invisible, as well as show
        * an animation when in collision with an entity.
         */

        // Bottom Left corner
        createBarrier(WIDTH*(-2), HEIGHT * (-2), true);
        createBarrier(WIDTH*(-2), HEIGHT * (-2), false);
        // Top Left corner
        createBarrier(WIDTH*(-2), HEIGHT * (2), true);
        // Bottom Right corner
        createBarrier(WIDTH*(2), HEIGHT * (-2), false);


    }

    public void createBarrier(float xPos, float yPos, boolean direction){
        /*
        * Helper function for creating the barriers in createArea.
        * Direction drawn is decided by the boolean:
        *   True = Draw Right
        *   False = Draw Up
         */

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(xPos / PPM, yPos / PPM);
        bodyDef.type = BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        if(direction) {
            shape.setAsBox(WIDTH*4 / PPM, 2 / PPM);
        }
        else{
            shape.setAsBox(2 / PPM, WIDTH*4 / PPM);
        }
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = BIT_BARRIER;
        fixtureDef.filter.maskBits = BIT_ENEMY | BIT_PLAYER;
        body.createFixture(fixtureDef).setUserData(BIT_BARRIER);
        body.setUserData("Barrier");

        shape.dispose();

    }

    public void createPlayer(){
        /*
        * This function creates the player and places him onto the
        * area.
         */
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(WIDTH / 4 / PPM - 10 / PPM, HEIGHT / 2 / PPM - 10 / PPM);
        bodyDef.type = BodyType.DynamicBody;
        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(10 / PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_BARRIER | BIT_ENEMY | BIT_FOOD | BIT_VIRUS;
        body.createFixture(fixtureDef).setUserData(BIT_PLAYER);

        player = new Player(body);
        body.setUserData(player);

        circle.dispose();
    }

    public static void createFood(){
        BodyDef bodyDef = new BodyDef();
        Random rand = new Random();
        int maxX = (int) (4*WIDTH);
        int maxY = (int) (4*HEIGHT);
        int randomX = rand.nextInt(maxX+1);
        int randomY = rand.nextInt(maxY+1);

        // Creating random coordinates between barriers
        randomX -= (int) (2*WIDTH);
        randomY -= (int) (2*HEIGHT);

        bodyDef.position.set(randomX / PPM, randomY / PPM);
        bodyDef.type = BodyType.StaticBody;
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(5 / PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.filter.categoryBits = BIT_FOOD;
        fixtureDef.filter.maskBits = BIT_ENEMY | BIT_PLAYER;
        body.createFixture(fixtureDef).setUserData(BIT_FOOD);

        Food food = new Food(body);
        body.setUserData(food);
        circle.dispose();
        foods.add(food);
    }

    @Override
    public void handleInput() {  }

    @Override
    public void update(float dt) {
        //System.out.println(player.getBody().getPosition().x + " " + player.getBody().getPosition().y);
        handleInput();
        world.step(dt, 6, 2);

        ArrayList<Body> bodies = eventHandler.getBodies();
        for(int i = 0; i < bodies.size(); i++) {
            System.out.println("Removing Food");
            Body b = bodies.get(i);
            foods.remove(b.getUserData());
            world.destroyBody(bodies.get(i));
            Play.createFood();
        }
        bodies.clear();

    }

    @Override
    public void render() {

        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.begin();

        sb.setProjectionMatrix(hudCamera.combined);
        background.render();

        sb.setProjectionMatrix(mainCamera.combined);
        mainCamera.position.set(
                player.getBody().getPosition().x * PPM * 2 + WIDTH / 4,
                player.getBody().getPosition().y * PPM * 4 + HEIGHT / 4,
                0
        );
        mainCamera.update();
        sb.end();

        // Draw box2d world
        b2dcam.position.set(
                player.getBody().getPosition().x ,//+ WIDTH / 8 / PPM ,
                player.getBody().getPosition().y,
                0
        );
        b2dcam.update();
        b2dr.render(world, b2dcam.combined);

    }

    @Override
    public void dispose() {
        world.dispose();
    }
}
