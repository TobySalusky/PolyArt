package poly;

import perspective.Camera;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PolyLayer {

    private List<Polygon> polyList = new ArrayList<>();

    private boolean visible = true;

    public void toggleVisibility() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public List<Polygon> getPolygons() {
        return polyList;
    }
}
