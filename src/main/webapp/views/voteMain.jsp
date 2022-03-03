<%@ include file="_header.jsp" %>

<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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

<style>
.alert-validation {
  display: none;
}

.custom-validated .alert-validation:not(.invisible) {
  display: block;
}

.candidate-box {
  border-radius: 0.25rem;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
  border: 1px solid transparent;
  transition: all 300ms;
  padding: 0.5rem;
  margin-bottom: 10px;
  border-color: rgba(0, 0, 0, 0.125);
  height: 65px;
  display: block;
  font-weight: 500;
  -webkit-user-select: none;  
  -moz-user-select: none;    
  -ms-user-select: none;      
  user-select: none;
}

.candidate-box:hover {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.2);
}
</style>

<div class="container">
    <div id="ballot-title" class="mt-3 mb-3">
        <span class="h3">MAY 9, 2022 NATIONAL AND LOCAL ELECTIONS</span>
        <br/>
        <span class="h6">${currentLocation.getName()}</span>
    </div>
    <form id="ballot-form"
          action="<c:url value='/vote/submit'/>"
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
                        <span class="badge bg-primary text-dark me-2"
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
                    You must vote for ${position.getVoteLimit()} candidate<c:out value="${candidatePlural}"/> in this position.
                </div>
                <div class="row">
                <c:forEach items="${candidates.get(positionStatus.index)}" var="candidate" varStatus="candidateStatus">
                    <c:if test="${candidateStatus.index % maxRows[positionStatus.index] == 0}">
                        <c:if test="${rowAdded}">
                            <c:out value="</div>" escapeXml="false"/>
                            <c:remove var="rowAdded"/>
                        </c:if>
                        <c:out value="<div class='col-sm-3'>" escapeXml="false"/>
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
                        <span class="badge bg-primary text-dark me-2"
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
                    You must vote for only 1 party list.
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
                                    <c:when test="${partylistStatus.index < 9}">
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
    <div class="mt-3 -grid gap-2 d-md-flex justify-content-md-end"">
        <input type="submit" value="Submit" class="btn btn-primary me-md-2">
        <input type="reset" value="Reset" class="btn btn-secondary">
    </div>
    </form>
</div>

<%@ include file="_footer.jsp" %>