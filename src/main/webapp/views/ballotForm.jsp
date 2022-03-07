<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageSubtitle" value="Your Ballot"/>

<%@ include file="_header.jsp" %>

<script>
    window.addEventListener("load", function () {
        let form = document.getElementById("ballot-form");
        form.addEventListener("submit", function (event) {
            let invalidResponse = !form.checkValidity();

            let voteContainers = document.getElementsByClassName("vote-container");
            for (let i = 0; i < voteContainers.length; i++) {
                let currentContainer = voteContainers[i];
                let limit = currentContainer.getAttribute("data-vote-limit");
                let position = currentContainer.getAttribute("data-vote-position");

                let checkedBoxes = document.querySelectorAll("input[name='vote-position-" + position + "']:checked");
                let alertBox = document.getElementById("alert-" + position);

                if (!alertBox) {
                    continue;
                }

                if (checkedBoxes.length != limit) {
                    alertBox.classList.remove("invisible");
                    invalidResponse = true;
                } else {
                	alertBox.classList.add("invisible");
                }
            }

            if (invalidResponse) {
                event.preventDefault()
                event.stopPropagation()
            }

            form.classList.add("custom-validated")
        }, false);
    });
    window.addEventListener("reset", function () {
        let form = document.getElementById("ballot-form");
        form.classList.remove("custom-validated");
    });
</script>

<div class="container">
    <div id="ballot-title" class="my-3">
        <div class="h3">MAY 9, 2022 NATIONAL AND LOCAL ELECTIONS</div>
        <div class="h6">${currentLocation.getName()}</div>
    </div>

    <form id="ballot-form"
          action="<c:url value='/ballot/submit'/>"
          method="post"
          class="needs-validation"
          novalidate>

    <c:set var="isForm" value="${true}"/>
    <%@ include file="/WEB-INF/jspf/choices.jspf" %>

    <div class="d-grid gap-2 d-md-flex justify-content-md-center my-3">
        <button type="submit" value="Submit" class="btn btn-success col-md">
            <i class="bi bi-box-arrow-right"></i>
            Submit
        </button>
        <button type="reset" class="btn btn-outline-secondary col-md">
            <i class="bi bi-backspace"></i>
            Reset
        </button>
        <a href="<c:url value='/ballot/status'/>" class="btn btn-outline-danger col-md">
            <i class="bi bi-x-circle"></i>
            Cancel
        </a>
    </div>
    </form>
</div>

<%@ include file="_footer.jsp" %>