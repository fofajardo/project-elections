<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageSubtitle" value="Your Ballot Receipt"/>

<%@ include file="_header.jsp" %>

<div class="container">
    <div id="ballot-title" class="mt-3">
        <span class="h3">Ballot Receipt</span>
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
                 data-vote-limit="${position.getVoteLimit()}"
                 data-vote-position="${position.getId()}"
                 aria-labelledby="heading-${positionStatus.index}">
                <div class="accordion-body">
                <div class="row">
                <c:forEach items="${candidates.get(position.getId())}" var="candidate" varStatus="candidateStatus">
                    <c:if test="${candidateStatus.index % maxRows[positionStatus.index] == 0}">
                        <c:if test="${rowAdded}">
                            <c:out value="</div>" escapeXml="false"/>
                            <c:remove var="rowAdded"/>
                        </c:if>
                        <c:out value="<div class='col-sm-3'>" escapeXml="false"/>
                        <c:set var="rowAdded" value="${true}"/>
                    </c:if>
                    <div class="candidate-box">
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
                 data-vote-limit="1"
                 data-vote-position="partylist"
                 aria-labelledby="heading-partylist">
                <div class="accordion-body">
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
                    <div class="candidate-box">
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
                    </div>
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
</div>

<%@ include file="_footer.jsp" %>