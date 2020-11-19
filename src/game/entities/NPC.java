package game.entities;

import game.Models;
import game.TextUtil;
import perspective.Camera;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class NPC extends Entity {

	private static final List<String> tips = new ArrayList<>();
	private static final String split = "!!9!!";
	private int tipIndex;

	private String text;
	private String currentText;
	private float timeLeft;
	private float timeSince;

	private final List<String> queuedText = new ArrayList<>();

	static {

		tips.add("Plus icons show where you can add parts.");
		tips.add("Start constructing by putting an aircraft body on the central plus icon.");
		//tips.add("When finished making your aircraft, you can test it using the 'Simulate' button.");

	}

	public NPC(Vector pos) {
		super(pos);

		addText(getIntro(), 10);
		addText("I'll be your mentor for today as you work to build an aircraft.", 10);
		addText("Various parts are scattered across the ground, as you can see.", 8);
		addText("Parts only fit with other parts of types they are meant to attach to, but feel free to pick any part from each selection.", 14);
		addText("Drag parts from the ground to the plus icons, to construct your aircraft, starting with a body-segment.", 10);
		//addText("When you're happy with the combination you've made, try it out using the 'Simulate' button.", 10);
	}

	public void addText(String text, float seconds) {
		queuedText.add(seconds + split + text);
	}

	public String getIntro() {
		return "Greetings, I'm " + getName() + ", " + getDescription() + ".";
	}

	public void startText(String text, float seconds) {
		this.text = text;
		currentText = "";
		timeLeft = seconds;
		timeSince = 0;
	}

	@Override
	public void update(float deltaTime) {
		timeLeft -= deltaTime;
		timeSince += deltaTime;
		if (text != null) {
			currentText = text.substring(0, Math.min((int) (timeSince / 0.025F), text.length()));
		}

		if (timeLeft <= 0) {
			text = null;
			if (queuedText.size() > 0) {
				String queued = queuedText.get(0);
				queuedText.remove(0);
				String[] parts = queued.split(split);
				startText(parts[1], Float.parseFloat(parts[0]));
			}
		}

	}

	@Override
	public void clicked(Vector pos) {

		if (queuedText.size() > 0) {

			if ((int) (timeSince / 0.025F) < text.length()) {
				timeSince += 100;
			} else {
				timeLeft = 0;
			}
			return;
		}

		startText(tips.get(tipIndex % tips.size()), 10);
		tipIndex++;
	}

	@Override
	public void render(Graphics g, Camera camera) {
		super.render(g, camera);

		if (text != null) {
			int height = TextUtil.drawWrappedText(g, text, 250, new Vector(0, 2000));

			Vector tl = new Vector(1350, 750 - height);

			g.setColor(Color.WHITE);
			g.fillRect(1350 - 5, 750 - height, 250 + 10, height + 10);
			TextUtil.drawWrappedText(g, Color.BLACK, currentText, 250, tl);
		}
	}

	public String getName() {
		return "N/A";
	}

	public String getDescription() {
		return "N/A";
	}

	public static class Kalpana extends NPC {

		public Kalpana(Vector pos) {
			super(pos);
			model = Models.kalpana;
		}

		@Override
		public String getName() {
			return "Kalpana Chawla";
		}

		@Override
		public String getDescription() {
			return "an American astronaut, aerospace engineer, and the first Indian woman to go to space";
		}
	}

	public static class Sylvia extends NPC {

		public Sylvia(Vector pos) {
			super(pos);
			model = Models.sylvia;
		}

		@Override
		public String getName() {
			return "Sylvia Acevedo";
		}

		@Override
		public String getDescription() {
			return "a former American aerospace engineer and businesswoman. I started my career in NASA's Jet Propulsion Laboratory";
		}
	}

	public static class Guion extends NPC {
		public Guion(Vector pos) {
			super(pos);
			model = Models.guion;
		}

		@Override
		public String getName() {
			return "Guion Bluford";
		}

		@Override
		public String getDescription() {
			return "an American aerospace engineer, as well as retired U.S. Air Force officer, fighter pilot, and former NASA astronaut, being the first African American to go to space";
		}
	}

	public static class Mae extends NPC {
		public Mae(Vector pos) {
			super(pos);
			model = Models.mae;
		}

		@Override
		public String getName() {
			return "Mae Jemison";
		}

		@Override
		public String getDescription() {
			return "an American aerospace engineer, physician, and former NASA astronaut. I became the first black woman to travel into space when I served as a mission specialist aboard the space shuttle Endeavour.";
		}
	}

	public static class Leroy extends NPC {
		public Leroy(Vector pos) {
			super(pos);
			model = Models.leroy;
		}

		@Override
		public String getName() {
			return "Leroy Chiao";
		}

		@Override
		public String getDescription() {
			return "an American chemical engineer, retired NASA astronaut, entrepreneur, and engineering consultant. I flew on three space shuttle flights, and commanded Expedition 10.";
		}
	}
}
