<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageSubtitle" value="Your Ballot Status"/>

<%@ include file="_header.jsp" %>

<div class="container">
    <div class="my-5 text-center">
        <img class="d-block mx-auto mb-4" src="<c:url value='/assets/images/entry.svg'/>" width="200" height="200">
        <h1 class="display-5 fw-bold">
        Hi, ${voter.getFirstName()}
        <c:if test="${voter.getMiddleName() != null}">
            ${voter.getMiddleName()}
        </c:if>
        ${voter.getLastName()}
        <c:if test="${voter.getSuffix() != null}">
            ${voter.getSuffix()}
        </c:if>
        </h1>
        <h5>
        ${locationName}
        </h5>
        <div class="col-lg-6 mx-auto">
            <p class="lead mb-4">
                Your identity has been confirmed and may now proceed to vote.
            </p>
            <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                <a href="<c:url value='/ballot/answer'/>" class="btn btn-primary btn-lg px-4 gap-3">Vote Now</a>
                <a href="<c:url value='/sign-out'/>" class="btn btn-outline-secondary btn-lg px-4">Sign Out</a>
            </div>
        </div>
    </div>
</div>

<%@ include file="_footer.jsp" %>