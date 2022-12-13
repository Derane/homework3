import lombok.SneakyThrows;
import org.junit.Test;
import secondTask.RawObject;
import secondTask.exception.NotSuchPropertyKeyException;

import java.nio.file.Path;
import java.time.Instant;

import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;
import static secondTask.CreateClassFromProperties.loadFromProperties;

public class CreateClassFromPropertiesTests {


	@Test(expected = NotSuchPropertyKeyException.class)
	@SneakyThrows
	public void throwsExceptionWhenDataIsNotValid() {
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

		assertThat(testFileFromProperty.getTimeProperty()).isEqualTo(Instant.parse("2022-11-28T22:19:35Z"));
		assertThat(testFileFromProperty.getMyNumber()).isEqualTo(20);
		assertThat(testFileFromProperty.getStringProperty()).isEqualTo("string");
	}

}
