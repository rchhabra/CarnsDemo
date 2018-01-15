<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/account"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%-- My Saved Payment --%>
<div class="panel-heading">
    <h3 class="panel-title title-collapse">
         <spring:theme code="text.account.savedPayments.title" text="My Saved Payments" />
    </h3>
    <button class="show-hide-button" type="button" data-toggle="collapse" data-target="#saved-payment" aria-expanded="true" aria-controls="saved-payment"><spring:theme code="account.bookings.button.collapseExpand" text="Collapse / Expand" /></button>
</div>
<div class="panel-body collapse in" id="saved-payment">
	<c:choose>
		<c:when test="${not empty paymentInfoData}">
			<c:forEach items="${paymentInfoData}" var="paymentInfo" varStatus="index">
				<div class="panel panel-default my-account-secondary-panel">
					<div class="panel-heading">
						<h3 class="panel-title">
							Card ${fn:escapeXml(index.count)}
							<c:if test="${paymentInfo.defaultPaymentInfo}">
								<span class="default"> - <spring:theme code="text.default" text="Default" />
								</span>
							</c:if>
						</h3>
					</div>
					<div class="panel-body option">
						<div class="fieldset">
							<div class="row">
								<account:paymentInfo paymentInfo="${paymentInfo}" />
								<div class="col-xs-12 col-md-6">
									<div class="form-group col-xs-6 col-sm-6 col-md-6 bottom-align-parent">
										<c:if test="${not paymentInfo.defaultPaymentInfo}">
											<c:url value="/my-account/set-default-payment-details" var="setDefaultPaymentActionUrl" />
											<form:form id="setDefaultPaymentDetails${fn:escapeXml(paymentInfo.id)}" action="${setDefaultPaymentActionUrl}" method="post">
												<input type="hidden" name="paymentInfoId" value="${fn:escapeXml(paymentInfo.id)}" />
												<ycommerce:testId code="paymentDetails_setAsDefault_button">
													<button class="btn btn-primary btn-block bottom-align">
														<spring:theme code="text.setDefault" text="Set as Default" />
													</button>
												</ycommerce:testId>
											</form:form>
										</c:if>
									</div>
									<div class="form-group col-xs-6 col-sm-6 col-md-6 bottom-align-parent">
										<button class="btn btn-default btn-block bottom-align" data-toggle="modal" data-target="#remove-card-modal-${fn:escapeXml(paymentInfo.id)}">
											<spring:theme code="text.account.savedPayments.remove.btn" text="Remove" />
										</button>
									</div>
								</div>
							</div>
							<%-- / .row --%>
						</div>
					</div>
					<div class="modal fade" id="remove-card-modal-${fn:escapeXml(paymentInfo.id)}" tabindex="-1" role="dialog" aria-labelledby="remove-card-modal-title" aria-hidden="true" style="display: none;">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<h4 class="modal-title" id="remove-card-modal-title">
										<spring:theme code="text.account.savedPayments.remove.modal.title" text="Remove" />
									</h4>
								</div>
								<div class="modal-body">
									<div class="container">
										<spring:theme code="text.account.paymentDetails.delete.following" text="The following payment method will be deleted" />
										<br /> <strong> <br>${fn:escapeXml(paymentInfo.accountHolderName)} <br>${fn:escapeXml(paymentInfo.cardTypeData.name)} <br>${fn:escapeXml(paymentInfo.cardNumber)} <br> <c:if test="${paymentInfo.expiryMonth lt 10}">0</c:if>${fn:escapeXml(paymentInfo.expiryMonth)}&nbsp;/&nbsp;${fn:escapeXml(paymentInfo.expiryYear)}
											<c:if test="${paymentInfo.billingAddress ne null}">
												<br>${fn:escapeXml(paymentInfo.billingAddress.line1)}
										<br>${fn:escapeXml(paymentInfo.billingAddress.town)}&nbsp;${fn:escapeXml(paymentInfo.billingAddress.region.isocodeShort)}
										<br>${fn:escapeXml(paymentInfo.billingAddress.country.name)}&nbsp;${fn:escapeXml(paymentInfo.billingAddress.postalCode)}
									</c:if>
										</strong>
									</div>
								</div>
								<div class="modal-footer">
									<div class="col-xs-12 button-inline">
										<c:url value="/my-account/remove-payment-method" var="removePaymentActionUrl" />
										<form:form id="removePaymentDetails${fn:escapeXml(paymentInfo.id)}" action="${removePaymentActionUrl}" method="post">
											<input type="hidden" name="paymentInfoId" value="${fn:escapeXml(paymentInfo.id)}" />
											<ycommerce:testId code="paymentDetailsDelete_delete_button">
												<div class="col-xs-6">
													<button type="submit" class="btn btn-primary paymentsDeleteBtn col-xs-12">
														<spring:theme code="text.account.paymentDetails.delete" text="Delete" />
													</button>
												</div>
											</ycommerce:testId>
											<div class="col-xs-6">
												<a class="btn btn-primary paymentsDeleteBtn col-xs-12" data-payment-id="${fn:escapeXml(paymentInfo.id)}" data-dismiss="modal">
													<spring:theme code="text.button.cancel" text="Cancel" />
												</a>
											</div>
										</form:form>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div class="fieldset">
			<p>
				<spring:theme code="text.account.savedPayments.nosavedpayments" text="You have no saved payment cards" />
			</p>
		</div>
		</c:otherwise>
	</c:choose>
</div>
<%-- / My Saved Payment --%>
