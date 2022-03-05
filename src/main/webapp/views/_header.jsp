<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>
        <c:if test="${pageSubtitle != null}">
            <c:out value="${pageSubtitle} - "/> 
        </c:if>
        Halal
        </title>
        <%-- Bootstrap --%>
        <link href="<c:url value='/assets/bs/bootstrap.min.css'/>" rel="stylesheet">
        <script src="<c:url value='/assets/bs/bootstrap.bundle.min.js'/>"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css">
    </head>
    <body class="bg-light">
    <c:if test="${!hideHeader}">
        <header class="p-2 shadow-sm sticky-top bg-white">
            <div class="container">
                <div class="d-flex flex-wrap align-items-center justify-content-between">
                    <a href="<c:url value='/'/>" class="d-flex align-items-center text-decoration-none">
                        <img src="<c:url value='/assets/branding/logo-32.svg'/>" width="160" height="32"/>
                    </a>
    
                    <div class="text-end">
                        <button type="button" class="btn btn-outline me-2">Login</button>
                        <button type="button" class="btn btn-warning">Sign-up</button>
                    </div>
                </div>
            </div>
        </header>
    </c:if>