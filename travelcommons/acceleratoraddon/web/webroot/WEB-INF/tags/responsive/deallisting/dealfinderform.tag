<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="finderFormTitleCode" required="true" type="java.lang.String"%>
<%@ attribute name="finderFormTitleText" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="modal fade more-packages-modal" id="more-packages-modal" role="dialog" aria-labelledby="more-packages-modal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header clearfix">
				<h3>
					<spring:message code="${finderFormTitleCode}" text="${fn:escapeXml(finderFormTitleText)}" />
				</h3>
				<button type="button" class="modal-only close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body" style="padding: 0;">
				<div class="container travelforms">
					<div class="row" id="y_finderContainer">
						<cms:pageSlot position="LeftContent" var="feature" element="div">
							<cms:component component="${feature}" />
						</cms:pageSlot>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
