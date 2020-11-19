package game.entities;

import game.Models;
import game.screens.BaseScreen;
import game.screens.GameScreen;
import perspective.Camera;
import util.Vector;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class SimulateButton extends Entity {

	private final Font font = new Font("Helvetica", Font.BOLD, 45);

	public SimulateButton(Vector pos) {
		super(pos);

		model = Models.simButton;
	}

	@Override
	public void render(Graphics g, Camera camera) {
		super.render(g, camera);

		g.setColor(Color.white);
		g.setFont(font);

		g.drawString("Simulate", 30, 1000);

	}

	@Override
	public void clicked(Vector pos) {
		super.clicked(pos);

		BaseScreen.triggerSim = true;
	}
}
