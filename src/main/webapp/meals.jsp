<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<jsp:useBean id="meals" scope="request" type="java.util.List"/>
<jsp:useBean id="dateFormat" scope="request" type="java.time.format.DateTimeFormatter"/>
<table border="1" cellspacing="0px" cellpadding="10px">
    <tr style="font-weight: bold">
        <td>Date</td>
        <td>Description</td>
        <td>Calories</td>
    </tr>
<c:forEach var="meal" items="${meals}">
    <tr style="color: ${meal.excess == true ? "red" : "green"};">
        <td><c:out value="${dateFormat.format(meal.dateTime)}"/></td>
        <td><c:out value="${meal.description}"/></td>
        <td><c:out value="${meal.calories}"/></td>
    </tr>
</c:forEach>
</table>
</body>
</html>
