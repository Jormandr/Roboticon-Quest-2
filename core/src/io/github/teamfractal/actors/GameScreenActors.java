package io.github.teamfractal.actors;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.LandPlot;
import io.github.teamfractal.entity.Player;
import io.github.teamfractal.entity.Roboticon;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.screens.AbstractAnimationScreen;
import io.github.teamfractal.screens.GameScreen;
import io.github.teamfractal.util.MessagePopUp;
import io.github.teamfractal.util.RandomEvents;
import io.github.teamfractal.util.SoundEffects;
import io.github.teamfractal.util.TileConverter;

/**
 * Creates all of the widgets that are displayed on the main game screen
 */
public class GameScreenActors {
	private final Stage stage;
	private RoboticonQuest game;
	private GameScreen screen;
	private Label phaseInfo;
	private Label playerStats;
	private TextButton buyLandPlotBtn;
	private TextButton installRoboticonBtn;
	private TextButton installRoboticonBtnCancel;
	private Label installRoboticonLabel;
	private SelectBox<String> installRoboticonSelect;
	private Label plotStats;
	private TextButton nextButton;
	private boolean dropDownActive;
	private boolean listUpdated;
	private Image backgroundImage;
	private SpriteBatch batch;
	private float scaleFactorX;
	private float scaleFactorY;
	//private Image chancellor;
	public TextButton chancellor;
	private int c1,c2,c3,c4;

	/**
	 * Initialise the main game screen components.
	 * @param game         The game manager {@link RoboticonQuest}
	 * @param screen       Current screen to display on.
	 */
	public GameScreenActors(final RoboticonQuest game, GameScreen screen) {
		this.game = game;
		this.screen = screen;
		this.stage = screen.getStage();

		//Added by Christian Beddows
		batch = (SpriteBatch) game.getBatch();
		backgroundImage = new Image(new Texture(Gdx.files.internal("background/space-stars.jpeg")));
		/*if(game.getPhase()==8){
			chancellor=new Image(new Texture(Gdx.files.internal("background/chancellor.jpeg")));
		}
		else
			chancellor=null;*/
	}

	/**
	 * returns the background image
	 * @return Image
	 */
	public Image getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * Setup buttons.
	 */
	public void initialiseButtons() {
		// Create UI components
		phaseInfo = new Label("", game.skin);
		plotStats = new Label("", game.skin);
		playerStats = new Label("", game.skin);
		nextButton = new TextButton("Next ->", game.skin);
		buyLandPlotBtn = new TextButton("Buy LandPlot", game.skin);
		createRoboticonInstallMenu();
		chancellor=new TextButton("Here I am", game.skin);
		// Adjust properties.
		listUpdated = false;
		hideInstallRoboticon();
		buyLandPlotBtn.setVisible(false);
		buyLandPlotBtn.pad(2, 10, 2, 10);
		phaseInfo.setAlignment(Align.right);
		plotStats.setAlignment(Align.topLeft);
		installRoboticonSelect.setSelected(null);
		// Bind events
		bindEvents();
		// Add to the stage for rendering.
		stage.addActor(nextButton);
		stage.addActor(buyLandPlotBtn);
		stage.addActor(installRoboticonTable);
		stage.addActor(phaseInfo);
		stage.addActor(plotStats);
		stage.addActor(playerStats);
		stage.addActor(chancellor);
		//chancellor movement
		Timer x=new Timer();
		x.schedule(new TimerTask(){
            @Override
            public void run() {
                chanrun();
            }
        }
        , 1000        //    (delay)
        , 1000     //    (seconds)
    );
		
		// Update UI positions.
		AbstractAnimationScreen.Size size = screen.getScreenSize();
		resizeScreen(size.Width, size.Height);
	}

	private void chanrun(){
		Random rand=new Random();
		this.c1=rand.nextInt((int) screen.getScreenSize().Width-3);
		this.c2=rand.nextInt((int) screen.getScreenSize().Height-50)+30;
		textUpdate();

	}
	private Table installRoboticonTable;

	/**
	 * Create the roboticon installation menu.
	 */
	private void createRoboticonInstallMenu() {
		installRoboticonTable = new Table();
		Table t = installRoboticonTable;

		installRoboticonSelect = new SelectBox<String>(game.skin);
		
		///// Changed getRoboticonAmountList() to getCustomisedRoboticonAmountList()
		///// Stop player's placing uncustomised roboticons
		installRoboticonSelect.setItems(game.getPlayer().getCustomisedRoboticonAmountList());

		installRoboticonLabel = new Label("Install Roboticon: ", game.skin);
		installRoboticonBtn = new TextButton("Confirm", game.skin);
		installRoboticonBtnCancel = new TextButton("Cancel", game.skin);

		t.add(installRoboticonLabel).colspan(2);
		t.row();
		t.add(installRoboticonSelect).colspan(2);
		t.row();
		t.add(installRoboticonBtn);
		t.add(installRoboticonBtnCancel);
		t.row();
	}


	/**
	 * Bind all button events.
	 */
	private void bindEvents() {
		buyLandPlotBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundEffects.click();
				event.stop();
				hideBuyLand();
				if (buyLandPlotBtn.isDisabled()) {
					return ;
				}
				LandPlot selectedPlot = screen.getSelectedPlot();

				if (selectedPlot.hasOwner()) {
					return;
				}
		 
				Player player = game.getPlayer();
				if (player.purchaseLandPlot(selectedPlot)) {
					//Added a random event where the player finds a chest containing money - Christian Beddows
					if (RandomEvents.tileHasChest()){
						SoundEffects.chime();
						int playerTreasure = RandomEvents.amountOfMoneyInTreasureChest(player);
						stage.addActor(new MessagePopUp("You found a treasure chest!","On your new tile you "
								+ "find a buried treasure chest containing " + Integer.toString(playerTreasure) + " money!"));
					}
					//Added a random event where you disturb a flock of geese on a plot - Ben
					if (RandomEvents.geeseAttack()){
						SoundEffects.anxiety();
						ResourceType resource = RandomEvents.getResourceStolenByGeese(player);
						int quantityLost = RandomEvents.geeseStealResources(player,resource);
						stage.addActor(new MessagePopUp("Disturbed a flock of Geese!","On your new tile you "
								+ "discover a flock of geese they attack!, you lost " + Integer.toString(quantityLost) + " "+resource));
					}
					TiledMapTileLayer.Cell playerTile = selectedPlot.getPlayerTile();
					playerTile.setTile(screen.getPlayerTile(player));
					textUpdate();
				}
				//Added a popup if you dont have enough money to buy a plot - Ben
				else{
					SoundEffects.error();
					stage.addActor(new MessagePopUp("Not enough money!","You dont have enough Money to buy this plot."));
				}
			}
		});

		nextButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundEffects.click();
				event.stop();
				if (nextButton.isDisabled()) {
					return ;
				}
				buyLandPlotBtn.setVisible(false);
				plotStats.setVisible(false);
				hideInstallRoboticon();
				game.nextPhase();
				dropDownActive = true;
				
				///// Changed getRoboticonAmountList() to getCustomisedRoboticonAmountList()
				///// Stop player's placing uncustomised roboticons
				installRoboticonSelect.setItems(game.getPlayer().getCustomisedRoboticonAmountList());
				textUpdate();
			}
		});
		chancellor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundEffects.click();
				event.stop();
				if (!chancellor.isVisible()) {
					return ;
				}
				buyLandPlotBtn.setVisible(false);
				plotStats.setVisible(false);
				hideInstallRoboticon();
				game.nextPhase();
				dropDownActive = true;
				game.getPlayer().gambleResult(true, 50);
				///// Changed getRoboticonAmountList() to getCustomisedRoboticonAmountList()
				///// Stop player's placing uncustomised roboticons
				textUpdate();
			}
		});
		installRoboticonBtn.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundEffects.click();
				event.stop();
				if (installRoboticonBtn.isDisabled()) {
					return ;
				}
				if (!listUpdated) { //prevents updating selection list from updating change listener
					LandPlot selectedPlot = screen.getSelectedPlot();
					if (selectedPlot.getOwner() == game.getPlayer() && !selectedPlot.hasRoboticon()) {
						Roboticon roboticon = null;
						ResourceType type = ResourceType.Unknown;
						int selection = installRoboticonSelect.getSelectedIndex();

						Array<Roboticon> roboticons = game.getPlayer().getRoboticons();
						switch (selection) {
							case 0:
								type = ResourceType.ORE;
								break;
							case 1:
								type = ResourceType.ENERGY;
								break;
							//added by andrew - added so that food appears in the drop down menu in roboticon placement
							case 2:
								type = ResourceType.FOOD;
								break;
							default:
								type = ResourceType.Unknown;
								break;
						}

						for (Roboticon r : roboticons) {
							if (!r.isInstalled() && r.getCustomisation() == type) {
								roboticon = r;
								break;
							}
						}

						if (roboticon != null) {
							///// Modified by Josh Neil - 
							///// Added if else branches previously was just the instructions in the 
							///// else branch followed by textUpdate()
							if(RandomEvents.roboticonIsFaulty()){
								// Roboticon was faulty and has broken (cannot be placed)
								SoundEffects.pulse();
								game.getPlayer().removeRoboticon(roboticon);
								stage.addActor(new MessagePopUp("That roboticon was faulty","That roboticon was faulty and exploded!"));
							}
							else{
								selectedPlot.installRoboticon(roboticon);
								TiledMapTileLayer.Cell roboticonTile = selectedPlot.getRoboticonTile();
								roboticonTile.setTile(TileConverter.getRoboticonTile(roboticon.getCustomisation()));
								selectedPlot.setHasRoboticon(true);
							}
							textUpdate();
						}

						hideInstallRoboticon();
						updateRoboticonList();
						dropDownActive = true;

					} else listUpdated = false;
				}
			}
		});

		installRoboticonBtnCancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				SoundEffects.click();
				event.stop();
				dropDownActive = false;
				hideInstallRoboticon();
			}
		});
	}

	/**
	 * Tile click callback event.
	 *
	 * @param plot The landplot tileClicked.
	 * @param x    Current mouse x position
	 * @param y    Current mouse y position
	 */
	public void tileClicked(LandPlot plot, float x, float y) {
		SoundEffects.click();
		Player player = game.getPlayer();

		switch (game.getPhase()) {
			// Phase 1:
			// Purchase LandPlot.
			case 1:
				buyLandPlotBtn.setPosition(x + 10, y);
				if (!plot.hasOwner()
						&& player.haveEnoughMoney(plot)) {
					buyLandPlotBtn.setDisabled(false);
					buyLandPlotBtn.setText("Buy LandPlot");
					showPlotStats(plot, x + 10, y);
					
					
				} else {
					buyLandPlotBtn.setDisabled(true);
					buyLandPlotBtn.setText("Unavailable");
					
				}
				

				buyLandPlotBtn.setVisible(true);
				break;

			// Phase 3:
			// Install Roboticon 
			case 3:
				if (player == plot.getOwner()) {
					installRoboticonTable.setPosition(x, y, Align.center);
					updateRoboticonList();
					installRoboticonTable.setVisible(true);
				} else {
					hideInstallRoboticon();
				}
		}


	}

	/**
	 * Update the dropdown list of roboticon available.
	 */
	private void updateRoboticonList() {
		
		///// Josh Neil changed getRoboticonAmountList() to getCustomisedRoboticonAmountList()
		///// Stop player's placing uncustomised roboticons
		installRoboticonSelect.setItems(game.getPlayer().getCustomisedRoboticonAmountList());
	}

	/**
	 * Get the "Buy Land" button.
	 * @return "Buy Land" button.
	 */
	public TextButton getBuyLandPlotBtn() {
		return buyLandPlotBtn;
	}

	/**
	 * Updates the UI display.
	 */
	public void textUpdate() {
		String phaseText = "Player " + (game.getPlayerInt() + 1) + "; Phase " + game.getPhase() + " - " + game.getPhaseString();
		phaseInfo.setText(phaseText);
		chancellor.setPosition(c1, c2);
		String statText = "Ore: " + game.getPlayer().getOre()
				+ " Energy: " + game.getPlayer().getEnergy()
				+ " Food: " + game.getPlayer().getFood()
				+ " Money: " + game.getPlayer().getMoney();
		playerStats.setText(statText);
		if(game.getPhase()==5)
			chancellor.setVisible(true);
		else
			chancellor.setVisible(false);
	}

	/**
	 * Callback event on window updates,
	 * to adjust the UI components position relative to the screen.
	 *
	 * @param width    The new Width.
	 * @param height   The new Height.
	 */
	public void resizeScreen(float width, float height) {
		float topBarY = height - 20;
		phaseInfo.setWidth(width - 10);
		phaseInfo.setPosition(0, topBarY);

		playerStats.setPosition(10, topBarY);
		nextButton.setPosition(width - nextButton.getWidth() - 10, 10);

		chancellor.setPosition(width/2, height/2);
		
		scaleFactorX = width/backgroundImage.getWidth();
		scaleFactorY = height/backgroundImage.getHeight();
		backgroundImage.setScale(scaleFactorX,scaleFactorY);
		backgroundImage.toBack();
		}


	/**
	 * Show plot information about current selected stats.
	 * @param plot           The land plot to show info.
	 * @param x              The <i>x</i> position to display the information.
	 * @param y              The <i>y</i> position to display the information.
	 */
	public void showPlotStats(LandPlot plot, float x, float y) {
		String plotStatText = "Ore: " + plot.getResource(ResourceType.ORE)
				+ "  Energy: " + plot.getResource(ResourceType.ENERGY)
				+ "  Food: " + plot.getResource(ResourceType.FOOD);

		plotStats.setText(plotStatText);
		plotStats.setPosition(x, y);
		plotStats.setVisible(true);
	}


	/**
	 * Hide "Buy Land" button and plot information.
	 */
	public void hideBuyLand() {
		buyLandPlotBtn.setVisible(false);
		plotStats.setVisible(false);
	}

	/**
	 * Hide install roboticon dialog.
	 */
	public void hideInstallRoboticon() {
		installRoboticonTable.setVisible(false);
	}

	/**
	 * Check if install roboticon dialog is visible.
	 * @return   <code>true</code> if is visible, <code>false</code> if not visible.
	 */
	public boolean installRoboticonVisible() {
		return installRoboticonTable.isVisible();
	}

	
	// Added by Josh Neil so that the next stage button can be removed during the resource generation stage
	public void hideNextStageButton() {
		nextButton.setVisible(false);
	}
	
	// Added by Josh Neil so that the next stage button can be removed during the resource generation stage
	// (and then added to the screen again afterwards)
	public void showNextStageButton(){
		nextButton.setVisible(true);
	}
}
