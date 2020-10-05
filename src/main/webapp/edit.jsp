<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Edit</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit ID <c:out value="${id}"/></h2>
<form method="POST" action='meals?action=edit' name="editMeal">
    <table>
        <input type="hidden" readonly="readonly" name="id" value="<c:out value="${id}"/>">
        <tr>
            <td>DateTime:</td>
            <td><input type="datetime-local" name="dateTime" value="<c:out value="${meal.dateTime}"/>"/></td>
        </tr>
        <tr>
            <td>Description:</td>
            <td><input type="text" name="description" value="<c:out value="${meal.description}"/>"/></td>
        </tr>
        <tr>
            <td>Calories:</td>
            <td><input type="text" name="calories" value="<c:out value="${meal.calories}"/>"/></td>
        </tr>
    </table>
    <br/>
    <button type="submit">Edit</button>
    <button type="reset">Reset</button>
    <button onclick="document.location='meals'" type="button">Cancel</button>
</form>
</body>
</html>
