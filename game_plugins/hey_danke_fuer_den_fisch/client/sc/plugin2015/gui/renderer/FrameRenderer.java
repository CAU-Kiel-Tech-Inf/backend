/**
 * 
 */
package sc.plugin2015.gui.renderer;

import java.awt.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import sc.plugin2015.gui.renderer.RenderConfigurationDialog;
import sc.plugin2015.GameState;
import sc.plugin2015.gui.renderer.primitives.Background;
import sc.plugin2015.gui.renderer.primitives.GuiBoard;
import sc.plugin2015.gui.renderer.primitives.ProgressBar;

/**
 * @author fdu
 */

public class FrameRenderer extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(FrameRenderer.class);

	private GuiBoard guiBoard;
	private Background background;
	private ProgressBar progressBar;

	public void setup() {
		// logger.debug("calling frameRenderer.size()");

		RenderConfiguration.loadSettings();

		// choosing renderer from options - using P2D as default
		if (RenderConfiguration.optionRenderer.equals("JAVA2D")) {
			size(this.width, this.height, JAVA2D);
			logger.debug("Using P2D as Renderer");
		} else if (RenderConfiguration.optionRenderer.equals("P3D")) {
			size(this.width, this.height, P3D);
			logger.debug("Using P3D as Renderer");
		} else {
			size(this.width, this.height, P2D);
			logger.debug("Using Java2D as Renderer");
		}

		noLoop(); // prevent thread from starving everything else
		smooth(2); // Anti Aliasing

		background = new Background(this);
		logger.debug("Dimension when creating board: (" + this.width + ","
				+ this.height + ")");
		guiBoard = new GuiBoard(this);
		progressBar = new ProgressBar(this);

		// initial draw
		background.draw();
		guiBoard.draw();
		progressBar.draw();

	}

	public void draw() {
		background.draw();
		guiBoard.draw();
		progressBar.draw();
	}

	public void updateGameState(GameState gameState) {
		guiBoard.update(gameState.getBoard());
	}

	public void requestMove(int maxTurn) {
		// TODO The User has to do a Move

	}

	public Image getImage() {
		// TODO return an Image of the current board
		return null;
	}

	public void mouseClicked() {
		this.redraw();
	}

	public void resize() {

	}

	public void keyPressed() {
		if (key == 'c' || key == 'C') {
			new RenderConfigurationDialog(FrameRenderer.this);
		}

	}

}