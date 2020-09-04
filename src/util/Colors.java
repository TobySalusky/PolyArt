package util;

import java.awt.*;

public class Colors {

    public static final Color midOrange = new Color(240, 120, 0), darkGrey = new Color(40, 40, 40), nearBlack = new Color(15, 15, 15),
            select = new Color(100, 100, 150, 70), background = Color.DARK_GRAY, panelOutline = new Color(100, 100, 110),
            selectedLayer = new Color(100, 100, 80), editLayer = new Color(110, 100, 70), axisBlue = new Color(0, 0, 255, 100),
            axisPivot = new Color(100, 255, 100, 200), errorText = new Color(200, 100, 100);

    public static Color fromHSV(float hue, float saturation, float value) {

        float C = value * saturation;
        float X = C * (1 - Math.abs((hue / 60) % 2 - 1));
        float m = value - C;

        float r, g, b;

        switch ((int) (hue / 60)) {
            case 0:
                r = C;
                g = X;
                b = 0;
                break;
            case 1:
                r = X;
                g = C;
                b = 0;
                break;
            case 2:
                r = 0;
                g = C;
                b = X;
                break;
            case 3:
                r = 0;
                g = X;
                b = C;
                break;
            case 4:
                r = X;
                g = 0;
                b = C;
                break;
            default:
                r = C;
                g = 0;
                b = X;
                break;
        }
        return new Color((int)((r + m) * 255), (int)((g + m) * 255), (int)((b + m) * 255));
    }
}
