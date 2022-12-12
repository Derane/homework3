package firstTask;

import lombok.SneakyThrows;
import org.json.JSONWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.*;
import static java.util.stream.Collectors.toMap;

public class Parser {
	private static final int THREADS_NUMBER = 8;
	private static final String VIOLATIONS_INPUT_DIRECTORY = "src/main/java/firstTask/violations/";

	public static void main(String[] args) throws Exception {
		long m = System.currentTimeMillis();
		parseXMLViolationIntoJSONAndWrite();
		System.out.println((double) (System.currentTimeMillis() - m));
	}

	private static void parseXMLViolationIntoJSONAndWrite() throws IOException {
		File violationsInputDirectory = new File(VIOLATIONS_INPUT_DIRECTORY);
		Map<String, Double> violationsMap = new HashMap<>();
		Optional<File[]> optionalFiles = ofNullable(violationsInputDirectory.listFiles());

		parseXmlAndFillMap(violationsMap, optionalFiles);
		var sortedMapOfViolations = violationsMap.entrySet()
				.stream()
				.sorted(comparingByValue(reverseOrder()))
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));


		writeInJSON(sortedMapOfViolations);

	}

	private static void writeInJSON(LinkedHashMap<String, Double> map) throws IOException {
		try (FileWriter file = new FileWriter("src/main/java/firstTask/summary.json")) {
			map.forEach((key, value) -> {
				new JSONWriter(file)
						.object()
						.key(key)
						.value(value)
						.endObject();
				try {
					file.write("\n");
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
			file.flush();
		}
	}

	@SneakyThrows
	private static void parseXmlAndFillMap(Map<String, Double> violationsMap, Optional<File[]> optionalFiles) {
		Lock lock = new ReentrantLock();
		ExecutorService executorService = newFixedThreadPool(THREADS_NUMBER);
		optionalFiles.ifPresent(files -> {
			Arrays.stream(files).forEach(file -> supplyAsync(() -> file, executorService).thenAccept(asyncFile -> {
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				SAXParser parser = null;
				try {
					parser = parserFactory.newSAXParser();
				} catch (ParserConfigurationException | SAXException e) {
					e.printStackTrace();
				}
				SAXHandler handler = new SAXHandler();
				try {
					parser.parse(VIOLATIONS_INPUT_DIRECTORY + asyncFile.getName(), handler);
				} catch (SAXException | IOException f) {
					f.printStackTrace();
				}
				lock.lock();
				try {
					handler.violations.forEach(violation -> {
						if (!violationsMap.containsKey(violation.getType())) {
							violationsMap.put(violation.getType(), violation.getFineAmount());
						} else {
							violationsMap.put(violation.getType(), violationsMap.get(violation.getType()) + violation.getFineAmount());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}

			}));
		});

		executorService.shutdown();

		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}

	private static class SAXHandler extends DefaultHandler {

		List<Violation> violations = new ArrayList<>();
		Violation violation = null;
		String content = null;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if ("violation".equals(qName)) {
				violation = new Violation();
			}

		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			switch (qName) {
				case "violation" -> {
					violations.add(violation);
				}
				case "type" -> {
					violation.setType(content);
				}
				case "fine_amount" -> {
					violation.setFineAmount(Double.valueOf(content));
				}
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) {
			content = String.copyValueOf(ch, start, length).trim();
		}
	}
}
