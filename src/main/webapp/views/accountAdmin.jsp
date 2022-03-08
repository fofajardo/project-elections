<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="pageSubtitle" value="Administration"/>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div id="ballot-title" class="my-3">
        <div class="h1">${pageSubtitle}</div>
    </div>
    <!-- Primary Content -->
    <div class="row g-3">
        <div class="col-md-9">
            <div class="card shadow-sm p-3">
                <h6 class="border-bottom pb-2 mb-0">Accounts</h6>
                <c:forEach items="${accounts}" var="targetAccount" varStatus="accountStatus">
                <c:remove var="lastItemBorder"/>
                <c:remove var="itemPadding"/>
                <c:if test="${!accountStatus.last}">
                    <c:set var="lastItemBorder" value="border-bottom"/>
                    <c:set var="itemPadding" value="pb-3"/>
                </c:if>
                <div class="d-flex text-muted pt-3 ${lastItemBorder}">
                    <div class="${itemPadding} mb-0 small lh-sm w-100">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <span class="d-block">
                                <strong class="text-gray-dark">
                                    ${targetAccount.getLastName()},
                                    ${targetAccount.getFirstName()}
                                    ${targetAccount.getMiddleName()}
                                    ${targetAccount.getSuffix()}
                                </strong>
                                (@${targetAccount.getUsername()})</span>
                                <span class="d-block">
                                    Last sign in:
                                    <c:choose>
                                        <c:when test="${targetAccount.getLastSignIn() == null}">
                                            N/A
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatDate type="both" dateStyle="long" timeStyle="short" value="${targetAccount.getLastSignIn()}" />
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                                <span class="d-block">
                                    Vote recorded on:
                                    <c:choose>
                                        <c:when test="${targetAccount.getVoteRecorded() == null}">
                                            N/A
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatDate type="both" dateStyle="long" timeStyle="short" value="${targetAccount.getVoteRecorded()}" />
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="btn-group">
                                <a href="<c:url value='/accounts/edit?id=${targetAccount.getId()}'/>" class="btn btn-outline-primary">
                                    <i class="bi bi-pencil-fill"></i>
                                </a>
                                <c:if test="${targetAccount.getId() != account.getId()}">
                                <a href="<c:url value='/accounts/delete?id=${targetAccount.getId()}'/>" class="btn btn-outline-danger">
                                    <i class="bi bi-trash-fill"></i>
                                </a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
                </c:forEach>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm p-3">
                <h6 class="border-bottom pb-2 mb-3">Actions</h6>
                <div id="list-actions" class="list-group">
                    <a class="list-group-item list-group-item-action" href="<c:url value='/accounts/add'/>">
                        <i class="bi bi-plus-circle-fill"></i>
                        Add new account
                    </a>
                    <a class="list-group-item list-group-item-action" href="<c:url value='/accounts/delete-responses'/>">
                        <i class="bi bi-trash-fill"></i>
                        Delete all responses
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>