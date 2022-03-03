<%@ include file="_header.jsp" %>

<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="container">
    <div class="my-5 text-center">
        <img class="d-block mx-auto mb-4" src="<c:url value='/assets/images/done.svg'/>" width="200" height="200">
        <h1 class="display-5 fw-bold">Your vote has been submitted</h1>
        <div class="col-lg-6 mx-auto">
            <p class="lead mb-4">
                Only one vote per person is allowed.
            </p>
            <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                <button type="button" class="btn btn-primary btn-lg px-4 gap-3">View Receipt</button>
                <button type="button" class="btn btn-outline-secondary btn-lg px-4">Sign Out</button>
            </div>
        </div>
    </div>
</div>

<%@ include file="_footer.jsp" %>