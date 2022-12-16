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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Objects;
import java.util.Properties;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

public class CreateClassFromProperties {

	public static <T> T loadFromProperties(Class<T> clazz, Path propertiesPath) throws Exception {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(String.valueOf(propertiesPath))) {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Constructor<T> constructor = clazz.getConstructor();
		T type = constructor.newInstance();

		Field[] fields = clazz.getDeclaredFields();
		stream(fields).forEach(field -> {
			field.setAccessible(true);
			Annotation[] annotations = field.getDeclaredAnnotations();
			if (annotations.length != 0) {
				setWithAnnotation(properties, type, field, annotations);
			} else {
				setWithoutAnnotation(properties, type, field);
			}
		});
		return type;
	}

	@SneakyThrows
	private static <T> void setWithAnnotation(Properties properties, T type, Field field, Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == Property.class) {
				Property property = (Property) annotation;
				if (Objects.equals(property.information(), "EMPTY") && properties.getProperty(field.getName())
						!= null && Objects.equals(property.format(), "EMPTY")) {
					setParameters(properties, type, field);
				} else if (Objects.equals(property.information(), "EMPTY")
						&& properties.getProperty(field.getName()) != null && !Objects.equals(property.format(), "EMPTY")) {
					setParametersWithProps(properties, property, type, field);
				} else if (properties.getProperty(property.information()) != null) {
					if (field.getType() == String.class) {
						field.set(type, properties.getProperty(property.information()));
					} else if (field.getType() == int.class) {
						field.set(type, parseInt(properties.getProperty(property.information())));
					} else if (field.getType() == Instant.class && !Objects.equals(property.format(), "EMPTY")) {
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
	}

	private static <T> void setWithoutAnnotation(Properties properties, T type, Field field) {
		ofNullable(properties.getProperty(field.getName()))
				.ifPresentOrElse(then -> {
					setParameters(properties, type, field);
				}, NotSuchPropertyKeyException::new);
	}

	@SneakyThrows
	private static <T> void setParameters(Properties properties, T type, Field field) {

		if (field.getType() == String.class) {
			field.set(type, properties.getProperty(field.getName()));
		} else if (field.getType() == int.class) {
			field.set(type, parseInt(properties.getProperty(field.getName())));
		} else if (field.getType() == Instant.class) {
			DateFormat format = new SimpleDateFormat("dd.MM.yyyy MM:ss");
			Instant instant = format.parse(properties.getProperty(field.getName())).toInstant();
			field.set(type, instant);
		}
	}

	@SneakyThrows
	private static <T> void setParametersWithProps(Properties properties, Property property, T type, Field field) {

		if (field.getType() == String.class) {
			field.set(type, properties.getProperty(field.getName()));
		} else if (field.getType() == int.class) {
			field.set(type, parseInt(properties.getProperty(field.getName())));
		} else if (field.getType() == Instant.class) {
			DateFormat format = new SimpleDateFormat(property.format());
			Instant instant = format.parse(properties.getProperty(field.getName())).toInstant();
			field.set(type, instant);
		}
	}

}