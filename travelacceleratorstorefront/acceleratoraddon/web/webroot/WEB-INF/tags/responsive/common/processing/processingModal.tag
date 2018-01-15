<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="visible" required="true" type="java.lang.Boolean"%>

<%-- Processing Modal --%>
<div class="processing-modal">
    <div class="modal ${visible ? 'show' : ''}" id="y_processingModal" tabindex="-1" aria-labelledby="y_processingModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-body">
                </div>
            </div>
        </div>
    </div>
</div>
