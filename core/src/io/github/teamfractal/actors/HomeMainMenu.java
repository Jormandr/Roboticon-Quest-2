package io.github.teamfractal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.util.SoundEffects;

/**
 * Creates all of the UI widgets that are displayed on the main menu screen
 *
 */
public class HomeMainMenu extends Table {
	private RoboticonQuest game;
	private TextButton btnNewGame;
	private TextButton btnExit;
	private TextButton players;
	private Image backgroundImage;
	private SpriteBatch batch;
	private float scaleFactorX;
	private float scaleFactorY; 
	private static Texture titleTexture = new Texture(Gdx.files.internal("roboticon_images/Roboticon_Quest_Title"));
	private TextButton addPlayerButton;
	private TextButton subPlayerButton;
	private Label playercount;
	/**
	 * Initialise the Home Menu.
	 * @param game    The game object.
	 */
	public HomeMainMenu(RoboticonQuest game) {
		this.game = game;
		playercount = new Label("4", game.skin);
		playercount.setText("4");
		//Added by Christian Beddows
		batch = (SpriteBatch) game.getBatch();
		backgroundImage = new Image(new Texture(Gdx.files.internal("background/corridor.jpg")));
		
		// Button to increase playercount
		addPlayerButton = new TextButton("+", game.skin);
				
		// Button to decrease bet amount
		subPlayerButton = new TextButton("-", game.skin);
				
		// Create UI Components
		final Image imgTitle = new Image();
		imgTitle.setDrawable(new TextureRegionDrawable(new TextureRegion(titleTexture)));
		
		btnNewGame = new TextButton("New game!", game.skin);
		btnExit = new TextButton("Exit", game.skin);
		
		// Adjust properties.
		btnNewGame.pad(10);
		btnExit.pad(10);

		// Bind events.
		bindEvents();

		// Add UI Components to table.
		add(imgTitle);
		row();
		add(subPlayerButton).padLeft(-80);
		add(playercount).padLeft(-535);
		add(addPlayerButton).padLeft(-455);
		row();
		add(btnNewGame).pad(5);
		row();
		add(btnExit).pad(5);

	}

	/**
	 * Returns the backgrounImage
	 * @return Image
	 */
	public Image getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * Scales the background to fit the screen
	 * @author cb1423
	 * @param width
	 * @param height
	 */
	public void resizeScreen(float width, float height) {
		scaleFactorX = width/backgroundImage.getWidth();
		scaleFactorY = height/backgroundImage.getHeight();
		backgroundImage.setScale(scaleFactorX,scaleFactorY);
	}

	/**
	 * Bind button events.
	 */
	private void bindEvents() {
		btnNewGame.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				SoundEffects.click();
				game.setScreen(game.gameScreen);
				game.gameScreen.newGame();
			}
		});
		
		addPlayerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.PLAYER_COUNT<4)
					game.PLAYER_COUNT += 1;
				playercount.setText(Integer.toString(game.PLAYER_COUNT));
			}
		});

		subPlayerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.PLAYER_COUNT>2)
					game.PLAYER_COUNT -= 1;
				playercount.setText(Integer.toString(game.PLAYER_COUNT));
			}
		});

		btnExit.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				SoundEffects.click();
				Gdx.app.exit();
			}
		});
	}
}
