package com.darrenswhite.rs.ironquest.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Darren White
 */
public class LogFormatter extends Formatter {

	/**
	 * System line separator
	 */
	private static final String LINE_SEPARATOR =
			System.getProperty("line.separator");

	@Override
	public String format(LogRecord record) {
		StringBuilder result = new StringBuilder();

		result.append("[").append(record.getLevel().getName()).append("] ");
		result.append(new Date(record.getMillis())).append(": ");
		result.append(record.getLoggerName()).append(": ");
		result.append(record.getMessage()).append(throwableToString(record.getThrown()));
		result.append(LogFormatter.LINE_SEPARATOR);

		return result.toString();
	}

	@Override
	public String formatMessage(LogRecord record) {
		return record.getMessage();
	}

	/**
	 * Converts a Throwable as a String
	 *
	 * @param t The Throwable to convert
	 * @return The Throwable as a String
	 */
	private static String throwableToString(Throwable t) {
		if (t == null) {
			return "";
		}

		StringWriter exception = new StringWriter();
		PrintWriter pw = new PrintWriter(exception);

		t.printStackTrace(pw);

		return exception.toString();
	}
}