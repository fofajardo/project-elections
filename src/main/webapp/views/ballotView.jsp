<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="pageSubtitle" value="Candidates"/>
<c:if test="${isReceipt}">
    <c:set var="pageSubtitle" value="Ballot Receipt"/>
    <c:set var="hideCandidateNumber" value="${true}"/>
</c:if>
<c:if test="${isResults}">
    <c:set var="pageSubtitle" value="Election Results"/>
    <c:set var="hideCandidateNumber" value="${true}"/>
    <c:set var="boxStyle" value="min"/>
</c:if>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div id="ballot-title" class="my-3">
        <div class="h1">${pageSubtitle}</div>
        <div class="h6">MAY 9, 2022 NATIONAL AND LOCAL ELECTIONS</div>
        <div class="h6">${locationName}</div>
        <c:if test="${isResults}">
        <div class="badge bg-primary f-3">
        As of <fmt:formatDate type="both" dateStyle="long" timeStyle="long" value="${retrieval}" /> 
        </div>
        </c:if>
    </div>

    <c:if test="${isResults}">
        <%@ include file="/WEB-INF/jspf/graphs.jspf" %>
        <h3 class="my-3">Tally</h3>
    </c:if>
    <%@ include file="/WEB-INF/jspf/choices.jspf" %>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>