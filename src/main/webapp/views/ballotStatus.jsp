<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageSubtitle" value="Your Ballot Status"/>

<%@ include file="_header.jsp" %>

<div class="container">
    <div class="my-5 text-center">
        <c:set var="mainImage" value="entry"/>
        <c:if test="${ballotSubmitted}">
            <c:set var="mainImage" value="done"/>
        </c:if>
        <img class="d-block mx-auto mb-4" src="<c:url value='/assets/images/${mainImage}.svg'/>" width="200" height="200">
        <h1 class="display-5 fw-bold">
        <c:choose>
            <c:when test="${ballotSubmitted}">
                Your ballot has been submitted
            </c:when>
            <c:otherwise>
                Hi, ${voter.getFirstName()}
                <c:if test="${voter.getMiddleName() != null}">
                    ${voter.getMiddleName()}
                </c:if>
                    ${voter.getLastName()}
                <c:if test="${voter.getSuffix() != null}">
                    ${voter.getSuffix()}
                </c:if>
            </c:otherwise>
        </c:choose>
        </h1>
        <c:if test="${!ballotSubmitted}">
        <h5>
            ${locationName}
        </h5>
        </c:if>
        <div class="col-lg-6 mx-auto">
            <p class="lead mb-4">
            <c:choose>
                <c:when test="${ballotSubmitted}">
                    Only one vote per person is allowed.
                </c:when>
                <c:otherwise>
                    Your identity has been confirmed and may now proceed to vote.
                </c:otherwise>
            </c:choose>
            </p>
            <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                <c:choose>
                    <c:when test="${ballotSubmitted}">
                        <a href="<c:url value='/ballot/receipt'/>" class="btn btn-primary btn-lg px-4 gap-3">View Receipt</a>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value='/ballot/answer'/>" class="btn btn-primary btn-lg px-4 gap-3">Vote Now</a>
                    </c:otherwise>
                </c:choose>
                <a href="<c:url value='/sign-out'/>" class="btn btn-outline-secondary btn-lg px-4">Sign Out</a>
            </div>
        </div>
    </div>
</div>

<%@ include file="_footer.jsp" %>