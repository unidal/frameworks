<%@ page contentType="text/html; charset=utf-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="a" uri="/WEB-INF/config.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core" %>
<jsp:useBean id="ctx" type="org.unidal.web.admin.config.home.Context" scope="request"/>
<jsp:useBean id="payload" type="org.unidal.web.admin.config.home.Payload" scope="request"/>
<jsp:useBean id="model" type="org.unidal.web.admin.config.home.Model" scope="request"/>

<a:config>
	<jsp:attribute name="head">
		<script src="${model.webapp}/js/admin/config.js"></script>
	</jsp:attribute>
	<jsp:body>
	
		<br>
		<ul class="nav nav-pills">
		  <c:forEach var="category" items="${model.categories}">
			  <li class="nav-item ${model.category eq category ? 'active' : ''}">
			    <a class="nav-link" href="${model.pageUri}/${category}">${category}</a>
			  </li>
		  </c:forEach>
		</ul>
	
		<table class='table table-condensed' style="width:100%;">
			<tr>
				<th width="30">#</th>
				<th>Name</th>
				<th>Description</th>
				<th>Action</th>
			</tr>
			<c:forEach var="config" items="${model.configBeans}" varStatus="status">
				<tr>
					<td>${status.index + 1}</td>
					<td><strong>${config.name}</strong></td>
					<td>${config.description}</td>
					<td><a href="#" class="edit" data-status="${status.index+1}">Edit</a></td>
				</tr>
				<tr style="display:${config.name eq payload.name?'':'none'}">
					<td></td>
					<td colspan="3">
						<div id="config-${status.index+1}">
							<form method="post" action="${model.pageUri}/${model.category}/${config.name}">
								<input type="hidden" name="op" value="edit"/>
								Description: <input type="text" name="description" value="${config.description}" style="width:100%">
								<textarea name="content" rows="5" style="width:100%">${config.details}</textarea>
								<br><button type="submit" name="update" value="Update" class="btn btn-primary">Update</button>
							</form>
						</div>
					</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="3"></td>
				<td><a href="#" class="edit" data-status="0">Add</a></td>
			</tr>
				<tr style="display:${not empty payload.name?'':'none'}">
					<td></td>
					<td colspan="3">
						<div id="config-0">
							<form method="post" action="${model.pageUri}/${model.category}">
								<input type="hidden" name="op" value="add"/>
								Name: <input type="text" name="name" value="${payload.name}" style="width:100%">
								Description: <input type="text" name="description" value="${payload.description}" style="width:100%">
								<textarea name="content" rows="5" style="width:100%">${payload.content}</textarea>
								<br><button type="submit" name="update" value="Update" class="btn btn-primary">Create</button>
							</form>
						</div>
					</td>
				</tr>
		</table>

	</jsp:body>
</a:config>