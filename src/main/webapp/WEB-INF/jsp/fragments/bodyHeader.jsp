<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-md navbar-dark bg-dark py-0">
    <div class="container">
        <a href="meals" class="navbar-brand"><img src="resources/images/icon-meal.png"> <spring:message
                code="app.title"/></a>
        <ul class="navbar-nav ml-auto">
            <li class="nav-item">
                <sec:authorize access="isAuthenticated()">
                    <form:form class="form-inline my-2" action="logout" method="post">
                        <sec:authorize access="hasRole('ADMIN')">
                            <a class="btn btn-info mr-1" href="users"><spring:message code="user.title"/></a>
                        </sec:authorize>
                        <a class="btn btn-info mr-1" href="profile">${userTo.name} <spring:message
                                code="app.profile"/></a>
                        <button class="btn btn-primary my-1" type="submit">
                            <span class="fa fa-sign-out"></span>
                        </button>
                    </form:form>
                </sec:authorize>
                <sec:authorize access="isAnonymous()">
                    <form:form class="form-inline my-2" id="login_form" action="spring_security_check" method="post">
                        <input class="form-control mr-1" type="text" placeholder="Email" name="username">
                        <input class="form-control mr-1" type="password" placeholder="Password" name="password">
                        <button class="btn btn-success" type="submit">
                            <span class="fa fa-sign-in"></span>
                        </button>
                    </form:form>
                </sec:authorize>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle my-1 ml-2" data-toggle="dropdown" aria-haspopup="true"
                   aria-expanded="false" id="locale"></a>
                <div class="dropdown-menu">
                    <a class="dropdown-item" id="enLocale"
                       href="${requestScope['javax.servlet.forward.request_uri']}?lang=en">English</a>
                    <a class="dropdown-item" id="ruLocale"
                       href="${requestScope['javax.servlet.forward.request_uri']}?lang=ru">Русский</a>
                </div>
            </li>
        </ul>
    </div>
</nav>
<script>
    var locale = "${pageContext.response.locale}";
    $("#locale").text(locale);
</script>
