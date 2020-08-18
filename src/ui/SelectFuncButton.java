package ui;

import lambda.Func;
import lambda.FuncB;
import util.Gizmo;

import java.awt.*;

public class SelectFuncButton extends FuncButton {

	private final FuncB sel;

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
