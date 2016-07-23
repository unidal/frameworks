//package org.unidal.data;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Assert;
//
//import org.junit.Test;
//
//import org.unidal.data.bean.BeanMd;
//import org.unidal.data.bean.MapMd;
//
//public class BeanMdTest {
//	@Test
//	public void testBean() {
//		User user = new User();
//		BeanMd<User> md = new BeanMd<User>(user);
//
//		Assert.assertEquals(false, md.getMeta().isNullable("id"));
//		Assert.assertEquals(true, md.getMeta().isNullable("name"));
//		Assert.assertEquals(true, md.getMeta().isNullable("married"));
//		Assert.assertEquals("[id, married, name, addresses]", md.getMeta().getKeys().toString());
//
//		Assert.assertSame(user, md.bean());
//		Assert.assertEquals(user.getId(), md.getData("id"));
//		Assert.assertEquals(user.getName(), md.getData("name"));
//		Assert.assertEquals(user.getMarried(), md.getData("married"));
//		Assert.assertEquals(true, md.hasData("id"));
//		Assert.assertEquals(false, md.hasData("name"));
//		Assert.assertEquals(false, md.hasData("married"));
//
//		user.setId(1);
//		Assert.assertEquals(1, user.getId());
//		Assert.assertEquals(1, md.getData("id"));
//
//		md.setData("id", 2);
//		Assert.assertEquals(2, user.getId());
//		Assert.assertEquals(2, md.getData("id"));
//
//		md.unsetData("id");
//		Assert.assertEquals(0, user.getId());
//		Assert.assertEquals(0, md.getData("id"));
//
//		user.setName("Tom");
//		Assert.assertEquals(true, md.hasData("name"));
//		Assert.assertEquals("Tom", md.getData("name"));
//
//		Assert.assertEquals("Tom", md.setData("name", "Jerry"));
//		Assert.assertEquals("Jerry", md.getData("name"));
//
//		md.unsetData("name");
//		Assert.assertEquals(null, md.getData("name"));
//		Assert.assertEquals(false, md.hasData("name"));
//
//		try {
//			md.getData("unknown");
//			Assert.fail("IllegalArgumentException expected");
//		} catch (IllegalArgumentException e) {
//			// expected
//		}
//
//		try {
//			md.setData("unknown", null);
//			Assert.fail("IllegalArgumentException expected");
//		} catch (IllegalArgumentException e) {
//			// expected
//		}
//	}
//
//	@Test
//	public void testUser() {
//		User user = new User();
//
//		user.setId(1234);
//		user.setName("Tom");
//		user.addAddress(new Address("billing", "address 1", 12345).setDefault(true));
//		user.addAddress(new Address("shipping", "address 2", 23456));
//
//		BeanMd<User> md = new BeanMd<User>(user);
//
//		Assert.assertEquals("Tom", md.getData("name"));
//		Assert.assertEquals("Tom", md.getData("addresses"));
//	}
//
//	@Test
//	public void testMap() {
//		Map<String, Object> user = new HashMap<String, Object>();
//		MapMd<String, Object> md = new MapMd<String, Object>(user);
//
//		Assert.assertEquals(true, md.getMeta().isNullable("id"));
//		Assert.assertEquals(true, md.getMeta().isNullable("name"));
//		Assert.assertEquals(true, md.getMeta().isNullable("married"));
//		Assert.assertEquals("[]", md.getMeta().getKeys().toString());
//
//		Assert.assertSame(user, md.map());
//		Assert.assertEquals(user.get("id"), md.get("id"));
//		Assert.assertEquals(user.get("name"), md.get("name"));
//		Assert.assertEquals(user.get("married"), md.get("married"));
//		Assert.assertEquals(false, md.hasData("id"));
//		Assert.assertEquals(false, md.hasData("name"));
//		Assert.assertEquals(false, md.hasData("married"));
//
//		user.put("id", 1);
//		Assert.assertEquals(1, user.get("id"));
//		Assert.assertEquals(1, md.get("id"));
//		Assert.assertEquals("[id]", md.getMeta().getKeys().toString());
//
//		md.setData("id", 2);
//		Assert.assertEquals(2, user.get("id"));
//		Assert.assertEquals(2, md.get("id"));
//
//		md.unsetData("id");
//		Assert.assertEquals(null, user.get("id"));
//		Assert.assertEquals(null, md.get("id"));
//
//		user.put("name", "Tom");
//		Assert.assertEquals(true, md.hasData("name"));
//		Assert.assertEquals("Tom", md.get("name"));
//
//		Assert.assertEquals("Tom", md.setData("name", "Jerry"));
//		Assert.assertEquals("Jerry", md.get("name"));
//
//		md.unsetData("name");
//		Assert.assertEquals(null, md.get("name"));
//		Assert.assertEquals(false, md.hasData("name"));
//
//		Assert.assertEquals(null, md.get("unknown"));
//		Assert.assertEquals(null, md.put("unknown", null));
//	}
//
//	static class Address {
//		private String m_id;
//
//		private String m_addressLine;
//
//		private Integer m_zipcode;
//
//		private boolean m_default;
//
//		public Address(String id, String addressLine, Integer zipCode) {
//			m_id = id;
//			m_addressLine = addressLine;
//			m_zipcode = zipCode;
//		}
//
//		public String getAddressLine() {
//			return m_addressLine;
//		}
//
//		public String getId() {
//			return m_id;
//		}
//
//		public Integer getZipcode() {
//			return m_zipcode;
//		}
//
//		public boolean isDefault() {
//			return m_default;
//		}
//
//		public Address setDefault(boolean isDefault) {
//			m_default = isDefault;
//			return this;
//		}
//	}
//
//	static class User {
//		private int m_id;
//
//		private String m_name;
//
//		private Boolean m_married;
//
//		private Map<String, Address> m_addresses = new HashMap<String, BeanMdTest.Address>();
//
//		public Map<String, Address> getAddresses() {
//			return m_addresses;
//		}
//
//		public int getId() {
//			return m_id;
//		}
//
//		public Boolean getMarried() {
//			return m_married;
//		}
//
//		public String getName() {
//			return m_name;
//		}
//
//		public User addAddress(Address address) {
//			m_addresses.put(address.getId(), address);
//			return this;
//		}
//
//		public void setId(int id) {
//			m_id = id;
//		}
//
//		public void setMarried(Boolean married) {
//			m_married = married;
//		}
//
//		public void setName(String name) {
//			m_name = name;
//		}
//	}
//}
