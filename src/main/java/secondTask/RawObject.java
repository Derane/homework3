package secondTask;

import secondTask.annotation.Property;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class RawObject {
	@Property(name = "valueInt")
	private int numberInt;

	private String lineString;
	@Property(format = "hh:mm:ss a, EEE M/d/uuuu")
	private Instant date;

	public int getNumberInt() {
		return numberInt;
	}

	public void setNumberInt(int numberInt) {
		this.numberInt = numberInt;
	}

	public String getLine() {
		return lineString;
	}

	public void setLine(String line) {
		this.lineString = line;
	}


	public Instant  getDate() {
		return date;
	}

	public void setDate(Instant  date) {

		this.date = date;
	}

	@Override
	public String toString() {
		return "RawClass{" + "numberInt=" + numberInt + ", line='" + lineString + '\'' + ", date=" + date + '}';
	}
}

