<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="modal fade" id="confirm-delete" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            	    <span aria-hidden="true">&times;</span>
            	</button>
            	<h3 class="modal-title" id="cancelBookingLabel">
                    <spring:theme code="account.bookings.unlink.modal.header" text="Remove booking" />
                </h3>
            </div>
            <div class="modal-body">
                <p>
                    <spring:theme code="account.bookings.unlink.modal.body" text="Do you want to remove this booking from your account?"/>
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                	<spring:theme code="account.bookings.unlink.modal.cancel" text="Cancel" />
                </button>
                <a class="btn btn-danger btn-ok">
                	<spring:theme code="account.bookings.unlink.modal.delete" text="Delete" />
                </a>
            </div>
        </div>
    </div>
</div>
