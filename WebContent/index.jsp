<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="api.end.point.PriceTickerAPIDynamicApplet"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<tr>
		<th>Change in:</th>
		<th>Initial Price:</th>
		<th>Current Price:</th>
		<th>Change Percentage</th>
	</tr>
	<%
	PriceTickerAPIDynamicApplet.Main();%>
	<%=
	PriceTickerAPIDynamicApplet.Result()%>
	
</body>
</html>