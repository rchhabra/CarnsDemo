<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="y_reservationOverlayTotalsComponent">
    <input type="hidden" value="${fn:escapeXml(component.uid)}" id="y_reservationOverlayTotalsComponentId"/>
    <c:if test="${not empty reservationTotal}">
        <div class="panel panel-primary panel-list booking room-details clearfix">
            <div class="journey-wrapper">
                <div class="panel-default my-account-secondary-panel total">
                    <div class="panel-body">
                        <div class="col-xs-12">
                            <dl class="text-right">
                                <dt>
                                    <c:choose>
                                        <c:when test="${reservationTotal.value < 0 }">
                                            <spring:theme code="reservation.total.refund.label" text="Total to refund"/>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:theme code="reservation.total.pay.label" text="Total"/>
                                        </c:otherwise>
                                    </c:choose>
                                </dt>
                                <dd><format:price priceData="${reservationTotal}"/></dd>
                            </dl>
                            <form action="#">
                                <input id="y_reservationCode" type="hidden" value="${fn:escapeXml(reservationCode)}">
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>
