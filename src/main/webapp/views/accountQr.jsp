<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageSubtitle" value="View QR Code"/>

<%@ include file="/WEB-INF/jspf/header.jspf" %>

<script src="<c:url value="/assets/qrcode.min.js"/>" type="text/javascript"></script>
<style>
#pov-qr > img {
  width: 200px;
  height: 200px;
}
</style>

<div class="container">
    <div class="my-5 d-flex flex-column justify-content-center align-items-center text-center">
        <div class="mb-4">
            <div class="d-block" id="pov-qr"></div>
        </div>
        <h1 class="display-5 fw-bold">Your QR Code</h1>
        <p class="lead mb-4 col-lg-6">
            You may download or save this code to your device and use it as a more convenient way of signing in to vote.
        </p>
    </div>
</div>

<input type="hidden" id="pov-reference-code" value="${account.getUuid()}">

<script type="text/javascript">
    new QRCode(
        document.getElementById("pov-qr"),
        {
            text: document.getElementById("pov-reference-code").value,
            width: 200,
            height: 200,
        }
    );
</script>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>