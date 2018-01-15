package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelrulesengine.constants.TravelrulesengineConstants;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates the user information in Offer Request Rao
 */
public class OfferRequestRaoUserRaoPopulator implements Populator<OfferRequestData, OfferRequestRAO>
{
	private Converter<UserModel, UserRAO> userConverter;
	private SessionService sessionService;

	@Override
	public void populate(final OfferRequestData source, final OfferRequestRAO target) throws ConversionException
	{
		if (Objects.nonNull(getSessionService().getAttribute(TravelrulesengineConstants.USER)))
		{
			target.setUser(getUserConverter().convert(getSessionService().getAttribute(TravelrulesengineConstants.USER)));
		}
	}

	/**
	 * @return the userConverter
	 */
	protected Converter<UserModel, UserRAO> getUserConverter()
	{
		return userConverter;
	}

	/**
	 * @param userConverter
	 *           the userConverter to set
	 */
	@Required
	public void setUserConverter(final Converter<UserModel, UserRAO> userConverter)
	{
		this.userConverter = userConverter;
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
