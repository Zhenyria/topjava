<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }
        .empty {
            display: none;
        }
        .filled {
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals</h2>
    <a href="meals?action=create">Add Meal</a>
    <br><br>
    <form method="get" action="meals">
        <table bgcolor="#5f9ea0" border="0" cellpadding="8" cellspacing="0">
            <input type="hidden" value="filter" name="action">
            <tr>
                <td>Date</td>
                <td>from (inclusive): <input type="date" name="startDate"></td>
                <td>to (inclusive): <input type="date" name="endDate"></td>
            </tr>
            <tr>
                <td>Time</td>
                <td>from (inclusive): <input type="time" name="startTime"></td>
                <td>to (inclusive): <input type="time" name="endTime"></td>
            </tr>
            <tr>
                <td colspan="3">
                    <button type="submit">Get results</button>
                </td>
            </tr>
        </table>
    </form>
    <c:set var="isEmpty" value="${meals.isEmpty()}"/>
    <table border="1" cellpadding="8" cellspacing="0" class="${isEmpty ? 'empty' : 'filled'}">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>
    <p class="${isEmpty ? 'filled' : 'empty'}">No meals entries found</p>
</section>
</body>
</html>