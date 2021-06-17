<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>App Usage</title>
</head>
<body>
<div align="center">
            <h1>Application usage</h1>
            
            <table border="1">
            <tr>
                <th>Application</th>
                <th>Metric</th>
                <th>Usage</th>
            </tr>  
                <c:forEach var="app" items="${usagelist}" >
                <tr>
                    <td>${app.application}</td>
                    <td>${app.metric}</td>
                    <td>${app.usage}</td>
                </tr>
                </c:forEach>             
            </table>
        </div>
</body>
</html>