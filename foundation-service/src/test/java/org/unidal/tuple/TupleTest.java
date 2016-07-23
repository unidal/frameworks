package org.unidal.tuple;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class TupleTest {
	@Test
	public void testAsList() {
		List<Object> list = new ArrayList<Object>();

		list.add(new Ref<String>("ref"));
		list.add(new Pair<String, String>("key", "value"));
		list.add(new Triple<String, String, String>("first", "middle", "last"));
		list.add(new Quad<String, String, String, String>("east", "south", "west", "north"));

		Assert.assertEquals(
		      "[Ref[value=ref], Pair[key=key, value=value], Triple[first=first, middle=middle, last=last], Quad[east=east, south=south, west=west, north=north]]",
		      list.toString());
	}

	@Test
	public void testAsSet() {
		Set<Object> set = new LinkedHashSet<Object>();

		set.add(new Ref<String>("ref"));
		set.add(new Pair<String, String>("key", "value"));
		set.add(new Triple<String, String, String>("first", "middle", "last"));
		set.add(new Quad<String, String, String, String>("east", "south", "west", "north"));

		Assert.assertEquals(
		      "[Ref[value=ref], Pair[key=key, value=value], Triple[first=first, middle=middle, last=last], Quad[east=east, south=south, west=west, north=north]]",
		      set.toString());
	}

	@Test
	public void testPair() {
		Pair<String, String> o1 = new Pair<String, String>();

		o1.setKey("key");
		o1.setValue("value");

		Pair<String, String> o2 = new Pair<String, String>("key", "value");

		Assert.assertEquals(o1, o2);
		Assert.assertEquals(o1.hashCode(), o2.hashCode());
		Assert.assertEquals(o1.toString(), o2.toString());
	}

	@Test
	public void testQuad() {
		Quad<String, String, String, String> o1 = new Quad<String, String, String, String>();

		o1.setEast("east");
		o1.setSouth("south");
		o1.setWest("west");
		o1.setNorth("north");

		Quad<String, String, String, String> o2 = new Quad<String, String, String, String>("east", "south", "west",
		      "north");

		Assert.assertEquals(o1, o2);
		Assert.assertEquals(o1.hashCode(), o2.hashCode());
		Assert.assertEquals(o1.toString(), o2.toString());
	}

	@Test
	public void testRef() {
		Ref<String> o1 = new Ref<String>();

		o1.setValue("ref");

		Ref<String> o2 = new Ref<String>("ref");

		Assert.assertEquals(o1, o2);
		Assert.assertEquals(o1.hashCode(), o2.hashCode());
		Assert.assertEquals(o1.toString(), o2.toString());
	}

	@Test
	public void testTriple() {
		Triple<String, String, String> o1 = new Triple<String, String, String>();

		o1.setFirst("first");
		o1.setMiddle("middle");
		o1.setLast("last");

		Triple<String, String, String> o2 = new Triple<String, String, String>("first", "middle", "last");

		Assert.assertEquals(o1, o2);
		Assert.assertEquals(o1.hashCode(), o2.hashCode());
		Assert.assertEquals(o1.toString(), o2.toString());
	}
}
