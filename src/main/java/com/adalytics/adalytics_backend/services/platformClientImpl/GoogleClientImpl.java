package com.adalytics.adalytics_backend.services.platformClientImpl;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadGatewayException;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.external.ApiService;
import com.adalytics.adalytics_backend.models.entities.Campaign;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.externalDTOs.googleDTOs.GoogleUserInfoDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import com.adalytics.adalytics_backend.utils.JsonUtil;
import com.adalytics.adalytics_backend.utils.QueryHelper;
import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v17.errors.GoogleAdsException;
import com.google.ads.googleads.v17.resources.CustomerClient;
import com.google.ads.googleads.v17.services.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.oauth2.UserCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class GoogleClientImpl{

    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String clientSecret;
    @Value("${google.scope}")
    private String scope;
    @Value("${google.redirect.uri}")
    private String redirectUri;
    @Value("${google.token.server.utl}")
    private String tokenUrl;
    @Value("${google.developer.token}")
    private String developerToken;

    @Autowired
    private ApiService apiService;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    @Autowired
    private IConnectorRepository connectorRepository;
    @Autowired
    private QueryHelper queryHelper;

    public void refreshAccessToken(Connector connector){
        // Load client secrets
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setInstalled(
                new GoogleClientSecrets.Details()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singletonList(scope))
                .setAccessType("offline")
                .build();

        // Load the refresh token and use it to get a new access token
        GoogleTokenResponse tokenResponse = new GoogleTokenResponse();
        tokenResponse.setRefreshToken(connector.getRefreshToken());

        try {
            Credential credential = flow.createAndStoreCredential(tokenResponse, connector.getPlatformUserId());
            // Refresh the access token
            if (credential.refreshToken()) {
                connector.setToken(credential.getAccessToken());
                connector.setAccessTokenExpirationTime(System.currentTimeMillis() + credential.getExpirationTimeMilliseconds());
                connectorRepository.save(connector);
            } else {
                throw new RuntimeException("Failed to refresh access token");
            }
        } catch (Exception ex) {
            log.error("Failed to refresh Access Token", ex);
            throw new BadGatewayException("Failed to refresh Access Token", ErrorCodes.Client_Not_Responding.getErrorCode());
        }
        log.info("===========================Done========================");
    }

    public void exchangeAuthorizationCode(Connector connector, String authorizationCode) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    tokenUrl,
                    clientId,
                    clientSecret,
                    authorizationCode,
                    redirectUri
            ).setScopes(Collections.singletonList(scope))
                    .execute();

            connector.setRefreshToken(tokenResponse.getRefreshToken());
            connector.setToken(tokenResponse.getAccessToken());
            connector.setExpirationTime(Long.MAX_VALUE);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), ErrorCodes.Platform_Token_Invalid.getErrorCode());
        }
    }

    public Connector populateUserInfo(Connector connector) {
        String url = String.format("https://www.googleapis.com/oauth2/v3/userinfo?access_token=%s",connector.getToken());
        String response = apiService.callExternalApi(url, "GET", null, null);
        GoogleUserInfoDTO googleUserInfoDTO = JsonUtil.getObjectFromJsonString(response, GoogleUserInfoDTO.class);
        if (nonNull(googleUserInfoDTO.getError())) {
            throw new BadRequestException(googleUserInfoDTO.getError().getMessage(), ErrorCodes.Platform_Invalid.getErrorCode());
        }
        connector.setPlatformUserId(googleUserInfoDTO.getSub());
        connector.setEmail(googleUserInfoDTO.getEmail());
        Optional<Connector> isExistingConnector = connectorRepository.findByPlatformUserIdAndOrganizationId(connector.getPlatformUserId(), ContextUtil.getCurrentOrgId());
        if (isExistingConnector.isPresent()) {
            throw new BadRequestException("Connector is already present.", ErrorCodes.Connector_Already_Present.getErrorCode());
        }
        return connector;
    }

    public List<Campaign> fetchCampaigns(Connector connector){
        List<Campaign> campaignList = new ArrayList<>();
        if(isNull(connector)) {
            return new ArrayList<>();
        }
        System.out.println(connector);
        List<String> scopes = new ArrayList<>() {
            {
                add("https://www.googleapis.com/auth/adwords");
                add("openid");
                add("https://www.googleapis.com/auth/userinfo.profile");
                add("https://www.googleapis.com/auth/userinfo.email");
                add("https://adwords.google.com/api/adwords");
                add("https://adwords.google.com/api/adwords/");
                add("https://adwords.google.com/api/adwords/cm");
            }
        };

        UserCredentials userCredentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(connector.getRefreshToken())
                .build();

        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder()
                .setCredentials(userCredentials)
                .setDeveloperToken(developerToken)
                .build();

        try (CustomerServiceClient customerServiceClient = googleAdsClient.getLatestVersion().createCustomerServiceClient()) {
            ListAccessibleCustomersRequest request = ListAccessibleCustomersRequest.newBuilder().build();
            ListAccessibleCustomersResponse response = customerServiceClient.listAccessibleCustomers(request);
            for (String customer : response.getResourceNamesList()) {
                String[] parts = customer.split("/");
                GoogleAdsClient googleAdsClient1 = googleAdsClient.toBuilder().setLoginCustomerId(Long.parseLong(parts[1])).build();
                List<Long> customerIds = createCustomerClientIdsList(Long.parseLong(parts[1]), googleAdsClient1);
                for (Long customerId : customerIds) {
                    campaignList.addAll(getCampaigns(googleAdsClient1, customerId));
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return campaignList;
    }

    private List<Campaign> getCampaigns(GoogleAdsClient googleAdsClient, Long customerId) {
        List<Campaign> campaignList = new ArrayList<>();
        String query = queryHelper.getQuery("get_campaign");
        GoogleAdsServiceClient.SearchPagedResponse response = null;
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                    .setCustomerId(customerId.toString())
                    .setQuery(query)
                    .build();
            response = googleAdsServiceClient.search(request);

            response.iterateAll().forEach(row -> {
                com.google.ads.googleads.v17.resources.Campaign googleCampaign = row.getCampaign();
                com.google.ads.googleads.v17.common.Metrics googleMetrics = row.getMetrics();

                Campaign campaign = new Campaign();
                campaign.setId(String.valueOf(googleCampaign.getId()));
                campaign.setName(googleCampaign.getName());
                campaign.setStartDate(googleCampaign.getStartDate());
                campaign.setEndDate(googleCampaign.getEndDate());

                Campaign.Metric metric = new Campaign.Metric();
                metric.setClicks(BigDecimal.valueOf(googleMetrics.getClicks()));
                metric.setConversions(BigDecimal.valueOf(googleMetrics.getConversions()));
                metric.setEngagements(BigDecimal.valueOf(googleMetrics.getEngagements()));
                metric.setImpressions(BigDecimal.valueOf(googleMetrics.getImpressions()));
                metric.setInteractions(BigDecimal.valueOf(googleMetrics.getInteractions()));
                campaign.setMetric(metric);

                campaignList.add(campaign);
            });
        } catch (Exception ex) {
            log.info("Error while fetching campaign from google : ", ex);
        }

        //TODO In future will fetch data for AD_GROUPS
//        for (GoogleAdsRow row : response.iterateAll()) {
//            System.out.printf(row.toString());
//            getAdGroup(googleAdsClient, customerId);
//        }

        return campaignList;
    }

    private void getAdGroup(GoogleAdsClient googleAdsClient, Long customerId) {
        String query = queryHelper.getQuery("get_adGroup");

        GoogleAdsServiceClient.SearchPagedResponse response = null;
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                    .setCustomerId(customerId.toString())
                    .setQuery(query)
                    .build();
            response = googleAdsServiceClient.search(request);
        } catch (Exception e) {
            // ignore
        }
        for (GoogleAdsRow row : response.iterateAll()) {
            System.out.printf(row.toString());
        }
    }

    private List<Long> createCustomerClientIdsList(Long loginCustomerId, GoogleAdsClient googleAdsClient) {
        Queue<Long> managerAccountsToSearch = new LinkedList<>();
        List<Long> customerIdsList = new ArrayList<>();

        try (GoogleAdsServiceClient googleAdsServiceClient =
                     googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {

            String query =
                    "SELECT customer_client.client_customer, customer_client.level, "
                            + "customer_client.manager, customer_client.descriptive_name, "
                            + "customer_client.currency_code, customer_client.time_zone, "
                            + "customer_client.id "
                            + "FROM customer_client "
                            + "WHERE customer_client.level <= 1";

            managerAccountsToSearch.add(loginCustomerId);

            while (!managerAccountsToSearch.isEmpty()) {
                long customerIdToSearchFrom = managerAccountsToSearch.poll();
                GoogleAdsServiceClient.SearchPagedResponse response;
                try {
                    // Issues a search request.
                    response =
                            googleAdsServiceClient.search(
                                    SearchGoogleAdsRequest.newBuilder()
                                            .setQuery(query)
                                            .setCustomerId(Long.toString(customerIdToSearchFrom))
                                            .build());

                    // Iterates over all rows in all pages to get all customer clients under the specified
                    // customer's hierarchy.
                    for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                        CustomerClient customerClient = googleAdsRow.getCustomerClient();
                        long clientId = customerClient.getId();

                        // Adds the customer ID to the list.
                        if (!customerIdsList.contains(clientId)) {
                            customerIdsList.add(clientId);
                        }

                        // Checks if the child account is a manager itself so that it can later be processed
                        // and added to the list if it hasn't been already.
                        if (customerClient.getManager() && customerClient.getLevel() == 1) {
                            if (!managerAccountsToSearch.contains(clientId)) {
                                managerAccountsToSearch.add(clientId);
                            }
                        }
                    }
                } catch (GoogleAdsException gae) {
                    System.out.printf(
                            "Unable to retrieve hierarchy for customer ID %d: %s%n",
                            customerIdToSearchFrom, gae.getGoogleAdsFailure().getErrors(0).getMessage());
                    return null;
                }
            }

            return customerIdsList;
        }
    }
}