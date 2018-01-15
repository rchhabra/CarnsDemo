<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="panel-heading">
	<h3 class="title"><spring:theme code="voucher.header.title" text="Vouchers"/></h3>
</div>

<div class="panel-body">
	<fieldset class="fieldset">
		<div class="panel-body collapse in" id="panel-voucher">
			<div id="y_vouchersDiv">
			  <c:choose>
				<c:when test="${appliedVoucherCodes ne null}">
				  <c:forEach var="voucherData" items="${appliedVoucherCodes}" varStatus="loop">
				  	<div class="row form-group y_appliedVoucherItem" id="y_appliedVoucherItem${loop.index}">
						<div class="col-xs-12 col-sm-3 col-md-3">
							<label><spring:theme code="voucher.applied.code" text="Applied voucher:"/></label>
						</div>
						<div class="col-xs-12 col-sm-4 col-md-4">
							<label>
								<span class="y_voucherCode">${fn:escapeXml(voucherData.voucherCode)}</span>
							</label>
						</div>
						<div class="col-xs-12 col-sm-5 col-md-5">
							<button type="button" data-code="${fn:escapeXml(voucherData.voucherCode)}" name="voucherReleaseBtn" class="btn btn-default btn-block y_voucherReleaseBtn" data-target="y_appliedVoucherItem${fn:escapeXml(loop.index)}">
								<spring:theme code="voucher.delete.name" text="Release voucher"/>
							</button>
						</div>
					</div>
				  </c:forEach>
				  </c:when>
				  <c:otherwise>
				  	<div class="row form-group y_appliedVoucherItem" id="y_appliedVoucherItem0" style="display:none;">
						<div class="col-xs-12 col-sm-3 col-md-3">
							<label><spring:theme code="voucher.applied.code" text="Applied voucher:"/></label>
						</div>
						<div class="col-xs-12 col-sm-4 col-md-4">
							<label>
								<span class="y_voucherCode"></span>
							</label>
						</div>
						<div class="col-xs-12 col-sm-5 col-md-5">
							<button type="button" data-code="" name="voucherReleaseBtn" class="btn btn-default btn-block y_voucherReleaseBtn" data-target="y_appliedVoucherItem0">
								<spring:theme code="voucher.delete.name" text="Release voucher"/>
							</button>
						</div>
					</div>
				  </c:otherwise>
				</c:choose>
			</div>
			<div class="row form-group">
				<div class="col-xs-12 col-sm-2 col-md-2">
					<label for="voucherCode">
						<spring:theme code="voucher.code.name" text="Voucher code:"/>
					</label>
				</div>
				<div class="form-group col-xs-12 col-sm-5 col-md-5">
					<input id="y_voucherCode" name="voucherCode" class="form-control" type="text" value="" />
				</div>
				<div class="col-xs-12 col-sm-5 col-md-5">
					<button type="button" name="voucherRedeemBtn" id="y_voucherRedeemBtn" class="btn btn-primary btn-block" data-target="y_appliedVoucherItem">
						<spring:theme code="voucher.add.name" text="Redeem voucher"/>
					</button>
				</div>
			</div>
			<div id="y_voucherMsgDiv" class="alert alert-dismissible" role="alert" style="display: none;">
				<button type="button" class="close" data-dismiss="alert" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<span id="y_voucherMsg"></span>
			</div>
		</div>
	</fieldset>
</div>
