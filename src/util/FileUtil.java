package util;

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

	public static String singleLineFileContents(String rawPath) {
		StringBuilder stringBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(rawPath))) {
			stream.forEach(stringBuilder::append);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

}
