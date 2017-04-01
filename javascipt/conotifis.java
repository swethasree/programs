package com.opentext.infofusion.is.spi.connectors.onedrive.rest.impl;

import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.opentext.infofusion.is.spi.connectors.onedrive.rest.typeDef.TypeManagerImpl;
import com.opentext.infofusion.is.spi.connectors.onedrive.rest.utils.AuthorizationCodeUtils;
import com.opentext.infofusion.is.spi.connectors.onedrive.rest.utils.ConfigurationSpecsUtils;
import com.opentext.infofusion.is.spi.connectors.onedrive.rest.utils.Constants;
import com.opentext.infofusion.is.spi.connectors.onedrive.rest.utils.Parameter;
import com.opentext.infofusion.is.spi.v2.ConfigurationSpecs;
import com.opentext.infofusion.is.spi.v2.Connection;
import com.opentext.infofusion.is.spi.v2.ConnectionStatus;
import com.opentext.infofusion.is.spi.v2.ConnectionStatusCode;
import com.opentext.infofusion.is.spi.v2.ParameterSpec;
import com.opentext.infofusion.is.spi.v2.PlainCredential;

/**
 * The Class ConnectionImpl.
 */
public class ConnectionImpl implements Connection {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionImpl.class);

	/** The type manager Type manager Implementation */
	private TypeManagerImpl typeManagerImpl;

	private final PlainCredential credentials;
	private final Map<String, String> params;

	private String clientID;
	private String clientSecret;
	private String redirectUri;
	private String tenantId;
	private String tenant;
	private RESTClient restClient;

	private long startTime;
	private long sessionStartTime;
	private long sleepTimeToAvoidTooManyRequests;

	private AuthenticationResult graphAuthentication;
	private AuthenticationResult oneDriveApiAuthentication;

	public ConnectionImpl(final PlainCredential credentials, final Map<String, String> params) {
		this.credentials = credentials;
		this.params = params;

		validateInput();

		this.sleepTimeToAvoidTooManyRequests = Long
				.valueOf(readParameter(Parameter.OD_SLEEP_TIME_TO_AVOID_TOO_MANY_REQUESTS));

		this.restClient = new RESTClient(credentials, false, sleepTimeToAvoidTooManyRequests);
		this.clientID = params.get(Parameter.CLIENT_ID);
		this.clientSecret = params.get(Parameter.CLIENT_SECRET);
		this.redirectUri = params.get(Parameter.REDIRECT_URI);
		this.tenantId = params.get(Parameter.TENANT_ID);
		this.tenant = params.get(Parameter.TENANT);

		this.sessionStartTime = (java.util.Calendar.getInstance().getTimeInMillis() / 1000);

		prepareConnection();
	}

	/**
	 * Prepares the connection
	 */
	private void prepareConnection() {
		// creating connection

		// to get authCode
		AuthorizationCodeUtils authorizationCode = this.restClient.populateAuthorizationCode(this.graphAuthentication,
				this.clientID, this.redirectUri, this.tenantId);

		this.graphAuthentication = this.restClient.populateAuthentication(this.tenant, this.tenantId, this.clientID,
				this.clientSecret, this.redirectUri, authorizationCode, Constants.GRAPH_API_SERVICE_RESOURCE);

		this.startTime = (java.util.Calendar.getInstance().getTimeInMillis() / 1000);

		this.oneDriveApiAuthentication = this.restClient.populateAuthenticationFromRefreshToken(this.tenantId,
				this.clientID, this.clientSecret, this.graphAuthentication.getRefreshToken(),
				Constants.ONE_DRIVE_API_SERVICE_RESOURCE.replace("tenant", this.tenant));

		// this.oneDriveApiAuthentication =
		// this.restClient.populateAuthentication(this.tenant, this.tenantId,
		// this.clientID, this.clientSecret, this.redirectUri,
		// authorizationCode,
		// Constants.ONE_DRIVE_API_SERVICE_RESOURCE);

		this.typeManagerImpl = new TypeManagerImpl();
		this.typeManagerImpl.initialize();
	}

	@Override
	public void close() {
		// logs out user from One Drive
		this.restClient.closeConnection(this.graphAuthentication, Constants.AUTHORIZATION_ENDPOINT, this.clientID,
				this.redirectUri);
		this.graphAuthentication = null;
	}

	@Override
	/**
	 * Tests the connection status
	 */
	public ConnectionStatus test() {

		if (this.restClient == null && this.graphAuthentication == null) {
			return new ConnectionStatus(ConnectionStatusCode.DISCONNECTED, "Authentication failed. Restart Service");
		} else {
			// get current system time
			long currentTime = (java.util.Calendar.getInstance().getTimeInMillis() / 1000);
			long totalTime = currentTime - this.startTime;
			long sessionTime = currentTime - this.sessionStartTime;

			// Check for 90 days from authorization code requested time and do a
			// new Authentication code flow
			// And also check for token expiration time and request new
			// Access token using refresh token
			if (sessionTime >= 7776000) {
				prepareConnection();
			} else if (totalTime >= (this.graphAuthentication.getExpiresOn() - 60)) {
				this.graphAuthentication = this.restClient.populateAuthenticationFromRefreshToken(this.tenantId,
						this.clientID, this.clientSecret, this.graphAuthentication.getRefreshToken(),
						Constants.GRAPH_API_SERVICE_RESOURCE);
				this.startTime = (java.util.Calendar.getInstance().getTimeInMillis() / 1000);
				this.oneDriveApiAuthentication = this.restClient.populateAuthenticationFromRefreshToken(this.tenantId,
						this.clientID, this.clientSecret, this.oneDriveApiAuthentication.getRefreshToken(),
						Constants.ONE_DRIVE_API_SERVICE_RESOURCE.replace("tenant", tenant));
			}

			return new ConnectionStatus(ConnectionStatusCode.CONNECTED, "connected");
		}
	}

	public TypeManagerImpl getTypeManagerImpl() {
		return this.typeManagerImpl;
	}

	public PlainCredential getCredentials() {
		return this.credentials;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public RESTClient getRESTClient() {
		return this.restClient;
	}

	public AuthenticationResult getAuthentication() {
		return this.graphAuthentication;
	}

	public AuthenticationResult getOneDriveApiAuthentication() {
		return this.oneDriveApiAuthentication;
	}

	private void validateInput() {
		if (StringUtils.isBlank(this.credentials.getUsername())) {
			final String message = "Parameter 'username' cannot be undefined or null.";
			LOG.error(message);
			throw new CmisInvalidArgumentException(message);
		}

		if (StringUtils.isBlank(this.credentials.getPassword())) {
			final String message = "Parameter 'password' cannot be undefined or null.";
			LOG.error(message);
			throw new CmisInvalidArgumentException(message);
		}

		if (StringUtils.isBlank(this.params.get(Parameter.CLIENT_ID))) {
			final String message = "Parameter 'CLIENT_ID' cannot be undefined or null.";
			LOG.error(message);
			throw new CmisInvalidArgumentException(message);
		}

		if (StringUtils.isBlank(this.params.get(Parameter.CLIENT_SECRET))) {
			final String message = "Parameter 'CLIENT_SECRET' cannot be undefined or null.";
			LOG.error(message);
			throw new CmisInvalidArgumentException(message);
		}

		if (StringUtils.isBlank(this.params.get(Parameter.TENANT_ID))) {
			final String message = "Parameter 'TENANT_ID' cannot be undefined or null.";
			LOG.error(message);
			throw new CmisInvalidArgumentException(message);
		}

		if (StringUtils.isBlank(this.params.get(Parameter.REDIRECT_URI))) {
			final String message = "Parameter 'REDIRECT_URI' cannot be undefined or null.";
			LOG.error(message);
			throw new CmisInvalidArgumentException(message);
		}

		if (StringUtils.isBlank(this.params.get(Parameter.TENANT))) {
			final String message = "Parameter 'TENANT' cannot be undefined or null.";
			LOG.error(message);
			throw new CmisInvalidArgumentException(message);
		}

	}

	private String readParameter(String paramName) {
		String paramValue = this.params.get(paramName);
		if (paramValue == null) {
			ConfigurationSpecs paramSpec = ConfigurationSpecsUtils.getConfigurationSpecs();
			List<ParameterSpec> paramSpecs = paramSpec.getParameterSpecs();
			if (paramSpecs != null) {
				for (ParameterSpec currentParam : paramSpecs) {
					if (currentParam.getName().equals(paramName)) {
						paramValue = currentParam.getDefaultValue();
						this.params.put(paramName, paramValue);
						return paramValue;
					}
				}
			}
		}

		return paramValue;
	}

}
