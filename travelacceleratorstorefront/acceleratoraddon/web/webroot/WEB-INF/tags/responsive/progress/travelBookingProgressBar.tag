<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="stage" required="true" type="java.lang.String"%>
<%@ attribute name="bookingJourney" required="true" type="java.lang.String"%>
<%@ attribute name="amend" required="false" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="breadcrumb-wrap hidden-xs">
	<div class="container">
		<ul class="breadcrumbs">
			<c:choose>
				<c:when test="${amend}">
					<li class="col-xs-4 breadcrumb ${!(stage eq 'payment') && !(stage eq 'confirmation') ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.amend" text="Amend Selections" />
						</span>
					</li>
					<li class="col-xs-4 breadcrumb ${stage eq 'payment' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.paymentrefund" text="Payment/Refund" />
						</span>
					</li>
					<li class="col-xs-4 breadcrumb ${stage eq 'confirmation' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.confirmation" text="Confirmation" />
						</span>
					</li>
				</c:when>
				<c:otherwise>
					<li class="col-xs-4 breadcrumb ${stage eq 'packages' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.package.listing" text="Packages" />
						</span>
					</li>
					<li class="col-xs-4 breadcrumb ${stage eq 'extras' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.package.extras" text="Extras" />
						</span>
					</li>
					<li class="col-xs-4 breadcrumb ${stage eq 'personaldetails' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.package.personalDetails" text="Personal Details" />
						</span>
					</li>
					<li class="col-xs-4 breadcrumb ${stage eq 'payment' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.package.payment" text="Payment" />
						</span>
					</li>
					<li class="col-xs-4 breadcrumb ${stage eq 'confirmation' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.package.confirmation" text="Confirmation" />
						</span>
					</li>
				</c:otherwise>
			</c:choose>
		</ul>
	</div>
</div>
