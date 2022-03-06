<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageSubtitle" value="Home"/>

<%@ include file="_header.jsp" %>

<div class="bg-primary text-center text-light px-4">
    <div class="d-flex align-items-center justify-content-center flex-column vh-100">
        <img class="d-block mx-auto mb-4" src="<c:url value='/assets/images/home-1.svg'/>" width="250" height="150">
        <h1 class="display-5 fw-bold">Reliable, safe, and secure</h1>
        <div class="col-lg-6 mx-auto">
            <p class="lead mb-4">
                Halal allows you to vote for your candidates online, wherever you are, right from the comfort of your own devices.
            </p>
            <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                <c:choose>
                    <c:when test="${ballotSubmitted != null}">
                        <a href="<c:url value='/ballot/receipt'/>" class="btn btn-outline-gold btn-lg px-4">View Ballot Receipt</a>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value='/accounts/sign-in'/>" class="btn btn-outline-light btn-lg px-4 gap-3">Vote Now</a>
                    </c:otherwise>
                </c:choose>
                <c:if test="${account == null}">
                    <a href="<c:url value='/accounts/sign-in-qr'/>" class="btn btn-outline-gold btn-lg px-4">Vote Now with QR</a>
                </c:if>
            </div>
        </div>
    </div>
</div>

<div class="container col-xxl-8 px-4 py-5">
    <div class="row flex-lg-row align-items-center g-5 py-5">
        <div class="col-10 col-sm-8 col-lg-6">
            <img src="<c:url value='/assets/images/home-3.svg'/>" class="d-block mx-lg-auto img-fluid" alt="Bootstrap Themes" width="700" height="500">
        </div>
        <div class="col-lg-6 text-end">
            <h1 class="display-5 fw-bold lh-1 mb-3">Know your candidates</h1>
            <p class="lead">
                A list of all candidates running for different positions is conveniently available for you.
            </p>
            <div class="d-flex justify-content-end">
                <a href="<c:url value='/candidates'/>" class="btn btn-primary btn-lg px-4">
                    View Candidates
                </a>
            </div>
        </div>
    </div>
</div>

<div class="container col-xxl-8 px-4 py-5">
    <div class="row flex-lg-row-reverse align-items-center g-5 py-5">
        <div class="col-10 col-sm-8 col-lg-6">
            <img src="<c:url value='/assets/images/home-2.svg'/>" class="d-block mx-lg-auto img-fluid" width="700" height="500">
        </div>
        <div class="col-lg-6">
            <h1 class="display-5 fw-bold lh-1 mb-3">See who's leading in places across the country</h1>
            <p class="lead">
                Election results are updated real-time, along with raw data and graphic visualizations.
            </p>
            <div class="d-flex justify-content-start">
                <a href="<c:url value='/election-results'/>" class="btn btn-primary btn-lg px-4">
                    View Election Results
                </a>
            </div>
        </div>
    </div>
</div>

<%-- Footer --%>
<footer class="mt-auto bg-light">
    <div class="container py-3 border-top">
        <div class="d-flex flex-wrap justify-content-between align-items-center">
            <p class="col-md-4 mb-0 text-muted">&copy; 2022</p>
            <a href="<c:url value='/'/>" class="col-md-4 d-flex align-items-center justify-content-end text-decoration-none">
                <img class="d-block" src="<c:url value='/assets/branding/pacific-tech.svg'/>" width="168" height="30">
            </a>
        </div>
    </div>
</footer>

<%@ include file="_footer.jsp" %>