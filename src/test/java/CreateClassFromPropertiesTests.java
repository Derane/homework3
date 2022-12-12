import org.junit.Test;
import secondTask.RawObject;
import secondTask.CreateClassFromProperties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class CreateClassFromPropertiesTests {


	@Test
	public void parsePropertiesTest() {
		Path path = Paths.get("src/main/java/secondTask/application.properties");
		RawObject testFileFromPropert = new RawObject();
		try {
			testFileFromPropert = CreateClassFromProperties.loadFromProperties(testFileFromPropert.getClass(), path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals("string", testFileFromPropert.getStringProperty());
		assertEquals(10, testFileFromPropert.getIntegerProperty());
		assertEquals(Instant.parse("2022-12-qqT22:18:30Z"), testFileFromPropert.getTimeProperty());
	}

}