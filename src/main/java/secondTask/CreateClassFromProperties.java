package secondTask;

import lombok.SneakyThrows;
import secondTask.annotation.Property;
import secondTask.exception.NotSuchPropertyKeyException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Properties;

import static java.lang.Integer.*;
import static java.util.Optional.*;

public class CreateClassFromProperties {

	public static <T> T loadFromProperties(Class<T> cls, Path propertiesPath) throws Exception {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(String.valueOf(propertiesPath))) {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Constructor<T> constructor = cls.getConstructor();
		T type = constructor.newInstance();

		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			Annotation[] annotations = field.getDeclaredAnnotations();
			if (annotations.length != 0) {
				for (Annotation annotation : annotations) {
					if (annotation.annotationType().equals(Property.class)) {
						Property property = (Property) annotation;
						if (property.information().equals("EMPTY") && properties.getProperty(field.getName())
								!= null && property.format().equals("EMPTY")) {
							setParameters(properties, type, field);
						} else if (property.information().equals("EMPTY")
								&& properties.getProperty(field.getName()) != null && !property.format()
								.equals("EMPTY")) {
							setParamsWithPropertyFormat(properties, property, type, field);
						} else if (properties.getProperty(property.information()) != null) {
							if (field.getType() == String.class) {
								field.set(type, properties.getProperty(property.information()));
							} else if (field.getType() == int.class) {
								field.set(type, parseInt(properties.getProperty(property.information())));
							} else if (field.getType() == Instant.class && !property.format().equals("EMPTY")) {
								try {
									SimpleDateFormat format = new SimpleDateFormat(property.format());
									Instant instant = format.parse(properties.getProperty(property.information()))
											.toInstant();
									field.set(type, instant);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (field.getType() == Instant.class) {
								SimpleDateFormat format = new SimpleDateFormat(property.format());
								Instant instant = format.parse(properties.getProperty(property.information())).toInstant();
								field.set(type, instant);
							}
						} else {
							throw new NotSuchPropertyKeyException();
						}

					}
				}
			} else {
				ofNullable(properties.getProperty(field.getName())).ifPresentOrElse((t) -> {
					try {
						setParameters(properties, type, field);
					} catch (IllegalAccessException | ParseException e) {
						e.printStackTrace();
					}
				}, NotSuchPropertyKeyException::new);
			}
		}
		return type;
	}

	private static <T> void setParameters(Properties properties, T t, Field field)
			throws IllegalAccessException, ParseException {

		if (field.getType() == String.class) {
			field.set(t, properties.getProperty(field.getName()));
		} else if (field.getType() == int.class) {
			field.set(t, parseInt(properties.getProperty(field.getName())));
		} else if (field.getType() == Instant.class) {
			SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy MM:ss");
			Instant instant = format.parse(properties.getProperty(field.getName())).toInstant();
			field.set(t, instant);
		}
	}
	@SneakyThrows
	private static <T> void setParamsWithPropertyFormat(Properties properties, Property property, T type, Field field) {

		if (field.getType() == String.class) {
			field.set(type, properties.getProperty(field.getName()));
		} else if (field.getType() == int.class) {
			field.set(type, parseInt(properties.getProperty(field.getName())));
		} else if (field.getType() == Instant.class) {
				SimpleDateFormat format = new SimpleDateFormat(property.format());
				Instant instant = format.parse(properties.getProperty(field.getName())).toInstant();
				field.set(type, instant);
		}
	}

}