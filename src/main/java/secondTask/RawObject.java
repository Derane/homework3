package secondTask;

import lombok.Getter;
import secondTask.annotation.Property;

import java.time.Instant;

@Getter
public class RawObject {

	private String stringProperty;

	@Property(information = "numberProperty")
	private int myNumber;

	@Property(information = "timeProperty", format = "dd.MM.yyyy mm:ss")
	private Instant timeProperty;

}