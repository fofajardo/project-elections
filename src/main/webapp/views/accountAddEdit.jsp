<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="accountAction" value="Add New"/>
<c:set var="authPasswordRequired" value="required"/>
<c:if test="${isEdit}">
    <c:set var="accountAction" value="Edit"/>
    <c:set var="authPassword" value="Password already set, type here to change"/>
    <c:remove var="authPasswordRequired"/>
</c:if>
<c:set var="pageSubtitle" value="${accountAction} Account"/>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<script>
    window.addEventListener("load", function () {
        let form = document.getElementById("account-form");
        form.addEventListener("submit", function (event) {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }
            
            form.classList.add("was-validated")
        }, false);
    });
    window.addEventListener("reset", function () {
        let form = document.getElementById("account-form");
        form.classList.remove("was-validated");
    });
</script>

<div class="container mx-auto">
    <!-- Primary Content -->
    <div class="row">
        <form id="account-form"
              class="col-md-9 mx-auto"
              method="post"
              class="needs-validation"
              novalidate>
            <div id="ballot-title" class="mt-3">
                <div class="h4">${pageSubtitle}</div>
            </div>
            <div class="card shadow-sm p-3 my-3">
                <h6 class="border-bottom pb-2">Personal Information</h6>
                <div class="row">
                    <div class="col-sm mb-3">
                        <label class="form-label" for="name-first">First Name</label>
                        <input type="text"
                               class="form-control"
                               id="name-first"
                               name="name-first"
                               value="${nameFirst}"
                               required>
                        <div class="invalid-feedback">
                            Please provide a first name.
                        </div>
                    </div>
                    <div class="col-sm mb-3">
                        <label class="form-label" for="name-mid">Middle Name</label>
                        <input type="text"
                               class="form-control"
                               id="name-middle"
                               name="name-middle"
                               value="${nameMiddle}">
                    </div>
                    <div class="col-sm mb-3">
                        <label class="form-label" for="name-last">Last Name</label>
                        <input type="text"
                               class="form-control"
                               id="name-last"
                               name="name-last"
                               value="${nameLast}"
                               required>
                        <div class="invalid-feedback">
                            Please provide a last name.
                        </div>
                    </div>
                    <div class="col-sm-2 mb-3">
                        <label class="form-label" for="name-suffix">Suffix</label>
                        <input type="text"
                               class="form-control"
                               id="name-suffix"
                               name="name-suffix"
                               value="${nameSuffix}">
                    </div>
                </div>

                <h6 class="border-bottom pb-2">Authentication</h6>
                <div class="row">
                    <div class="col-sm mb-3">
                        <label class="form-label" for="auth-username">Username</label>
                        <input type="text"
                               class="form-control"
                               id="auth-username"
                               name="auth-username"
                               value="${authUsername}"
                               required>
                        <div class="invalid-feedback">
                            Please provide a username.
                        </div>
                    </div>
                    <div class="col-sm mb-3">
                        <label class="form-label" for="auth-email">Email</label>
                        <input type="email"
                               class="form-control"
                               id="auth-email"
                               name="auth-email"
                               value="${authEmail}"
                               required>
                        <div class="invalid-feedback">
                            Please provide an email.
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm mb-3">
                        <label class="form-label" for="auth-password">Password</label>
                        <input type="password"
                               class="form-control"
                               id="auth-password"
                               name="auth-password"
                               placeholder="${authPassword}"
                               ${authPasswordRequired}>
                        <div class="invalid-feedback">
                            Please provide a password.
                        </div>
                    </div>
                    <div class="col-sm mb-3">
                        <label class="form-label" for="auth-role">Role</label>
                        <select class="form-select" id="auth-role" name="auth-role" required>
                            <option selected disabled value="">Choose…</option>
                            <option value="0">0 - Voter</option>
                            <option value="1">1 - Administrator</option>
                        </select>
                        <c:if test="${isEdit}">
                        <script>
                            var authRole = document.getElementById("auth-role");
                            authRole.selectedIndex = ${authRole + 1};
                        </script>
                        </c:if>
                    </div>
                    <input type="hidden" id="target-id" name="target-id" value="${targetId}">
                </div>
            </div>
            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <button class="col-md-2 btn btn-primary" type="submit">Save</button>
                <a class="col-md-2 btn btn-primary"
                   href="<c:url value='/accounts/admin'/>">
                   Cancel
               </a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>