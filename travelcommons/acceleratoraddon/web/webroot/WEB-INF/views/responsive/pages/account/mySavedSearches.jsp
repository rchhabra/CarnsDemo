<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading">
	<h3 class="title title-collapse">
		<spring:theme code="text.account.mysavedsearch" />
	</h3>
	<button class="show-hide-button" data-toggle="collapse" data-target="#saved-search" aria-expanded="true" aria-controls="saved-search">
		<spring:theme code="text.account.mysavedsearch.button.collapseExpand" />
	</button>
</div>
<div class="panel-body collapse in" id="saved-search">
	<c:if test="${not empty error}">
		<div class="alert alert-info alert-dismissable">
			<spring:theme code="text.account.mysavedsearch.remove.error" />
		</div>
	</c:if>
	<c:choose>
		<c:when test="${not empty savedSearches}">
			<c:forEach var="savedSearch" items="${savedSearches}" varStatus="searchIdx">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:theme code="text.page.savedsearch.panel.label" arguments="${searchIdx.index+1}" />
						</h3>
					</div>
					<div class="panel-body ">
						<div class="summary">
							<div class="row">
								<div class="col-xs-12">
									<div class="col-xs-12 col-sm-6">
										<dl>
											<dt>
												<spring:theme code="text.page.savedsearch.triptype.label" />
											</dt>
											<dd>
												<spring:theme code="text.page.savedsearch.triptype.${savedSearch.tripType}" />
											</dd>
										</dl>
									</div>
									<div class="col-xs-12 col-sm-6">
										<dl>
											<dt>
												<spring:theme code="text.page.savedsearch.cabinclass.label" />
											</dt>
											<dd>${fn:escapeXml(savedSearch.cabinClass)}</dd>
										</dl>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-xs-12">
									<div class="col-xs-12 col-sm-6">
										<dl>
											<dt>
												<spring:theme code="text.page.savedsearch.departurelocationname.label" />
											</dt>
											<dd>${fn:escapeXml(savedSearch.departureLocationName)}</dd>
										</dl>
									</div>
									<div class="col-xs-12 col-sm-6">
										<dl>
											<dt>
												<spring:theme code="text.page.savedsearch.arrivallocationname.label" />
											</dt>
											<dd>${fn:escapeXml(savedSearch.arrivalLocationName)}</dd>
										</dl>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-xs-12">
									<div class="col-xs-12 col-sm-6">
										<dl>
											<dt>
												<spring:theme code="text.page.savedsearch.departingdatetime.label" />
											</dt>
											<dd>${fn:escapeXml(savedSearch.departingDateTime)}</dd>
										</dl>
									</div>
									<c:if test="${savedSearch.tripType=='RETURN'}">
										<div class="col-xs-12 col-sm-6">
											<dl>
												<dt>
													<spring:theme code="text.page.savedsearch.returndatetime.label" />
												</dt>
												<dd>${fn:escapeXml(savedSearch.returnDateTime)}</dd>
											</dl>
										</div>
									</c:if>
								</div>
							</div>
							<div class="row">
								<div class="col-xs-12">
									<div class="col-xs-12 col-sm-12 col-md-5">
										<dl>
											<dt>
												<spring:theme code="text.page.savedsearch.passengertype.header.label" />
											</dt>
											<c:forEach items="${savedSearch.passengerTypeQuantities}" var="passengerTypeQuantity" varStatus="ptqIdx">
												<dd>
													<spring:theme code="text.page.savedsearch.passengertype.details" arguments="${passengerTypeQuantity.quantity},${passengerTypeQuantity.passengerType.name}" />
												</dd>
											</c:forEach>
										</dl>
									</div>
									<c:url var="fareFinderUrl" value="/fare-selection">
										<c:param name="departureLocationName" value="${savedSearch.departureLocationName}" />
										<c:param name="returnDateTime" value="${savedSearch.returnDateTime}" />
										<c:param name="cabinClass" value="${savedSearch.cabinClass}" />
										<c:param name="tripType" value="${savedSearch.tripType}" />
										<c:param name="departureLocation" value="${savedSearch.departureLocation}" />
										<c:param name="departingDateTime" value="${savedSearch.departingDateTime}" />
										<c:param name="arrivalLocationName" value="${savedSearch.arrivalLocationName}" />
										<c:param name="departureLocationSuggestionType" value="${savedSearch.departureLocationSuggestionType}" />
										<c:param name="arrivalLocationSuggestionType" value="${savedSearch.arrivalLocationSuggestionType}" />
										<c:forEach items="${savedSearch.passengerTypeQuantities}" var="passengerTypeQuantity" varStatus="ptqIdx">
											<c:param name="${passengerTypeQuantity.passengerType.code}" value="${passengerTypeQuantity.quantity}" />
										</c:forEach>
										<c:param name="arrivalLocation" value="${savedSearch.arrivalLocation}" />
									</c:url>
									<c:url var="removeSaveSearchURL" value="/my-account/remove-saved-searches/${savedSearch.PK}" />
									<div class="form-group col-xs-6 col-sm-6 col-md-3 col-md-offset-1 bottom-align-parent">
										<a class="btn btn-primary btn-block bottom-align" href="${fareFinderUrl}">
											<spring:theme code="text.account.mysavedsearch.button.use" />
										</a>
									</div>
									<div class="form-group col-xs-6 col-sm-6 col-md-3 bottom-align-parent">
										<a class="btn btn-primary btn-block bottom-align" href="${removeSaveSearchURL}">
											<spring:theme code="text.account.mysavedsearch.button.remove" />
										</a>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div class="fieldset">
				<p>
					<spring:theme code="text.account.mysavedsearch.nosavedsearch" />
				</p>
			</div>
		</c:otherwise>
	</c:choose>
</div>
