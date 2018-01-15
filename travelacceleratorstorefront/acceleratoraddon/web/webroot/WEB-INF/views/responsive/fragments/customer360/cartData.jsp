<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="cart" value="${fragmentData}"  scope="request"/>
<c:choose>
  <c:when test="${ empty cart or empty cart.entries}">
		<div class="asm__customer360-subheadline">
        	<spring:message code="text.customer360.overview.customer.cart" text="Active Cart"/>
   		 </div>
   		 <div class="asm__customer360-overview-saved-section">
	        <div class="row">
	        	<div class="col-xs-12">
                    <div class="asm__customer360-overview-divider">
                        <spring:theme
                             code="text.customer360.overview.customer.cart.no"
                             text="There are currently no Active Cart items" />
                     </div>
	             </div>
	        </div>
    	</div>
    </c:when>
	<c:otherwise>
	
		<div class="asm__customer360-headline">
	        <spring:message code="text.customer360.overview.customer.cart" text="Cart"/>&nbsp;<a href="#" class="y_asmSeeFullReservation">${fn:escapeXml(cart.code)}</a>
	        <span class="asm__customer360-overview-saved-card-info"><spring:message code="text.customer360.overview.customer.cart.total.items" text="Total No. Items" />&nbsp;${fn:escapeXml(cart.totalUnitCount)}<span class="asm__customer360-overview-saved-card-divider"></span><spring:message code="text.customer360.overview.customer.cart.total.price" text="Total Price" />&nbsp;${fn:escapeXml(cart.totalPrice.formattedValue)}</span>
	    </div>
	
	    <div class="asm__customer360-overview-saved-section">
	        <div class="row asm__customer360-overview-saved-wrap">
	            <c:forEach var="entry" items="${cart.entries}">
	                <div class="col-sm-6 col-md-4 asm__customer360-overview-saved-box">
	                    <div class="asm__customer360-overview-saved">
	                        <div class="asm__customer360-overview-product-img">
	                            <product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
	                        </div>
	                        <div class="asm__customer360-overview-saved-text-wrap">
                                <div class="asm__customer360-overview-product-name">
                                        <div class="hide_overflow">${fn:escapeXml(entry.product.name)}</div>
                                </div>
                                <div class="asm__customer360-overview-product-sku">${fn:escapeXml(entry.product.code)}</div>
                                <div class="asm__customer360-overview-product-price">
                                	<c:choose>
	                                	<c:when test="${entry.quantity > 1}">
	                                        <span class="asm__customer360-overview-qty-divider">
	                                            <spring:message code="text.customer360.overview.customer.cart.items.price" text="Item price" />:&nbsp;${fn:escapeXml(entry.basePrice.formattedValue)}
	                                        </span>
	                                        <spring:message code="text.customer360.overview.customer.cart.item.qty" text="QTY" />:&nbsp;${fn:escapeXml(entry.quantity)}
	                                        <div class="asm__customer360-overview-saved-total">
	                                        	<spring:message code="text.customer360.overview.customer.cart.total.price" text="Total Price" />:&nbsp;${fn:escapeXml(entry.totalPrice.formattedValue)}
	                                   		 </div>
	                                    </c:when>
	                                    <c:otherwise>
	                                    	<div class="asm__customer360-overview-saved-total">
	                                            <spring:message code="text.customer360.overview.customer.cart.items.price" text="Item price" />:&nbsp;${fn:escapeXml(entry.basePrice.formattedValue)}
	                                        </div>
	                                    </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
	                    </div>
	                </div>
	            </c:forEach>
	        </div>
	    </div>
   </c:otherwise>
</c:choose>
