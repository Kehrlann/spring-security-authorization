package wf.garnier.spring.security.authorization;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.function.SingletonSupplier;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

/**
 * Simple OpenFGA client, targeting a hardcoded OpenFGA server. See the {@code openfga/}
 * directory in the root of this repository.
 */
public class OpenFgaClient {

	private static final Logger logger = LoggerFactory.getLogger(OpenFgaClient.class);

	private static final String OPENFGA_URL = "http://localhost:9000";

	private static final String STORE_NAME = "authorization-demo";

	private final SingletonSupplier<String> storeId;

	private final SingletonSupplier<String> authorizationModelId;

	private final RestClient restClient;

	private OpenFgaClient(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.baseUrl(OPENFGA_URL).build();
		this.storeId = SingletonSupplier.of(() -> this.restClient.get()
			.uri("/stores")
			.retrieve()
			.body(StoresResponse.class)
			.stores()
			.stream()
			.filter(s -> s.name().equals(STORE_NAME))
			.findFirst()
			.get()
			.id());
		this.authorizationModelId = SingletonSupplier.of(() -> this.restClient.get()
			.uri("/stores/{storeId}/authorization-models?page_size=1", this.storeId.get())
			.retrieve()
			.body(AuthorizationModelsResponse.class)
			.authorizationModels()
			.get(0)
			.id());

	}

	public boolean checkPermission(String user, String relation, String object) {
		return this.restClient.post()
			.uri("/stores/{storeId}/check", this.storeId.get())
			.body(new CheckRequest(authorizationModelId.get(), new CheckRequest.TupleKey(user, relation, object)))
			.retrieve()
			.body(CheckResponse.class)
			.allowed();
	}

	public static OpenFgaClient create(RestClient.Builder builder) {
		try {
			builder.build().get().uri(OPENFGA_URL).retrieve().toBodilessEntity();
		}
		catch (HttpStatusCodeException e) {
			if (e.getStatusCode().is4xxClientError()) {
				logger.info("🔐✅ OpenFGA available");
				return new OpenFgaClient(builder);
			}
		}
		catch (Exception ignored) {
		}
		logger.info("🔐❌ OpenFGA not available");
		return new DenyAll();
	}

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	private record CheckRequest(String authorizationModelId, TupleKey tupleKey) {
		record TupleKey(String user, String relation, String object) {

		}
	}

	private record CheckResponse(boolean allowed, String resolution) {

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record StoresResponse(List<Store> stores) {
		@JsonIgnoreProperties(ignoreUnknown = true)
		record Store(String id, String name) {

		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	private record AuthorizationModelsResponse(List<AuthorizationModels> authorizationModels) {
		@JsonIgnoreProperties(ignoreUnknown = true)
		record AuthorizationModels(String id) {

		}
	}

	public static class DenyAll extends OpenFgaClient {

		public DenyAll() {
			super(RestClient.builder());
		}

		@Override
		public boolean checkPermission(String user, String relation, String object) {
			return false;
		}

	}

}
