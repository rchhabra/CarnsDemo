<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${isPackageInOrder}">
	<div class="panel panel-default total">
		<div class="panel-body">
			<div class="col-xs-12">
				<dl class="text-right">
					<dt>
						<spring:theme code="text.cms.booking.transport.total.title" text="Transport Booking Total" />
					</dt>
					<dd>
						<format:price priceData="${transportBookingTotalAmount}" />
					</dd>
					<dt>
						<spring:theme code="text.cms.booking.accommodation.total.title" text="Accommodation Booking Total" />
					</dt>
					<dd>
						<format:price priceData="${accommodationBookingTotalAmount}" />
					</dd>
					<dt>
						<spring:theme code="text.cms.booking.total.title" text="Booking Total" />
					</dt>
					<dd>
						<format:price priceData="${total}" />
					</dd>
					<c:if test="${partialPaymentPaid.value >= 0 }">
						<dt>
							<spring:theme code="reservation.partial.pay.label" text="Paid" />
						</dt>
						<dd>
							<format:price priceData="${partialPaymentPaid}" />
						</dd>
						<c:if test="${notRefundable.value gt 0}">
                    		<dd>(<spring:theme code="reservation.not.refundable.paid.label" text="Included not refundable:" /><format:price priceData="${notRefundable}"/>) </dd>
                    	</c:if>
					</c:if>
					<c:if test="${partialPaymentDue ne null && partialPaymentDue.value > 0 }">
						<dt>
							<spring:theme code="reservation.partial.due.label" text="Due" />
						</dt>
						<dd>
							<format:price priceData="${partialPaymentDue}" />
						<dd>
					</c:if>
				</dl>
			</div>
		</div>
	</div>
</c:if>
