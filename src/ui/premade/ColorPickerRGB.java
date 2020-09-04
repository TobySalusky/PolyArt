package ui.premade;

import perspective.Camera;
import screens.PolyScreen;
import ui.SizedButton;
import util.Vector;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ColorPickerRGB extends SizedButton {

    private float mousePercentX = 0F;
    private float mousePercentY = 0F;

    private BufferedImage wheel;

    private boolean clicked = false;

    private Color color = Color.RED;
    private final Color leftColor = Color.WHITE;
    private final Color bottomColor = Color.BLACK;

    private final PolyScreen screen;

    public ColorPickerRGB(PolyScreen screen) {
        resize(Vector.zero, Vector.one);
        createWheel();
        this.screen = screen;
    }

    public void resize(Vector pos, Vector size) {
        super.resize(pos, size);

        createWheel();
    }

    public void render(Graphics g, Camera camera) {
        g.drawImage(wheel, (int) (pos.x - size.x / 2), (int) (pos.y - size.y / 2), (int) size.x, (int) size.y, null);
        g.setColor(Color.LIGHT_GRAY);
        int pickerSize = 4;
        g.drawRect((int) (pos.x - size.x / 2 + mousePercentX * size.x - pickerSize / 2), (int) (pos.y - size.y / 2 + mousePercentY * size.y - pickerSize / 2), pickerSize, pickerSize);
    }

    public void createWheel() {

        wheel = new BufferedImage((int) size.x, (int) size.y, BufferedImage.TYPE_INT_ARGB);
        for (int row = 0; row < wheel.getWidth(); row++) {
            for (int col = 0; col < wheel.getHeight(); col++) {

                wheel.setRGB(row, col, findColor(row, col).getRGB());

            }
        }

    }

    public Color findColor(float xPixel, float yPixel) {

        float xCount = xPixel / (wheel.getWidth()); //subtract 1?
        float yCount = yPixel / (wheel.getHeight());

        int red = (int) ((1 - yCount) * (xCount * color.getRed() + (1 - xCount) * leftColor.getRed()) + yCount * bottomColor.getRed());
        int green = (int) ((1 - yCount) * (xCount * color.getGreen() + (1 - xCount) * leftColor.getGreen()) + yCount * bottomColor.getGreen());
        int blue = (int) ((1 - yCount) * (xCount * color.getBlue() + (1 - xCount) * leftColor.getBlue()) + yCount * bottomColor.getBlue());

        //red = Math.min(Math.max(red,0),255);
        //green = Math.min(Math.max(green,0),255);
        //blue = Math.min(Math.max(blue,0),255);

        return new Color(red, green, blue);
    }

    @Override
    protected void hitAction(Vector mousePos) {
        clicked = true;
    }

    @Override
    public boolean mouseUp(MouseEvent e) {
        clicked = false;
        return false;
    }

    @Override
    public void mouseAt(Vector pos) {
        super.mouseAt(pos);

        if (clicked) {
            toMouse(pos.x, pos.y);
            changeColor();
        }
    }

    public void hueChange(Color newColor) {
        color = newColor;
        createWheel();
    }

    public void changeColor() {
        screen.colorSelected(findColor(mousePercentX * wheel.getWidth(), mousePercentY * wheel.getHeight()));
    }

    public void toMouse(float mouseX, float mouseY) {

        mousePercentX = Math.max(Math.min((mouseX - (pos.x - size.x / 2)), wheel.getWidth()), 0) / wheel.getWidth();
        mousePercentY = Math.max(Math.min((mouseY - (pos.y - size.y / 2)), wheel.getHeight()), 0) / wheel.getHeight();
    }
}
