package secondTask;

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

public class ParseProperties {

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
						if (property.name().equals("N/A") && properties.getProperty(field.getName()) != null && property.format().equals("N/A")) {
							setParams(properties, type, field);
						} else if (property.name().equals("N/A")
								&& properties.getProperty(field.getName()) != null && !property.format()
								.equals("N/A")) {
							setParamsWithPropertyFormat(properties, property, type, field);
						} else if (properties.getProperty(property.name()) != null) {
							if (field.getType() == String.class) {
								field.set(type, (String) properties.getProperty(property.name()));
							} else if (field.getType() == int.class) {
								field.set(type, (int) Integer.parseInt(properties.getProperty(property.name())));
							} else if (field.getType() == Instant.class && !property.format().equals("N/A")) {
								try {
									SimpleDateFormat format = new SimpleDateFormat(property.format());
									Instant instant = format.parse(properties.getProperty(property.name()))
											.toInstant();
									field.set(type, instant);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (field.getType() == Instant.class) {
								SimpleDateFormat format = new SimpleDateFormat(property.format());
								Instant instant = format.parse(properties.getProperty(property.name())).toInstant();
								field.set(type, instant);
							}
						} else {
							throw new NotSuchPropertyKeyException();
						}

					}
				}
			} else if (properties.getProperty(field.getName()) != null) {
				setParams(properties, type, field);
			} else {
				throw new NotSuchPropertyKeyException();
			}


		}

		return type;
	}

	private static <T> void setParams(Properties properties, T t, Field field)
			throws IllegalAccessException, ParseException {

		if (field.getType() == String.class) {
			field.set(t, properties.getProperty(field.getName()));
		} else if (field.getType() == int.class) {
			field.set(t, Integer.parseInt(properties.getProperty(field.getName())));
		} else if (field.getType() == Instant.class) {
			SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy mm:ss");
			Instant instant = format.parse(properties.getProperty(field.getName())).toInstant();
			field.set(t, instant);
		}
	}

	private static <T> void setParamsWithPropertyFormat(Properties properties, Property property, T t, Field field)
			throws IllegalAccessException {

		if (field.getType() == String.class) {
			field.set(t, properties.getProperty(field.getName()));
		} else if (field.getType() == int.class) {
			field.set(t, Integer.parseInt(properties.getProperty(field.getName())));
		} else if (field.getType() == Instant.class) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(property.format());
				Instant instant = format.parse(properties.getProperty(field.getName())).toInstant();
				field.set(t, instant);
			} catch (Exception e) {
				System.err.println("Wrong format");
				e.printStackTrace();
			}
		}
	}

}