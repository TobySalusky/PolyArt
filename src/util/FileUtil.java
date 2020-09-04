package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class FileUtil {

	public static void writeTextFile(String rawPath, String contents) {
		try (PrintWriter out = new PrintWriter(rawPath)) {
			out.println(contents);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage fileToImage(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			return null;
		}
	}

	public static void imageToPNG(BufferedImage image) {
		imageToPNG(image, "C://tmp/image" + Maths.randomInt(10000) + ".png");
	}

		public static void imageToPNG(BufferedImage image, String path) {
		try {
			ImageIO.write(image, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String singleLineFileContents(String rawPath) {
		StringBuilder stringBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(rawPath))) {
			stream.forEach(stringBuilder::append);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return stringBuilder.toString();
	}

}
