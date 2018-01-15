<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="reservation" required="true"
              type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="panel-heading">
    <h3 class="panel-title"><spring:theme code="reservation.global.extras.title" text="Additional Extras"/></h3>
</div>
<div class="panel-body collapse in" id="extras-section">
    <div class="fieldset">
        <div class="responsive-table booking-table">
            <table class="table">
                <tbody>
                    <c:forEach items="${reservation.offerBreakdowns}" var="offerBreakdown" varStatus="idx">
                        <tr class="responsive-table-item">
                            <c:if test="${offerBreakdown.product.productType != 'ACCOMMODATION' || offerBreakdown.totalFare.basePrice.value.intValue() != 0 }">
                                <td class="responsive-table-cell">${fn:escapeXml(offerBreakdown.quantity)}&nbsp;x&nbsp;${fn:escapeXml(offerBreakdown.product.name)}</td>
                                <td class="status">
                                    <c:choose>
                                        <c:when test="${offerBreakdown.included}">
                                            <spring:theme code="reservation.extras.included"/>
                                        </c:when>
                                        <c:otherwise>
                                            ${fn:escapeXml(offerBreakdown.totalFare.totalPrice.formattedValue)}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div>
            <span class="cart-total pull-right"> <spring:theme code="reservation.total"/>:&nbsp;${fn:escapeXml(reservation.totalFare.extrasPrice.formattedValue)}</span>
        </div>
    </div>
</div>
