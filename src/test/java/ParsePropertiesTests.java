import org.junit.Test;
import secondTask.RawObject;
import secondTask.ParseProperties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class ParsePropertiesTests {


	@Test
	public void parsePropertiesTest() {
		Path path = Paths.get("src/main/java/secondTask/application.properties");
		RawObject test = new RawObject();
		try {
			test = ParseProperties.loadFromProperties(test.getClass(), path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals("value1", test.getStringProperty());
		assertEquals(10, test.getIntegerProperty());
		assertEquals(Instant.parse("2022-11-28T22:18:30Z"), test.getTimeProperty());
	}

}