<%@ page import="elections.data.*" %>
<%@ page import="elections.models.*" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageSubtitle" value="Sign In"/>
<c:set var="hideHeader" value="${true}"/>

<%@ include file="_header.jsp" %>

<c:if test="${useQr}">
<div id="load-overlay">
    <div class="lds-ring"><div></div><div></div><div></div><div></div></div>
</div>
<style>
#load-overlay {
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  position: fixed;
  z-index: 5;
  background: rgba(0, 0, 0, 0.5);
  transition: opacity 500ms;
  display: flex;
  align-items: center;
  justify-content: center;
}
#load-overlay:not([active="true"]) {
  opacity: 0;
  pointer-events: none;
}
.lds-ring {
  display: inline-block;
  position: relative;
  width: 80px;
  height: 80px;
}
.lds-ring div {
  box-sizing: border-box;
  display: block;
  position: absolute;
  width: 64px;
  height: 64px;
  margin: 8px;
  border: 8px solid #fff;
  border-radius: 50%;
  animation: lds-ring 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
  border-color: #fff transparent transparent transparent;
}
.lds-ring div:nth-child(1) {
  animation-delay: -0.45s;
}
.lds-ring div:nth-child(2) {
  animation-delay: -0.3s;
}
.lds-ring div:nth-child(3) {
  animation-delay: -0.15s;
}
@keyframes lds-ring {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
</c:if>

<script>
    window.addEventListener("load", function () {
        let form = document.getElementById("login-form");
        form.addEventListener("submit", function (event) {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }
            
            form.classList.add("was-validated")
        }, false);
    });
    window.addEventListener("reset", function () {
        let form = document.getElementById("login-form");
        form.classList.remove("was-validated");
    });
</script>

<div class="container col-xl-10 col-xxl-8 px-4">
    <div class="row align-items-center g-lg-5 py-5 vh-100">
        <div class="col-lg-7 text-center text-lg-start">
            <img class="mb-2" src="<c:url value='/assets/branding/logo-32.svg'/>" width="320" height="64"/>
            <p class="col-lg-10 fs-4 h5">Shape the future of the Philippines, vote for your candidates with Halal.</p>
        </div>
        <div class="col-md-10 mx-auto col-lg-5">
            <form id="login-form" name="loginForm" class="needs-validation" method="post" novalidate>
                <div class="p-4 m-2 rounded bg-white shadow">
<c:choose>
    <c:when test="${useQr}">
                    <script src="<c:url value="/assets/qrcode-scan.min.js"/>" type="text/javascript"></script>
                    <div hidden>
                        <div id="reader"></div>
                        <input type="file" id="qr-selector" accept="image/*">
                        <script type="module">
                            const html5QrCode = new Html5Qrcode("reader");
                            const fileinput = document.getElementById("qr-selector");
                            fileinput.addEventListener('change', e => {
                                if (e.target.files.length == 0) {
                                    return;
                                }
                                document.getElementById("load-overlay").setAttribute("active", "true");
                                const uuidField = document.getElementById("auth-uuid");
                                const imageFile = e.target.files[0];
                                html5QrCode.scanFile(imageFile, true)
                                    .then(decodedText => {
                                        uuidField.value = decodedText;
                                        document.loginForm.submit();
                                    })
                                    .catch(err => {
                                        uuidField.value = "-1";
                                        document.loginForm.submit();
                                    });
                            });
                        </script>
                        <input type="text" id="auth-uuid" name="auth-uuid" required>
                    </div>
                    <c:if test="${authInvalid}">
                        <div class="alert alert-danger" role="alert">
                            The QR code you've scanned is invalid.
                        </div>
                    </c:if>
                    <a href="#" class="w-100 btn btn-lg btn-primary mb-2" onclick="document.getElementById('qr-selector').click();">
                        <i class="bi bi-qr-code-scan"></i>
                        Scan QR Code
                    </a>
                    <a href="<c:url value='/accounts/sign-in'/>" class="w-100 btn btn-lg btn-gold">
                        <i class="bi bi-envelope"></i>
                        <span class="ms-1">Sign In with Email instead</span>
                    </a>
    </c:when>
    <c:otherwise>
                    <div class="form-floating mb-3">
                        <input type="text" class="form-control" id="floatingInput" name="auth-emailOrUsername" placeholder="name@example.com" required>
                        <label for="floatingInput">Email or username</label>
                        <div class="invalid-feedback">
                            Please provide a valid email address or username.
                        </div>
                    </div>
                    <div class="form-floating mb-3">
                        <input type="password" class="form-control" id="floatingPassword" name="auth-password" placeholder="password" required>
                        <label for="floatingPassword">Password</label>
                        <div class="invalid-feedback">
                            Please provide a password.
                        </div>
                    </div>
                    <c:if test="${authInvalid}">
                        <div class="alert alert-danger" role="alert">
                            The credentials you've entered does not match any account.
                        </div>
                    </c:if>
                    <button class="w-100 btn btn-lg btn-primary mb-2" type="submit">Sign In</button>
                    <a href="<c:url value='/accounts/sign-in-qr'/>" class="w-100 btn btn-lg btn-gold">
                        <i class="bi bi-qr-code-scan"></i>
                        <span class="ms-1">Sign In with QR</span>
                    </a>
    </c:otherwise>
</c:choose>
                </div>
                <div class="d-flex align-items-center justify-items-center pt-3">
                    <a href="<c:url value='/'/>" class="w-100 text-decoration-none link-secondary text-center">
                        <i class="bi bi-arrow-left-circle"></i>
                        <span class="ms-1">Return to Home</span>
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="_footer.jsp" %>