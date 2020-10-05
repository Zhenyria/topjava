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
<a href="meals?action=insert">Add meal</a>
<table border="1" cellpadding="10px" cellspacing="0px">
    <tr style="font-weight: bold">
        <td>Date</td>
        <td>Description</td>
        <td>Calories</td>
        <td colspan="2" align="center">Options</td>
    </tr>
<c:forEach var="meal" items="${meals}">
    <tr style="color: ${meal.excess == true ? "red" : "green"};">
        <td><c:out value="${dateFormat.format(meal.dateTime)}"/></td>
        <td><c:out value="${meal.description}"/></td>
        <td><c:out value="${meal.calories}"/></td>
        <td><a href="meals?action=edit&id=<c:out value="${meal.id}"/>">Update</a></td>
        <td><a href="meals?action=delete&id=<c:out value="${meal.id}"/>">Delete</a></td>
    </tr>
</c:forEach>
</table>
</body>
</html>
