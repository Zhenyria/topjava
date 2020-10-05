<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Insert</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Insert meal</h2>
<form method="POST" action='meals?action=insert' name="insertMeal">
    <table>
        <tr>
            <td>DateTime:</td>
            <td><input type="datetime-local" required="required" name="dateTime" value="<c:out value="${meal.dateTime}"/>"/></td>
        </tr>
        <tr>
            <td>Description:</td>
            <td><input type="text" required="required" name="description" value="<c:out value="${meal.description}"/>"/></td>
        </tr>
        <tr>
            <td>Calories:</td>
            <td><input type="text" required="required" name="calories" value="<c:out value="${meal.calories}"/>"/></td>
        </tr>
    </table>
    <br/>
    <button type="submit">Insert</button>
    <button type="reset">Reset</button>
    <button onclick="document.location='meals'" type="button">Cancel</button>
</form>
</body>
</html>
