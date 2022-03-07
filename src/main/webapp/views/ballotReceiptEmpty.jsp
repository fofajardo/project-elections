<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageSubtitle" value="Your Ballot Receipt"/>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="my-5 text-center">
        <img class="d-block mx-auto mb-4" src="<c:url value='/assets/images/no-data.svg'/>" width="200" height="200">
        <h1 class="display-5 fw-bold">We didn't find anything</h1>
        <div class="col-lg-6 mx-auto">
            <p class="lead mb-4">
                Ballot receipts are available only after voting.
            </p>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>