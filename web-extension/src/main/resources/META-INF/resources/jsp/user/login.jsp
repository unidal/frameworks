<%@ page contentType="text/html; charset=utf-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core" %>
<jsp:useBean id="ctx" type="org.unidal.web.admin.user.login.Context" scope="request"/>
<jsp:useBean id="payload" type="org.unidal.web.admin.user.login.Payload" scope="request"/>
<jsp:useBean id="model" type="org.unidal.web.admin.user.login.Model" scope="request"/>

<a:layout>
	<jsp:attribute name="navbar">false</jsp:attribute>
	<jsp:body>
	    <div class="row">
	      <div class="span4"></div>
	      <div class="span4">
				<form class="form-horizontal" method="post" action="${model.pageUri}">
				  <input type="hidden" name="rtnUrl" value="${payload.returnUrl}"/>
				  <div class="control-group">
				    <h2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please Login</h2>
				    <c:if test="${payload.submit}">
					    <div class="alert alert-error">
						   <button type="button" class="close" data-dismiss="alert">&times;</button>
						   <strong>Login failed!</strong> please correct your user name and password!
						 </div>
					 </c:if>
				  </div>
				  <div class="control-group">
				    <label class="control-label" for="inputUsername">User Name:</label>
				    <div class="controls">
				      <input type="text" id="inputUsername" name="username" value="${payload.username}" placeholder="User Name">
				    </div>
				  </div>
				  <div class="control-group">
				    <label class="control-label" for="inputPassword">Password:</label>
				    <div class="controls">
				      <input type="password" name="password" id="inputPassword">
				    </div>
				  </div>
				  <div class="control-group">
				    <div class="controls">
				      <button type="submit" name="submit" value="submit" class="btn btn-primary">Sign in</button>
				    </div>
				  </div>
				</form>
	      </div>
	      <div class="span4"></div>
	    </div>
	
	</jsp:body>
</a:layout>