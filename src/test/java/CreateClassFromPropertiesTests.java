import lombok.SneakyThrows;
import org.junit.Test;
import secondTask.CreateClassFromProperties;
import secondTask.RawObject;
import secondTask.exception.NotSuchPropertyKeyException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static java.nio.file.Paths.*;
import static org.assertj.core.api.Assertions.assertThat;
import static secondTask.CreateClassFromProperties.*;

public class CreateClassFromPropertiesTests {


	@Test(expected = NotSuchPropertyKeyException.class)
	@SneakyThrows
	public void throwsExcpetionWhenDataIsNotValid() {
		Path path = get("src/main/java/secondTask/allProperties/notValid.properties");
		RawObject testFileFromProperty = new RawObject();
		testFileFromProperty = loadFromProperties(testFileFromProperty.getClass(), path);
	}

	@Test
	@SneakyThrows
	public void parsePropertiesTest() {
		Path path = get("src/main/java/secondTask/allProperties/application.properties");
		RawObject testFileFromProperty = new RawObject();

		testFileFromProperty = loadFromProperties(testFileFromProperty.getClass(), path);

		assertThat(testFileFromProperty.getStringProperty()).isEqualTo("string");
		assertThat(testFileFromProperty.getMyNumber()).isEqualTo(20);
		assertThat(testFileFromProperty.getTimeProperty()).isEqualTo(Instant.parse("2022-11-28T22:19:35Z"));
	}

}
