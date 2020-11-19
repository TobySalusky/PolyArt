package game.entities.part;

import game.Game;
import game.building.SnapPoint;
import game.entities.Entity;
import game.screens.BaseScreen;
import perspective.Camera;
import poly.Model;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Part extends Entity {

	private static final float gravity = -750F, groundLevel = 900;

	private boolean held = false;
	private Vector grabOffset;

	protected partTypes partType;

	public SnapPoint snappedTo;
	public Vector snapPoint;
	public List<SnapPoint> snapPoints = new ArrayList<>();

	public String name = "Untitled Part";
	public float weight, lift, drag, thrust;

	public enum partTypes {
		head, body, tail, wings, topper, front
	}

	public Part(Vector pos, Model model) {
		super(pos);
		this.model = model;
	}

	public void clicked(Vector pos) {
		held = true;
		grabOffset = this.pos.subbed(pos);
	}

	@Override
	public void render(Graphics g, Camera camera) {
		super.render(g, camera);
	}

	public void unclick() {
		held = false;

		boolean snap = false;
		for (SnapPoint snapPoint : BaseScreen.snapPoints) {
			if (snapPoint.shouldSnap(this)) {
				snapTo(snapPoint);
				snap = true;
				break;
			}
		}

		if (snap == false) {
			unsnap();
		}
	}

	public void snapTo(SnapPoint snapPoint) {
		snappedTo = snapPoint;
		snapPoint.snapped = this;
	}

	public void unsnap() {
		if (snappedTo != null) {
			if (snappedTo.snapped != this) {
				System.out.println("error mismatch");
			}
			snappedTo.snapped = null;
		}
		snappedTo = null;
	}

	@Override
	public void update(float deltaTime) {


		vel.add(Vector.down.multed(gravity * deltaTime));

		if (held && !Game.mouseDown) {
			unclick();
		}

		if (held || pos.y >= groundLevel || snappedTo != null) {
			vel = new Vector();
		}

		if (held) {
			pos.setTo(Game.mousePos.added(grabOffset));
		} else if (snappedTo != null) {
			Vector diff = snapPoint.subbed(pos);
			pos.setTo(snappedTo.point.subbed(diff));
		}

		pos.y = Math.min(pos.y, groundLevel);

		super.update(deltaTime);
	}

	public partTypes getPartType() {
		return partType;
	}
}
