package game.base;

import perspective.Camera;

import java.awt.Graphics;

public interface GameObject {

	void update(float deltaTime);

	void render(Graphics g, Camera camera);

}
