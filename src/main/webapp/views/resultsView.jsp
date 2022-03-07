<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="pageSubtitle" value="Election Results"/>

<%@ include file="_header.jsp" %>

<div class="container">
    <div id="ballot-title" class="mt-3">
        <span class="h3">
            Election Results
        </span>
        <br/>
        <span class="badge bg-primary f-3">
        As of <fmt:formatDate type="both" dateStyle="long" timeStyle="long" value="${retrieval}" /> 
        </span>
        <br/>
        <span class="h6">MAY 9, 2022 NATIONAL AND LOCAL ELECTIONS</span>
        <br/>
        <span class="h6">${locationName}</span>
    </div>
    <div class="accordion shadow-sm my-3" id="accordionBallot">
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
                    <span class="h6 m-0">
                        ${position.getName()}
                    </span>
                </button>
            </div>
            <div class="accordion-collapse collapse vote-container"
                 id="collapse-${positionStatus.index}"  
                 data-vote-limit="${position.getVoteLimit()}"
                 data-vote-position="${position.getId()}"
                 aria-labelledby="heading-${positionStatus.index}">
                <div class="accordion-body">
                <div class="row">
                <c:forEach items="${candidates.get(position.getId())}" var="candidate" varStatus="candidateStatus">
                    <div class="candidate-box min">
                        <div>
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
                        </div>
                        <span class="fw-bold fs-3">
                        <c:choose>
                            <c:when test="${candidateVotes.get(candidate.getId()) != null}">
                                <fmt:formatNumber type="number" maxFractionDigits="0" value="${candidateVotes.get(candidate.getId())}" />
                            </c:when>
                            <c:otherwise>
                                0
                            </c:otherwise>
                        </c:choose>
                        </span>
                    </div>
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
                    <span class="h6 m-0">
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
                    <div class="candidate-box min">
                        ${partylist.getName()}
                        <span class="fw-bold fs-3">
                        <c:choose>
                            <c:when test="${partylistVotes.get(partylist.getId()) != null}">
                                <fmt:formatNumber type="number"
                                                  maxFractionDigits="0"
                                                  value="${partylistVotes.get(partylist.getId())}" />
                            </c:when>
                            <c:otherwise>
                                0
                            </c:otherwise>
                        </c:choose>
                        </span>
                    </div>
                </c:forEach>
                </div>
                </div>
            </div>
        </div>
    </div>
    </div>
</div>

<%@ include file="_footer.jsp" %>