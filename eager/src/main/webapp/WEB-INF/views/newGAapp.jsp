<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Application logs</title>
<style type="text/css">
    label {
        display: inline-block;
        width: 200px;
        margin: 5px;
        text-align: left;
    }     
    button {
        padding: 10px;
        margin: 10px;
    }
</style>
</head>
<body>
<div align="center">
        <h2>URL for Google Analytics Report</h2>
        <form:form action="addgaapp" method="post" modelAttribute="gaApp">
            <form:label path="application">Application:</form:label>
            <form:input path="application"/><br/>
             
            <form:label path="url">URL:</form:label>
            <form:input path="url"/><br/>
                             
            <form:button>Add</form:button>
        </form:form>
    </div>



</body>
</html>