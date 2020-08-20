package ui;

import lambda.Func;
import lambda.FuncB;
import util.Gizmo;
import util.Vector;

import java.awt.*;

public class SelectFuncButton extends FuncButton {

	private final FuncB sel;

	public SelectFuncButton(Func func, FuncB sel, float x, float y, float width, float height) {
		super(func, x, y, width, height);
		this.sel = sel;
	}

	public SelectFuncButton(Func func, FuncB sel, float x, float y) {
		super(func, x, y);
		this.sel = sel;
	}

	@Override
	protected Color findColor() {

		if (sel.isTrue()) {
			return super.findColor();
		}
		return Gizmo.darkGrey;
	}
}
