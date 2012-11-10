package org.unidal.web.mvc.lifecycle;

import static com.site.lookup.util.ReflectUtils.createInstance;
import static com.site.lookup.util.ReflectUtils.invokeMethod;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.PayloadProvider;
import org.unidal.web.mvc.Validator;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.payload.DefaultPayloadProvider;
import org.unidal.web.mvc.payload.annotation.PayloadProviderMeta;

import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.site.lookup.ContainerHolder;
import com.site.lookup.annotation.Inject;
import com.site.lookup.util.ReflectUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultInboundActionHandler extends ContainerHolder implements InboundActionHandler, LogEnabled {
	@Inject
	private MessageProducer m_cat;

	private InboundActionModel m_inboundAction;

	private Class<?> m_payloadClass;

	private PayloadProvider m_payloadProvider;

	private List<Validator<ActionContext<?>>> m_validators;

	private Logger m_logger;

	public void handle(ActionContext ctx) throws ActionException {
		Transaction t = m_cat.newTransaction("MVC", "InboundPhase");

		try {
			if (m_payloadClass != null) {
				RequestContext requestContext = ctx.getRequestContext();
				ActionPayload payload = createInstance(m_payloadClass);

				payload.setPage(requestContext.getAction());
				m_payloadProvider.process(requestContext.getUrlMapping(), requestContext.getParameterProvider(), payload);
				payload.validate(ctx);
				ctx.setPayload(payload);
			}

			for (Validator<ActionContext<?>> validator : m_validators) {
				validator.validate(ctx);
			}

			invokeMethod(m_inboundAction.getActionMethod(), m_inboundAction.getModuleInstance(), ctx);
			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			String actionName = m_inboundAction.getActionName();

			m_cat.logError(e);
			t.setStatus(e);
			throw new ActionException("Error occured during handling inbound action(" + actionName + ")!", e);
		} finally {
			t.complete();
		}
	}

	private PayloadProvider createPayloadProviderInstance(Class<? extends PayloadProvider<?, ?>> payloadProviderClass) {
		if (hasComponent(payloadProviderClass)) {
			return lookup(payloadProviderClass);
		} else {
			// create a POJO instance with default constructor
			return ReflectUtils.createInstance(payloadProviderClass);
		}
	}

	public void initialize(InboundActionModel inboundAction) {
		m_inboundAction = inboundAction;
		m_payloadClass = inboundAction.getPayloadClass();

		if (m_payloadClass != null) {
			PayloadProviderMeta providerMeta = m_payloadClass.getAnnotation(PayloadProviderMeta.class);

			if (providerMeta == null) {
				m_payloadProvider = createPayloadProviderInstance(DefaultPayloadProvider.class);
			} else {
				m_payloadProvider = createPayloadProviderInstance(providerMeta.value());
			}

			m_payloadProvider.register(m_payloadClass);
		}

		m_validators = new ArrayList<Validator<ActionContext<?>>>();

		for (Class<?> validatorClass : inboundAction.getValidationClasses()) {
			Validator<ActionContext<?>> validator = createInstance(validatorClass);

			m_validators.add(validator);
		}

		m_logger.debug(getClass().getSimpleName() + " initialized for  " + inboundAction.getActionName());
	}

	public void enableLogging(Logger logger) {
		m_logger = logger;
	}
}
