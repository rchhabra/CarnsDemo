<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="modal fade reservation-modal" id="y_fullReservationModal" tabindex="-1" role="dialog" aria-labelledby="y_fullReservationModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h3 class="modal-title" id="fullReservationModal">
                    <spring:theme code="reservation.full.modal.header"/>
                </h3>
            </div>
            <div class="container reservation-booking-reference">
                <div class="row">
                    <div class="col-xs-12 col-sm-6 col-lg-8">
                        <dl class="booking-number">
                            <dt><spring:theme code="reservation.orderreference"/> <span id="y_targetReservationCode">${fn:escapeXml(reservationCode)}</span></dt>
                        </dl>
                    </div>
                </div>
            </div>
            <div class="modal-body">
                <div class="panel panel-primary panel-list">
                    <cms:pageSlot position="ReservationOverlay" var="feature" element="div">
                        <cms:component component="${feature}"/>
                    </cms:pageSlot>
                </div>
            </div>
        </div>
    </div>
</div>
