package de.thg.photoalbum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberConverterTest {

	Pattern intPattern = null;
	Pattern floatPattern = null;

	@BeforeEach
	public void setUp() throws Exception {
		intPattern = Pattern.compile("[+-]?[1-9]{1}\\d*");
		floatPattern = Pattern.compile("[+-]?([0-9]*[.])+[0-9]+");
	}

	@Test
	public void test() {
		assertEquals(NumberFormat.INT, checkForNumber("12345"));
		assertEquals(NumberFormat.FLOAT, checkForNumber("12.345"));
		assertEquals(NumberFormat.NONE, checkForNumber("01234"));
	}

	private NumberFormat checkForNumber(String numericString) {
		if (intPattern.matcher(numericString).matches()) {
			return NumberFormat.INT;
		} else if(floatPattern.matcher(numericString).matches()) {
			return NumberFormat.FLOAT;
		}
		return NumberFormat.NONE;
	}

	private enum NumberFormat {
		INT, FLOAT, NONE;
	}

}
