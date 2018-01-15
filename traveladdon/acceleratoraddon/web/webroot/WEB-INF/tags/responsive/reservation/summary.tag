<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="panel-heading">
    <h3 class="panel-title"><spring:theme code="reservation.summary.title"/></h3>
</div>
<div class="panel-body collapse in" id="summary-section">
    <div class="fieldset">
        <div class="responsive-table booking-table">
            <table class="table">
                <tbody>
                <c:forEach items="${ptcFareBreakdownSummary}" var="ptcFareBreakdown" varStatus="ptcIdx">
                    <tr class="responsive-table-item">
                        <td class="responsive-table-cell">${fn:escapeXml(ptcFareBreakdown.passengerTypeQuantity.quantity)}&nbsp;x&nbsp;${fn:escapeXml(ptcFareBreakdown.passengerTypeQuantity.passengerType.name)}</td>
                        <td class="status">${fn:escapeXml(ptcFareBreakdown.passengerFare.baseFare.formattedValue)}</td>
                    </tr>
                </c:forEach>
                <c:if test="${not empty extrasSummary && extrasSummary.value > 0}">
                    <tr class="responsive-table-item">
                        <td class="responsive-table-cell"><spring:theme code="reservation.extras"/></td>
                        <td class="status">${fn:escapeXml(extrasSummary.formattedValue)}</td>
                    </tr>
                </c:if>
                <c:if test="${not empty taxesFeesSummary && taxesFeesSummary.value > 0}">
                    <tr class="responsive-table-item">
                        <td class="responsive-table-cell"><spring:theme code="reservation.summary.taxesfees"/></td>
                        <td class="status">${fn:escapeXml(taxesFeesSummary.formattedValue)}</td>
                    </tr>
                </c:if>
                <c:if test="${not empty discountsSummary && discountsSummary.value > 0}">
                    <tr class="responsive-table-item">
                        <td class="responsive-table-cell"><spring:theme code="reservation.summary.discounts"/></td>
                        <td class="status">-${fn:escapeXml(discountsSummary.formattedValue)}</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
        <div>
            <span class="cart-total pull-right"><spring:theme
                    code="reservation.total"/>:&nbsp;${fn:escapeXml(reservation.totalFare.totalPrice.formattedValue)}</span>
        </div>
    </div>
</div>
