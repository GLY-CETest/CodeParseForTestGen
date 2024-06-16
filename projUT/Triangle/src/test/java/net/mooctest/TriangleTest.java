package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;

public class TriangleTest {

	@Test
	public void test() {
		Triangle T = new Triangle(3, 8, 3);
		assertEquals(false, T.isTriangle(T));
	}
	



}
