package org.unidal.dal.jdbc.query.token;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class TokenParserTest extends ComponentTestCase {
	@Test
	public void testParseEscaping() throws Exception {
		TokenParser parser = lookup(TokenParser.class);
		Assert.assertNotNull(parser);

		String pattern = "SELECT <FIELDS/> FROM <TABLE/> WHERE <IF type='NOT_ZERO' param='state'> AND state \\>= ${state} </IF> <IF type='NOT_ZERO' param='airline-id'> AND airline_id = ${airline-id} </IF> <IF type='NOT_NULL' param='carrier-code'> AND carrier_code like concat('%', ${carrier-code}, '%') </IF> <IF type='NOT_NULL' param='class-type'> AND class_type like concat('%', ${class-type}, '%') </IF> <IF type='NOT_NULL' param='class-code'> AND class_code like concat('%', ${class-code}, '%') </IF> <IF type='NOT_ZERO' param='airfare-status'> AND airfare_status = ${airfare-status} </IF> <IF type='NOT_NULL' param='last-modified-by'> AND last_modified_by like concat('%', ${last-modified-by}, '%') </IF> <IF type='NOT_NULL' param='comments'> AND comments like concat('%', ${comments}, '%') </IF> ORDER BY airfare_id ASC LIMIT ${start}, ${count}";
		List<Token> tokens = parser.parse(pattern);
		StringBuilder sb = new StringBuilder(pattern.length());

		for (Token token : tokens) {
			sb.append(token);
		}

		Assert.assertEquals(pattern.replaceAll(Pattern.quote("\\>"), ">"), sb.toString());
	}

	@Test
	public void testParseJoins() throws Exception {
		TokenParser parser = lookup(TokenParser.class);
		Assert.assertNotNull(parser);

		String pattern = "SELECT <FIELDS/> FROM <TABLES/> WHERE <JOINS/> AND <IF type='NOT_ZERO' param='state'> AND state = ${state} </IF> <IF type='NOT_ZERO' param='airline-id'> AND airline_id = ${airline-id} </IF> <IF type='NOT_NULL' param='carrier-code'> AND carrier_code like concat('%', ${carrier-code}, '%') </IF> <IF type='NOT_NULL' param='class-type'> AND class_type like concat('%', ${class-type}, '%') </IF> <IF type='NOT_NULL' param='class-code'> AND class_code like concat('%', ${class-code}, '%') </IF> <IF type='NOT_ZERO' param='airfare-status'> AND airfare_status = ${airfare-status} </IF> <IF type='NOT_NULL' param='last-modified-by'> AND last_modified_by like concat('%', ${last-modified-by}, '%') </IF> <IF type='NOT_NULL' param='comments'> AND comments like concat('%', ${comments}, '%') </IF> ORDER BY airfare_id ASC LIMIT ${start}, ${count}";
		List<Token> tokens = parser.parse(pattern);
		StringBuilder sb = new StringBuilder(pattern.length());

		for (Token token : tokens) {
			sb.append(token);
		}

		Assert.assertEquals(pattern, sb.toString());
	}

	@Test
	public void testParseSimple() throws Exception {
		TokenParser parser = lookup(TokenParser.class);
		Assert.assertNotNull(parser);

		String pattern = "SELECT <FIELDS/> FROM <TABLE/> WHERE 1 = 1 <IF type='NOT_ZERO' param='state'> AND <FIELD name='state'/> = ${state} </IF> <IF type='NOT_ZERO' param='airline-id'> AND <FIELD name='airline-id'/> = ${airline-id} </IF> <IF type='NOT_NULL' param='carrier-code'> AND carrier_code like concat('%', ${carrier-code}, '%') </IF> <IF type='NOT_NULL' param='class-type'> AND class_type like concat('%', ${class-type}, '%') </IF> <IF type='NOT_NULL' param='class-code'> AND class_code like concat('%', ${class-code}, '%') </IF> <IF type='NOT_ZERO' param='airfare-status'> AND airfare_status = ${airfare-status} </IF> <IF type='NOT_NULL' param='last-modified-by'> AND last_modified_by like concat('%', ${last-modified-by}, '%') </IF> <IF type='NOT_NULL' param='comments'> AND comments like concat('%', ${comments}, '%') </IF> ORDER BY airfare_id ASC LIMIT ${start}, ${count}";
		List<Token> tokens = parser.parse(pattern);
		StringBuilder sb = new StringBuilder(pattern.length());

		for (Token token : tokens) {
			sb.append(token);
		}

		Assert.assertEquals(pattern, sb.toString());
	}
	
	@Test
	public void testParseBug1() {
	     TokenParser parser = lookup(TokenParser.class);
	      Assert.assertNotNull(parser);

	      String pattern = "SELECT <FIELDS/> FROM <TABLE/> WHERE <FIELD name='id' /> IN (SELECT MAX(<FIELD name='id' />) FROM <TABLE/> GROUP BY <FIELD name='name'/>)";
	      List<Token> tokens = parser.parse(pattern);
	      StringBuilder sb = new StringBuilder(pattern.length());

	      for (Token token : tokens) {
	         sb.append(token);
	      }

	      String expected = "SELECT <FIELDS/> FROM <TABLE/> WHERE <FIELD name='id'/> IN (SELECT MAX(<FIELD name='id'/>) FROM <TABLE/> GROUP BY <FIELD name='name'/>)";
	      Assert.assertEquals(expected, sb.toString());
	}
	
	@Test
	public void testParseQuote() throws Exception {
	   TokenParser parser = lookup(TokenParser.class);
	   Assert.assertNotNull(parser);
	   
	   String pattern = "SELECT <FIELDS/> FROM <TABLE/> WHERE 1 = 1 <IF type='NOT_ZERO' param='state'> AND <FIELD name=\"state\"/> = ${state} </IF> <IF type='NOT_ZERO' param='airline-id'> AND <FIELD name='airline-id'/> = ${airline-id} </IF> <IF type='NOT_NULL' param='carrier-code'> AND carrier_code like concat('%', ${carrier-code}, '%') </IF> <IF type='NOT_NULL' param='class-type'> AND class_type like concat('%', ${class-type}, '%') </IF> <IF type='NOT_NULL' param='class-code'> AND class_code like concat('%', ${class-code}, '%') </IF> <IF type='NOT_ZERO' param='airfare-status'> AND airfare_status = ${airfare-status} </IF> <IF type='NOT_NULL' param='last-modified-by'> AND last_modified_by like concat('%', ${last-modified-by}, '%') </IF> <IF type='NOT_NULL' param='comments'> AND comments like concat('%', ${comments}, '%') </IF> ORDER BY airfare_id ASC LIMIT ${start}, ${count}";
	   List<Token> tokens = parser.parse(pattern);
	   StringBuilder sb = new StringBuilder(pattern.length());
	   
	   for (Token token : tokens) {
	      sb.append(token);
	   }
	   
	   Assert.assertEquals(pattern.replace('"', '\''), sb.toString());
	}

	@Test
	public void testParseLessAndGreatInIf() throws Exception {
		TokenParser parser = lookup(TokenParser.class);
		Assert.assertNotNull(parser);

		String pattern = "SELECT <FIELDS/> FROM <TABLE/> WHERE 1 = 1 <IF type='NOT_ZERO' param='state'> AND <FIELD name='state'/> = ${state} </IF> <IF type='NOT_ZERO' param='airline-id'> AND <FIELD name='airline-id'/> > ${airline-id} </IF><IF type='NOT_ZERO' param='airline-id'> AND <FIELD name='airline-id'/> < ${airline-id} </IF> <IF type='NOT_NULL' param='carrier-code'> AND carrier_code like concat('%', ${carrier-code}, '%') </IF> <IF type='NOT_NULL' param='class-type'> AND class_type like concat('%', ${class-type}, '%') </IF> <IF type='NOT_NULL' param='class-code'> AND class_code like concat('%', ${class-code}, '%') </IF> <IF type='NOT_ZERO' param='airfare-status'> AND airfare_status = ${airfare-status} </IF> <IF type='NOT_NULL' param='last-modified-by'> AND last_modified_by like concat('%', ${last-modified-by}, '%') </IF> <IF type='NOT_NULL' param='comments'> AND comments like concat('%', ${comments}, '%') </IF> ORDER BY airfare_id ASC LIMIT ${start}, ${count}";
		List<Token> tokens = parser.parse(pattern);
		StringBuilder sb = new StringBuilder(pattern.length());

		for (Token token : tokens) {
			sb.append(token);
		}

		Assert.assertEquals(pattern, sb.toString());
	}
}
