<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Log List</title>
    </head>
    <body>
   <div align="center">
            <h1>Logs</h1>
            
            <table border="1">
            <tr>
                <th>Datetime</th>
                <th>Application</th>
                <th>Method</th>
                <th>Description</th>
            </tr>  
                <c:forEach var="Log" items="${listLogs}" >
                <tr>
                     
                    <td>${Log.datetime}</td>
                    <td>${Log.application}</td>
                    <td>${Log.method}</td>
                    <td>${Log.description}</td>
                             
                </tr>
                </c:forEach>             
            </table>
        </div>
        </body>
</html>
