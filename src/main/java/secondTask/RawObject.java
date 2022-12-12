package secondTask;

import secondTask.annotation.Property;

import java.time.Instant;

public class RawObject {

	private String stringProperty;

	@Property(information = "integerProperty")
	private int integerProperty;

	@Property(information = "timeProperty", format = "dd.MM.yyyy mm:ss")
	private Instant timeProperty;

	public String getStringProperty() {
		return stringProperty;
	}

	public int getIntegerProperty() {
		return integerProperty;
	}

	public Instant getTimeProperty() {
		return timeProperty;
	}
}