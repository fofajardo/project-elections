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
    <div id="ballot-title" class="mt-3 mb-3">
        <span class="h3">MAY 9, 2022 NATIONAL AND LOCAL ELECTIONS</span>
        <br/>
        <span class="h6">${currentLocation.getName()}</span>
    </div>
    <form id="ballot-form"
          action="<c:url value='/ballot/submit'/>"
          method="post"
          class="needs-validation"
          novalidate>
    <div class="accordion shadow-sm" id="accordionBallot">
    <!-- 1: Positions -->
    <c:forEach items="${positions}" var="position" varStatus="positionStatus">
        <div class="accordion-item">
            <div class="accordion-header"
                 id="heading-${positionStatus.index}">
                <button class="accordion-button collapsed"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapse-${positionStatus.index}"
                        aria-expanded="false"
                        aria-controls="collapse-${positionStatus.index}">
                    <span class="h6 text-dark m-0">
                        <span class="badge bg-primary me-2"
                              style="min-width: 80px;">
                              Vote for ${position.getVoteLimit()}
                        </span>
                        ${position.getName()}
                    </span>
                </button>
            </div>
            <div class="accordion-collapse collapse vote-container"
                 id="collapse-${positionStatus.index}"
                 data-bs-parent="#accordionBallot"
                 data-vote-limit="${position.getVoteLimit()}"
                 data-vote-position="${position.getId()}"
                 aria-labelledby="heading-${positionStatus.index}">
                <div class="accordion-body">
                <c:if test="${position.getVoteLimit() > 1}">
                    <c:set var="candidatePlural" value="s"/>
                </c:if>
                <div id="alert-${position.getId()}" class="alert alert-danger alert-validation invisible" role="alert">
                    You must vote for <span class="fw-bold">only ${position.getVoteLimit()} candidate<c:out value="${candidatePlural}"/></span> in this position.
                </div>
                <div class="row">
                <c:forEach items="${candidates.get(positionStatus.index)}" var="candidate" varStatus="candidateStatus">
                    <c:if test="${candidateStatus.index % maxRows[positionStatus.index] == 0}">
                        <c:if test="${rowAdded}">
                            <c:out value="</div>" escapeXml="false"/>
                            <c:remove var="rowAdded"/>
                        </c:if>
                        <c:out value="<div class='col-lg-3'>" escapeXml="false"/>
                        <c:set var="rowAdded" value="${true}"/>
                    </c:if>
                    <label class="candidate-box" for="candidate-${candidate.getId()}">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="vote-position-${position.getId()}"
                                   id="candidate-${candidate.getId()}" value="${candidate.getId()}">
                            <label class="form-check-label" for="candidate-${candidate.getId()}">
                                <span class="badge rounded-pill bg-secondary">${candidateStatus.index + 1}</span> 
                                ${candidate.getLastName()}, ${candidate.getFirstName()}
                                <c:if test="${candidate.getMiddleName() != null}">
                                    ${candidate.getMiddleName()}
                                </c:if>
                                <c:if test="${candidate.getSuffix() != null}">
                                    ${candidate.getSuffix()}
                                </c:if>
                                <c:if test="${candidate.getAttachedParty() != null}">
                                    (${candidate.getAttachedParty().getAlias()})
                                </c:if>
                            </label>
                        </div>
                    </label>
                    <c:if test="${candidateStatus.last && rowAdded}">
                        <c:out value="</div>" escapeXml="false"/>
                        <c:remove var="rowAdded"/>
                    </c:if>
                </c:forEach>
                </div>
                </div>
            </div>
        </div>
    </c:forEach>
    <!-- 2: Partylists -->
        <div class="accordion-item">
            <div class="accordion-header"
                 id="heading-partylist">
                <button class="accordion-button collapsed"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapse-partylist"
                        aria-expanded="false"
                        aria-controls="collapse-partylist">
                    <span class="h6 text-dark m-0">
                        <span class="badge bg-primary me-2"
                              style="min-width: 80px;">
                              Vote for 1
                        </span>
                        Party List
                    </span>
                </button>
            </div>
            <div class="accordion-collapse collapse vote-container"
                 id="collapse-partylist"
                 data-bs-parent="#accordionBallot"
                 data-vote-limit="1"
                 data-vote-position="partylist"
                 aria-labelledby="heading-partylist">
                <div class="accordion-body">
                <div id="alert-partylist" class="alert alert-danger alert-validation invisible" role="alert">
                    You must vote for <span class="fw-bold">only 1 party list</span>.
                </div>
                <div class="row">
                <c:forEach items="${partylists}" var="partylist" varStatus="partylistStatus">
                    <c:if test="${partylistStatus.index % partylistMaxRows == 0}">
                        <c:if test="${rowAdded}">
                            <c:out value="</div>" escapeXml="false"/>
                            <c:remove var="rowAdded"/>
                        </c:if>
                        <c:out value="<div class='col-sm-3'>" escapeXml="false"/>
                        <c:set var="rowAdded" value="${true}"/>
                    </c:if>
                    <label class="candidate-box" for="partylist-${partylist.getId()}">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="vote-position-partylist"
                                   id="partylist-${partylist.getId()}" value="${partylist.getId()}">
                            <label class="form-check-label" for="partylist-${partylist.getId()}">
                                <span class="badge rounded-pill bg-secondary">
                                <c:choose>
                                    <c:when test="${partylist.getCustomOrder() < 9}">
                                        0${partylist.getCustomOrder()}
                                    </c:when>
                                    <c:otherwise>
                                        ${partylist.getCustomOrder()}
                                    </c:otherwise>
                                </c:choose>
                                </span>
                                ${partylist.getName()}
                            </label>
                        </div>
                    </label>
                    <c:if test="${partylistStatus.last && rowAdded}">
                        <c:out value="</div>" escapeXml="false"/>
                        <c:remove var="rowAdded"/>
                    </c:if>
                </c:forEach>
                </div>
                </div>
            </div>
        </div>
    </div>
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