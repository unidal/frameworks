<%@ tag isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="head" fragment="true" required="false"%>
<%@ attribute name="navbar" required="false" rtexprvalue="true"%>

<!DOCTYPE html>
<html lang="en">

<head>
	<title>Configuration</title>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="Configuration">
	<link href="${model.webapp}/css/bootstrap.css" type="text/css" rel="stylesheet">
	<link href="${model.webapp}/css/bootstrap-responsive.css" type="text/css" rel="stylesheet">
	<style media="screen" type="text/css">
		.nav > li.selected > a{
			color: white
		}
		
		.mycell {
			display: inline-block;
			vertical-align: top;
			padding: 10px
		}
	</style>
	<script src="${model.webapp}/js/jquery-1.8.3.min.js" type="text/javascript"></script>
	<script type="text/javascript">var contextpath = "${model.webapp}";</script>
	<jsp:invoke fragment="head"/>
</head>

<body data-spy="scroll" data-target=".subnav" data-offset="50">
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li class="${payload.name eq 'edit' ? 'selected' : ''}"><a href="${model.webapp}/">Home</a></li>
 					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid" style="min-height:524px;">
		<div class="row-fluid">
			<div class="span12">
				<br><br>
				<jsp:doBody />
			</div>
		</div>
	</div>
	<!--/.fluid-container-->

	<script src="${model.webapp}/js/bootstrap.js" type="text/javascript"></script>
</body>
</html>
