<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="stage" required="true" type="java.lang.String" %>
<%@ attribute name="bookingJourney" required="true" type="java.lang.String" %>
<%@ attribute name="amend" required="false" type="java.lang.Boolean" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="breadcrumb-wrap hidden-xs">
    <div class="container">
        <ul class="breadcrumbs">
            <c:choose>
                <c:when test="${amend}">
                    <li class="col-xs-4 breadcrumb ${!(stage eq 'payment') && !(stage eq 'confirmation') ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.amend" text="Amend Selections"/>
						</span>
                    </li>
                    <li class="col-xs-4 breadcrumb ${stage eq 'payment' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.paymentrefund" text="Payment/Refund"/>
						</span>
                    </li>
                    <li class="col-xs-4 breadcrumb ${stage eq 'confirmation' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.confirmation" text="Confirmation"/>
						</span>
                    </li>
                </c:when>
                <c:otherwise>
                    <c:if test="${!(bookingJourney eq 'BOOKING_ACCOMMODATION_ONLY')}">
                        <li class="col-xs-4 breadcrumb ${stage eq 'flights' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.flights" text="Flight Search"/>
						</span>
                        </li>
                        <li class="col-xs-4 breadcrumb ${stage eq 'ancillary' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.ancillary" text="Ancillary"/>
						</span>
                        </li>
                    </c:if>
                    <c:if test="${!(bookingJourney eq 'BOOKING_TRANSPORT_ONLY')}">
                        <li class="col-xs-4 breadcrumb ${stage eq 'accommodation' ? 'active' : ''}">
                            <span><spring:theme code="text.booking.progress.bar.accommodation" text="Hotels"/></span>
                        </li>
                    </c:if>

                    <li class="col-xs-4 breadcrumb ${stage eq 'personalDetails' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.personaldetails" text="Personal Details"/>
						</span>
                    </li>
                    <li class="col-xs-4 breadcrumb ${stage eq 'payment' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.payment" text="Payment"/>
						</span>
                    </li>
                    <li class="col-xs-4 breadcrumb ${stage eq 'confirmation' ? 'active' : ''}">
						<span>
							<spring:theme code="text.booking.progress.bar.confirmation" text="Confirmation"/>
						</span>
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</div>
