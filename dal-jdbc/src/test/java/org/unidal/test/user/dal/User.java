package org.unidal.test.user.dal;

import static org.unidal.test.user.dal.UserEntity.*;
import static org.unidal.test.user.dal.UserEntity.CREATION_DATE;
import static org.unidal.test.user.dal.UserEntity.HOME_ADDRESS;
import static org.unidal.test.user.dal.UserEntity.KEY_USER_ID;
import static org.unidal.test.user.dal.UserEntity.LAST_MODIFIED_DATE;
import static org.unidal.test.user.dal.UserEntity.OFFICE_ADDRESS;
import static org.unidal.test.user.dal.UserEntity.UPPER_USER_NAME;
import static org.unidal.test.user.dal.UserEntity.USER_ID;
import static org.unidal.test.user.dal.UserEntity.USER_ID_ARRAY;
import static org.unidal.test.user.dal.UserEntity.USER_ID_LIST;
import static org.unidal.test.user.dal.UserEntity.USER_NAME;

import java.util.Date;
import java.util.List;

import org.unidal.dal.jdbc.DataObject;
import org.unidal.test.user.address.dal.UserAddress;

public class User extends DataObject {
   private long m_userId;

   private String m_userName;

   private Date m_creationDate;

   private Date m_lastModifiedDate;

   private String m_upperUserName;

   private UserAddress m_homeAddress;

   private UserAddress m_officeAddress;

   private UserAddress m_billingAddress;

   private long m_keyUserId;

   private long[] m_userIdArray;

   private List<Long> m_userIdList;

   private int m_pageSize;

   private String m_encryptedPassword;

   private String m_password;

   public User() {
      super();
   }

   public UserAddress getBillingAddress() {
      return m_billingAddress;
   }

   public Date getCreationDate() {
      return m_creationDate;
   }

   public String getEncryptedPassword() {
      return m_encryptedPassword;
   }

   public UserAddress getHomeAddress() {
      return m_homeAddress;
   }

   public long getKeyUserId() {
      return m_keyUserId;
   }

   public Date getLastModifiedDate() {
      return m_lastModifiedDate;
   }

   public UserAddress getOfficeAddress() {
      return m_officeAddress;
   }

   public int getPageSize() {
      return m_pageSize;
   }

   public String getPassword() {
      return m_password;
   }

   public String getUpperUserName() {
      return m_upperUserName;
   }

   public long getUserId() {
      return m_userId;
   }

   public long[] getUserIdArray() {
      return m_userIdArray;
   }

   public List<Long> getUserIdList() {
      return m_userIdList;
   }

   public String getUserName() {
      return m_userName;
   }

   public void setBillingAddress(UserAddress billingAddress) {
      setFieldUsed(BILLING_ADDRESS, true);
      m_billingAddress = billingAddress;
   }

   public void setCreationDate(Date creationDate) {
      setFieldUsed(CREATION_DATE, true);
      m_creationDate = creationDate;
   }

   public void setEncryptedPassword(String encryptedPassword) {
      setFieldUsed(ENCRYPTED_PASSWORD, true);
      m_encryptedPassword = encryptedPassword;
   }

   public void setHomeAddress(UserAddress homeAddress) {
      setFieldUsed(HOME_ADDRESS, true);
      m_homeAddress = homeAddress;
   }

   public void setKeyUserId(long keyUserId) {
      setFieldUsed(KEY_USER_ID, true);
      m_keyUserId = keyUserId;
   }

   public void setLastModifiedDate(Date lastModifiedDate) {
      setFieldUsed(LAST_MODIFIED_DATE, true);
      m_lastModifiedDate = lastModifiedDate;
   }

   public void setOfficeAddress(UserAddress officeAddress) {
      setFieldUsed(OFFICE_ADDRESS, true);
      m_officeAddress = officeAddress;
   }

   public void setPageSize(int pageSize) {
      m_pageSize = pageSize;
   }

   public void setPassword(String password) {
      setFieldUsed(PASSWORD, true);
      m_password = password;
   }

   public void setUpperUserName(String upperUserName) {
      setFieldUsed(UPPER_USER_NAME, true);
      m_upperUserName = upperUserName;
   }

   public void setUserId(long userId) {
      setFieldUsed(USER_ID, true);
      m_userId = userId;
   }

   public void setUserIdArray(long[] userIdArray) {
      setFieldUsed(USER_ID_ARRAY, true);
      m_userIdArray = userIdArray;
   }

   public void setUserIdList(List<Long> userIdList) {
      setFieldUsed(USER_ID_LIST, true);
      m_userIdList = userIdList;
   }

   public void setUserName(String userName) {
      setFieldUsed(USER_NAME, true);
      m_userName = userName;
   }
}
