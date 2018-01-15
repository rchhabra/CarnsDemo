/*
* [y] hybris Platform
*
* Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* Hybris ("Confidential Information"). You shall not disclose such
* Confidential Information and shall use it only in accordance with the
* terms of the license agreement you entered into with SAP Hybris.
*
*/

package de.hybris.platform.travelrulesengine.services.impl;

import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengineservices.enums.FactContextType;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.travelrulesengine.enums.RefundActionType;
import de.hybris.platform.travelrulesengine.model.BundleFilterResultModel;
import de.hybris.platform.travelrulesengine.model.BundleTemplateShowResultModel;
import de.hybris.platform.travelrulesengine.model.FareFilterResultModel;
import de.hybris.platform.travelrulesengine.model.ShowProductActionResultModel;
import de.hybris.platform.travelrulesengine.model.ShowProductCategoryActionResultModel;
import de.hybris.platform.travelrulesengine.model.TransportOfferingFilterResultModel;
import de.hybris.platform.travelrulesengine.rao.FeeRAO;
import de.hybris.platform.travelrulesengine.rao.RefundActionRAO;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Drools implementation for the rules service interface {@link TravelRulesService}
 */
public class DefaultTravelDroolsService extends AbstractTravelEngineService implements TravelRulesService
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelDroolsService.class);

	@Override
	public List<CartEntryModel> evaluateCart(final CartModel cart)
	{
		final RuleEvaluationResult result = evaluate(Stream.of(cart).collect(Collectors.toList()), FactContextType.CART);
		final Set<Object> facts = result.getFacts();
		LOG.debug(facts);
		final RuleEngineResultRAO resultRAO = result.getResult();
		if (resultRAO == null || CollectionUtils.isEmpty(resultRAO.getActions()))
		{
			LOG.debug("Fee not found");
			return Collections.emptyList();
		}
		final List<ItemModel> feeCartEntries = this.getRuleActionService().applyAllActions(resultRAO);
		LOG.debug("Fee entries:" + feeCartEntries);
		final List<CartEntryModel> cartEntries = new ArrayList<CartEntryModel>();
		feeCartEntries.forEach(feeEntry -> cartEntries.add((CartEntryModel) feeEntry));
		return cartEntries;
	}

	@Override
	public double getTotalFee()
	{
		double totalFee = 0;
		final RuleEvaluationResult result = evaluate(Stream.of(createCart()).collect(Collectors.toList()), FactContextType.FEE);
		final Set<Object> facts = result.getFacts();
		LOG.debug(facts);
		final RuleEngineResultRAO resultRAO = result.getResult();
		if (resultRAO == null || CollectionUtils.isEmpty(resultRAO.getActions()))
		{
			LOG.debug("Fee not found");
			return totalFee;
		}
		filterActions(resultRAO, FeeRAO.class);
		final List<ItemModel> productsList = this.getRuleActionService().applyAllActions(resultRAO);
		LOG.debug("Fee product:" + productsList);
		totalFee = resultRAO.getActions().stream().mapToDouble(ruleActionRao -> ((FeeRAO) ruleActionRao).getPrice().doubleValue())
				.sum();
		return totalFee;
	}

	@Override
	public RefundActionType getRefundAction(final OrderModel orderModel)
	{
		final RuleEvaluationResult result = evaluate(Stream.of(orderModel).collect(Collectors.toList()), FactContextType.REFUND);
		final Set<Object> facts = result.getFacts();
		LOG.debug(facts);
		final RuleEngineResultRAO resultRAO = result.getResult();
		if (resultRAO == null || CollectionUtils.isEmpty(resultRAO.getActions()))
		{
			return null;
		}
		filterActions(resultRAO, RefundActionRAO.class);
		this.getRuleActionService().applyAllActions(resultRAO);
		final RefundActionRAO refundActionRAO = (RefundActionRAO) resultRAO.getActions().iterator().next();
		return refundActionRAO.getRefundAction();
	}

	/**
	 * Method to filter actions.
	 *
	 * @param <T>
	 * 		the type parameter
	 * @param resultRAO
	 * 		the result rao
	 * @param type
	 * 		the type
	 */
	protected <T> void filterActions(final RuleEngineResultRAO resultRAO, final Class<T> type)
	{
		final Set<AbstractRuleActionRAO> actions = resultRAO.getActions();
		resultRAO.setActions(actions.stream().filter(type::isInstance).collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	@Override
	public List<FareProductData> filterFareProducts(final List<FareProductData> fareProducts,
			final FareSearchRequestData fareSearchRequest)
	{
		final List<FareFilterResultModel> filteringResults = new ArrayList<>();
		fareProducts.forEach(fareProduct ->
		{
			final RuleEvaluationResult result = this
					.evaluate(Arrays.asList(fareProduct, fareSearchRequest), FactContextType.FILTER_FARE);
			final RuleEngineResultRAO resultRAO = result.getResult();
			if (resultRAO != null && CollectionUtils.isNotEmpty(resultRAO.getActions()))
			{
				final List<ItemModel> fareFilterResults = this.getRuleActionService().applyAllActions(resultRAO);
				collectFareFilterResults(filteringResults, fareFilterResults);
			}
		});

		if (CollectionUtils.isEmpty(filteringResults))
		{
			return Collections.emptyList();
		}
		final List<FareProductData> excludedFareProducts = new ArrayList<>();

		filteringResults.forEach(filteringResult ->
		{
			if (!filteringResult.getValid())
			{
				final FareProductData excludedFareProduct = findFareProduct(fareProducts, filteringResult.getFareProductCode());
				if (excludedFareProduct != null)
				{
					final FareProductData excludedFareProductInList = findFareProduct(excludedFareProducts,
							excludedFareProduct.getCode());
					if (excludedFareProductInList == null)
					{
						excludedFareProducts.add(excludedFareProduct);
					}
				}
			}
		});

		return excludedFareProducts;
	}

	/**
	 * Returns the list of bundle templates that needs to be filtered from the search result
	 *
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @param currentUser
	 * 		the current user
	 * @return
	 * @deprecated since version 4.0
	 */
	@Deprecated
	@Override
	public List<String> filterBundles(final FareSearchRequestData fareSearchRequestData, final UserModel currentUser)
	{
		final List<BundleFilterResultModel> filteringResults = new ArrayList<>();

		final RuleEvaluationResult result = this
				.evaluate(Arrays.asList(fareSearchRequestData, currentUser), FactContextType.FILTER_BUNDLE);
		final RuleEngineResultRAO resultRAO = result.getResult();
		if (resultRAO != null && CollectionUtils.isNotEmpty(resultRAO.getActions()))
		{
			final List<ItemModel> filterResults = this.getRuleActionService().applyAllActions(resultRAO);
			collectBundleFilterResults(filteringResults, filterResults);
		}

		final List<String> excludedBundles = new ArrayList<>();

		if (CollectionUtils.isEmpty(filteringResults))
		{
			return Collections.emptyList();
		}

		filteringResults.forEach(filteringResult ->
		{
			if (!filteringResult.getValid() && (CollectionUtils.isEmpty(excludedBundles) || !excludedBundles
					.contains(filteringResult.getBundleType())))
			{
				excludedBundles.add(filteringResult.getBundleType());
			}
		});

		return excludedBundles;
	}

	@Override
	public List<String> showBundleTemplates(final FareSearchRequestData fareSearchRequestData)
	{
		final List<BundleTemplateShowResultModel> showingResults = new ArrayList<>();

		final RuleEvaluationResult result = this
				.evaluate(Collections.singletonList(fareSearchRequestData), FactContextType.SHOW_BUNDLE_TEMPLATE);
		final RuleEngineResultRAO resultRAO = result.getResult();
		if (resultRAO != null && CollectionUtils.isNotEmpty(resultRAO.getActions()))
		{
			final List<ItemModel> showResults = this.getRuleActionService().applyAllActions(resultRAO);
			collectBundleTemplateShowResult(showingResults, showResults);
		}

		final List<String> bundlesToInclude = new ArrayList<>();

		if (CollectionUtils.isEmpty(showingResults))
		{
			return Collections.emptyList();
		}

		showingResults.forEach(showingResult -> bundlesToInclude.addAll(showingResult.getBundleTemplates()));

		return bundlesToInclude;
	}

	@Override
	public List<String> showProducts(final OfferRequestData offerRequestData)
	{
		final List<ShowProductActionResultModel> showProductActionResults = new ArrayList<>();
		final RuleEvaluationResult result = this.evaluate(Collections.singletonList(offerRequestData),
				FactContextType.SHOW_PRODUCTS);
		final RuleEngineResultRAO resultRAO = result.getResult();
		if (resultRAO != null && CollectionUtils.isNotEmpty(resultRAO.getActions()))
		{
			final List<ItemModel> showResults = this.getRuleActionService().applyAllActions(resultRAO);
			collectShowProductsResult(showProductActionResults, showResults);
		}
		final List<String> productsToInclude = new ArrayList<>();

		if (CollectionUtils.isEmpty(showProductActionResults))
		{
			return Collections.emptyList();
		}

		showProductActionResults.forEach(productResult -> productsToInclude.addAll(productResult.getProducts()));

		return productsToInclude;
	}

	protected void collectShowProductsResult(final List<ShowProductActionResultModel> showProductActionResults,
			final List<ItemModel> showResults)
	{
		if (CollectionUtils.isEmpty(showResults))
		{
			return;
		}
		showResults.stream().filter(showProductActionResult -> showProductActionResult instanceof ShowProductActionResultModel)
				.forEach(showProductActionResult -> showProductActionResults
						.add((ShowProductActionResultModel) showProductActionResult));
	}

	@Override
	public List<String> showProductCategories(final OfferRequestData offerRequestData)
	{
		final List<ShowProductCategoryActionResultModel> showProductCategoriesActionResults = new ArrayList<>();
		final RuleEvaluationResult result = this.evaluate(Collections.singletonList(offerRequestData),
				FactContextType.SHOW_CATEGORIES);
		final RuleEngineResultRAO resultRAO = result.getResult();
		if (resultRAO != null && CollectionUtils.isNotEmpty(resultRAO.getActions()))
		{
			final List<ItemModel> showResults = this.getRuleActionService().applyAllActions(resultRAO);
			collectProductCategoriesResult(showProductCategoriesActionResults, showResults);
		}
		final List<String> categoriesToInclude = new ArrayList<>();

		if (CollectionUtils.isEmpty(showProductCategoriesActionResults))
		{
			return Collections.emptyList();
		}

		showProductCategoriesActionResults.forEach(productCategoryResult -> categoriesToInclude.addAll(productCategoryResult.getCategories()));
		return categoriesToInclude;
	}

	/**
	 * This method collects product categories show result
	 *
	 * @param showProductCategoriesActionResults
	 * @param showResults
	 */
	protected void collectProductCategoriesResult(
			final List<ShowProductCategoryActionResultModel> showProductCategoriesActionResults,
			final List<ItemModel> showResults)
	{
		if(CollectionUtils.isEmpty(showResults))
		{
			return;
		}
		showResults.stream().filter(showProductCategoryActionResult -> showProductCategoryActionResult instanceof ShowProductCategoryActionResultModel)
				.forEach(result -> showProductCategoriesActionResults
						.add((ShowProductCategoryActionResultModel)result));
	}

	/**
	 * Method to create a dummy cart. This is used to validate the admin fee rule which expects a cart by default.
	 *
	 * @return cart cart model
	 */
	protected CartModel createCart()
	{
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("GBP");
		final CartModel cart = new CartModel();
		cart.setCurrency(currency);
		return cart;
	}

	/**
	 * Finds a fare product from a list with a matching fare product code
	 *
	 * @param fareProducts
	 * 		the fare products
	 * @param fareProductCode
	 * 		the fare product code
	 * @return fare product matching given code
	 */
	protected FareProductData findFareProduct(final List<FareProductData> fareProducts, final String fareProductCode)
	{
		final Optional<FareProductData> filteredFareProduct = fareProducts.stream()
				.filter(fareProduct -> StringUtils.equalsIgnoreCase(fareProductCode, fareProduct.getCode())).findFirst();
		return filteredFareProduct.orElse(null);
	}

	/**
	 * Collects the result of fare filtering which then will be used to apply relevant actions
	 *
	 * @param filteringResults
	 * 		the filtering results
	 * @param fareFilterResults
	 * 		the fare filter results
	 */
	protected void collectFareFilterResults(final List<FareFilterResultModel> filteringResults,
			final List<ItemModel> fareFilterResults)
	{
		if (CollectionUtils.isEmpty(fareFilterResults))
		{
			return;
		}
		fareFilterResults.stream().filter(fareFilterResult -> fareFilterResult instanceof FareFilterResultModel)
				.forEach(fareFilterResult -> filteringResults.add((FareFilterResultModel) fareFilterResult));
	}

	/**
	 * Collects the result of bundle filtering which then will be used to apply relevant actions
	 *
	 * @param filteringResults
	 * 		the filtering results
	 * @param filterResults
	 * 		the filter resultsdata
	 * @deprecated since version 4.0 use {@link #collectBundleTemplateShowResult(List, List)} instead
	 */
	@Deprecated
	protected void collectBundleFilterResults(final List<BundleFilterResultModel> filteringResults,
			final List<ItemModel> filterResults)
	{
		if (CollectionUtils.isEmpty(filterResults))
		{
			return;
		}
		filterResults.stream().filter(bundleFilterResult -> bundleFilterResult instanceof BundleFilterResultModel)
				.forEach(bundleFilterResult -> filteringResults.add((BundleFilterResultModel) bundleFilterResult));
	}


	/**
	 * Collect bundle template show result.
	 *
	 * @param bundleTemplateShowResults
	 * 		the filtering results
	 * @param showResults
	 * 		the filter results
	 */
	protected void collectBundleTemplateShowResult(final List<BundleTemplateShowResultModel> bundleTemplateShowResults,
			final List<ItemModel> showResults)
	{
		if (CollectionUtils.isEmpty(showResults))
		{
			return;
		}
		showResults.stream().filter(bundleFilterResult -> bundleFilterResult instanceof BundleTemplateShowResultModel)
				.forEach(bundleFilterResult -> bundleTemplateShowResults.add((BundleTemplateShowResultModel) bundleFilterResult));
	}

	@Override
	public void filterTransportOfferings(final List<TransportOfferingData> transportOfferings,
			final FareSearchRequestData fareSearchRequest)
	{
		final ListIterator<TransportOfferingData> itr = transportOfferings.listIterator();
		while (itr.hasNext())
		{
			final TransportOfferingData transportOffering = itr.next();
			final RuleEvaluationResult result = this
					.evaluate(Arrays.asList(transportOffering, fareSearchRequest), FactContextType.FILTER_TRANSPORTOFFERING);
			final RuleEngineResultRAO resultRAO = result.getResult();

			if (resultRAO != null && CollectionUtils.isNotEmpty(resultRAO.getActions()))
			{
				final List<ItemModel> transportOfferingFilterResults = this.getRuleActionService().applyAllActions(resultRAO);
				transportOfferingFilterResults.stream().filter(
						transportOfferingFilterResult -> transportOfferingFilterResult instanceof TransportOfferingFilterResultModel)
						.forEach(transportOfferingFilterResult ->
						{
							if (StringUtils.equalsIgnoreCase(
									((TransportOfferingFilterResultModel) transportOfferingFilterResult).getTransportOfferingCode(),
									transportOffering.getCode()) && !((TransportOfferingFilterResultModel) transportOfferingFilterResult)
									.getValid())
							{
								itr.remove();
							}
						});
			}

		}
	}

}
