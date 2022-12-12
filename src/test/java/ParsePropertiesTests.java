import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Properties;
import org.junit.Test;
import secondTask.ApplicationPropertyObject;
import secondTask.ParseProperties;
import secondTask.RawObject;
import secondTask.annotation.Property;
import secondTask.exception.NotSuchPropertyKeyException;

public class ParsePropertiesTests {

	Path path = Paths.get("src/main/java/secondTask/application.properties");


	RawObject rawClass = new RawObject();



	@Test
	public void correctlyWorkTest() {

		ApplicationPropertyObject test = new ApplicationPropertyObject();
		try {
			test = ParseProperties.loadFromProperties(test.getClass(), path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals("value1", test.getStringProperty());
		assertEquals(10, test.getNumbProperty());
		assertEquals(Instant.parse("2022-11-28T22:18:30Z"), test.getTimeProperty());
	}

}