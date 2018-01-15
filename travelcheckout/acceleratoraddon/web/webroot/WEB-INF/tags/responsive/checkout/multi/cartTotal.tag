<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>

<c:if test="${not empty cartTotal}">
	<div class="panel panel-primary panel-list booking room-details clearfix">
		<div class="journey-wrapper">
			<div class="panel-default my-account-secondary-panel total">
				<div class="panel-body">
					<div class="col-xs-12">
						<dl class="text-right">
							<dt>
								<spring:theme code="text.cms.booking.total.title" text="Booking Total" />
							</dt>
							<dd><format:price priceData="${cartTotal}" /></dd>
						</dl>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>
