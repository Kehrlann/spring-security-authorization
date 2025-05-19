package wf.garnier.spring.security.authorization;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;

class Shipment {

	private final UUID id;

	private final String address;

	private final Status status;

	private final List<String> events;

	public Shipment(UUID id, String address, Status status, List<String> events) {
		this.id = id;
		this.address = address;
		this.status = status;
		this.events = events;
	}

	public UUID getId() {
		return id;
	}

	@PreAuthorize("hasRole('admin')")
	@HandleAuthorizationDenied(handlerClass = RedactedAuthorizationHandler.class)
	public String getAddress() {
		return address;
	}

	public Status getStatus() {
		return status;
	}

	public List<String> getEvents() {
		return events;
	}

	enum Status {

		PREPARING, DELIVERING, DELIVERED, UNKNOWN

	}

}
