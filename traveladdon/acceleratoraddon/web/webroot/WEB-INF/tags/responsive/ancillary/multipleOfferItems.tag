<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="offerGroupCode" required="true" type="java.lang.String"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>
<%@ attribute name="tabIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="travellerMinOfferGroupQty" required="true" type="java.lang.Integer"%>
<%@ attribute name="travellerMaxOfferGroupQty" required="true" type="java.lang.Integer"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:choose>
	<c:when test="${not empty traveller}">
		<c:choose>
			<c:when test="${not empty transportOfferings}">
				<c:forEach var="transportOffering" items="${transportOfferings}">
					<c:if test="${traveller.travellerStatusInfo[transportOffering.code] == 'CHECKED_IN'}">
						<c:set var="noAmend" value="true" />
					</c:if>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:forEach var="entry" items="${traveller.travellerStatusInfo}">
					<c:if test="${entry.value == 'CHECKED_IN'}">
						<c:set var="noAmend" value="true" />
					</c:if>
				</c:forEach>
			</c:otherwise>
		</c:choose>
		<div class="row input-row">
			<div class="col-xs-12">
				<c:set var="travellerCodeMap" value="${travellersNamesMap[traveller.travellerInfo.passengerType.code]}" />
				<label for="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}ancillary${fn:escapeXml(index+1)}">${fn:escapeXml(travellerCodeMap[traveller.label])}</label>
				<input type="hidden" class="y_ancillary_travellerCode" value="${fn:escapeXml(traveller.label)}">
			</div>
			<c:set var="selectedOffer" value="false" />
			<c:set var="selectedOfferNum" value="0" />
			<c:forEach var="offerPricingInfoDataOuterLoop" items="${offerPricingInfos}" varStatus="outerLoop">
				<c:forEach var="travellerBreakdownDataOuterLoop" items="${offerPricingInfoDataOuterLoop.travellerBreakdowns}">
					<c:if test="${travellerBreakdownDataOuterLoop.traveller.label == traveller.label}">
						<c:if test="${travellerBreakdownDataOuterLoop.quantity >= 1}">
							<c:set var="selectedOffer" value="true" />
							<c:set var="selectedOfferNum" value="${selectedOfferNum + 1}" />
							<div id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" class="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary y_ancillaryOfferItem form-inline full-width">
								<div class="col-xs-6 col-sm-6 form-group">
									<select class="form-control" name="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" ${fn:escapeXml(noAmend ? 'disabled' : '' )}>
										<option value="">
											<spring:theme code="text.ancillary.offers.select.item" text="Select Item" />
										</option>
										<c:forEach var="offerPricingInfoDataInnerLoop" items="${offerPricingInfos}">
											<c:forEach var="travellerBreakdownData" items="${offerPricingInfoDataInnerLoop.travellerBreakdowns}">
												<c:if test="${travellerBreakdownData.traveller.label eq travellerBreakdownDataOuterLoop.traveller.label}">
													<c:set var="travellerMinOfferQty" value="${offerPricingInfoDataInnerLoop.travelRestriction.travellerMinOfferQty > 1 ? offerPricingInfoDataInnerLoop.travelRestriction.travellerMinOfferQty : 1}" />
													<c:set var="travellerMaxOfferQty" value="${offerPricingInfoDataInnerLoop.travelRestriction.travellerMaxOfferQty != -1 ? offerPricingInfoDataInnerLoop.travelRestriction.travellerMaxOfferQty : ''}" />
													<c:set var="promoDesc" value="${promotionData.description}" />
													<c:forEach var="promotionData" items="${offerPricingInfoDataInnerLoop.product.potentialPromotions}">
														<c:set var="promoDesc" value="${promotionData.description}" />
													</c:forEach>
													<c:set var="contained" value="false" />
													<c:forEach var="type" items="${offerPricingInfoDataInnerLoop.travelRestriction.passengerTypes}">
														<c:if test="${type == travellerBreakdownData.traveller.travellerInfo.passengerType.code}">
															<c:set var="contained" value="true" />
														</c:if>
													</c:forEach>
													<c:if test="${empty offerPricingInfoDataInnerLoop.travelRestriction.passengerTypes || contained}">
														<c:choose>
															<c:when test="${offerPricingInfoDataOuterLoop.product.code eq offerPricingInfoDataInnerLoop.product.code}">
																<option class="y_OfferProductSelectionCode" value="${fn:escapeXml(offerPricingInfoDataInnerLoop.product.code)}" data-min="${fn:escapeXml(travellerMinOfferQty)}" data-max="${fn:escapeXml(travellerMaxOfferQty)}" selected disabled><c:out value="${offerPricingInfoDataInnerLoop.product.name}" /> &nbsp;
																	<c:out value="${travellerBreakdownData.passengerFare.baseFare.formattedValue}" /> &nbsp;
																	<c:out value="${promoDesc}" /></option>
																<c:set var="inputMax" value="${travellerMaxOfferQty}" />
																<c:set var="inputMin" value="${travellerMinOfferQty}" />
															</c:when>
															<c:otherwise>
																<option class="y_OfferProductSelectionCode" value="${fn:escapeXml(offerPricingInfoDataInnerLoop.product.code)}" data-min="${fn:escapeXml(travellerMinOfferQty)}" data-max="${fn:escapeXml(travellerMaxOfferQty)}"><c:out value="${offerPricingInfoDataInnerLoop.product.name}" /> &nbsp;
																	<c:out value="${travellerBreakdownData.passengerFare.baseFare.formattedValue}" /> &nbsp;
																	<c:out value="${promoDesc}" /></option>
															</c:otherwise>
														</c:choose>
													</c:if>
												</c:if>
											</c:forEach>
										</c:forEach>
									</select>
								</div>
								<div class="col-xs-4 col-sm-5 form-group">
									<%-- Adult Row --%>
									<div class="input-group">
										<span class="input-group-btn">
											<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" ${fn:escapeXml(travellerBreakdownDataOuterLoop.quantity == inputMin ? 'disabled="disabled"' : '')} data-type="minus" data-field="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}"
												style="${inputMin == inputMax || noAmend ? 'display:none' : ''}">
												<span class="glyphicon glyphicon-minus">
													<span class="sr-only">
														<spring:theme code="text.ancillary.offers.quantity.selector.remove" text="Remove one" />
													</span>
												</span>
											</button>
										</span>
										<label for="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}ancillary${fn:escapeXml(outerLoop.index+1)}-tab${fn:escapeXml(tabIndex+1)}Qty" class="sr-only">${fn:escapeXml(traveller.label)}</label>
										<input type="text" name="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(outerLoop.index+1)}ancillary-tab${fn:escapeXml(tabIndex+1)}Qty" class="form-control y_inputNumber" value="${fn:escapeXml(travellerBreakdownDataOuterLoop.quantity)}"
											data-defaultvalue="0" data-min="${fn:escapeXml(inputMin)}" data-max="${fn:escapeXml(inputMax)}" readonly>
										<span class="input-group-btn">
											<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" ${travellerBreakdownDataOuterLoop.quantity == inputMax ? 'disabled="disabled"' : ''} data-type="plus" data-field="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}"
												style="${inputMin == inputMax || noAmend ? 'display:none' : ''}">
												<span class="glyphicon glyphicon-plus">
													<span class="sr-only">
														<spring:theme code="text.ancillary.offers.quantity.selector.add" text="Add one" />
													</span>
												</span>
											</button>
										</span>
									</div>
									<%-- / Adult Row --%>
								</div>
								<div class="col-xs-1 col-sm-1">
									<a class="btn-delete btn y_ancillaryFormDeleteBlock" tabindex="0" data-target="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" style="${fn:escapeXml(noAmend ? 'display:none' : '')}">
										<span class="glyphicon glyphicon-remove"></span>
										<span class="sr-only">
											<spring:theme code="text.ancillary.offers.selector.remove" text="Remove" />
										</span>
									</a>
								</div>
							</div>
						</c:if>
					</c:if>
				</c:forEach>
			</c:forEach>
			<c:if test="${selectedOffer == 'false' || selectedOfferNum < travellerMinOfferGroupQty}">
				<c:forEach begin="${selectedOfferNum + 1}" end="${travellerMinOfferGroupQty != 0 ? travellerMinOfferGroupQty : 1}" var="idx">
					<div id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" class="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary y_ancillaryOfferItem form-inline full-width">
						<div class="col-xs-6 col-sm-6 form-group">
							<select class="form-control" name="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" ${noAmend ? 'disabled' : '' }>
								<option value="" disabled selected>
									<spring:theme code="text.ancillary.offers.select.item" text="Select Item" />
								</option>
								<c:set var="hasItem" value="false" />
								<c:forEach var="offerPricingInfoDataLoop" items="${offerPricingInfos}">
									<c:forEach var="travellerBreakdownData" items="${offerPricingInfoDataLoop.travellerBreakdowns}">
										<c:if test="${travellerBreakdownData.traveller.label == traveller.label}">
											<c:set var="travellerMinOfferQty" value="${offerPricingInfoDataLoop.travelRestriction.travellerMinOfferQty > 1 ? offerPricingInfoDataLoop.travelRestriction.travellerMinOfferQty : 1}" />
											<c:set var="travellerMaxOfferQty" value="${offerPricingInfoDataLoop.travelRestriction.travellerMaxOfferQty != -1 ? offerPricingInfoDataLoop.travelRestriction.travellerMaxOfferQty : ''}" />
											<c:set var="promoDesc" value="" />
											<c:forEach var="promotionData" items="${offerPricingInfoDataLoop.product.potentialPromotions}">
												<c:set var="promoDesc" value="${promotionData.description}" />
											</c:forEach>
											<c:set var="contained" value="false" />
											<c:forEach var="type" items="${offerPricingInfoDataLoop.travelRestriction.passengerTypes}">
												<c:if test="${type == travellerBreakdownData.traveller.travellerInfo.passengerType.code}">
													<c:set var="contained" value="true" />
												</c:if>
											</c:forEach>
											<c:if test="${empty offerPricingInfoDataLoop.travelRestriction.passengerTypes || contained}">
												<c:set var="hasItem" value="true" />
												<option class="y_OfferProductSelectionCode" value="${fn:escapeXml(offerPricingInfoDataLoop.product.code)}" data-min="${fn:escapeXml(travellerMinOfferQty)}" data-max="${fn:escapeXml(travellerMaxOfferQty)}"><c:out value="${offerPricingInfoDataLoop.product.name}" /> &nbsp;
													<c:out value="${travellerBreakdownData.passengerFare.baseFare.formattedValue}" /> &nbsp;
													<c:out value="${promoDesc}" /></option>
											</c:if>
										</c:if>
									</c:forEach>
								</c:forEach>
								<c:if test="${not hasItem}">
									<option class="y_noProductsAvailable" disabled>
										<spring:theme code="text.ancillary.offers.product.not.available" text="Not available for this passenger" />
									</option>
								</c:if>
							</select>
						</div>
						<div class="col-xs-4 col-sm-5 form-group">
							<%-- Adult Row --%>
							<div class="input-group">
								<span class="input-group-btn">
									<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" disabled="disabled" data-type="minus" data-field="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" style="display: none;">
										<span class="glyphicon glyphicon-minus">
											<span class="sr-only">
												<spring:theme code="text.ancillary.offers.quantity.selector.remove" text="Remove one" />
											</span>
										</span>
									</button>
								</span>
								<label for="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}ancillary${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}Qty" class="sr-only">${fn:escapeXml(traveller.label)}</label>
								<input type="text" name="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}ancillary-tab${fn:escapeXml(tabIndex+1)}Qty" class="form-control y_inputNumber" value="0" data-defaultvalue="0" data-min="0" data-max="9" readonly>
								<span class="input-group-btn">
									<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" disabled="disabled" data-type="plus" data-field="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" style="display: none;">
										<span class="glyphicon glyphicon-plus">
											<span class="sr-only">
												<spring:theme code="text.ancillary.offers.quantity.selector.add" text="Add one" />
											</span>
										</span>
									</button>
								</span>
							</div>
							<%-- / Adult Row --%>
						</div>
						<div class="col-xs-1 col-sm-1">
							<a class="btn-delete btn y_ancillaryFormDeleteBlock" tabindex="0" data-target="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" style="display: none;">
								<span class="glyphicon glyphicon-remove"></span>
								<span class="sr-only">
									<spring:theme code="text.ancillary.offers.selector.remove" text="Remove" />
								</span>
							</a>
						</div>
					</div>
				</c:forEach>
			</c:if>
			<div class="col-xs-12">
				<c:set var="offerPricingInfoDataLength" value="${fn:length(offerPricingInfos) }" />
				<c:set var="maxQty" value="${travellerMaxOfferGroupQty > offerPricingInfoDataLength ? travellerMaxOfferGroupQty : offerPricingInfoDataLength }" />
				<c:set var="minQty" value="${travellerMinOfferGroupQty > 0 ? travellerMinOfferGroupQty : 0 }" />
				<c:choose>
					<c:when test="${ selectedOfferNum >= minQty && selectedOfferNum > 0 && selectedOfferNum < maxQty && minQty != maxQty && !noAmend  && offerPricingInfoDataLength > 1}">
						<a class="link-trigger y_ancillaryFormAddBlock y_${fn:escapeXml(fn:toLowerCase(offerGroupCode))}ancillary pull-left" data-target="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary" tabindex="0">
							<spring:theme code="text.ancillary.offers.add.another.item" text="+ Add another item" />
						</a>
					</c:when>
					<c:otherwise>
						<a class="link-trigger y_ancillaryFormAddBlock y_${fn:escapeXml(fn:toLowerCase(offerGroupCode))}ancillary pull-left" data-target="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary" style="display: none;" tabindex="0">
							<spring:theme code="text.ancillary.offers.add.another.item" text="+ Add another item" />
						</a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<c:forEach var="travellerBreakdownData" items="${offerPricingInfoData.travellerBreakdowns}">
			<c:if test="${travellerBreakdownData.traveller.travellerStatusInfo[transportOffering.code] == 'CHECKED_IN'}">
				<c:set var="noAmend" value="${noAmend && true}" />
			</c:if>
		</c:forEach>
		<div class="row input-row">
			<c:set var="selectedOffer" value="false" />
			<c:set var="selectedOfferNum" value="0" />
			<c:forEach var="offerPricingInfoDataOuterLoop" items="${offerPricingInfos}" varStatus="outerLoop">
				<c:if test="${offerPricingInfoDataOuterLoop.bookingBreakdown.quantity >= 1}">
					<c:set var="selectedOffer" value="true" />
					<c:set var="selectedOfferNum" value="${selectedOfferNum + 1}" />
					<div id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" class="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary y_ancillaryOfferItem form-inline full-width no-traveller-label">
						<div class="col-xs-6 col-sm-6 form-group">
							<select class="form-control" name="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" ${fn:escapeXml(noAmend ? 'disabled' : '' )}>
								<option value="">
									<spring:theme code="text.ancillary.offers.select.item" text="Select Item" />
								</option>
								<c:forEach var="offerPricingInfoDataInnerLoop" items="${offerPricingInfos}">
									<c:set var="travellerMinOfferQty" value="${offerPricingInfoDataInnerLoop.travelRestriction.travellerMinOfferQty > 1 ? offerPricingInfoDataInnerLoop.travelRestriction.travellerMinOfferQty : 1}" />
									<c:set var="travellerMaxOfferQty" value="${offerPricingInfoDataInnerLoop.travelRestriction.travellerMaxOfferQty != -1 ? offerPricingInfoDataInnerLoop.travelRestriction.travellerMaxOfferQty : ''}" />
									<c:set var="promoDesc" value="${promotionData.description}" />
									<c:forEach var="promotionData" items="${offerPricingInfoDataInnerLoop.product.potentialPromotions}">
										<c:set var="promoDesc" value="${promotionData.description}" />
									</c:forEach>
									<c:choose>
										<c:when test="${offerPricingInfoDataOuterLoop.product.code eq offerPricingInfoDataInnerLoop.product.code}">
											<option class="y_OfferProductSelectionCode" value="${fn:escapeXml(offerPricingInfoDataInnerLoop.product.code)}" data-min="${fn:escapeXml(travellerMinOfferQty)}" data-max="${fn:escapeXml(travellerMaxOfferQty)}" selected disabled>
												<c:out value="${offerPricingInfoDataInnerLoop.product.name}" /> &nbsp;
												<c:out value="${offerPricingInfoDataInnerLoop.bookingBreakdown.passengerFare.baseFare.formattedValue}" /> &nbsp;
												<c:out value="${promoDesc}" />
											</option>
											<c:set var="inputMax" value="${travellerMaxOfferQty}" />
											<c:set var="inputMin" value="${travellerMinOfferQty}" />
										</c:when>
										<c:otherwise>
											<option class="y_OfferProductSelectionCode" value="${fn:escapeXml(offerPricingInfoDataInnerLoop.product.code)}" data-min="${fn:escapeXml(travellerMinOfferQty)}" data-max="${fn:escapeXml(travellerMaxOfferQty)}">
												<c:out value="${offerPricingInfoDataInnerLoop.product.name}" /> &nbsp;
												<c:out value="${offerPricingInfoDataInnerLoop.bookingBreakdown.passengerFare.baseFare.formattedValue}" /> &nbsp;
												<c:out value="${promoDesc}" />
											</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</div>
						<div class="col-xs-4 col-sm-5 form-group">
							<div class="input-group">
								<span class="input-group-btn">
									<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" ${fn:escapeXml(offerPricingInfoDataInnerLoop.bookingBreakdown.quantity == inputMin ? 'disabled="disabled"' : '')} data-type="minus"
										data-field="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" style="${fn:escapeXml(inputMin == inputMax || noAmend ? 'display:none' : '')}">
										<span class="glyphicon glyphicon-minus">
											<span class="sr-only">
												<spring:theme code="text.ancillary.offers.quantity.selector.remove" text="Remove one" />
											</span>
										</span>
									</button>
								</span>
								<input type="text" name="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(outerLoop.index+1)}ancillary-tab${fn:escapeXml(tabIndex+1)}Qty" class="form-control y_inputNumber"
									value="${fn:escapeXml(offerPricingInfoDataOuterLoop.bookingBreakdown.quantity)}" data-defaultvalue="0" data-min="${fn:escapeXml(inputMin)}" data-max="${fn:escapeXml(inputMax)}" readonly>
								<span class="input-group-btn">
									<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" ${offerPricingInfoDataOuterLoop.bookingBreakdown.quantity == inputMax ? 'disabled="disabled"' : ''} data-type="plus"
										data-field="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" style="${fn:escapeXml(inputMin == inputMax || noAmend ? 'display:none' : '')}">
										<span class="glyphicon glyphicon-plus">
											<span class="sr-only">
												<spring:theme code="text.ancillary.offers.quantity.selector.add" text="Add one" />
											</span>
										</span>
									</button>
								</span>
							</div>
						</div>
						<div class="col-xs-1 col-sm-1">
							<a class="btn-delete btn y_ancillaryFormDeleteBlock" tabindex="0" data-target="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(outerLoop.index+1)}" style="${noAmend ? 'display:none' : ''}">
								<span class="glyphicon glyphicon-remove"></span>
								<span class="sr-only">
									<spring:theme code="text.ancillary.offers.selector.remove" text="Remove" />
								</span>
							</a>
						</div>
					</div>
				</c:if>
			</c:forEach>
			<c:if test="${selectedOffer == 'false' || selectedOfferNum < travellerMinOfferGroupQty}">
				<c:forEach begin="${selectedOfferNum + 1}" end="${travellerMinOfferGroupQty != 0 ? travellerMinOfferGroupQty : 1}" var="idx">
					<div id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" class="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary y_ancillaryOfferItem form-inline full-width no-traveller-label">
						<div class="col-xs-6 col-sm-6 form-group">
							<select class="form-control" name="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" ${fn:escapeXml(noAmend ? 'disabled' : '' )}>
								<option value="" disabled selected>
									<spring:theme code="text.ancillary.offers.select.item" text="Select Item" />
								</option>
								<c:forEach var="offerPricingInfoDataLoop" items="${offerPricingInfos}">
									<c:set var="travellerMinOfferQty" value="${offerPricingInfoDataLoop.travelRestriction.travellerMinOfferQty > 1 ? offerPricingInfoDataLoop.travelRestriction.travellerMinOfferQty : 1}" />
									<c:set var="travellerMaxOfferQty" value="${offerPricingInfoDataLoop.travelRestriction.travellerMaxOfferQty != -1 ? offerPricingInfoDataLoop.travelRestriction.travellerMaxOfferQty : ''}" />
									<c:set var="promoDesc" value="" />
									<c:forEach var="promotionData" items="${offerPricingInfoDataLoop.product.potentialPromotions}">
										<c:set var="promoDesc" value="${promotionData.description}" />
									</c:forEach>
									<option class="y_OfferProductSelectionCode" value="${fn:escapeXml(offerPricingInfoDataLoop.product.code)}" data-min="${fn:escapeXml(travellerMinOfferQty)}" data-max="${fn:escapeXml(travellerMaxOfferQty)}">
										<c:out value="${offerPricingInfoDataLoop.product.name}" /> &nbsp;
										<c:out value="${offerPricingInfoDataLoop.bookingBreakdown.passengerFare.baseFare.formattedValue}" /> &nbsp;
										<c:out value="${promoDesc}" />
									</option>
								</c:forEach>
							</select>
						</div>
						<div class="col-xs-4 col-sm-5 form-group">
							<div class="input-group">
								<span class="input-group-btn">
									<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" disabled="disabled" data-type="minus" data-field="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" style="display: none;">
										<span class="glyphicon glyphicon-minus">
											<span class="sr-only">
												<spring:theme code="text.ancillary.offers.quantity.selector.remove" text="Remove one" />
											</span>
										</span>
									</button>
								</span>
								<input type="text" name="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}ancillary-tab${fn:escapeXml(tabIndex+1)}Qty" class="form-control y_inputNumber" value="0" data-defaultvalue="0" data-min="0" data-max="9" readonly>
								<span class="input-group-btn">
									<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" disabled="disabled" data-type="plus" data-field="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" style="display: none;">
										<span class="glyphicon glyphicon-plus">
											<span class="sr-only">
												<spring:theme code="text.ancillary.offers.quantity.selector.add" text="Add one" />
											</span>
										</span>
									</button>
								</span>
							</div>
						</div>
						<div class="col-xs-1 col-sm-1">
							<a class="btn-delete btn y_ancillaryFormDeleteBlock" tabindex="0" data-target="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}ancillary${fn:escapeXml(idx)}" style="display: none;">
								<span class="glyphicon glyphicon-remove"></span>
								<span class="sr-only">
									<spring:theme code="text.ancillary.offers.selector.remove" text="Remove" />
								</span>
							</a>
						</div>
					</div>
				</c:forEach>
			</c:if>
		</div>
	</c:otherwise>
</c:choose>
