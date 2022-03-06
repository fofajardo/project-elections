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
    <%-- Header --%>
    <c:if test="${!hideHeader}">
        <%-- Main Navigation --%>
        <header class="p-2 shadow-sm sticky-top bg-white">
            <div class="container">
                <div class="d-flex flex-wrap align-items-center justify-content-between">
                    <div class="navbar-light">
                        <button class="navbar-toggler p-0 border-0"
                                type="button"
                                data-bs-toggle="offcanvas"
                                data-bs-target="#mainSidebar"
                                aria-controls="mainSidebar">
                            <span class="navbar-toggler-icon"></span>
                        </button>
                    </div>
                    <a href="<c:url value='/'/>" class="d-flex align-items-center text-decoration-none">
                        <img src="<c:url value='/assets/branding/logo-32.svg'/>" width="160" height="32" alt="Halal"/>
                    </a>
                    <div class="spacer" style="width: 30px;">
                    </div>
                </div>
            </div>
        </header>
        <%-- Offcanvas Sidebar --%>
        <div class="offcanvas offcanvas-start bg-light" tabindex="-1" id="mainSidebar">
 <div class="d-flex flex-column flex-shrink-0 p-3 h-100">
    <div class="d-flex align-items-center justify-content-between me-md-auto w-100">
        <strong style="overflow: auto; max-width: 85%;">
            <i class="bi bi-person-circle me-2"></i>
            <c:choose>
                <c:when test="${account != null}">
                        ${account.getFirstName()}
                    <c:if test="${account.getMiddleName() != null}">
                        ${account.getMiddleName()}
                    </c:if>
                        ${account.getLastName()}
                    <c:if test="${account.getSuffix() != null}">
                        ${account.getSuffix()}
                    </c:if>
                </c:when>
                <c:otherwise>
                Not Signed In
                </c:otherwise>
            </c:choose>
        </strong>
        <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    <hr>
    <ul class="nav nav-pills flex-column mb-auto">
        <li class="nav-item">
            <a href="<c:url value='/'/>" class="nav-link link-dark ${navActiveHome}">
                <i class="bi bi-house-fill me-2"></i>
                Home
            </a>
        </li>
        <li>
            <a href="<c:url value='/candidates'/>" class="nav-link link-dark ${navActiveCandidates}">
                <i class="bi bi-people-fill me-2"></i>
                Candidates
            </a>
        </li>
        <li>
            <a href="<c:url value='/election-results'/>" class="nav-link link-dark ${navActiveResults}">
                <i class="bi bi-clipboard-fill me-2"></i>
                Election Results
            </a>
        </li>
        <c:if test="${account != null}">
        <li>
            <a href="<c:url value='/ballot/status'/>" class="nav-link link-dark ${navActiveBallot}">
                <i class="bi bi-pen-fill me-2"></i>
                Vote Now
            </a>
        </li>
        <li>
            <a href="<c:url value='/ballot/receipt'/>" class="nav-link link-dark ${navActiveReceipt}">
                <i class="bi bi-receipt me-2"></i>
                View Ballot Receipt
            </a>
        </li>
        </c:if>
    </ul>
    <hr>
    <ul class="nav nav-pills flex-column">
    <c:choose>
        <c:when test="${account != null}">
        <li class="nav-item">
            <a href="<c:url value='/accounts/sign-out'/>" class="nav-link link-dark">
                <i class="bi bi-door-open me-2"></i>
                Sign Out
            </a>
        </li>
        </c:when>
        <c:otherwise>
        <li class="nav-item">
            <a href="<c:url value='/accounts/sign-in'/>" class="nav-link link-dark">
                <i class="bi bi-envelope me-2"></i>
                Sign In
            </a>
        </li>
        <li class="nav-item">
            <a href="<c:url value='/accounts/sign-in-qr'/>" class="nav-link link-dark">
                <i class="bi bi-qr-code-scan me-2"></i>
                Sign In with QR
            </a>
        </li>
        </c:otherwise>
    </c:choose>
    </ul>
</div>
        </div>
    </c:if>