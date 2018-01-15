<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<form:form id="selectPaymentTypeForm" commandName="paymentTypeForm" action="${fn:escapeXml(request.contextPath)}/checkout/multi/payment-type/choose" method="post">
	<div class="panel panel-primary panel-list">
		<div class="panel-heading">
			<h3 class="title">
				<spring:theme code="checkout.summary.paymentType.header" text="Payment Type" />
			</h3>
		</div>
		<div class="step-body-form">
			<div class="radiobuttons_paymentselection">
				<c:forEach items="${paymentTypes}" var="paymentType">
					<div class="radio-button-row">
						<form:radiobutton path="paymentType" id="PaymentTypeSelection_${paymentType.code}" value="${paymentType.code}" label="${paymentType.displayName}" />
					</div>
				</c:forEach>
			</div>
			<div id="costCenter">
				<c:if test="${not empty costCenters}">
					<formElement:formSelectBox idKey="costCenterSelect" labelKey="checkout.multi.costCenter.label" path="costCenterId" skipBlank="false" skipBlankMessageKey="checkout.multi.costCenter.title.pleaseSelect" itemValue="code" itemLabel="name" items="${costCenters}" mandatory="true"
						selectCSSClass="form-control" />
				</c:if>
			</div>
			<div class="row">
				<div class="col-xs-12 col-sm-6 col-md-3 pull-right">
					<button id="choosePaymentType_continue_button" type="submit" class="btn btn-secondary btn-block checkout-next">
						<spring:theme code="checkout.multi.paymentType.continue" />
					</button>
				</div>
			</div>
		</div>
	</div>
</form:form>
