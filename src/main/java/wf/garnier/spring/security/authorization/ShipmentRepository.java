package wf.garnier.spring.security.authorization;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;
import org.springframework.stereotype.Component;

@Component
class ShipmentRepository {

	private final List<Shipment> shipments = List.of(
			new Shipment(UUID.randomUUID(), "10 Downing Street, London", Shipment.Status.PREPARING, List.of()),
			new Shipment(UUID.randomUUID(), "Bag End, Hobbiton", Shipment.Status.DELIVERING,
					List.of("Received package", "Shipped to processing center", "Left processing center")),
			new Shipment(UUID.randomUUID(), "221B Baker Street, London", Shipment.Status.UNKNOWN,
					List.of("Picked up at center by a M. Moriarty")),
			new Shipment(UUID.randomUUID(), "Palau de Congressos, Av. de la Reina Maria Cristina",
					Shipment.Status.DELIVERED, List.of("What a great conference ❤️")));


	@HasDomain(domains = {"corp.example.com", "example.com"})
	@AuthorizeReturnObject
	public List<Shipment> findAll() {
		return shipments;
	}

	@PreAuthorize("hasRole('admin')")
	@HandleAuthorizationDenied(handlerClass = NullAuthorizationHandler.class)
	public Integer count() {
		return shipments.size();
	}

}
