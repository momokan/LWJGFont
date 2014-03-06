/**
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2014 momokan (http://lwjgfont.chocolapod.net)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.chocolapod.lwjgfont.cli;

import static net.chocolapod.lwjgfont.cli.CliOption._h;
import static net.chocolapod.lwjgfont.cli.CliOption._l;
import static net.chocolapod.lwjgfont.cli.CliOption._p;
import static net.chocolapod.lwjgfont.cli.CliOption._v;
import static net.chocolapod.lwjgfont.cli.CliOption._x;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.chocolapod.lwjgfont.exception.cli.MissingOptionValueException;
import net.chocolapod.lwjgfont.exception.cli.UnknownArgumentException;

import org.junit.Test;

public class TestCliArgumentParser_options {

	@Test
	public void p() {
		assertHasSingleOption(_p, "dummy", "-p dummy");
	}
	
	@Test(expected = MissingOptionValueException.class)
	public void pHasValue() {
		assertHasSingleOption(_p, null, "-p");
	}
	
	@Test
	public void x() {
		assertHasSingleOption(_x, null, "-x");
	}

	@Test(expected = UnknownArgumentException.class)
	public void xHasNoValue() {
		assertHasSingleOption(_x, null, "-x dummy");
	}
	
	@Test
	public void v() {
		assertHasSingleOption(_v, null, "-v");
	}

	@Test(expected = UnknownArgumentException.class)
	public void vHasNoValue() {
		assertHasSingleOption(_v, null, "-v dummy");
	}
	
	@Test
	public void l() {
		assertHasSingleOption(_l, null, "-l");
	}

	@Test(expected = UnknownArgumentException.class)
	public void lHasNoValue() {
		assertHasSingleOption(_l, null, "-l dummy");
	}

	@Test
	public void h() {
		assertHasSingleOption(_h, null, "-h");
	}

	@Test(expected = UnknownArgumentException.class)
	public void hHasNoValue() {
		assertHasSingleOption(_h, null, "-h dummy");
	}

	private void assertHasSingleOption(CliOption expectedOption, String expectedValue, String optionString) {
		CliArgumentParser	parser = new CliArgumentParser(optionString.split(" +"));
		CliArgument[]			arguments = parser.listArguments();

		assertEquals(1, arguments.length);
		assertEquals(expectedOption, arguments[0]);
		assertTrue(parser.hasOption(expectedOption));
		assertEquals(expectedValue, parser.get(expectedOption));
	}

}
