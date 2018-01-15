<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel panel-primary panel-list">
	<div class="panel-heading">
		<h3 class="title">
			<spring:theme code="payment.option.select.your.option" />
		</h3>
	</div>
	<div class="panel-body">
		<fieldset class="fieldset">
			<div class="alert alert-danger y_paymentOptionError" role="alert" style="display: none;">
				<p class="y_paymentOptionErrorContent"></p>
			</div>
			<legend class="sr-only">
				<spring:theme code="payment.option.screen.reader" />
			</legend>
			<div class="row">
				<c:set var="isSingleOption" value="${fn:length(paymentOptions) eq 1}" />
				<div class="col-xs-12">
					<p>
						<c:if test="${not isSingleOption}">
							<spring:theme code="payment.option.select.your.option.label" />
						</c:if>
					</p>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12">
					<div class="form-group" id="payment-type">
						<c:url var="submitUrl" value="/view/PaymentOptionsComponentController/select-payment-option" />
						<c:forEach var="paymentOption" items="${paymentOptions}" varStatus="optionID">
							<div>
								<form:form id="y_selectPaymentOptionForm${fn:escapeXml(optionID.index)}" commandName="paymentOptionForm" action="${submitUrl}" method="POST">
									<input id="y_paymentType${fn:escapeXml(optionID.index)}" name="payment-type" class="y_paymentOptionRadio" type="${isSingleOption? 'hidden' : 'radio'}" value="y_selectPaymentOptionForm${fn:escapeXml(optionID.index)}">
									<label for="y_paymentType${fn:escapeXml(optionID.index)}">
									<c:choose>
										<c:when test="${paymentOption.bookingTimeAmount.value gt 0}"> 
											<spring:theme code="payment.option.pay.now.message" argumentSeparator="#" arguments="${paymentOption.bookingTimeAmount.formattedValue}" />
											<c:if test="${reservationTotal.value gt 0 and not (paymentOption.prepaymentAmount.value gt 0) and not (paymentOption.checkInPayOff.value gt 0) and paymentOption.bookingTimeAmount.value gt reservationTotal.value}">
												(<spring:theme code="payment.option.previous.pending.amount.message" />)
											</c:if>
										</c:when>
										<c:otherwise>
											<spring:theme code="payment.option.card.authorizing.message"/> 
										</c:otherwise>	
									</c:choose>
									<c:if test="${paymentOption.prepaymentAmount.value gt 0 or  paymentOption.checkInPayOff.value gt 0}">
										(<c:choose>
											<c:when test="${(paymentOption.prepaymentAmount.value gt 0) && (paymentOption.checkInPayOff.value gt 0)}">
												<spring:theme code="payment.option.prepayment.message" argumentSeparator="#" arguments="${paymentOption.prepaymentAmount.formattedValue}# ${paymentOption.prepaymentDeadlineInDays}" />&nbsp;<spring:theme code="payment.option.checkin.payment.message" argumentSeparator="#" arguments="${fn:escapeXml(paymentOption.checkInPayOff.formattedValue)}" />
											</c:when>
											<c:otherwise>
												<c:if test="${paymentOption.prepaymentAmount.value gt 0}">
													<spring:theme code="payment.option.prepayment.message" argumentSeparator="#" arguments="${paymentOption.prepaymentAmount.formattedValue}# ${paymentOption.prepaymentDeadlineInDays}" />
												</c:if>
												<c:if test="${paymentOption.checkInPayOff.value gt 0}">
													<spring:theme code="payment.option.checkin.payment.message" argumentSeparator="#" arguments="${paymentOption.checkInPayOff.formattedValue}" />
												</c:if>
											</c:otherwise>
										</c:choose>)
									</c:if>
									</label>
									<c:forEach var="transaction" items="${paymentOption.associatedTransactions}" varStatus="tranID">
										<form:input type="hidden" path="transactions[${fn:escapeXml(tranID.index)}].amount" value="${fn:escapeXml(transaction.transactionAmount)}" />
										<c:forEach var="entry" items="${transaction.entryNumbers}" varStatus="entryID">
											<form:input type="hidden" path="transactions[${fn:escapeXml(tranID.index)}].entryNumbers[${fn:escapeXml(entryID.index)}]" value="${fn:escapeXml(transaction.entryNumbers[entryID.index])}" />
										</c:forEach>
									</c:forEach>
								</form:form>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
		</fieldset>
	</div>
</div>
